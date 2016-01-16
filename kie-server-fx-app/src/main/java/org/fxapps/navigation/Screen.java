package org.fxapps.navigation;

import java.net.URL;

public enum Screen {

	LOGIN("login.fxml"), CONTAINERS("containers.fxml"), NEW_CONTAINER(
			"newContainer.fxml"), COMMANDS("executeCommands.fxml"), EXECUTION_RESULTS(
			"executionResults.fxml"), PROCESSES_DEFINITIONS(
			"processesDefinitions.fxml"), PROCESS_DEFINITION_DETAIL(
			"processDefinitionDetails.fxml"), TASK_PARAMETERS(
			"taskParameters.fxml"), PROCESS_INSTANCES("processInstances.fxml"), PROC_INSTANCE_DETAILS("procInstanceDetails.fxml");

	private String path;

	private Screen(String path) {
		this.path = path;
	}

	public URL getURL() {
		return this.getClass().getResource("/screens/" + path);
	}

}