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
import javafx.scene.control.ListView;
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
	ListView<KieContainerResource> lstContainers;

	private KieServerClientService service;

	/*
	 * Fill the screen
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		service = KieServerClientManager.getInstance();
		configureBindings();
		updateData();
	}

	public void doLogout() {
		Navigation.getInstance().goTo(Screen.LOGIN);
	}

	public void disposeContainer() {
		KieContainerResource container;
		try {
			container = lstContainers.getSelectionModel().getSelectedItem();
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
		Navigation.getInstance().goTo(Screen.NEW_CONTAINER);
	}


	public void executeCommands() {
		saveSelectedContainer();
		Navigation.getInstance().goTo(Screen.COMMANDS);
	}

	private class ContainerListCell extends ListCell<KieContainerResource> {
		@Override
		public void updateItem(KieContainerResource item, boolean empty) {
			super.updateItem(item, empty);
			if (item == null)
				return;
			Paint color;
			String id = item.getContainerId();
			String resolvedRelease = "Artifact not resolved";
			if (item.getResolvedReleaseId() != null) {
				resolvedRelease = item.getResolvedReleaseId().toString();
			}
			switch (item.getStatus()) {
			case STARTED:
				color = Color.DARKBLUE;
				break;
			case STOPPED:
				color = Color.RED;
			default:
				color = Color.DARKORANGE;
				break;
			}
			setTextFill(color);
			String txt = id + " (" + resolvedRelease + ")";
			setText(txt);
		}
	}
	
	public void openProcesses() {
		saveSelectedContainer();
		Navigation.getInstance().goTo(Screen.PROCESSES_DEFINITIONS);
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
		BooleanBinding selectedItem = lstContainers.getSelectionModel()
				.selectedItemProperty().isNull();
		btnProcesses.disableProperty().bind(selectedItem);
		btnDispose.disableProperty().bind(selectedItem.and(runRulesProp));
		btnCommands.disableProperty().bind(selectedItem.and(runProcessProp));
	}

	private void fillContainers(List<KieContainerResource> listContainers) {
		lstContainers.getItems().clear();
		listContainers.forEach(lstContainers.getItems()::add);
		lstContainers.setCellFactory(lst -> {
			return new ContainerListCell();
		});
	}
	
	private void saveSelectedContainer() {
		KieContainerResource container = lstContainers.getSelectionModel()
				.getSelectedItem();
		Navigation.getInstance().getData().put(Param.CONTAINER, container);
	}
}