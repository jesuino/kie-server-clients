package org.fxapps.kieserverclient.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.fxapps.kieserverclient.navigation.Navigation;
import org.fxapps.kieserverclient.navigation.Param;
import org.fxapps.kieserverclient.service.KieServerClientManager;
import org.fxapps.kieserverclient.service.KieServerClientService;
import org.fxapps.kieserverclient.utils.AppUtils;
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
	@Inject
	Navigation navigation;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		service = KieServerClientManager.getInstance();
		pInstances = (List<Long>) navigation.data().get(Param.PROCESS_INSTANCES);
		KieContainerResource container = (KieContainerResource) navigation.data().get(Param.CONTAINER);
		containerId = container.getContainerId();
		Long piid = pInstances.get(0);
		lblTitle.setText("Signal Instances: " + pInstances.toString());
		cmbSignal.getItems().clear();
		AppUtils.doAsyncWork(() -> service.getAvailableSignals(containerId, piid), cmbSignal.getItems()::addAll,
				AppUtils::showExceptionDialog);
		AppUtils.disableIfNotSelected(cmbSignal.getSelectionModel(), btnSend);
	}

	public void sendSignal() {
		String signalName = cmbSignal.getSelectionModel().getSelectedItem();
		String signalContent = txtContent.getText();
		AppUtils.doBlockingAsyncWork(() -> {
			service.signalProcessInstances(containerId, pInstances, signalName, signalContent);
			return null;
		}, r -> {
			AppUtils.showSuccessDialog("Processes signal done with success!");
		}, AppUtils::showExceptionDialog);
	}

}