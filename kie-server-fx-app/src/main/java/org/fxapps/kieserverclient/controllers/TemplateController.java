package org.fxapps.kieserverclient.controllers;

import javax.inject.Inject;

import org.fxapps.kieserverclient.navigation.Navigation;
import org.fxapps.kieserverclient.navigation.Screen;

public class TemplateController {

	@Inject
	Navigation navigation;

	public void doLogout() {
		navigation.goTo(Screen.LOGIN);
	}

	public void goBack() {
		navigation.goToPreviousScreen();
	}
	
	public void goHome() {
		navigation.goHome();
	}

}
