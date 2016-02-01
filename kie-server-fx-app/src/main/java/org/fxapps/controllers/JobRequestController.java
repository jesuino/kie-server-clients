package org.fxapps.controllers;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fxapps.navigation.Navigation;
import org.fxapps.navigation.Param;
import org.fxapps.navigation.Screen;
import org.fxapps.service.KieServerClientManager;
import org.fxapps.service.KieServerClientService;
import org.fxapps.utils.AppUtils;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.instance.JobRequestInstance;

import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class JobRequestController implements Initializable {
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
	private KieServerClientService service;
	private String containerId;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		service = KieServerClientManager.getInstance();
		KieContainerResource container = (KieContainerResource) Navigation.get().data().get(Param.CONTAINER);
		containerId = container.getContainerId();
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
		BooleanBinding dataNull = txtData.textProperty().isNull();
		BooleanBinding dataEmptyOrNull = txtData.textProperty().isEmpty().or(dataNull);
		btnSend.disableProperty().bind(dtNull.or(cmdEmptyOrNull).or(dataEmptyOrNull));
	}

	public void back() {
		Navigation.get().goTo(Screen.JOBS);
	}

	public void send() {
		int hours = spHours.getValue();
		int minutes = spMinutes.getValue();
		int seconds = spSeconds.getValue();
		String dataStr = txtData.getText();
		Map<String, Object> data = Stream.of(dataStr.split("\n")).map(s -> s.split("="))
				.collect(Collectors.toMap(s -> s[0], s -> s[1]));
		System.out.println(data.toString());
		LocalDateTime time = dtScheduleDate.getValue().atTime(hours, minutes, seconds);
		Date date = Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
		JobRequestInstance request = new JobRequestInstance();
		request.setCommand(txtCommand.getText());
		request.setData(data);
		request.setScheduledDate(date);
		AppUtils.doBlockingAsyncWork(() -> service.scheduleRequest(containerId, request), l -> {
			AppUtils.showSuccessDialog("Job Request accepted with response: " + l);
			back();
		} , AppUtils::showExceptionDialog);
	}

}