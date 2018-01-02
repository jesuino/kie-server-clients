package org.fxapps.kieserverclient.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.inject.Inject;

import org.fxapps.kieserverclient.navigation.Navigation;
import org.fxapps.kieserverclient.navigation.Param;
import org.fxapps.kieserverclient.navigation.Screen;
import org.fxapps.kieserverclient.service.KieServerClientManager;
import org.fxapps.kieserverclient.service.KieServerClientService;
import org.fxapps.kieserverclient.utils.AppUtils;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.instance.TaskSummary;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class UserTasksListController implements Initializable {
	@Inject
	Navigation navigation;
	@FXML
	private TableView<TaskSummary> tblTaskSummary;
	@FXML
	private TableColumn<TaskSummary, String> clId;
	@FXML
	private TableColumn<TaskSummary, String> clName;
	@FXML
	private TableColumn<TaskSummary, String> clSubject;
	@FXML
	private TableColumn<TaskSummary, String> clStatus;
	@FXML
	private TableColumn<TaskSummary, String> clPriority;
	@FXML
	private TableColumn<TaskSummary, String> clCreatedBy;
	@FXML
	private TableColumn<TaskSummary, String> clProcessId;
	@FXML
	private TableColumn<TaskSummary, String> clCreatedOn;
	@FXML
	private ContextMenu mnTask;

	private Supplier<List<TaskSummary>> updateTasks;
	private KieServerClientService service;
	private String userId;
	private String containerId;
	private long taskId;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// I <3 Java 8
		updateTasks = (Supplier<List<TaskSummary>>) navigation.data().get(Param.UPDATE_USER_TASKS_ACTION);
		service = KieServerClientManager.getInstance();
		userId = (String) navigation.data().get(Param.USER);
		tblTaskSummary.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
			if (n != null) {
				taskId = n.getId();
			}
		});
		mnTask.getItems().forEach(m -> {
			m.disableProperty().bind(tblTaskSummary.getSelectionModel().selectedItemProperty().isNull());
		});
		KieContainerResource container = (KieContainerResource) navigation.data().get(Param.CONTAINER);
		containerId = container.getContainerId();
		updateData();
		configureColumns();
	}

	private void updateData() {
		AppUtils.doAsyncWork(updateTasks, tblTaskSummary.getItems()::setAll, AppUtils::showExceptionDialog);
	}

	private void configureColumns() {
		clId.setCellValueFactory(new PropertyValueFactory<>("id"));
		clName.setCellValueFactory(new PropertyValueFactory<>("name"));
		clSubject.setCellValueFactory(new PropertyValueFactory<>("subject"));
		clStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
		clPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
		clCreatedBy.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
		clProcessId.setCellValueFactory(new PropertyValueFactory<>("processInstanceId"));
		clCreatedOn.setCellValueFactory(new PropertyValueFactory<>("createdOn"));
	}

	public void details() {
		navigation.data().put(Param.DETAILS, tblTaskSummary.getItems());
		navigation.goTo(Screen.DETAILS);
	}

	public void activate() {
		doTaskOperation(n -> service.activateTask(containerId, taskId, userId));
	}

	public void claim() {
		doTaskOperation(n -> service.claimTask(containerId, taskId, userId));
	}

	public void complete() {
		doTaskOperation(n -> service.completeTask(containerId, taskId, userId));
	}

	public void delegate() {
		AppUtils.askInputFromUser("Delegate Task", "Enter the user to delegate the task: ")
				.filter(usr -> !usr.isEmpty()).ifPresent(usr -> {
					doTaskOperation(n -> service.delegateTask(containerId, taskId, userId, usr));
				});
	}

	public void exit() {
		doTaskOperation(n -> service.exitTask(containerId, taskId, userId));
	}

	public void fail() {
		doTaskOperation(n -> service.failTask(containerId, taskId, userId));
	}

	public void forward() {
		AppUtils.askInputFromUser("Forward Task", "Enter the entity to forward the task: ")
				.filter(usr -> !usr.isEmpty()).ifPresent(usr -> {
					doTaskOperation(n -> service.forwardTask(containerId, taskId, userId, usr));
				});
	}

	public void release() {
		doTaskOperation(n -> service.releaseTask(containerId, taskId, userId));
	}

	public void resume() {
		doTaskOperation(n -> service.resumeTask(containerId, taskId, userId));
	}

	public void skip() {
		doTaskOperation(n -> service.skipTask(containerId, taskId, userId));
	}

	public void start() {
		doTaskOperation(n -> service.startTask(containerId, taskId, userId));
	}

	public void stop() {
		doTaskOperation(n -> service.stopTask(containerId, taskId, userId));
	}

	public void suspend() {
		doTaskOperation(n -> service.suspendTask(containerId, taskId, userId));
	}

	public void nominate() {
		AppUtils.showErrorDialog("Not supported at the moment...");
	}

	private void doTaskOperation(Consumer<Void> function) {
		AppUtils.doBlockingAsyncWork(() -> {
			function.accept(null);
			return null;
		}, r -> {
			AppUtils.showSuccessDialog("Task Operation finished with success");
			updateData();
		}, AppUtils::showExceptionDialog);
	}

}