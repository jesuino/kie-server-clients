package org.fxapps.kieserverclient;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.fxapps.kieserverclient.cdi.StartupScene;
import org.fxapps.kieserverclient.navigation.Navigation;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
	
	@Inject
	Navigation navigation;


	public void start(@Observes @StartupScene Stage stage) {
		Scene scene = navigation.getScene();
		stage.setScene(scene);
		stage.setTitle("KIE Server Management Client");
		stage.show();
	}
	
}