package org.fxapps.kieserverclient.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.fxapps.kieserverclient.navigation.Navigation;
import org.fxapps.kieserverclient.service.KieServerClientService;
import org.fxapps.kieserverclient.utils.AppUtils;
import org.kie.server.api.model.definition.QueryDefinition;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class NewQueryDefinitionController implements Initializable {

	// copied from the jbpm query API
	enum Target {
		PROCESS, TASK, BA_TASK, PO_TASK, JOBS, FILTERED_PROCESS, FILTERED_BA_TASK, FILTERED_PO_TASK, CUSTOM;
	}

	@Inject
	KieServerClientService service;

	@Inject
	Navigation navigation;

	@FXML
	ChoiceBox<Target> cbTarget;

	@FXML
	TextField txtName;

	@FXML
	TextField txtSource;

	@FXML
	TextArea txtExpression;

	@FXML
	Button btnRegister;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		cbTarget.getItems().addAll(Target.values());
	}

	public void register() {
		QueryDefinition def = new QueryDefinition();
		def.setExpression(txtExpression.getText());
		def.setName(txtName.getText());
		def.setSource(txtSource.getText());
		def.setTarget(cbTarget.getSelectionModel().getSelectedItem().toString());
		AppUtils.doBlockingAsyncWork(() -> {
			service.registerQuery(def);
			return null;
		}, r -> {
			AppUtils.showSuccessDialog("Query Definition registered with sucess!");
			navigation.goToPreviousScreen();
		}, AppUtils::showExceptionDialog);
	}

}
