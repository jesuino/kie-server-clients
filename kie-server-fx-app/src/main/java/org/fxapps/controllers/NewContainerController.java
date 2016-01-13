package org.fxapps.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import org.fxapps.navigation.Navigation;
import org.fxapps.service.KieServerClientManager;
import org.fxapps.service.KieServerClientService;
import org.fxapps.utils.AppUtils;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.api.model.ServiceResponse.ResponseType;

public class NewContainerController implements Initializable {

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

	private KieServerClientService service;

	Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		service = KieServerClientManager.getInstance();
		addButtonBinding();

	}

	private void addButtonBinding() {
		BooleanBinding id = txtContainerId.textProperty().isEmpty();
		BooleanBinding group = txtGroupID.textProperty().isEmpty();
		BooleanBinding artifact = txtArtifactId.textProperty().isEmpty();
		BooleanBinding version = txtVersion.textProperty().isEmpty();
		BooleanBinding emptyFields = id.or(group).or(artifact).or(version);
		btnAdd.disableProperty().bind(emptyFields);
	}

	public void goBack() {
		Navigation.getInstance().goToPreviousScreen();
	}

	public void addContainer() {
		String id = txtContainerId.getText();
		String artifactId = txtArtifactId.getText();
		String groupId = txtGroupID.getText();
		String version = txtVersion.getText();
		try {
			ServiceResponse<KieContainerResource> response = service
					.createContainer(id, groupId, artifactId, version);
			if (response.getType() == ResponseType.FAILURE) {
				AppUtils.showErrorDialog("Container created with errors (see logs)");
				logger.warning(response.getMsg());
			} else {
				AppUtils.showSuccessDialog("Container created with success!");
				goBack();
			}
		} catch (Exception e) {
			AppUtils.showExceptionDialog(
					"Error creating container! (check logs)", e);
			e.printStackTrace();
		}

	}

}
