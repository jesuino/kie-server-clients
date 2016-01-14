package org.fxapps.navigation;

import java.net.URL;

public enum Screen {

	LOGIN("/screens/login.fxml"), CONTAINERS("/screens/containers.fxml"), NEW_CONTAINER(
			"/screens/newContainer.fxml"), COMMANDS(
			"/screens/executeCommands.fxml"), EXECUTION_RESULTS(
			"/screens/executionResults.fxml"), PROCESSES_DEFINITIONS(
			"/screens/processesDefinitions.fxml"), PROCESS_DEFINITION_DETAIL(
			"/screens/processDefinitionDetails.fxml");

	private String path;

	private Screen(String path) {
		this.path = path;
	}

	public URL getURL() {
		return this.getClass().getResource(path);
	}

}