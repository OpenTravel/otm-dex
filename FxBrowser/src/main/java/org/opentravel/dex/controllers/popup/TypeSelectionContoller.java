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
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller for type selection dialog pop-up menu.
 * 
 * @author dmh
 *
 */
public class TypeSelectionContoller extends DexPopupControllerBase {
	private static Log log = LogFactory.getLog(TypeSelectionContoller.class);

	public static final String LAYOUT_FILE = "/TypeSelectionDialog.fxml";

	private static String dialogTitle = "Type Selection Dialog";
	private static String helpText = "Select a type.";

	protected static Stage dialogStage;

	/**
	 * Initialize this controller using the passed FXML loader.
	 * <p>
	 * Note: This approach using a static stage and main controller hides the complexity from calling controller.
	 * Otherwise, this code must migrate into the calling controller.
	 * 
	 */
	public static TypeSelectionContoller init() {
		FXMLLoader loader = new FXMLLoader(TypeSelectionContoller.class.getResource(LAYOUT_FILE));
		TypeSelectionContoller controller = null;
		try {
			// Load the fxml file initialize controller it declares.
			Pane pane = loader.load();
			// Create scene and stage
			dialogStage = new Stage();
			dialogStage.setScene(new Scene(pane));
			dialogStage.initModality(Modality.APPLICATION_MODAL);

			// get the controller from loader.
			controller = loader.getController();
			if (!(controller instanceof TypeSelectionContoller))
				throw new IllegalStateException("Error creating type selection dialog controller.");
		} catch (IOException e1) {
			log.error("Error loading dialog box. " + e1.getLocalizedMessage() + "\n" + e1.getCause().toString());
		}
		return controller;
	}

	@FXML
	Button cancelButton;
	@FXML
	Button selectButton;

	@Override
	public void checkNodes() {
		if (dialogStage == null)
			throw new IllegalStateException("Missing stage.");
		if (cancelButton == null || selectButton == null)
			throw new IllegalStateException("Null FXML injected node.");
	}

	@Override
	public void doOK() {
		super.doOK();
		// Handle selection
	}

	@Override
	protected void setup(String message) {
		super.setStage(dialogTitle, dialogStage);

		cancelButton.setOnAction(e -> doCancel());
		selectButton.setOnAction(e -> doOK());
	}

}