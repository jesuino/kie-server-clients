package org.fxapps.kieserverclient.cdi;

import javax.enterprise.util.AnnotationLiteral;

import org.fxapps.kieserverclient.utils.CDIUtils;

import javafx.application.Application;
import javafx.stage.Stage;

public class CDIApplication extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	@SuppressWarnings("serial")
	public void start(Stage primaryStage) throws Exception {
		CDIUtils.fireEvent(primaryStage, new AnnotationLiteral<StartupScene>() {
		});
	}

}