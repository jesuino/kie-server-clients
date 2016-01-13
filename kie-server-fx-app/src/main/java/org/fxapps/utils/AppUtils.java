package org.fxapps.utils;

import javafx.scene.control.Alert;

public class AppUtils {

	private AppUtils() {
	}

	public static void showExceptionDialog(String title, Exception e) {
		Alert dialog = new Alert(Alert.AlertType.ERROR);
		dialog.setTitle(title);

		if (e.getCause() != null) {
			dialog.setHeaderText(e.getMessage());
			dialog.setContentText(e.getCause().getMessage());
		} else {
			dialog.setHeaderText(null);
			dialog.setContentText(e.getMessage());
		}
		dialog.showAndWait();
	}

	public static void showSuccessDialog(String content) {
		Alert dialog = new Alert(Alert.AlertType.INFORMATION);
		dialog.setTitle("Success!");
		dialog.setHeaderText(null);
		dialog.setContentText(content);
		dialog.showAndWait();

	}

	public static void showErrorDialog(String content) {
		Alert dialog = new Alert(Alert.AlertType.ERROR);
		dialog.setTitle("Error...");
		dialog.setHeaderText(null);
		dialog.setContentText(content);
		dialog.showAndWait();

	}

}
