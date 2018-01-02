package org.fxapps.kieserverclient.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.inject.Inject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import org.fxapps.kieserverclient.navigation.Navigation;
import org.fxapps.kieserverclient.navigation.Param;
import org.fxapps.kieserverclient.service.KieServerClientManager;
import org.fxapps.kieserverclient.utils.FormatUtils;
import org.kie.server.api.marshalling.Marshaller;

public class DetailsController implements Initializable {

	private final String JSON = "JSON";
	private final String PLAIN_TEXT = "Plain Text";
	private final String CSV = "CSV";
	
	@Inject
	Navigation navigation;
	
	@FXML
	ComboBox<String> cmbFormat;
	@FXML
	TextArea txtDetails;
	private List<Object> obj;
	private Marshaller marshaller;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		obj = (List<Object>) navigation.data().get(Param.DETAILS);
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
			newContent = FormatUtils.toPlainText(obj);
			break;
		case JSON:
			newContent = marshaller.marshall(obj);
			break;
		case CSV:
			newContent = FormatUtils.toCSV(obj);
			break;
		default:
			break;
		}
		txtDetails.setText(newContent);
	}

	public void selectText() {
		txtDetails.selectAll();
		txtDetails.requestFocus();
	}

}