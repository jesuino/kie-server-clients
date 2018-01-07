package org.fxapps.kieserverclient.controllers;

import static org.fxapps.kieserverclient.utils.AppUtils.doAsyncWork;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.fxapps.kieserverclient.navigation.Navigation;
import org.fxapps.kieserverclient.navigation.Param;
import org.fxapps.kieserverclient.navigation.Screen;
import org.fxapps.kieserverclient.service.KieServerClientService;
import org.fxapps.kieserverclient.utils.AppUtils;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.api.model.instance.TaskSummary;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProcessInstancesController implements Initializable {

	@Inject
	Navigation navigation;
	@Inject
	KieServerClientService service;
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
	Button btnDetails;
	@FXML
	Button btnUserTasks;
	@FXML
	Button btnNodes;

	private ProcessDefinition proc;
	private KieContainerResource container;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeVariables();
		configureBindings();
		configureColumns();
		fillData();
	}

	private void configureBindings() {
		AppUtils.disableIfNotSelected(tblInstances.getSelectionModel(), btnAbort, btnSignal, btnDetails, btnUserTasks,
				btnNodes);
	}

	private void initializeVariables() {
		container = (KieContainerResource) navigation.data().get(Param.CONTAINER);
		proc = (ProcessDefinition) navigation.data().get(Param.PROCESS_DEFINITION);
		lblTitle.setText("Process " + proc.getName() + " v" + proc.getVersion() + " Instances");
		tblInstances.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}

	public void abort() {
		String selectedStr = getSelectedAsString();
		List<Long> ids = getSelectedIds();
		boolean okay = AppUtils
				.askIfOk("Are you sure you want to abort the following process instances ID?: " + selectedStr);
		if (okay) {
			AppUtils.doBlockingAsyncWork(() -> {
				service.abortProcessInstances(container.getContainerId(), ids);
				return null;
			}, n -> {
				fillData();
			}, AppUtils::showExceptionDialog);
		}
	}

	public void signal() {
		List<Long> ids = getSelectedIds();
		navigation.data().put(Param.PROCESS_INSTANCES, ids);
		navigation.goTo(Screen.SEND_SIGNAL);
	}

	public void details() {
		List<ProcessInstance> pi = tblInstances.getSelectionModel().getSelectedItems();
		navigation.data().put(Param.DETAILS, pi);
		navigation.goTo(Screen.DETAILS);
	}

	private List<Long> getSelectedIds() {
		List<ProcessInstance> selected = tblInstances.getSelectionModel().getSelectedItems();
		return selected.stream().map(ProcessInstance::getId).collect(Collectors.toList());
	}

	private String getSelectedAsString() {
		List<ProcessInstance> selected = tblInstances.getSelectionModel().getSelectedItems();
		return selected.stream().map(p -> p.getId().toString()).collect(Collectors.joining(", "));
	}

	private void fillData() {
		doAsyncWork(() -> service.findProcessInstancesByProcessId(proc.getId(), null, 0, 100),
				tblInstances.getItems()::setAll, AppUtils::showExceptionDialog);
	}

	private void configureColumns() {
		clId.setCellValueFactory(new PropertyValueFactory<>("id"));
		clProcId.setCellValueFactory(new PropertyValueFactory<>("processId"));
		clProcName.setCellValueFactory(new PropertyValueFactory<>("processName"));
		clVersion.setCellValueFactory(new PropertyValueFactory<>("processVersion"));
		clState.setCellValueFactory(new PropertyValueFactory<>("state"));
		clStarted.setCellValueFactory(new PropertyValueFactory<>("date"));
		clInitiator.setCellValueFactory(new PropertyValueFactory<>("initiator"));
	}

	public void showTasks() {
		ProcessInstance pi = tblInstances.getSelectionModel().getSelectedItem();
		Supplier<List<TaskSummary>> updateTasks = () -> service.findTasksByProcessInstanceId(pi.getId());
		navigation.data().put(Param.UPDATE_USER_TASKS_ACTION, updateTasks);

		navigation.data().put(Param.CALLER_SCREEN, Screen.PROCESS_INSTANCES);
		navigation.goTo(Screen.USER_TASK_LIST);
	}

	public void showNodes() {
		ProcessInstance pi = tblInstances.getSelectionModel().getSelectedItem();
		navigation.data().put(Param.PROCESS_INSTANCE, pi);
		navigation.goTo(Screen.NODES);
	}
}