package org.fxapps.kieserverclient.navigation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

@ApplicationScoped
public class Navigation {

	@Inject
	FXMLLoader fxmlLoader;

	private static final int DEFAULT_H = 600;

	private static final int DEFAULT_W = 800;

	private static final int TRANSITION_DURATION = 100;

	private static final String MAIN_CSS = "/style/main.css";

	final Screen INITIAL_SCREEN = Screen.LOGIN;

	/**
	 * Used to transfer data between screens
	 */
	private Map<Param, Object> data;

	private Screen home;

	private Scene scene;
	
	Stack<Screen> screenHistory;
	
	@PostConstruct
	private void construct() {
		screenHistory = new Stack<>();
		Parent root;
		try {
			root = fxmlLoader.load(Screen.TEMPLATE.getURL().openStream());
			this.scene = new Scene(root, DEFAULT_W, DEFAULT_H);
			this.scene.getStylesheets().add(MAIN_CSS);
			this.data = new HashMap<>();
			// we start at login screen
			goTo(Screen.LOGIN);
			// home screen is the containers
			this.home = Screen.CONTAINERS;
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void goTo(Screen screen) {
		goTo(screen, true);
	}
	
	public Screen getHome() {
		return home;
	}

	public void goHome() {
		screenHistory.clear();
		goTo(home, false);
	}
	
	public void blockScreen() {
		scene.getRoot().setDisable(true);
	}

	public void unblockScreen() {
		scene.getRoot().setDisable(false);
	}

	public Scene getScene() {
		return scene;
	}

	public Map<Param, Object> data() {
		return data;
	}

	public void goBack() {
		if(screenHistory.empty()) {
			return;
		}
		screenHistory.pop();
		goTo(screenHistory.peek(), false);
	}

	private Parent loadScreen(Screen screen) throws IOException {
		fxmlLoader.setRoot(null);
		fxmlLoader.setController(null);
		Parent newScreen = fxmlLoader.load(screen.getURL().openStream());
		return newScreen;
	}
	
	public void logout() {
		goTo(Screen.LOGIN, false);
	}
	
	private void goTo(Screen screen, boolean keepHistory) {
		System.out.println("GOING TO SCREEN " + screenHistory);
		if(keepHistory) {
			screenHistory.push(screen);
		}
		Node border = scene.getRoot().getChildrenUnmodifiable()
			.stream()
			.filter(n -> n instanceof BorderPane)
			.findFirst().get();
		BorderPane root = (BorderPane) border;
		Node current = root.getCenter();
		FadeTransition hideCurrent = new FadeTransition();
		hideCurrent.setDuration(Duration.millis(TRANSITION_DURATION));
		hideCurrent.setNode(current);
		hideCurrent.setFromValue(1);
		hideCurrent.setToValue(0);
		hideCurrent.setOnFinished(e -> {
			try {
				Parent newScreen = loadScreen(screen);
				newScreen.setOpacity(0);
				root.setCenter(newScreen);
				FadeTransition showNew = new FadeTransition();
				showNew.setDuration(Duration.millis(TRANSITION_DURATION));
				showNew.setNode(newScreen);
				showNew.setFromValue(0);
				showNew.setToValue(1);
				showNew.play();
			} catch (Exception e1) {
				e1.printStackTrace();
				throw new Error(e1.getMessage());
			}
		});
		hideCurrent.play();
		if (screen == Screen.LOGIN) {
			root.getBottom().setVisible(false);
		} else {
			root.getBottom().setVisible(true);
		}
		// improve later plz
		if (screen == home) {
			root.lookup("#btnBack").setDisable(true);
		} else {
			root.lookup("#btnBack").setDisable(false);
		}
	}

	
}