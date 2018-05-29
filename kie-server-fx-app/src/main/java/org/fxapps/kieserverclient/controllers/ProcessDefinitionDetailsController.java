package org.fxapps.kieserverclient.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.fxapps.kieserverclient.navigation.Navigation;
import org.fxapps.kieserverclient.navigation.Param;
import org.fxapps.kieserverclient.navigation.Screen;
import org.fxapps.kieserverclient.service.KieServerClientService;
import org.fxapps.kieserverclient.utils.AppUtils;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.definition.UserTaskDefinition;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.web.WebView;
import javafx.util.Pair;

public class ProcessDefinitionDetailsController implements Initializable {
	
	@Inject
	Navigation navigation;
	
	@Inject
	KieServerClientService service;

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

	@FXML
	WebView processSVGViewer;

	@FXML
	Label lblSVGNotAvailable;

	private ProcessDefinition definition;
	private KieContainerResource container;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		container = (KieContainerResource) navigation.data().get(Param.CONTAINER);
		definition = (ProcessDefinition) navigation.data().get(Param.PROCESS_DEFINITION);
		lblTitle.setText("Process " + definition.getName() + " details");
		configureColumns();
		performAsyncCalls();
		loadProcessImage();
		AppUtils.disableIfNotSelected(tblUserTasks.getSelectionModel(), btnViewTasksParams);
	}

	private void loadProcessImage() {
		String svg = service.getProcessImage(container.getContainerId(), definition.getId());
		System.out.println(">>SVG: " + svg);
		if (svg != null && !svg.trim().isEmpty()) {
			lblSVGNotAvailable.setVisible(false);
			processSVGViewer.getEngine().loadContent(svg);
		} else {
			lblSVGNotAvailable.setVisible(true);
			processSVGViewer.setVisible(false);
		}
	}

	private void configureColumns() {
		AppUtils.configureColumnsForPair(clVarName, clVarType);
		AppUtils.configureColumnsForPair(clServiceName, clServiceType);
		clTaskName.setCellValueFactory(new PropertyValueFactory<>("name"));
		clTaskPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
		clTaskComment.setCellValueFactory(new PropertyValueFactory<>("comment"));
		clTaskCreatedBy.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
		clTaskSkippable.setCellValueFactory(new PropertyValueFactory<>("skippable"));
	}

	public void viewTaskParameters() {
		UserTaskDefinition def = tblUserTasks.getSelectionModel().getSelectedItem();
		Navigation n = navigation;
		n.data().put(Param.USER_TASK_DEFINITION, def);
		n.goTo(Screen.TASK_PARAMETERS);
	}

	private void performAsyncCalls() {
		AppUtils.doAsyncWork(() -> service.getVariableDefinitions(container.getContainerId(), definition.getId()),
				v -> {
					List<Pair<String, String>> list = AppUtils.convertMapToPair(v.getVariables());
					tblVariables.getItems().setAll(list);

				}, AppUtils::showExceptionDialog);

		AppUtils.doAsyncWork(() -> service.getServiceTaskDefinitions(container.getContainerId(), definition.getId()),
				t -> {
					List<Pair<String, String>> list = AppUtils.convertMapToPair(t.getServiceTasks());
					tblServiceTasks.getItems().setAll(list);
				}, AppUtils::showExceptionDialog);
		AppUtils.doAsyncWork(() -> service.getUserTaskDefinitions(container.getContainerId(), definition.getId()),
				t -> {
					if (t != null && t.getTasks() != null)
						tblUserTasks.getItems().setAll(t.getTasks());
					else
						tblUserTasks.getItems().clear();
				}, AppUtils::showExceptionDialog);
	}

	public void details() {
		navigation.data().put(Param.DETAILS, tblUserTasks.getItems());
		navigation.goTo(Screen.DETAILS);
	}
}
