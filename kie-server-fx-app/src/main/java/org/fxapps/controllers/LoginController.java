package org.fxapps.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import org.fxapps.navigation.Navigation;
import org.fxapps.navigation.Param;
import org.fxapps.navigation.Screen;
import org.fxapps.service.KieServerClientManager;
import org.fxapps.utils.AppUtils;

public class LoginController implements Initializable {

	final String DEFAULT_SERVER_URL = "http://localhost:8180/kie-server/services/rest/server";
	final String DEFAULT_USER = "kieserver";
	final String DEFAULT_PASSWORD = "kieserver1!";
	Logger logger = Logger.getLogger(this.getClass().getName());

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
		KieServerClientManager.logout();
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
			KieServerClientManager.login(url, usr, psw);
			return null;
		} , r -> {
			Navigation.get().data().put(Param.USER, usr);
			Navigation.get().data().put(Param.PASSWORD, psw);
			Navigation.get().goTo(Screen.CONTAINERS);
		} , AppUtils::showExceptionDialog);
	}

	public void doExit() {
		logger.info("User is leaving the system");
		System.exit(0);
	}

}