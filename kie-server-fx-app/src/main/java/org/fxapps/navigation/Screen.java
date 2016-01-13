package org.fxapps.navigation;

import java.net.URL;

public enum Screen {

	LOGIN("/screens/login.fxml"), CONTAINERS("/screens/containers.fxml"), NEW_CONTAINER(
			"/screens/newContainer.fxml"), COMMANDS(
			"/screens/executeCommands.fxml"), EXECUTION_RESULTS(
			"/screens/executionResults.fxml");

	private String path;

	private Screen(String path) {
		this.path = path;
	}

	public URL getURL() {
		return this.getClass().getResource(path);
	}

}