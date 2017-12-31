package org.fxapps.kieserverclient.navigation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.util.Duration;

@ApplicationScoped
public class Navigation {
	
	@Inject FXMLLoader fxmlLoader;
	
	private static final int DEFAULT_H = 600;

	private static final int DEFAULT_W = 800;

	private static final int TRANSITION_DURATION = 100;

	final Screen INITIAL_SCREEN = Screen.LOGIN;

	/**
	 * Used to transfer data between screens
	 */
	private Map<Param, Object> data;

	private Screen previousScreen;
	private Screen currentScreen;

	private Scene scene;

	@PostConstruct
	public void construct() {
		Parent root;
		try {
			root = fxmlLoader.load(Screen.LOGIN.getURL().openStream());
			this.scene = new Scene(root, DEFAULT_W, DEFAULT_H);
			// we start at login screen
			currentScreen = Screen.LOGIN;
			this.data = new HashMap<>();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void goTo(Screen screen) {
		previousScreen = currentScreen;
		currentScreen = screen;
		Parent current = scene.getRoot();
		FadeTransition hideCurrent = new FadeTransition();
		hideCurrent.setDuration(Duration.millis(TRANSITION_DURATION));
		hideCurrent.setNode(current);
		hideCurrent.setFromValue(1);
		hideCurrent.setToValue(0);
		hideCurrent.play();
		hideCurrent.setOnFinished(e -> {
			try {
				fxmlLoader.setRoot(null);
				fxmlLoader.setController(null);
				Parent newScreen = fxmlLoader.load(screen.getURL().openStream());
				newScreen.setOpacity(0);
				scene.setRoot(newScreen);
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

	public void goToPreviousScreen() {
		goTo(previousScreen);
	}

}