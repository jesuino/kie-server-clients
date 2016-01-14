package org.fxapps.controllers;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Pair;

import org.fxapps.navigation.Navigation;
import org.fxapps.navigation.Param;
import org.fxapps.utils.AppUtils;
import org.kie.server.api.model.definition.UserTaskDefinition;

public class TaskParamsController implements Initializable {
	
	@FXML
	Label lblTitle;

	@FXML
	TableView<Pair<String, String>> tblInParams;
	@FXML
	TableColumn<String, String> clInName;
	@FXML
	TableColumn<String, String> clInType;

	@FXML
	TableView<Pair<String, String>> tblOutParams;
	@FXML
	TableColumn<String, String> clOutName;
	@FXML
	TableColumn<String, String> clOutType;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Navigation n = Navigation.get();
		UserTaskDefinition t = (UserTaskDefinition) n.data().get(
				Param.USER_TASK_DEFINITION);
		AppUtils.configureColumnsForPair(clInName, clInType);
		AppUtils.configureColumnsForPair(clOutName, clOutType);
		Map<String, String> inMappings = t.getTaskInputMappings();
		tblInParams.getItems().setAll(AppUtils.convertMapToPair(inMappings));
		Map<String, String> outMappings = t.getTaskOutputMappings();
		tblOutParams.getItems().setAll(AppUtils.convertMapToPair(outMappings));
		lblTitle.setText(t.getName() + " params");
	}

	public void goBack() {
		Navigation.get().goToPreviousScreen();
	}

}