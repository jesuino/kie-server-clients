package org.fxapps.kieserverclient.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import javax.inject.Inject;

import org.fxapps.kieserverclient.navigation.Navigation;
import org.fxapps.kieserverclient.navigation.Param;
import org.fxapps.kieserverclient.navigation.Screen;
import org.fxapps.kieserverclient.service.KieServerClientService;
import org.fxapps.kieserverclient.utils.AppUtils;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieContainerResourceList;
import org.kie.server.api.model.KieServerInfo;
import org.kie.server.api.model.ServiceResponse.ResponseType;
import org.kie.server.api.model.instance.TaskSummary;

import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

public class ContainersController implements Initializable {

	@Inject
	Navigation navigation;
	
	@Inject
	KieServerClientService service;
	
	@FXML
	Label lblServerInfo;

	@FXML
	MenuItem mnCommands;

	@FXML
	MenuItem mnProcesses;
	
	@FXML
	MenuItem mnUserTasks;
	
	@FXML
	MenuItem mnMigration;

	@FXML
	MenuItem mnJobs;
	
	@FXML
	MenuItem mnQueries;

	@FXML
	MenuItem mnDispose;
	
	@FXML
	MenuItem mnProcessTasksCharts;

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

	/*
	 * Fill the screen
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		configureTableColumns();
		configureBindings();
		updateData();
	}


	public void importContainers() {
		navigation.goTo(Screen.IMPORT_CONTAINERS);
	}

	public void newContainer() {
		navigation.goTo(Screen.NEW_CONTAINER);
	}

	public void disposeContainer() {
		final KieContainerResource container = tblContainers.getSelectionModel().getSelectedItem();
		AppUtils.doBlockingAsyncWork(() -> {
			return service.disposeContainer(container.getContainerId());

		}, r -> {
			if (r.getType() == ResponseType.SUCCESS) {
				AppUtils.showSuccessDialog("Container " + container.getContainerId() + " disposed");
			} else {
				AppUtils.showErrorDialog("Error disposing container", r.getMsg());
			}
			updateData();
		}, AppUtils::showExceptionDialog);
	}

	public void fillServerInfo(KieServerInfo serverInfo) {
		String name = serverInfo.getName();
		String version = serverInfo.getVersion();
		String infoStr = name + " v" + version;
		lblServerInfo.setText(infoStr);
	}

	public void executeCommands() {
		saveSelectedContainer();
		navigation.goTo(Screen.COMMANDS);
	}

	public void openProcesses() {
		saveSelectedContainer();
		navigation.goTo(Screen.PROCESSES_DEFINITIONS);
	}

	private void configureTableColumns() {
		clContainerId.setCellValueFactory(new PropertyValueFactory<>("containerId"));
		clResolvedReleaseId.setCellValueFactory(new PropertyValueFactory<>("resolvedReleaseId"));
		clStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
	}

	private void updateData() {
		fillServerInfo(service.getServerInfo());
		fillContainers(service.listContainers());
	}

	private void configureBindings() {
		BooleanBinding noBPM = service.getSupportsBPM().not();
		BooleanBinding noBRM = service.getSupportsBRM().not();
//		BooleanBinding noSolver = service.getSupportsSolver().not();
		BooleanBinding selectedItem = tblContainers.getSelectionModel().selectedItemProperty().isNull();
		mnDispose.disableProperty().bind(selectedItem);
		mnProcessTasksCharts.disableProperty().bind(selectedItem);
		mnProcesses.disableProperty().bind(selectedItem.or(noBPM));
		mnUserTasks.disableProperty().bind(selectedItem.or(noBPM));
//		mnSolvers.disableProperty().bind(selectedItem.or(noSolver));
		mnQueries.disableProperty().bind(selectedItem.or(noBPM));
		mnJobs.disableProperty().bind(noBPM);
		mnCommands.disableProperty().bind(selectedItem.or(noBRM));
		mnMigration.disableProperty().bind(selectedItem.or(noBPM));
		tblContainers.getSelectionModel().selectedItemProperty().addListener((a,b,c) -> saveSelectedContainer());
	}

	private void fillContainers(List<KieContainerResource> listContainers) {
		tblContainers.getItems().setAll(listContainers);
	}

	private void saveSelectedContainer() {
		KieContainerResource container = tblContainers.getSelectionModel().getSelectedItem();
		navigation.data().put(Param.CONTAINER, container);
	}

	public void export() {
		FileChooser saveFileChooser = new FileChooser();
		saveFileChooser.setTitle("Export Containers");
		File f = saveFileChooser.showSaveDialog(null);
		if (f != null) {
			KieContainerResourceList list = new KieContainerResourceList(tblContainers.getItems());
			String containers = service.getMarshaller().marshall(list);
			try {
				Files.write(Paths.get(f.getAbsolutePath()), containers.getBytes());
				AppUtils.showSuccessDialog("Exported with success!");
			} catch (IOException e) {
				AppUtils.showErrorDialog("Failed to save file " + f.getAbsolutePath());
				e.printStackTrace();
			}
		}
	}

	public void loadTasks() {
		String user = (String) navigation.data().get(Param.USER);
		Supplier<List<TaskSummary>> updateTasks = () -> service.findTasks(user);
		navigation.data().put(Param.UPDATE_USER_TASKS_ACTION, updateTasks);
		navigation.data().put(Param.CALLER_SCREEN, Screen.CONTAINERS);
		navigation.goTo(Screen.USER_TASK_LIST);
	}

	public void jobs() {
		navigation.goTo(Screen.JOBS);
	}
	
	public void processTasksCharts() {
		navigation.goTo(Screen.PROCESS_TASKS_CHARTS);
	}
	
	public void solvers() {
		navigation.goTo(Screen.SOLVERS);
	}
	
	public void queries() {
		navigation.goTo(Screen.QUERIES);
	}
	
	public void migration() {
		navigation.goTo(Screen.MIGRATION);
	}
}