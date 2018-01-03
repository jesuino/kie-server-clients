package org.fxapps.kieserverclient.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.fxapps.kieserverclient.navigation.Navigation;
import org.fxapps.kieserverclient.navigation.Param;
import org.fxapps.kieserverclient.navigation.Screen;
import org.fxapps.kieserverclient.service.KieServerClientService;
import org.fxapps.kieserverclient.utils.AppUtils;
import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.ServiceResponse;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;


/**
 * 
 * A first implementation of what could be the Command Builder Screen, more user
 * friendly and helpful
 * 
 * @author wsiqueir
 *
 */
public class ExecuteCommandsController implements Initializable {

	private static final String QUERY = "Query";
	// Caused by: org.codehaus.jackson.map.JsonMappingException: Could not
	// resolve type id 'DeleteObjectCommand' into a subtype of [simple type,
	// class org.drools.core.command.impl.GenericCommand<java.lang.Object>]
	// private static final String DELETE_OBJECT = "Delete Object";
	private static final String GET_GLOBAL = "Get Global";
	private static final String AGENDA_GROUP_SET_FOCUS = "Agenda Group Set Focus";
	private static final String FIRE_ALL_RULES = "Fire All Rules";
	private static final String INSERT_OBJECT = "Insert Object";
	// Caused by: org.codehaus.jackson.map.JsonMappingException: No serializer
	// found for class org.drools.core.command.runtime.rule.GetFactHandleCommand
	// private static final String GET_FACT_HANDLE = "Get Fact Handle";
	private static final String GET_OBJECTS = "Get Objects";
	private static final String SET_GLOBAL = "Set Global";
	
	@Inject
	Navigation navigation;
	
	@Inject
	KieServerClientService service;
	
	@FXML
	ComboBox<String> cmbCommands;

	@FXML
	Label lblContainerId;

	@FXML
	TextArea txtCommand;

	@FXML
	Button btnAdd;

	@FXML
	Button btnExecute;

	@FXML
	Button btnUp;

	@FXML
	Button btnDown;

	@FXML
	Button btnRemove;

	@FXML
	Button btnSave;

	@FXML
	Label lblListEmpty;

	@FXML
	ListView<Command<?>> lstCommands;

	private SimpleStringProperty previousTextProperty;

	Logger logger = Logger.getLogger(this.getClass().getName());

	KieContainerResource container;

	private KieCommands cmdFactory;

