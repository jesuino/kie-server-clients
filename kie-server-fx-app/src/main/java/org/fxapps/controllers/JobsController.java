package org.fxapps.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.fxapps.navigation.Navigation;
import org.fxapps.navigation.Param;
import org.fxapps.navigation.Screen;
import org.fxapps.service.KieServerClientManager;
import org.fxapps.service.KieServerClientService;
import org.fxapps.utils.AppUtils;
import org.kie.server.api.model.instance.RequestInfoInstance;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class JobsController implements Initializable {
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
	@FXML
	private Button btnRequeue;
	@FXML
	private ComboBox<String> cmbStatus;
	private KieServerClientService service;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		service = KieServerClientManager.getInstance();
		configureColumns();
		updateData();
	}

	public void back() {
		Navigation.get().goTo(Screen.CONTAINERS);
	}
	

	private void updateData() {
		AppUtils.doBlockingAsyncWork(() -> service.getAllJobsRequest(), tblJobs.getItems()::setAll,
				AppUtils::showExceptionDialog);
	}

	private void configureColumns() {
		clBusinessKey.setCellValueFactory(new PropertyValueFactory<>("businessKey"));
		clCommand.setCellValueFactory(new PropertyValueFactory<>("command"));
		clId.setCellValueFactory(new PropertyValueFactory<>("id"));
		clMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
		clRetries.setCellValueFactory(new PropertyValueFactory<>("retries"));
		clStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
	}

	public void cancel() {

	}

	public void schedule() {
		Navigation.get().goTo(Screen.JOB_REQUEST);
	}

	public void requeue() {

	}

	public void details() {
		Navigation.get().data().put(Param.DETAILS, tblJobs.getItems());
		Navigation.get().goTo(Screen.DETAILS);
	}

}
