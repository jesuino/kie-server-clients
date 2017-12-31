package org.fxapps.kieserverclient.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.fxapps.kieserverclient.navigation.Navigation;
import org.fxapps.kieserverclient.navigation.Param;
import org.fxapps.kieserverclient.navigation.Screen;
import org.fxapps.kieserverclient.service.KieServerClientManager;
import org.fxapps.kieserverclient.service.KieServerClientService;
import org.fxapps.kieserverclient.utils.AppUtils;
import org.kie.server.api.model.instance.RequestInfoInstance;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class SolversController implements Initializable {

	@Inject
	Navigation navigation;
	
	private KieServerClientService service;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		service = KieServerClientManager.getInstance();
	}

	public void back() {
		navigation.goTo(Screen.CONTAINERS);
	}

}
