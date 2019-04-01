/**
 * 
 */
package org.opentravel.dex.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.dex.repository.NamespacesDAO;
import org.opentravel.model.OtmModelManager;
import org.opentravel.objecteditor.DexController;
import org.opentravel.objecteditor.DexIncludedController;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;

/**
 * Manage the menu bar.
 * 
 * @author dmh
 *
 */
public class MenuBarWithProjectController implements DexIncludedController<String> {
	private static Log log = LogFactory.getLog(MenuBarWithProjectController.class);

	// FXML inject
	@FXML
	private ComboBox<String> projectCombo;
	@FXML
	private Label projectLabel;

	@FXML
	public void appExit(ActionEvent e) {
		log.debug("exit");
		// TODO
		// primaryStage.close();
	}

	@FXML
	public void doClose(ActionEvent e) {
		log.debug("Close menu item selected.");
	}

	@FXML
	public void fileOpen(Event e) {
		log.debug("File Open selected.");
	}

	/** *********************************************************** **/
	/**
	 * Show or hide the project combo box and its label.
	 * 
	 * @param value
	 *            true to show, false to hide
	 */
	public void showProjectCombo(boolean value) {
		projectCombo.setVisible(value);
		projectLabel.setVisible(value);
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

	@Override
	public ReadOnlyObjectProperty<TreeItem<NamespacesDAO>> getSelectable() {
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
	}

}
