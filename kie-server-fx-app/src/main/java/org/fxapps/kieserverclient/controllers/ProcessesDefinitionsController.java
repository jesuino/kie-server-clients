package org.fxapps.kieserverclient.controllers;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.inject.Inject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import org.fxapps.kieserverclient.navigation.Navigation;
import org.fxapps.kieserverclient.navigation.Param;
import org.fxapps.kieserverclient.navigation.Screen;
import org.fxapps.kieserverclient.service.KieServerClientManager;
import org.fxapps.kieserverclient.service.KieServerClientService;
import org.fxapps.kieserverclient.utils.AppUtils;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.definition.ProcessDefinition;

public class ProcessesDefinitionsController implements Initializable {

	@Inject
	Navigation navigation;
	@FXML
	Button btnDefinitions;
	@FXML
	Button btnInstances;
	@FXML
	Button btnStart;
	@FXML
	Label lblTitle;
	@FXML
	TableView<ProcessDefinition> tblProcesses;
	@FXML
	TableColumn<ProcessDefinition, String> clId;
	@FXML
	TableColumn<ProcessDefinition, String> clName;
	@FXML
	TableColumn<ProcessDefinition, String> clPackage;
	@FXML
	TableColumn<ProcessDefinition, String> clVersion;

	private KieContainerResource container;
	private List<ProcessDefinition> processes;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		container = (KieContainerResource) navigation.data().get(Param.CONTAINER);
		configureTableColumns();
		updateInterface();
		bindings();
		updateTable();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void configureTableColumns() {
		clId.setCellValueFactory(new PropertyValueFactory("id"));
		clName.setCellValueFactory(new PropertyValueFactory("name"));
		clPackage.setCellValueFactory(new PropertyValueFactory("packageName"));
		clVersion.setCellValueFactory(new PropertyValueFactory("version"));
	}

	public void goBack() {
		navigation.goTo(Screen.CONTAINERS);
	}

	public void openInstancesScreen() {
		saveSelectedProcess();
		navigation.goTo(Screen.PROCESS_INSTANCES);
	}

	public void openDefinitionsDetailsScreen() {
		saveSelectedProcess();
		navigation.goTo(Screen.PROCESS_DEFINITION_DETAIL);
	}

	public void details() {
		navigation.data().put(Param.DETAILS, tblProcesses.getItems());
		navigation.goTo(Screen.DETAILS);
	}

	public void start() {
		KieServerClientService service = KieServerClientManager.getInstance();
		ProcessDefinition processDefinition = tblProcesses.getSelectionModel().getSelectedItem();
		Map<String, Object> param = new HashMap<String, Object>();
		AppUtils.doBlockingAsyncWork(
				() -> service.startProcess(container.getContainerId(), processDefinition.getId(), param), piid -> {
					AppUtils.showSuccessDialog("Process Instance created with ID " + piid);
				}, AppUtils::showExceptionDialog);
	}

	private void saveSelectedProcess() {
		navigation.data().put(Param.PROCESS_DEFINITION, tblProcesses.getSelectionModel().getSelectedItem());
	}

	private void updateTable() {
		processes = KieServerClientManager.getInstance().getProcessesDefinitions(container.getContainerId());
		tblProcesses.getItems().setAll(processes);
	}

	private void updateInterface() {
		lblTitle.setText("\"" + container.getContainerId() + "\" Processes Definitions");
	}

	private void bindings() {
		AppUtils.disableIfNotSelected(tblProcesses.getSelectionModel(), btnDefinitions, btnInstances, btnStart);
	}
}
