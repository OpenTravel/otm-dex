/**
 * 
 */
package org.opentravel.dex.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.DialogBox;
import org.opentravel.common.ImageManager;
import org.opentravel.model.OtmModelManager;
import org.opentravel.objecteditor.DexController;
import org.opentravel.objecteditor.DexIncludedController;
import org.opentravel.objecteditor.dialogbox.DialogBoxContoller;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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
public class MenuBarWithProjectController implements DexIncludedController<String> {
	private static Log log = LogFactory.getLog(MenuBarWithProjectController.class);

	// FXML injected objects
	@FXML
	private ComboBox<String> projectCombo;
	@FXML
	private Label projectLabel;

	private DialogBoxContoller dialogBoxController = null;
	private Stage stage;

	@FXML
	public void appExit(ActionEvent e) {
		log.debug("exit");
		e.consume(); // take the event away from windows
		if (DialogBox.display("Exit", "Do you really want to exit?"))
			stage.close();
	}

	@FXML
	public MenuItem doCloseItem;

	public void setdoCloseHandler(EventHandler<ActionEvent> handler) {
		doCloseItem.setOnAction(handler);
	}

	@FXML
	public void doClose(ActionEvent e) {
		// This is only run if the handler is not set.
		log.debug("Close menu item selected.");
		if (dialogBoxController != null)
			dialogBoxController.show("Close", "Not Implemented");
	}

	@FXML
	public MenuItem fileOpenItem;

	public void setFileOpenHandler(EventHandler<ActionEvent> handler) {
		fileOpenItem.setOnAction(handler);
	}

	@FXML
	public void fileOpen(ActionEvent e) {
		// This is only run if the handler is not set.
		log.debug("File Open selected.");
		if (dialogBoxController != null)
			dialogBoxController.show("Open", "Not implemented");
	}

	@FXML
	public void aboutApplication(ActionEvent event) {
		AboutDialogController.createAboutDialog(stage).showAndWait();
	}

	/** *********************************************************** **/
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

	public void setComboLabel(String text) {
		projectLabel.setText(text);
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

	@Deprecated
	@Override
	public ReadOnlyObjectProperty<String> getSelectable() {
		return projectCombo.getSelectionModel().selectedItemProperty();
		// return null;
	}

	private void checkNodes() {
		log.debug("FXML Nodes checked OK.");
	}

	public MenuBarWithProjectController() {
		log.debug("Starting constructor.");
	}

	@Override
	@FXML
	public void initialize() {
		log.debug("Status Controller initialized.");
	}

	/**
	 * @param primaryStage
	 */
	public void setStage(Stage primaryStage) {
		checkNodes();
		log.debug("Stage set.");
		stage = primaryStage;
	}

	public void setDialogBox(DialogBoxContoller controller) {
		this.dialogBoxController = controller;
	}

	@Override
	public void postProgress(double percent) {
	}

	@Override
	public void postStatus(String status) {
	}

	// @Override
	public void refresh() {
	}

	@Override
	public ImageManager getImageManager() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return null
	 */
	@Override
	public OtmModelManager getModelManager() {
		return null;
	}

	@Override
	public void clear() {
	}

	@Override
	public void setParent(DexController parent) {
	}

	@Override
	public void post(String businessData) throws Exception {
		// No-op - no active fields to post into
	}

}
