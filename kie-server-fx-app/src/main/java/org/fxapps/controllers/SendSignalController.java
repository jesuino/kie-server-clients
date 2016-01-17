package org.fxapps.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.fxapps.navigation.Navigation;
import org.fxapps.navigation.Param;
import org.fxapps.service.KieServerClientManager;
import org.fxapps.service.KieServerClientService;
import org.fxapps.utils.AppUtils;
import org.kie.server.api.model.KieContainerResource;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class SendSignalController implements Initializable {
	@FXML
	private Label lblTitle;
	@FXML
	private TextArea txtContent;
	@FXML
	private ComboBox<String> cmbSignal;
	@FXML
	private Button btnSend;
	private KieServerClientService service;
	private String containerId;
	private List<Long> pInstances;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		service = KieServerClientManager.getInstance();
		pInstances = (List<Long>) Navigation.get().data().get(Param.PROCESS_INSTANCES);
		KieContainerResource container = (KieContainerResource) Navigation.get().data().get(Param.CONTAINER);
		containerId = container.getContainerId();
		Long piid = pInstances.get(0);
		lblTitle.setText("Signal Instances: " + pInstances.toString());
		cmbSignal.getItems().clear();
		AppUtils.doAsyncWork(() -> service.getAvailableSignals(containerId, piid), cmbSignal.getItems()::addAll,
				AppUtils::showExceptionDialog);
		AppUtils.disableIfNotSelected(cmbSignal.getSelectionModel(), btnSend);
	}

	public void goBack() {
		Navigation.get().goToPreviousScreen();
	}

	public void sendSignal() {
		String signalName = cmbSignal.getSelectionModel().getSelectedItem();
		String signalContent = txtContent.getText();
		AppUtils.doAsyncWork(() -> {
			service.signalProcessInstances(containerId, pInstances, signalName, signalContent);
			return null;
		} , r -> {
			AppUtils.showSuccessDialog("Processes signal done with success!");
			goBack();
		} , AppUtils::showExceptionDialog);
	}

}