package org.fxapps.navigation;

import java.net.URL;

public enum Screen {

	LOGIN("login.fxml"), 
	CONTAINERS("containers.fxml"), 
	NEW_CONTAINER("newContainer.fxml"), 
	COMMANDS("executeCommands.fxml"), 
	EXECUTION_RESULTS("executionResults.fxml"), 
	PROCESSES_DEFINITIONS("processesDefinitions.fxml"), 
	PROCESS_DEFINITION_DETAIL("processDefinitionDetails.fxml"), 
	TASK_PARAMETERS("taskParameters.fxml"), 
	PROCESS_INSTANCES("processInstances.fxml"), 
	DETAILS("details.fxml"), 
	SEND_SIGNAL("sendSignal.fxml"), 
	IMPORT_CONTAINERS("importContainers.fxml"), 
	USER_TASK_LIST("userTasksList.fxml"), 
	JOBS("jobs.fxml"), 
	SOLVERS("solvers.fxml"),
	JOB_REQUEST("jobRequest.fxml"),
	PROCESS_TASKS_CHARTS("processTasksCharts.fxml");

	private String path;

	private Screen(String path) {
		this.path = path;
	}

	public URL getURL() {
		return this.getClass().getResource("/screens/" + path);
	}

}