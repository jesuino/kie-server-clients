package org.fxapps.kieserverclient.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.fxapps.kieserverclient.navigation.Navigation;
import org.fxapps.kieserverclient.navigation.Param;
import org.fxapps.kieserverclient.navigation.Screen;
import org.fxapps.kieserverclient.service.KieServerClientManager;
import org.fxapps.kieserverclient.service.KieServerClientService;
import org.fxapps.kieserverclient.utils.AppUtils;
import org.kie.api.builder.ReleaseId;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieContainerResourceList;
import org.kie.server.api.model.ServiceResponse;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ImportController implements Initializable {

	@Inject
	Navigation navigation;
	
	@FXML
	private TextField txtFile;
	@FXML
	private Button btnImport;
	@FXML
	private TableView<ServiceResponse<?>> tblResponse;
	@FXML
	private TableColumn<ServiceResponse<?>, String> clType;
	@FXML
	private TableColumn<ServiceResponse<?>, String> clMessage;

	private KieServerClientService service;
	private IntegerProperty containersToProcess;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		containersToProcess = new SimpleIntegerProperty(-1);
		// once we process all containers we release the screen
		containersToProcess.addListener(obs -> {
			if (containersToProcess.get() == 0) {
				navigation.unblockScreen();

			}
		});
		service = KieServerClientManager.getInstance();
		btnImport.disableProperty().bind(txtFile.textProperty().isEmpty());
		clType.setCellValueFactory(new PropertyValueFactory<>("type"));
		clMessage.setCellValueFactory(new PropertyValueFactory<>("msg"));

	}

	public void importContainers() {
		String containersStr = null;
		KieContainerResourceList containers = null;
		String strFile = txtFile.getText();
		try {
			containersStr = new String(Files.readAllBytes(Paths.get(strFile)));
		} catch (IOException e) {
			AppUtils.showErrorDialog("Not able to read file " + strFile);
			e.printStackTrace();
		}
		if (containersStr != null) {
			try {
				containers = service.getMarshaller().unmarshall(containersStr, KieContainerResourceList.class);
			} catch (Exception e) {
				AppUtils.showErrorDialog("Not able to parse file content. File: " + strFile);
				e.printStackTrace();
			}
		}
		if (containers != null) {
			containersToProcess.set(containers.getContainers().size());
			navigation.blockScreen();
			for (KieContainerResource container : containers.getContainers()) {
				ReleaseId gav = container.getReleaseId();
				AppUtils.doAsyncWork(() -> {
					return service.createContainer(container.getContainerId(), gav.getGroupId(), gav.getArtifactId(),
							gav.getVersion());
				} ,r -> { 
					containersToProcess.set(containersToProcess.get() - 1);
					tblResponse.getItems().add(r);
				}, e -> {
					containersToProcess.set(containersToProcess.get() - 1);
					AppUtils.showExceptionDialog(e);
				});
			}
		}
	}

	public void goBack() {
		navigation.goTo(Screen.CONTAINERS);
	}

	public void selectFile() {
		FileChooser openFileChooser = new FileChooser();
		openFileChooser.getExtensionFilters().add(new ExtensionFilter("JSON Files", "*.json"));
		openFileChooser.setTitle("Select file to import");
		File f = openFileChooser.showOpenDialog(null);
		if (f != null) {
			txtFile.setText(f.getAbsolutePath());
		}
	}

	public void details() {
		navigation.data().put(Param.DETAILS, tblResponse.getItems());
		navigation.goTo(Screen.DETAILS);
	}
}