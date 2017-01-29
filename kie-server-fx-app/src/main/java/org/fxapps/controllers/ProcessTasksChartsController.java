package org.fxapps.controllers;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
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

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;

public class ProcessTasksChartsController implements Initializable {
	
	// TODO: Add filters to the process definition
	// TODO: add parameters to filter the number of process etc
	
	@FXML
	BarChart<String, Integer> chartPiByStatus;
	@FXML
	BarChart<String, Integer> chartPiByDate;
	@FXML
	BarChart<String, Integer> chartTasksByStatus;
	
	private KieServerClientService service;
	private String containerId;

	private List<ProcessInstance> allProcessInstances;
	
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
		
		// TODO: do async
		allProcessInstances = service.allProcessInstances(containerId, 100);
		List<TaskSummary> allUserTasks = service.allUserTasks(100);
		
		Series<String, Integer>  piByStatusSeries = new Series<>();
		Series<String, Integer>  piByDateSeries = new Series<>();
		Series<String, Integer>  taskByStatusSeries = new Series<>();

		allProcessInstances.stream()
			.collect(Collectors.groupingBy(ProcessInstance::getState))
			.forEach((k, v) -> piByStatusSeries.getData().add(new Data<>(AppUtils.getStatusName(k), v.size()))
		);
		
		allProcessInstances.stream()
		 	.collect(Collectors.groupingBy(pi -> dateFormat.format(pi.getDate())))
		 	.forEach((k, v) -> piByDateSeries.getData().add(new Data<>(k, v.size()))
		 );
		
		allUserTasks.stream()
			.collect(Collectors.groupingBy(TaskSummary::getStatus))
			.forEach((k, v) -> taskByStatusSeries.getData().add(new Data<>(k, v.size()))
		 );
		
		chartPiByStatus.getData().add(piByStatusSeries);
		chartPiByDate.getData().add(piByDateSeries);
		chartTasksByStatus.getData().add(taskByStatusSeries);
		
	}

	public void goBack() {
		Navigation.get().goToPreviousScreen();
	}


}
