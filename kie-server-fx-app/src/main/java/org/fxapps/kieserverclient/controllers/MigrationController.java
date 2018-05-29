package org.fxapps.kieserverclient.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.fxapps.kieserverclient.navigation.Navigation;
import org.fxapps.kieserverclient.navigation.Param;
import org.fxapps.kieserverclient.service.KieServerClientService;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.definition.ProcessDefinition;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;

public class MigrationController implements Initializable {
	
	@Inject
	Navigation navigation;
	
	@Inject
	KieServerClientService service;

	@FXML
	ChoiceBox<String> cbDefinitions;

	@FXML
	ChoiceBox<String> cbNewContainer;
	
	@FXML
	Button btnAddInstances;

	@FXML
	Button btnRemoveInstances;
	
	@FXML
	ListView<Long> lstInstances;
	
	@FXML
	ListView<Long> lstRemoveInstances;

	private KieContainerResource container;
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		container = (KieContainerResource) navigation.data().get(Param.CONTAINER);
		List<ProcessDefinition> processesDefinitions = service.getProcessesDefinitions(container.getContainerId());
		processesDefinitions.stream().map(d -> d.getName() + " (" + d.getId() + ")").peek(System.out::println).forEach(cbDefinitions.getItems()::add);
	}
}
