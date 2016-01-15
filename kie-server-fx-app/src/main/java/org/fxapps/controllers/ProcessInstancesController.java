package org.fxapps.controllers;

import static org.fxapps.utils.AppUtils.doAsyncWork;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import org.fxapps.navigation.Navigation;
import org.fxapps.navigation.Param;
import org.fxapps.service.KieServerClientManager;
import org.fxapps.service.KieServerClientService;
import org.fxapps.utils.AppUtils;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.instance.ProcessInstance;

public class ProcessInstancesController implements Initializable {

	@FXML
	TableView<ProcessInstance> tblInstances;
	@FXML
	TableColumn<ProcessInstance, Long> clId;
	@FXML
	TableColumn<ProcessInstance, String> clProcId;
	@FXML
	TableColumn<ProcessInstance, String> clProcName;
	@FXML
	TableColumn<ProcessInstance, String> clVersion;
	@FXML
	TableColumn<ProcessInstance, Integer> clState;
	@FXML
	TableColumn<ProcessInstance, Date> clStarted;
	@FXML
	TableColumn<ProcessInstance, String> clInitiator;

	@FXML
	Label lblTitle;

	@FXML
	Button btnAbort;
	@FXML
	Button btnSignal;
	@FXML
	Button btnVariables;
	@FXML
	Button btnUserTasks;

	private KieServerClientService service;
	private ProcessDefinition proc;
	private KieContainerResource container;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		container = (KieContainerResource) Navigation.get().data()
				.get(Param.CONTAINER);
		service = KieServerClientManager.getInstance();
		proc = (ProcessDefinition) Navigation.get().data()
				.get(Param.PROCESS_DEFINITION);
		lblTitle.setText("Process " + proc.getName() + " v" + proc.getVersion()
				+ " Instances");
		tblInstances.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		AppUtils.disableIfNotSelected(tblInstances.getSelectionModel(),
				btnAbort, btnSignal, btnUserTasks, btnVariables);
		configureColumns();
		fillData();
	}

	public void goBack() {
		Navigation.get().goToPreviousScreen();
	}

	private void fillData() {
		doAsyncWork(() -> service.findProcessInstancesByProcessId(proc.getId(),
				null, 0, 100), tblInstances.getItems()::setAll,
				AppUtils::showExceptionDialog);

	}

	private void configureColumns() {
		clId.setCellValueFactory(new PropertyValueFactory<>("id"));
		clProcId.setCellValueFactory(new PropertyValueFactory<>("processId"));
		clProcName
				.setCellValueFactory(new PropertyValueFactory<>("processName"));
		clVersion.setCellValueFactory(new PropertyValueFactory<>(
				"processVersion"));
		clState.setCellValueFactory(new PropertyValueFactory<>("state"));
		clStarted.setCellValueFactory(new PropertyValueFactory<>("date"));
		clInitiator
				.setCellValueFactory(new PropertyValueFactory<>("initiator"));
	}

	public void abort() {
		List<ProcessInstance> selected = tblInstances.getSelectionModel()
				.getSelectedItems();
		
		String selectedStr = selected.stream().map(p -> p.getId().toString())
				.collect(Collectors.joining(", "));
		List<Long> ids = selected.stream().map(ProcessInstance::getId)
				.collect(Collectors.toList());
		boolean okay = AppUtils
				.askIfOk("Are you sure you want to abort the following process instances ID?: "
						+ selectedStr);
		if (okay) {
			service.abortProcessInstances(container.getContainerId(), ids);
		}

	}

	public void signal() {

	}

	public void variables() {

	}

	public void userTasks() {

	}
}