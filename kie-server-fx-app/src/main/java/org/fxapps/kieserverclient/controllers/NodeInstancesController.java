package org.fxapps.kieserverclient.controllers;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.fxapps.kieserverclient.navigation.Navigation;
import org.fxapps.kieserverclient.navigation.Param;
import org.fxapps.kieserverclient.service.KieServerClientService;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.api.model.instance.NodeInstance;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class NodeInstancesController implements Initializable {
	
	@Inject
	Navigation navigation;
	
	@Inject
	KieServerClientService service;
	
	@FXML
	TableView<NodeInstance> tblNodes;
	
	@FXML
	TableColumn<NodeInstance, String> clId;
	
	@FXML
	TableColumn<NodeInstance, String> clName;
	
	@FXML
	TableColumn<NodeInstance, String> clNodeId;
	
	
	@FXML
	TableColumn<NodeInstance, String> clType;
	
	@FXML
	TableColumn<NodeInstance, String> clConnection;
	
	@FXML
	TableColumn<NodeInstance, Date> clStartDate;
	
	@FXML
	TableColumn<NodeInstance, String> clCompleted;
	
	@FXML
	Label lblTitle;

	private ProcessInstance pi;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeVariables();
		configureTableColumns();
		loadData();
	}

	private void initializeVariables() {
		pi = (ProcessInstance) navigation.data().get(Param.PROCESS_INSTANCE);
		lblTitle.setText("Node instances for process instance " + pi.getId());
	}

	private void configureTableColumns() {
		clName.setCellValueFactory(new PropertyValueFactory<>("name"));
		clId.setCellValueFactory(new PropertyValueFactory<>("id"));
		clNodeId.setCellValueFactory(new PropertyValueFactory<>("nodeId"));
		clType.setCellValueFactory(new PropertyValueFactory<>("nodeType"));
		clConnection.setCellValueFactory(new PropertyValueFactory<>("connection"));
		clStartDate.setCellValueFactory(new PropertyValueFactory<>("date"));
		clCompleted.setCellValueFactory(new PropertyValueFactory<>("completed"));
	}

	private void loadData() {
		List<NodeInstance> nodeInstances = service.findNodeInstances(pi.getId(), 20);
		tblNodes.getItems().clear();
		tblNodes.getItems().addAll(nodeInstances);
	}
	
}