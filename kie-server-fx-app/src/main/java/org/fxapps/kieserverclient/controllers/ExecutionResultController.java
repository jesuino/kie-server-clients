package org.fxapps.kieserverclient.controllers;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import javax.inject.Inject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import org.fxapps.kieserverclient.navigation.Navigation;
import org.fxapps.kieserverclient.navigation.Param;
import org.kie.api.executor.ExecutionResults;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.api.model.ServiceResponse.ResponseType;

public class ExecutionResultController implements Initializable {

	@Inject
	Navigation navigation;
	
	@FXML
	TextArea txtRequest;

	@FXML
	TextArea txtResponse;

	@Override
	@SuppressWarnings("unchecked")
	public void initialize(URL arg0, ResourceBundle arg1) {
		Map<Param, Object> data = navigation.data();
		ServiceResponse<ExecutionResults> resp = (ServiceResponse<ExecutionResults>) data
				.get(Param.RESPONSE);
		String req = data.get(Param.REQUEST).toString();
		if (resp.getType() == ResponseType.FAILURE) {
			txtResponse.setStyle("-fx-text-fill: red");
		} else {
			txtResponse.setStyle("-fx-text-fill: blue");
		}
		// TODO: Change this to a better text representation
//		txtResponse.setText(resp.getResult());
		txtRequest.setText(req);
	}

}
