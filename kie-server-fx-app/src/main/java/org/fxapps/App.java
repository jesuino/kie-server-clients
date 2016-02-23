package org.fxapps;

import org.fxapps.navigation.Navigation;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Scene scene = Navigation.get().getScene();
		stage.setScene(scene);
		stage.setTitle("KIE Server Management Client");
		stage.show();
	}
}