/**
 * 
 */
package org.opentravel.dex.controllers.dialogbox;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
public class DialogBoxContoller implements DexPopupController {
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

	private static Stage popupStage;

	Parent root;
	Scene scene;

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
		return init(loader);
	}

	// Use init() instead
	@Deprecated
	public static DialogBoxContoller init(FXMLLoader loader) {
		DialogBoxContoller controller = null;
		try {
			// Load the fxml file initialize controller it declares.
			Pane pane = loader.load();
			// Create scene and stage
			Stage dialogStage = new Stage();
			dialogStage.setScene(new Scene(pane));
			dialogStage.initModality(Modality.APPLICATION_MODAL);
			popupStage = dialogStage;

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

	/**
	 * Show the title and message in a pop-up dialog window.
	 * 
	 * @param title
	 * @param message
	 */
	@Override
	public void show(String title, String message) {
		if (popupStage == null)
			throw new IllegalAccessError("Must set stage before use.");

		if (dialogButtonClose != null)
			dialogButtonClose.setOnAction(e -> close());
		// TODO - how to know if/when to show OK or not?
		if (dialogButtonOK != null)
			// dialogButtonOK.setOnAction(e -> close());
			dialogButtonOK.setVisible(false);

		if (dialogTitle != null) {
			dialogTitle.getChildren().clear();
			dialogTitle.getChildren().add(new Text(title));
			popupStage.setTitle(dialogTitleLabel.getText());
		}
		if (dialogText != null)
			dialogText.setText(message);

		popupStage.show();
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
		popupStage.close();
	}

	@Override
	public void clear() {
		// dialogText.getChildren().clear();
		dialogTitle.getChildren().clear();
	}

	// @Override
	// public ReadOnlyObjectProperty<?> getSelectable() {
	// return null;
	// }

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
	}

}