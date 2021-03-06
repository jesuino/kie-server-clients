package org.fxapps.kieserverclient.controllers;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.fxapps.kieserverclient.navigation.Navigation;
import org.fxapps.kieserverclient.navigation.Param;
import org.fxapps.kieserverclient.service.KieServerClientService;
import org.fxapps.kieserverclient.utils.AppUtils;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.instance.ProcessInstance;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

public class MigrationController implements Initializable {

	@Inject
	Navigation navigation;

	@Inject
	KieServerClientService service;

	@FXML
	ComboBox<String> cbDefinitions;

	@FXML
	ChoiceBox<String> cbNewContainer;

	@FXML
	Button btnAddInstances;

	@FXML
	Button btnRemoveInstances;

	@FXML
	ListView<Long> lstInstances;

	@FXML
	ListView<Long> lstSelectedInstances;

	@FXML
	Button btnMigrate;

	private KieContainerResource container;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		container = (KieContainerResource) navigation.data().get(Param.CONTAINER);
		loadData();
		initializeAndConfigureBindings();
	}

	private void loadData() {
		service.listContainers().stream().filter(l -> !l.getContainerId().equals(container.getContainerId()))
				.map(KieContainerResource::getContainerId).forEach(cbNewContainer.getItems()::add);
		AppUtils.doBlockingAsyncWork(() -> service.getProcessesDefinitions(container.getContainerId()),
				def -> def.stream().map(ProcessDefinition::getId).forEach(cbDefinitions.getItems()::add),
				e -> AppUtils.showErrorDialog(e.getMessage()));
	}

	private void initializeAndConfigureBindings() {
		lstInstances.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		lstSelectedInstances.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		cbDefinitions.getSelectionModel().selectedItemProperty().addListener((a, b, c) -> {
			lstInstances.getItems().clear();
			AppUtils.doBlockingAsyncWork(() -> service.findProcessInstancesByProcessId(c, null, 0, 100),
					lst -> lst.stream().map(ProcessInstance::getId).forEach(lstInstances.getItems()::add),
					e -> AppUtils.showErrorDialog(e.getMessage()));
		});
		btnMigrate.disableProperty().bind(cbNewContainer.getSelectionModel().selectedItemProperty().isNull()
				.or(lstSelectedInstances.itemsProperty().isNull()));
		btnAddInstances.disableProperty().bind(lstInstances.getSelectionModel().selectedItemProperty().isNull());
		btnRemoveInstances.disableProperty()
				.bind(lstSelectedInstances.getSelectionModel().selectedItemProperty().isNull());
		cbNewContainer.valueProperty().addListener((a, b, c) -> checkSelectedContainer(c));
	}

	private void checkSelectedContainer(String c) {
		if (c == null) {
			return;
		}
		String targetDef = cbDefinitions.getValue();
		List<ProcessDefinition> def = service.getProcessesDefinitions(c);
		boolean hasDef = def.stream().filter(d -> d.getId().equals(targetDef)).findAny().isPresent();
		if (!hasDef) {
			AppUtils.showErrorDialog(c + " does not contain definition " + targetDef);
			cbNewContainer.setValue(null);
		}
	}

	public void selectInstances() {
		ObservableList<Long> selectedItems = lstInstances.getSelectionModel().getSelectedItems();
		lstSelectedInstances.getItems().addAll(selectedItems);
		lstInstances.getItems().removeAll(selectedItems);
	}

	public void removeSelectedInstances() {
		ObservableList<Long> selectedItems = lstSelectedInstances.getSelectionModel().getSelectedItems();
		lstInstances.getItems().addAll(selectedItems);
		lstSelectedInstances.getItems().removeAll(selectedItems);
	}

	public void migrate() {
		final String containerId = container.getContainerId();
		final List<Long> processInstancesId = lstSelectedInstances.getItems();
		final String targetContainerId = cbNewContainer.getValue();
		final String targetProcessId = cbDefinitions.getValue();
		final Map<String, String> emptyMap = Collections.emptyMap();
		AppUtils.doAsyncWork(() -> {
			return service.migrateProcessInstances(containerId, processInstancesId, targetContainerId, targetProcessId,
					emptyMap);
		}, report -> {
			// TODO: implement screen for migration results
			System.out.println("MIGRATION REPORT IS: " + report);
		}, AppUtils::showExceptionDialog);
	}

}
