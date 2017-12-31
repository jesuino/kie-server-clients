package org.fxapps.kieserverclient.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.fxapps.kieserverclient.navigation.Navigation;
import org.fxapps.kieserverclient.navigation.Param;
import org.fxapps.kieserverclient.navigation.Screen;
import org.fxapps.kieserverclient.service.KieServerClientManager;
import org.fxapps.kieserverclient.service.KieServerClientService;
import org.fxapps.kieserverclient.utils.AppUtils;
import org.kie.server.api.model.instance.RequestInfoInstance;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class JobsController implements Initializable {

	@Inject
	Navigation navigation;
	@FXML
	private TableView<RequestInfoInstance> tblJobs;
	@FXML
	private TableColumn<RequestInfoInstance, String> clId;
	@FXML
	private TableColumn<RequestInfoInstance, String> clStatus;
	@FXML
	private TableColumn<RequestInfoInstance, String> clBusinessKey;
	@FXML
	private TableColumn<RequestInfoInstance, String> clMessage;
	@FXML
	private TableColumn<RequestInfoInstance, String> clRetries;
	@FXML
	private TableColumn<RequestInfoInstance, String> clCommand;
	@FXML
	private Button btnCancel;
	@FXML
	private Button btnSchedule;

	private KieServerClientService service;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		service = KieServerClientManager.getInstance();
		configureColumns();
		updateData();
		AppUtils.disableIfNotSelected(tblJobs.getSelectionModel(), btnCancel);
	}

	public void back() {
		navigation.goTo(Screen.CONTAINERS);
	}

	private void updateData() {
		AppUtils.doBlockingAsyncWork(() -> service.getAllJobsRequest(), tblJobs.getItems()::setAll,
				AppUtils::showExceptionDialog);
	}

	private void configureColumns() {
		clBusinessKey.setCellValueFactory(new PropertyValueFactory<>("businessKey"));
		clCommand.setCellValueFactory(new PropertyValueFactory<>("commandName"));
		clId.setCellValueFactory(new PropertyValueFactory<>("id"));
		clMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
		clRetries.setCellValueFactory(new PropertyValueFactory<>("retries"));
		clStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
	}

	public void cancel() {
		long jobId = tblJobs.getSelectionModel().getSelectedItem().getId();
		AppUtils.doBlockingAsyncWork(() -> {
			service.cancelRequest(jobId);
			return null;
		}, n -> {
			AppUtils.showSuccessDialog("Job request " + jobId + " cancelled with success.");
			updateData();
		}, AppUtils::showExceptionDialog);
	}

	public void schedule() {
		navigation.goTo(Screen.JOB_REQUEST);
	}

	public void update() {
		updateData();
	}

	public void details() {
		navigation.data().put(Param.DETAILS, tblJobs.getItems());
		navigation.goTo(Screen.DETAILS);
	}

}
