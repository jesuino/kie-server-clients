package org.fxapps.controllers;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Pair;

import org.fxapps.navigation.Navigation;
import org.fxapps.navigation.Param;
import org.fxapps.navigation.Screen;
import org.fxapps.service.KieServerClientManager;
import org.fxapps.service.KieServerClientService;
import org.fxapps.utils.AppUtils;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.definition.UserTaskDefinition;

public class ProcessDefinitionDetailsController implements Initializable {

	@FXML
	Label lblTitle;

	@FXML
	TableView<Pair<String, String>> tblVariables;
	@FXML
	TableColumn<String, String> clVarName;
	@FXML
	TableColumn<String, String> clVarType;

	@FXML
	TableView<UserTaskDefinition> tblUserTasks;
	@FXML
	TableColumn<UserTaskDefinition, String> clTaskName;
	@FXML
	TableColumn<UserTaskDefinition, String> clTaskPriority;
	@FXML
	TableColumn<UserTaskDefinition, String> clTaskComment;
	@FXML
	TableColumn<UserTaskDefinition, String> clTaskCreatedBy;
	@FXML
	TableColumn<UserTaskDefinition, String> clTaskSkippable;

	@FXML
	TableView<Pair<String, String>> tblServiceTasks;
	@FXML
	TableColumn<String, String> clServiceName;
	@FXML
	TableColumn<String, String> clServiceType;

	@FXML
	Button btnViewTasksParams;

	private ProcessDefinition definition;
	private KieServerClientService service;
	private KieContainerResource container;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		container = (KieContainerResource) Navigation.getInstance().getData()
				.get(Param.CONTAINER);
		definition = (ProcessDefinition) Navigation.getInstance().getData()
				.get(Param.PROCESS_DEFINITION);
		service = KieServerClientManager.getInstance();
		lblTitle.setText("Process " + definition.getName() + " details");
		configureColumns();
		performAsyncCalls();
		AppUtils.disableIfNotSelected(tblUserTasks.getSelectionModel(),
				btnViewTasksParams);
	}

	private void configureColumns() {
		AppUtils.configureColumnsForPair(clVarName, clVarType);
		AppUtils.configureColumnsForPair(clServiceName, clServiceType);
		clTaskName.setCellValueFactory(new PropertyValueFactory<>("name"));
		clTaskPriority.setCellValueFactory(new PropertyValueFactory<>(
				"priority"));
		clTaskComment
				.setCellValueFactory(new PropertyValueFactory<>("comment"));
		clTaskCreatedBy.setCellValueFactory(new PropertyValueFactory<>(
				"createdBy"));
		clTaskSkippable.setCellValueFactory(new PropertyValueFactory<>(
				"skippable"));
	}

	public void goBack() {
		Navigation.getInstance().goTo(Screen.PROCESSES_DEFINITIONS);
	}

	public void viewTaskParameters() {
		UserTaskDefinition def = tblUserTasks.getSelectionModel()
				.getSelectedItem();
		Navigation n = Navigation.getInstance();
		n.getData().put(Param.USER_TASK_DEFINITION, def);
		n.goTo(Screen.TASK_PARAMETERS);
	}

	private void performAsyncCalls() {
		AppUtils.doAsyncWork(
				() -> {
					return service.getVariableDefinitions(
							container.getContainerId(), definition.getId());
				},
				v -> {
					List<Pair<String, String>> list = AppUtils
							.convertMapToPair(v.getVariables());
					tblVariables.getItems().setAll(list);

				}, this::handleException);

		AppUtils.doAsyncWork(
				() -> {
					return service.getServiceTaskDefinitions(
							container.getContainerId(), definition.getId());
				},
				t -> {
					List<Pair<String, String>> list = AppUtils
							.convertMapToPair(t.getServiceTasks());
					tblServiceTasks.getItems().setAll(list);
				}, this::handleException);
		AppUtils.doAsyncWork(
				() -> {
					return service.getUserTaskDefinitions(
							container.getContainerId(), definition.getId());
				}, t -> {
					tblUserTasks.getItems().setAll(t.getTasks());
				}, this::handleException);
	}

	private void handleException(Throwable e) {
		AppUtils.showExceptionDialog("Error when sending request", e);
	}

}
