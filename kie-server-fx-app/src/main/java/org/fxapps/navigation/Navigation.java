package org.fxapps.navigation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class Navigation {

	private static final int TRANSITION_DURATION = 100;

	final Screen INITIAL_SCREEN = Screen.LOGIN;
	final int WIDTH = 640;
	final int HEIGHT = 480;

	/**
	 * Used to transfer data between screens
	 */
	private Map<Param, Object> data;

	private static Navigation INSTANCE;

	private Screen previousScreen;
	private Screen currentScreen;

	private Scene scene;

	private Parent oldParent;

	private Navigation(Scene scene) {
		super();
		// we start at login screen
		currentScreen = Screen.LOGIN;
		this.scene = scene;
		this.data = new HashMap<>();
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
				Parent newScreen = FXMLLoader.load(screen.getURL());
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

	public static Navigation get() {
		if (INSTANCE == null) {
			try {
				Parent root = FXMLLoader.load(Screen.LOGIN.getURL());
				Scene scene = new Scene(root);
				INSTANCE = new Navigation(scene);
			} catch (IOException e) {
				e.printStackTrace();
				throw new Error(e.getMessage());
			}
		}
		return INSTANCE;
	}

	public void showProgressIndicator() {
		oldParent = scene.getRoot();
		oldParent.setDisable(true);
		StackPane newParent = new StackPane(oldParent, new ProgressIndicator());
		scene.setRoot(newParent);
	}
	
	public void hideProgressIndicator() {
		oldParent.setDisable(false);
		scene.setRoot(oldParent);
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