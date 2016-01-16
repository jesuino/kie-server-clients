package org.fxapps.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import org.fxapps.navigation.Navigation;
import org.fxapps.navigation.Param;
import org.fxapps.navigation.Screen;
import org.fxapps.service.KieServerClientManager;
import org.fxapps.service.KieServerClientService;
import org.fxapps.utils.AppUtils;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieServerInfo;

public class ContainersController implements Initializable {

	@FXML
	Label lblServerInfo;

	@FXML
	Button btnCommands;

	@FXML
	Button btnProcesses;

	@FXML
	Button btnDispose;

	@FXML
	Label lblInfo;

	@FXML
	TableView<KieContainerResource> tblContainers;
	@FXML
	TableColumn<KieContainerResource, String> clContainerId;
	@FXML
	TableColumn<KieContainerResource, String> clResolvedReleaseId;
	@FXML
	TableColumn<KieContainerResource, String> clStatus;

	private KieServerClientService service;

	/*
	 * Fill the screen
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		service = KieServerClientManager.getInstance();
		configureTableColumns();
		configureBindings();
		updateData();
	}

	public void doLogout() {
		Navigation.get().goTo(Screen.LOGIN);
	}

	public void disposeContainer() {
		KieContainerResource container;
		try {
			container = tblContainers.getSelectionModel().getSelectedItem();
			service.disposeContainer(container.getContainerId());
			AppUtils.showSuccessDialog("Container "
					+ container.getContainerId() + " disposed");
			updateData();
		} catch (Exception e) {
			AppUtils.showExceptionDialog("Error disposing container!", e);
			e.printStackTrace();
		}
	}

	public void fillServerInfo(KieServerInfo serverInfo) {
		String name = serverInfo.getName();
		String version = serverInfo.getVersion();
		String infoStr = name + " v" + version;
		lblServerInfo.setText(infoStr);
	}

	public void addNewContainer() {
		Navigation.get().goTo(Screen.NEW_CONTAINER);
	}

	public void executeCommands() {
		saveSelectedContainer();
		Navigation.get().goTo(Screen.COMMANDS);
	}

	public void openProcesses() {
		saveSelectedContainer();
		Navigation.get().goTo(Screen.PROCESSES_DEFINITIONS);
	}

	private void configureTableColumns() {
		clContainerId.setCellValueFactory(new PropertyValueFactory<>(
				"containerId"));
		clResolvedReleaseId.setCellValueFactory(new PropertyValueFactory<>(
				"resolvedReleaseId"));
		clStatus.setCellValueFactory(new PropertyValueFactory<>(
				"status"));
	}

	private void updateData() {
		fillServerInfo(service.getServerInfo());
		fillContainers(service.listContainers());
	}

	private void configureBindings() {
		boolean runProcess = service.canRunProcess();
		boolean runRules = service.canRunRules();
		BooleanProperty runProcessProp = new SimpleBooleanProperty(runProcess);
		BooleanProperty runRulesProp = new SimpleBooleanProperty(runRules);
		BooleanBinding selectedItem = tblContainers.getSelectionModel()
				.selectedItemProperty().isNull();
		btnProcesses.disableProperty().bind(selectedItem);
		btnDispose.disableProperty().bind(selectedItem.and(runRulesProp));
		btnCommands.disableProperty().bind(selectedItem.and(runProcessProp));
	}

	private void fillContainers(List<KieContainerResource> listContainers) {
		tblContainers.getItems().setAll(listContainers);
	}

	private void saveSelectedContainer() {
		KieContainerResource container = tblContainers.getSelectionModel()
				.getSelectedItem();
		System.out.println(container.getScanner().getPollInterval());
		System.out.println(container.getScanner().getStatus());
		Navigation.get().data().put(Param.CONTAINER, container);
	}
}