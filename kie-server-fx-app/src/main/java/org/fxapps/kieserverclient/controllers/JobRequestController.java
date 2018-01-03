package org.fxapps.kieserverclient.controllers;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.fxapps.kieserverclient.navigation.Navigation;
import org.fxapps.kieserverclient.navigation.Screen;
import org.fxapps.kieserverclient.service.KieServerClientService;
import org.fxapps.kieserverclient.utils.AppUtils;
import org.kie.server.api.model.instance.JobRequestInstance;

import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class JobRequestController implements Initializable {

	private static final String[] COMMANDS = { "org.jbpm.executor.commands.CDIPrintOutCommand",
			"org.jbpm.executor.commands.DelayedPrintOutCommand", "org.jbpm.executor.commands.LogCleanupCommand",
			"org.jbpm.executor.commands.PrintOutCommand", "org.jbpm.executor.commands.ReoccurringPrintOutCommand",
			"org.jbpm.executor.commands.RequeueRunningJobsCommand" };
	@Inject
	Navigation navigation;
	@Inject
	KieServerClientService service;
	@FXML
	private TextField txtCommand;
	@FXML
	private DatePicker dtScheduleDate;
	@FXML
	private TextArea txtData;
	@FXML
	Spinner<Integer> spSeconds;
	@FXML
	Spinner<Integer> spHours;
	@FXML
	Spinner<Integer> spMinutes;
	@FXML
	private Button btnSend;
	@FXML
	private ComboBox<String> cmbCommands;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		cmbCommands.getItems().addAll(COMMANDS);
		configureSpinners();
		bindings();
	}

	private void configureSpinners() {
		IntegerSpinnerValueFactory hoursFac = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 12);
		IntegerSpinnerValueFactory minFac = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60);
		IntegerSpinnerValueFactory secFac = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60);
		spHours.setValueFactory(hoursFac);
		spMinutes.setValueFactory(minFac);
		spSeconds.setValueFactory(secFac);
	}

	private void bindings() {
		BooleanBinding dtNull = dtScheduleDate.valueProperty().isNull();
		BooleanBinding cmdNull = txtCommand.textProperty().isNull();
		BooleanBinding cmdEmptyOrNull = txtCommand.textProperty().isEmpty().or(cmdNull);
		btnSend.disableProperty().bind(dtNull.or(cmdEmptyOrNull));
	}

	public void back() {
		navigation.goTo(Screen.JOBS);
	}

	public void selectCommand() {
		String selectedCmd = cmbCommands.getSelectionModel().getSelectedItem();
		if (selectedCmd != null) {
			txtCommand.setText(selectedCmd);
		}
	}

	public void send() {
		int hours = spHours.getValue();
		int minutes = spMinutes.getValue();
		int seconds = spSeconds.getValue();
		String dataStr = txtData.getText();
		Map<String, Object> data = Collections.emptyMap();
		if (!dataStr.isEmpty()) {
			try {
				data = Stream.of(dataStr.split("\n")).map(s -> s.split("="))
						.collect(Collectors.toMap(s -> s[0], s -> s[1]));
			} catch (Exception e) {
				AppUtils.showErrorDialog("Set data correcly, for example: param=value\nparam2=value2");
				e.printStackTrace();
			}
		}
		LocalDateTime time = dtScheduleDate.getValue().atTime(hours, minutes, seconds);
		Date date = Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
		JobRequestInstance request = new JobRequestInstance();
		request.setCommand(txtCommand.getText());
		request.setData(data);
		request.setScheduledDate(date);
		AppUtils.doBlockingAsyncWork(() -> service.scheduleRequest(request), l -> {
			AppUtils.showSuccessDialog("Job Request accepted with response: " + l);
			back();
		}, AppUtils::showExceptionDialog);
	}

}