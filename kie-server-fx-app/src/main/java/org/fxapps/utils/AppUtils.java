package org.fxapps.utils;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.fxapps.navigation.Navigation;

import javafx.beans.binding.BooleanBinding;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Pair;

public class AppUtils {

	private AppUtils() {
	}

	@SuppressWarnings("rawtypes")
	public static void disableIfNotSelected(SelectionModel selectionModel,
			Node... nodes) {
		BooleanBinding selected = selectionModel.selectedItemProperty()
				.isNull();
		for (Node node : nodes) {
			node.disableProperty().bind(selected);
		}
	}

	public static void showExceptionDialog(String title, Throwable e) {
		Alert dialog = new Alert(Alert.AlertType.ERROR);
		dialog.setTitle(title);
		dialog.setResizable(true);
		if (e.getCause() != null) {
			dialog.setHeaderText(e.getMessage());
			dialog.setContentText(e.getCause().getMessage());
		} else {
			dialog.setHeaderText(null);
			dialog.setContentText(e.getMessage());
		}
		dialog.showAndWait();
	}

	public static void showExceptionDialog(Throwable e) {
		showExceptionDialog("Error when sending request", e);
	}

	public static void showSuccessDialog(String content) {
		Alert dialog = new Alert(Alert.AlertType.INFORMATION);
		dialog.setTitle("Success!");
		dialog.setResizable(true);
		dialog.setHeaderText(null);
		dialog.setContentText(content);
		dialog.showAndWait();
	}

	public static boolean askIfOk(String msg) {
		Alert dialog = new Alert(AlertType.CONFIRMATION);
		dialog.setTitle("Confirmation");
		dialog.setResizable(true);
		dialog.setContentText(msg);
		dialog.setHeaderText(null);
		dialog.showAndWait();
		return dialog.getResult() == ButtonType.OK;
	}

	public static void showErrorDialog(String content) {
		Alert dialog = new Alert(Alert.AlertType.ERROR);
		dialog.setTitle("Error...");
		dialog.setHeaderText(null);
		dialog.setContentText(content);
		dialog.showAndWait();
	}

	public static List<Pair<String, String>> convertMapToPair(
			Map<String, String> m) {
		return m.entrySet().stream()
				.map(e -> new Pair<String, String>(e.getKey(), e.getValue()))
				.collect(Collectors.toList());
	}

	/**
	 * 
	 * Perform an async call
	 * 
	 * @param action
	 * @param success
	 * @param error
	 */
	public static <T extends Object> void doAsyncWork(Supplier<T> action,
			Consumer<T> success, Consumer<Throwable> error) {
		Task<T> tarefaCargaPg = new Task<T>() {
			@Override
			protected T call() throws Exception {
				return action.get();
			}

			@Override
			protected void succeeded() {
				success.accept(getValue());
			}

			@Override
			protected void failed() {
				error.accept(getException());
			}
		};
		Thread t = new Thread(tarefaCargaPg);
		t.setDaemon(true);
		t.start();
	}
	
	/**
	 * 
	 * Perform an async call and show a progress indicator
	 * 
	 * @param action
	 * @param success
	 * @param error
	 */
	public static <T extends Object> void doBlockingAsyncWork(Supplier<T> action,
			Consumer<T> success, Consumer<Throwable> error) {
		Navigation n = Navigation.get();
		Task<T> tarefaCargaPg = new Task<T>() {
			@Override
			protected T call() throws Exception {
				return action.get();
			}

			@Override
			protected void succeeded() {
				n.showProgressIndicator();
				success.accept(getValue());
			}

			@Override
			protected void failed() {
				n.hideProgressIndicator();
				error.accept(getException());
			}
		};
		Thread t = new Thread(tarefaCargaPg);
		t.setDaemon(true);
		t.start();
	}

	public static void configureColumnsForPair(
			TableColumn<String, String> clKey,
			TableColumn<String, String> clValue) {
		clKey.setCellValueFactory(new PropertyValueFactory<>("key"));
		clValue.setCellValueFactory(new PropertyValueFactory<>("value"));
	}

}