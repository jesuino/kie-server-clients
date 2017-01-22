package org.fxapps.controllers;

import java.net.URL;
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

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;

public class ProcessTasksChartsController implements Initializable {
	
	// TODO: Add filters to the process definition
	// TODO: add parameters to filter the number of process etc
	
	@FXML
	BarChart<String, Long> chartPiByStatus;
	
	private KieServerClientService service;
	private String containerId;

	private List<ProcessInstance> allProcessInstances;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		service = KieServerClientManager.getInstance();
		loadData();
	}
	
	private void loadData() {
		containerId = ((KieContainerResource)Navigation.get().data().get(Param.CONTAINER)).getContainerId();
		
		// TODO: do async
		allProcessInstances = service.allProcessInstances(containerId, 100);
		List<Integer> states = allProcessInstances.stream().map(pi -> pi.getState()).collect(Collectors.toList());
		Series<String, Long>  piByStatusSeries = new Series<>();
		
		System.out.println(allProcessInstances.size());
		for (Integer state : states) {
			long total = allProcessInstances.stream().filter(pi -> pi.getState() == state).count();
			String stateStr = AppUtils.getStatusName(state);
			piByStatusSeries.getData().add(new Data<>(stateStr, total));
		}
		
		chartPiByStatus.getData().add(piByStatusSeries);
	}

	public void goBack() {
		Navigation.get().goToPreviousScreen();
	}


}
