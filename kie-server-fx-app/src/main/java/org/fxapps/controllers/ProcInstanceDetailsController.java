package org.fxapps.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import org.fxapps.navigation.Navigation;
import org.fxapps.navigation.Param;
import org.fxapps.navigation.Screen;
import org.fxapps.service.KieServerClientManager;
import org.fxapps.utils.FormatUtils;
import org.kie.server.api.marshalling.Marshaller;
import org.kie.server.api.model.instance.ProcessInstance;

public class ProcInstanceDetailsController implements Initializable {

	private final String JSON = "JSON";
	private final String PLAIN_TEXT = "Plain Text";
	private final String CSV = "CSV";

	@FXML
	ComboBox<String> cmbFormat;
	@FXML
	TextArea txtDetails;
	private List<ProcessInstance> pi;
	private Marshaller marshaller;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		pi = (List<ProcessInstance>) Navigation.get().data().get(Param.PROC_INSTANCE);
		marshaller = KieServerClientManager.getInstance().getMarshaller();
		configureCmbFormat();
	}

	private void configureCmbFormat() {
		cmbFormat.getItems().clear();
		cmbFormat.getItems().addAll(PLAIN_TEXT, CSV, JSON);
		cmbFormat.getSelectionModel().selectedItemProperty()
		.addListener((obs, o, n) -> {
			changedFormat(n);
		});
		cmbFormat.getSelectionModel().select(PLAIN_TEXT);
	}

	public void changedFormat(String n) {
		String newContent = "";
		switch (n) {
		case PLAIN_TEXT:
			newContent = FormatUtils.toPlainText(pi);
			break;
		case JSON:
			newContent = marshaller.marshall(pi);
			break;
		case CSV:
			newContent = FormatUtils.toCSV(pi);
			break;
		default:
			break;
		}
		txtDetails.setText(newContent);
	}

	public void selectText() {
		txtDetails.selectAll();
	}

	public void goBack() {
		Navigation.get().goTo(Screen.PROCESS_INSTANCES);
	}

}