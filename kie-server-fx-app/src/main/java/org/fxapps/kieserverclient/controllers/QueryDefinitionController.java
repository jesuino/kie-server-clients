package org.fxapps.kieserverclient.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.fxapps.kieserverclient.service.KieServerClientService;
import org.kie.server.api.model.definition.QueryDefinition;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class QueryDefinitionController implements Initializable {

	@Inject
	KieServerClientService service;
	
	@FXML
	TableView<QueryDefinition> tblQueries;
	@FXML
	TableColumn<QueryDefinition, String> clName;
	@FXML
	TableColumn<QueryDefinition, String> clExpression;
	@FXML
	TableColumn<QueryDefinition, String> clSource;
	@FXML
	TableColumn<QueryDefinition, String> clTarget;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		configureTableColumns();
		loadData();
	}
	

	private void configureTableColumns() {
		clName.setCellValueFactory(new PropertyValueFactory<>("name"));
		clExpression.setCellValueFactory(new PropertyValueFactory<>("expression"));
		clSource.setCellValueFactory(new PropertyValueFactory<>("source"));
		clTarget.setCellValueFactory(new PropertyValueFactory<>("target"));
	}

	private void loadData() {
		tblQueries.getItems().clear();
		tblQueries.getItems().addAll(service.queries(20));
	}

}
