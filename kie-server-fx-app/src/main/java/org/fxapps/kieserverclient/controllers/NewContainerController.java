package org.fxapps.kieserverclient.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.fxapps.kieserverclient.navigation.Navigation;
import org.fxapps.kieserverclient.service.KieServerClientService;
import org.fxapps.kieserverclient.utils.AppUtils;
import org.kie.server.api.model.ServiceResponse.ResponseType;

import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class NewContainerController implements Initializable {

	@Inject
	Navigation navigation;

	@Inject
	KieServerClientService service;

	@FXML
	TextField txtContainerId;

	@FXML
	TextField txtGroupID;

	@FXML
	TextField txtArtifactId;

	@FXML
	TextField txtVersion;

	@FXML
	Button btnAdd;

	Logger logger = Logger.getLogger(this.getClass().getName());

	private BooleanBinding emptyFields;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		addButtonBinding();

	}

	public void addContainer() {
		if(emptyFields.get()) {
			return;
		}
		String id = txtContainerId.getText();
		String artifactId = txtArtifactId.getText();
		String groupId = txtGroupID.getText();
		String version = txtVersion.getText();
		AppUtils.doBlockingAsyncWork(() -> {
			return service.createContainer(id, groupId, artifactId, version);
		}, r -> {
			if (r.getType() == ResponseType.FAILURE) {
				AppUtils.showErrorDialog("Container created with errors (see logs)");
				logger.warning(r.getMsg());
			} else {
				AppUtils.showSuccessDialog("Container created with success!");
			}
			navigation.goToPreviousScreen();
		}, AppUtils::showExceptionDialog);
	}

	private void addButtonBinding() {
		BooleanBinding id = txtContainerId.textProperty().isEmpty();
		BooleanBinding group = txtGroupID.textProperty().isEmpty();
		BooleanBinding artifact = txtArtifactId.textProperty().isEmpty();
		BooleanBinding version = txtVersion.textProperty().isEmpty();
		emptyFields = id.or(group).or(artifact).or(version);
		btnAdd.disableProperty().bind(emptyFields);
	}
	
}
