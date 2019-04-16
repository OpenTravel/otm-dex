/**
 * 
 */
package org.opentravel.dex.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.DialogBox;
import org.opentravel.dex.controllers.dialogbox.DialogBoxContoller;
import org.opentravel.dex.events.DexEventDispatcher;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

/**
 * Manage the menu bar.
 * 
 * @author dmh
 *
 */
public class MenuBarWithProjectController extends DexIncludedControllerBase<String> {
	private static Log log = LogFactory.getLog(MenuBarWithProjectController.class);

	// FXML injected objects
	@FXML
	private ComboBox<String> projectCombo;
	@FXML
	private Label projectLabel;

	private Stage stage;

	@FXML
	public MenuItem doCloseItem;

	@FXML
	public MenuItem fileOpenItem;
	@FXML
	private Label actionCount;
	@FXML
	private Button undoActionButton;

	public MenuBarWithProjectController() {
		log.debug("Starting constructor.");
	}

	@FXML
	public void aboutApplication(ActionEvent event) {
		AboutDialogController.createAboutDialog(stage).showAndWait();
	}

	@FXML
	public void appExit(Event e) {
		log.debug("exit");
		e.consume(); // take the event away from windows in case they answer no.
		if (DialogBox.display("Exit", "Do you really want to exit?"))
			stage.close();
	}

	@Override
	public void checkNodes() {
		if (projectCombo == null || projectLabel == null)
			throw new IllegalStateException("Menu bar is missing FXML injected fields.");
		log.debug("FXML Nodes checked OK.");
	}

	@Override
	public void configure(DexMainController mainController) {
		super.configure(mainController);

		stage = mainController.getStage();
		// handle window and other close request events
		stage.setOnCloseRequest(this::appExit);
		// For debugging, intercept and log DexEvents
		stage.setEventDispatcher(new DexEventDispatcher(stage.getEventDispatcher()));
	}

	/**
	 * Configure the combo box with a list and listener.
	 * <p>
	 * Usage: menuBarWithProjectController.configureProjectMenuButton(projectList, this::projectComboSelectionListener);
	 * 
	 * @param projectList
	 * @param listener
	 */
	public void configureComboBox(ObservableList<String> projectList, EventHandler<ActionEvent> listener) {
		// log.debug("Setting combo.");
		projectList.sort(null);
		projectCombo.setItems(projectList);
		projectCombo.setOnAction(listener);
	}

	/** *********************************************************** **/

	@FXML
	public void undoAction(ActionEvent e) {
		log.debug("Close menu item selected.");
		DialogBoxContoller.init().show("Undo", "Not Implemented");
	}

	public void setUndoAction(EventHandler<ActionEvent> handler) {
		undoActionButton.setOnAction(handler);
	}

	public void updateActionQueueSize(int size) {
		actionCount.setText(Integer.toString(size));
	}

	@FXML
	public void doClose(ActionEvent e) {
		// This is only run if the handler is not set.
		log.debug("Close menu item selected.");
		DialogBoxContoller.init().show("Close", "Not Implemented");
	}

	@FXML
	public void fileOpen(ActionEvent e) {
		// This is only run if the handler is not set.
		log.debug("File Open selected.");
		DialogBoxContoller.init().show("Open", "Not implemented");
	}

	public void setComboLabel(String text) {
		projectLabel.setText(text);
	}

	public void setdoCloseHandler(EventHandler<ActionEvent> handler) {
		doCloseItem.setOnAction(handler);
	}

	public void setFileOpenHandler(EventHandler<ActionEvent> handler) {
		fileOpenItem.setOnAction(handler);
	}

	/**
	 * Show or hide the combo box and its label.
	 * 
	 * @param value
	 *            true to show, false to hide
	 */
	public void showCombo(boolean value) {
		projectCombo.setVisible(value);
		projectLabel.setVisible(value);
	}
}
