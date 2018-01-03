package org.fxapps.kieserverclient.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.fxapps.kieserverclient.navigation.Navigation;
import org.fxapps.kieserverclient.navigation.Param;
import org.fxapps.kieserverclient.navigation.Screen;
import org.fxapps.kieserverclient.service.KieServerClientService;
import org.fxapps.kieserverclient.utils.AppUtils;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

public class LoginController implements Initializable {

	final String DEFAULT_SERVER_URL = "http://localhost:8180/kie-server/services/rest/server";
	final String DEFAULT_USER = "kieserver";
	final String DEFAULT_PASSWORD = "kieserver1!";

	Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Inject
	Navigation navigation;
	
	@Inject
	KieServerClientService service;
	
	@FXML
	private TextField txtURL;

	@FXML
	private TextField txtUsr;

	@FXML
	private TextField txtPsw;

	/**
	 * Do cleanup stuff before showing the login Screen
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		service.logout();
	}

	public void doLogin() {
		final String url;
		final String usr;
		final String psw;
		if (!txtURL.getText().trim().isEmpty()) {
			url = txtURL.getText();
		} else {
			url = DEFAULT_SERVER_URL;
		}
		if (!txtUsr.getText().trim().isEmpty()) {
			usr = txtUsr.getText();
		} else {
			usr = DEFAULT_USER;
		}
		if (!txtPsw.getText().trim().isEmpty()) {
			psw = txtPsw.getText();
		} else {
			psw = DEFAULT_PASSWORD;
		}
		AppUtils.doBlockingAsyncWork(() -> {
			service.login(url, usr, psw);
			return null;
		} , r -> {
			navigation.data().put(Param.USER, usr);
			navigation.data().put(Param.PASSWORD, psw);
			navigation.data().put(Param.URL, url);
			navigation.goTo(Screen.CONTAINERS);
		} , AppUtils::showExceptionDialog);
	}

	public void doExit() {
		logger.info("User is leaving the system");
		System.exit(0);
	}

}