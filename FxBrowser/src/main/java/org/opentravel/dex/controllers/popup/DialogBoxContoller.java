/**
 * 
 */
package org.opentravel.dex.controllers.popup;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller for dialog box pop-up menu. LAYOUT_FILE = "/DialogBox.fxml"
 * <p>
 * Note: must be in same directory as primary controller or it will not get injected with FXML objects.
 * <p>
 * This MUST be constructed by passing an FXMLLoader instance which needs access to default constructor.
 * 
 * @author dmh
 *
 */
public class DialogBoxContoller extends DexPopupControllerBase {
	private static Log log = LogFactory.getLog(DialogBoxContoller.class);

	public static final String LAYOUT_FILE = "/DialogBox.fxml";

	@FXML
	BorderPane dialogBox;
	@FXML
	TextArea dialogText;
	@FXML
	TextFlow dialogTitle;
	@FXML
	Button dialogButtonClose;
	@FXML
	Button dialogButtonOK;
	@FXML
	Label dialogTitleLabel;

	private static Stage dialogStage;

	/**
	 * Is run when the associated .fxml file is loaded.
	 */
	@Override
	@FXML
	public void initialize() {
		log.debug("Initialize injection point.");
	}

	@Override
	public void checkNodes() {
		if (dialogStage == null)
			throw new IllegalStateException("Missing stage.");
		if (dialogButtonClose == null)
			throw new IllegalStateException("Missing close.");
		// if (dialogButtonOK == null)
		// throw new IllegalStateException("Missing ok.");
		if (dialogTitle == null)
			throw new IllegalStateException("Missing title.");
		if (dialogText == null)
			throw new IllegalStateException("Missing dialog text.");
	}

	/**
	 * Initialize this controller using the passed FXML loader.
	 * <p>
	 * Note: This approach using a static stage and main controller hides the complexity from calling controller.
	 * Otherwise, this code must migrate into the calling controller.
	 * 
	 * @param loader
	 *            FXML loaded for DialogBox.fxml
	 * @param mainController
	 * @return dialog box controller or null
	 */
	public static DialogBoxContoller init() {
		FXMLLoader loader = new FXMLLoader(DialogBoxContoller.class.getResource(LAYOUT_FILE));
		DialogBoxContoller controller = null;
		try {
			// Load the fxml file initialize controller it declares.
			Pane pane = loader.load();
			// Create scene and stage
			dialogStage = new Stage();
			dialogStage.setScene(new Scene(pane));
			dialogStage.initModality(Modality.APPLICATION_MODAL);

			// get the controller from it.
			controller = loader.getController();
			if (!(controller instanceof DialogBoxContoller)) {
				throw new IllegalStateException("Error creating dialog box controller.");
			}
		} catch (IOException e1) {
			log.error("Error loading dialog box: " + e1.getLocalizedMessage());
			throw new IllegalStateException("Error creating dialog box controller.");
		}
		return controller;
	}

	@Override
	protected void setup(String message) {
		super.setStage(getTitle(), dialogStage);
	}

	/**
	 * Show the title and message in a pop-up dialog window.
	 * 
	 * @param title
	 * @param message
	 */
	public void show(String title, String message) {
		setTitle(title);
		show(message);
	}

	@Override
	public void show(String message) {
		super.show(message);

		dialogButtonClose.setOnAction(e -> close());

		// TODO - how to know if/when to show OK or not?
		if (dialogButtonOK != null)
			dialogButtonOK.setVisible(false);

		dialogTitle.getChildren().clear();
		dialogTitle.getChildren().add(new Text(title));

		dialogText.setText(message);
	}

	/**
	 * Add the message to the displayed text
	 * 
	 * @param message
	 */
	public void add(String message) {
		if (dialogText != null)
			dialogText.setText(message);
	}

	public void close() {
		clear();
		dialogStage.close();
	}

	@Override
	public void clear() {
		dialogTitle.getChildren().clear();
	}

}