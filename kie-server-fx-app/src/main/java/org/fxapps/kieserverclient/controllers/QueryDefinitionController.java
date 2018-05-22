package org.fxapps.kieserverclient.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.fxapps.kieserverclient.navigation.Navigation;
import org.fxapps.kieserverclient.navigation.Screen;
import org.fxapps.kieserverclient.service.KieServerClientService;
import org.fxapps.kieserverclient.utils.AppUtils;
import org.kie.server.api.model.definition.QueryDefinition;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class QueryDefinitionController implements Initializable {

	@Inject
	Navigation navigation;
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

	@FXML
	Button btnUnregister;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		configureTableColumns();
		configureBindings();
		loadData();
	}

	private void configureBindings() {
		AppUtils.disableIfNotSelected(tblQueries.getSelectionModel(), btnUnregister);
	}

	private void configureTableColumns() {
		clName.setCellValueFactory(new PropertyValueFactory<>("name"));
		clExpression.setCellValueFactory(new PropertyValueFactory<>("expression"));
		clSource.setCellValueFactory(new PropertyValueFactory<>("source"));
		clTarget.setCellValueFactory(new PropertyValueFactory<>("target"));
	}

	private void loadData() {
		tblQueries.getItems().clear();
		tblQueries.getItems().addAll(service.queries(100));
	}

	public void removeQuery() {
		QueryDefinition query = tblQueries.getSelectionModel().getSelectedItem();
		boolean remove = AppUtils.askIfOk("Would you like to unregister the query " + query.getName() + "?");
		if (remove) {
			AppUtils.doBlockingAsyncWork(() -> {
				service.unregisterQuery(query.getName());
				loadData();
				return null;
			}, r -> {
				AppUtils.showSuccessDialog("Query Definition removed with sucess!");
			}, AppUtils::showExceptionDialog);
		}
	}

	public void register() {
		navigation.goTo(Screen.NEW_QUERY);
	}

}