	private BatchExecutionCommandImpl batchCmd;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Map<Param, Object> data = navigation.data();
		container = (KieContainerResource) data.get(Param.CONTAINER);
		cmdFactory = KieServices.Factory.get().getCommands();
		batchCmd = (BatchExecutionCommandImpl) cmdFactory.newBatchExecution(new ArrayList<>());
		initializeInterface();
		doBindings();
		if (data.containsKey(Param.REQUEST)) {
			txtCommand.setText(data.get(Param.REQUEST).toString());
			validateAndSaveCommands();
		} else {
			updateText();
		}
		previousTextProperty.set(txtCommand.getText());
	}

	public void executeCommands() {
		if (validateCommandsString()) {
			Map<Param, Object> data = navigation.data();
			String id = container.getContainerId();
			ServiceResponse<ExecutionResults> resp = service.executeCommand(id, batchCmd);
			data.put(Param.REQUEST, txtCommand.getText());
			data.put(Param.RESPONSE, resp);
			navigation.goTo(Screen.EXECUTION_RESULTS);
		}
	}

	public void validateAndSaveCommands() {
		if (validateCommandsString()) {
			lstCommands.getItems().setAll(batchCmd.getCommands());
			previousTextProperty.set(txtCommand.getText());
		}
	}

	public void addCommand() {
		String cmdStr = cmbCommands.getSelectionModel().getSelectedItem();
		cmdFromString(cmdStr).ifPresent(lstCommands.getItems()::add);
		updateText();
	}

	public void removeCommand() {
		int i = lstCommands.getSelectionModel().getSelectedIndex();
		lstCommands.getItems().remove(i);
		updateText();
	}

	public void changedFormat() {
		updateText();
	}

	public void moveDown() {
		int currentPos = lstCommands.getSelectionModel().getSelectedIndex();
		if (currentPos != lstCommands.getItems().size() - 1) {
			swapItems(currentPos, currentPos + 1);
		}
	}

	public void moveUp() {
		int currentPos = lstCommands.getSelectionModel().getSelectedIndex();
		if (currentPos != 0) {
			swapItems(currentPos, currentPos - 1);
		}
	}

	private void updateText() {
		batchCmd.getCommands().clear();
		lstCommands.getItems().stream().map(c -> (GenericCommand<?>) c).forEach(batchCmd.getCommands()::add);
		txtCommand.setText(service.getMarshaller().marshall(batchCmd));
	}

	private Optional<Command<?>> cmdFromString(String selectedCommand) {
		Command<?> cmd;
		switch (selectedCommand) {
		case AGENDA_GROUP_SET_FOCUS:
			cmd = cmdFactory.newAgendaGroupSetFocus("group");
			break;
		case GET_OBJECTS:
			cmd = cmdFactory.newGetObjects();
			break;
		case GET_GLOBAL:
			cmd = cmdFactory.newGetGlobal("global ");
			break;
		case SET_GLOBAL:
			cmd = cmdFactory.newSetGlobal("global name", new Object());
			break;
		case INSERT_OBJECT:
			cmd = cmdFactory.newInsert("Testing");
			break;
		case FIRE_ALL_RULES:
			cmd = cmdFactory.newFireAllRules();
			break;
		case QUERY:
			cmd = cmdFactory.newQuery(null, null);
			break;
		default:
			logger.info("Command not recognized: " + selectedCommand);
			return Optional.empty();
		}
		return Optional.of(cmd);
	}

	private boolean validateCommandsString() {
		boolean validated = false;
		try {
			batchCmd = service.getMarshaller().unmarshall(txtCommand.getText(), BatchExecutionCommandImpl.class);
			validated = true;
			txtCommand.setStyle("-fx-text-fill: blue;");
		} catch (Exception e) {
			txtCommand.setStyle("-fx-text-fill: red;");
			AppUtils.showExceptionDialog("Error when parsing commands", e);
			e.printStackTrace();
		}
		return validated;
	}

	private void initializeInterface() {
		lblContainerId.setText("Send commands to container " + container.getContainerId());
		previousTextProperty = new SimpleStringProperty();
		lstCommands.setCellFactory(f -> {
			return new ListCell<Command<?>>() {
				public void updateItem(Command<?> cmd, boolean empty) {
					super.updateItem(cmd, empty);
					if (empty) {
						setText("");
					} else {
						setText(cmd.getClass().getSimpleName());
					}
				}
			};
		});
		// Adding only a few commands by now - TODO: improve this and add all
		// commands
		cmbCommands.getItems().setAll(QUERY, GET_GLOBAL, AGENDA_GROUP_SET_FOCUS, FIRE_ALL_RULES, INSERT_OBJECT,
				GET_OBJECTS, SET_GLOBAL);
	}

	private void doBindings() {
		BooleanBinding comboSelected = cmbCommands.getSelectionModel().selectedItemProperty().isNull();
		BooleanBinding listSelected = lstCommands.getSelectionModel().selectedItemProperty().isNull();
		btnAdd.disableProperty().bind(comboSelected);
		btnRemove.disableProperty().bind(listSelected);
		btnDown.disableProperty().bind(listSelected);
		btnUp.disableProperty().bind(listSelected);
		BooleanBinding saved = previousTextProperty.isEqualTo(txtCommand.textProperty());
		btnSave.disableProperty().bind(saved);
		btnExecute.setDisable(true);
		lstCommands.getItems().addListener(new InvalidationListener() {
			public void invalidated(Observable arg0) {
					boolean empty = lstCommands.getItems().isEmpty();
					lblListEmpty.setVisible(empty);
					btnExecute.setDisable(empty);
			}
		});
	}

	private void swapItems(int currentPos, int newPos) {
		Command<?> current = lstCommands.getItems().get(currentPos);
		Command<?> future = lstCommands.getItems().get(newPos);
		lstCommands.getItems().set(currentPos, future);
		lstCommands.getItems().set(newPos, current);
		lstCommands.getSelectionModel().select(newPos);
		updateText();
	}

}
