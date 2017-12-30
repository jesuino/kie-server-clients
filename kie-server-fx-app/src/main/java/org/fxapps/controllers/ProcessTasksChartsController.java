package org.fxapps.controllers;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.fxapps.navigation.Navigation;
import org.fxapps.navigation.Param;
import org.fxapps.service.KieServerClientManager;
import org.fxapps.service.KieServerClientService;
import org.fxapps.utils.AppUtils;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.api.model.instance.TaskSummary;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class ProcessTasksChartsController implements Initializable {
	
	// TODO: Add filters to the process definition
	// TODO: add parameters to filter the number of process etc
	
	@FXML
	BarChart<String, Integer> chartPiByStatus;
	@FXML
	BarChart<String, Integer> chartPiByDate;	
	@FXML
	BarChart<String, Integer> chartPiByDefinition;
	@FXML
	BarChart<String, Integer> chartTasksByStatus;	
	@FXML
	BarChart<String, Integer> chartTasksByDate;
	@FXML
	BarChart<String, Integer> chartTasksByProcessDef;
	
	@FXML
	StackPane spCharts;
	
	private KieServerClientService service;
	private String containerId;

	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy\nhh:mm a");
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		service = KieServerClientManager.getInstance();
		configure();
		loadData();
	}
	
	private void configure() {
	}

	private void loadData() {
		containerId = ((KieContainerResource)Navigation.get().data().get(Param.CONTAINER)).getContainerId();
		AppUtils.doAsyncWork(() -> service.allProcessInstances(containerId, 1000), this::fillProcessInstanceCharts, AppUtils::showExceptionDialog);
		AppUtils.doAsyncWork(() ->  service.allUserTasks(1000), this::fillUserTasks, AppUtils::showExceptionDialog);
	}

	private void fillUserTasks(List<TaskSummary> allUserTasks) {
		Map<String, List<TaskSummary>> tasksGroupedByDef = allUserTasks.stream()
				.collect(Collectors.groupingBy(TaskSummary::getProcessId));
		tasksGroupedByDef.forEach((def, list) -> {
			Series<String, Integer>  taskByStatusSeries = new Series<>();
			Series<String, Integer>  taskByDateSeries = new Series<>();
			Series<String, Integer> taskByProcessDefSeries = new Series<>();
			taskByStatusSeries.setName(def);
			taskByDateSeries.setName(def);
			taskByProcessDefSeries.setName(def);
			list.stream()
				.collect(Collectors.groupingBy(TaskSummary::getStatus))
				.forEach((k, v) -> taskByStatusSeries.getData().add(new Data<>(k, v.size()))
			);
		
			list.stream()
				.collect(Collectors.groupingBy(t -> dateFormat.format(t.getCreatedOn())))
				.forEach((k, v) -> taskByDateSeries.getData().add(new Data<>(k, v.size()))
			);		
		
			list.stream()
				.collect(Collectors.groupingBy(TaskSummary::getProcessId))
				.forEach((k, v) -> taskByProcessDefSeries.getData().add(new Data<>(k, v.size()))
			);	
			
			chartTasksByStatus.getData().add(taskByStatusSeries);
			chartTasksByDate.getData().add(taskByDateSeries);
			chartTasksByProcessDef.getData().add(taskByProcessDefSeries);
		});
	}
	
	public void fillProcessInstanceCharts(List<ProcessInstance> allProcessInstances) {
		Map<String, List<ProcessInstance>> piGroupedByDef = allProcessInstances.stream()
				.collect(Collectors.groupingBy(ProcessInstance::getProcessId));
	
		piGroupedByDef.forEach((def, list) -> {
			Series<String, Integer>  piByStatusSeries = new Series<>();
			Series<String, Integer>  piByDateSeries = new Series<>();
			Series<String, Integer>  piByDefSeries = new Series<>();
			piByStatusSeries.setName(def);
			piByDateSeries.setName(def);
			piByDefSeries.setName(def);
			list.stream()
			 	.collect(Collectors.groupingBy(pi -> dateFormat.format(pi.getDate())))
			 	.forEach((k, v) -> piByDateSeries.getData().add(new Data<>(k, v.size()))
			 );
			list.stream()
				.collect(Collectors.groupingBy(ProcessInstance::getState))
				.forEach((k, v) -> piByStatusSeries.getData().add(new Data<>(AppUtils.getStatusName(k), v.size()))
			);
			list.stream()
				.collect(Collectors.groupingBy(ProcessInstance::getProcessId))
				.forEach((k, v) -> piByDefSeries.getData().add(new Data<>(k, v.size()))
			);
			
			chartPiByStatus.getData().add(piByStatusSeries);
			chartPiByDate.getData().add(piByDateSeries);
			chartPiByDefinition.getData().add(piByDefSeries);
		});
		boolean hasVisibleChart = spCharts.getChildren().stream().filter(Node::isVisible).findFirst().isPresent();
		if(!hasVisibleChart) {
			animateChart(chartPiByDate);
		}
	}

	public void goBack() {
		Navigation.get().goToPreviousScreen();
	}
	
	@FXML
	public void selectChart(ActionEvent e) {
		String id = ((RadioMenuItem)e.getSource()).getId();
		switch (id) {
		case "piByCreationDate":
			animateChart(chartPiByDate);
			break;
		case "piByStatus":
			animateChart(chartPiByStatus);
			break;
		case "piByDefinition":
			animateChart(chartPiByDefinition);
			break;
		case "tasksByCreationDate":
			animateChart(chartTasksByDate);
			break;
		case "tasksByStatus":
			animateChart(chartTasksByStatus);
			break;
		case "tasksByProcessDef":
			animateChart(chartTasksByProcessDef);
			break;
		default:
			break;
		}
	}
	
	private void animateChart(Chart chart) {
		Optional<Node> selectedChart = spCharts.getChildren().stream().filter(Node::isVisible).findFirst();
		SequentialTransition transitions = new SequentialTransition();
		FadeTransition show = new FadeTransition(Duration.millis(400), chart);
		chart.setVisible(true);
		chart.setOpacity(0);
		show.setFromValue(0);
		show.setToValue(1);
		show.setOnFinished(e -> spCharts.getChildren().stream().filter(c -> c != chart).forEach(c -> c.setVisible(false)));
		if(selectedChart.isPresent() && selectedChart.get() != chart) {
			FadeTransition hide = new FadeTransition(Duration.millis(400));
			hide.setNode(selectedChart.get());
			hide.setFromValue(1);
			hide.setToValue(0);
			transitions.getChildren().add(hide);
		}		
		transitions.getChildren().add(show);
		transitions.play();
	}
}