package org.fxapps.kieserverclient.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.fxapps.kieserverclient.navigation.Navigation;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.util.Pair;

public class TemplateController implements Initializable {

	@Inject
	Navigation navigation;

	private Dialog<Pair<String, String>> loggingDialog;
	private TextArea loggingContent;

	@FXML
	Button btnLogs;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		buildLoggingDialog();
		createLogger();
	}
	
	public void doLogout() {
		navigation.logout();
	}

	public void goBack() {
		navigation.goBack();
	}

	public void goHome() {
		navigation.goHome();
	}

	public void showLogs() {
		loggingDialog.showAndWait();
	}

	public void receiveLogging(ILoggingEvent loggingEvent) {
		System.out.println("Receiving event");
		if (loggingDialog.isShowing()) {
			Platform.runLater(() -> {
				loggingContent.setText(loggingEvent.getFormattedMessage());
			});
		}
	}

	private void buildLoggingDialog() {
		loggingDialog = new Dialog<>();
		loggingDialog.initModality(Modality.NONE);
		loggingDialog.initStyle(StageStyle.UTILITY);
		loggingDialog.setTitle("Logging");
		loggingContent = new TextArea();
		loggingContent.setEditable(false);
		loggingDialog.getDialogPane().setContent(loggingContent);
		loggingDialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
		loggingContent.setPrefWidth(600);
		loggingContent.setPrefHeight(400);
		loggingDialog.setOnHiding(h -> loggingContent.setText(""));
		btnLogs.disableProperty().bind(loggingDialog.showingProperty());
	}

	private void createLogger() {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		PatternLayoutEncoder ple = new PatternLayoutEncoder();
		ple.setPattern("%date %msg%n");
		ple.setContext(lc);
		ple.start();
		AppenderBase<ILoggingEvent> customAppender = new AppenderBase<ILoggingEvent>() {

			@Override
			protected void append(ILoggingEvent eventObject) {
				if (loggingDialog.isShowing()) {
					Platform.runLater(() -> {
						loggingContent.appendText("\n");
						loggingContent.appendText(eventObject.getFormattedMessage());
						loggingContent.setScrollTop(Double.MAX_VALUE);
						loggingContent.setScrollLeft(Double.MAX_VALUE);
					});
				}
			}
		};
		customAppender.setContext(lc);
		customAppender.start();
		Logger logger = (Logger) LoggerFactory.getLogger("org.kie.server.client.impl");
		logger.addAppender(customAppender);
		logger.setLevel(Level.TRACE);
		logger.setAdditive(true);
	}
}
