package org.fxapps.controllers;

import org.fxapps.navigation.Navigation;

public class ProcessDefinitionDetailsController {

	public void goBack() {
		Navigation.getInstance().goToPreviousScreen();
	}

}
