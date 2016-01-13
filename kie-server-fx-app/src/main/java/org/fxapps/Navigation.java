package org.fxapps;

import java.io.IOException;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.util.Duration;

public class Navigation {

	private static final int TRANSITION_DURATION = 300;

	final Screen INITIAL_SCREEN = Screen.LOGIN;

	/**
	 * Used to transfer data between screens
	 */
	private Object data;
	
	private static Navigation INSTANCE;
	
	private Scene scene;

	private Navigation(Scene scene) {
		super();
		this.scene = scene;
	}

	public void goTo(Screen screen) {
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

	public static Navigation getInstance() {
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

	public Scene getScene() {
		return scene;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}