/**
 * 
 */
package org.opentravel.objecteditor.dialogbox;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.model.OtmModelManager;
import org.opentravel.objecteditor.DexController;
import org.opentravel.objecteditor.DexPopupController;

import javafx.beans.property.ReadOnlyObjectProperty;
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
	private static DexController mainController;

	Parent root;
	Scene scene;

	/**
	 * Is run when the associated .fxml file is loaded.
	 */
	@FXML
	public void initialize() {
		log.debug("Initialize injection point.");
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
	public static DialogBoxContoller init(FXMLLoader loader, DexController mainController) {
		DialogBoxContoller.mainController = mainController;
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
			if (!(controller instanceof DialogBoxContoller))
				log.error("Error creating dialog box controller.");
			// else {
			// controller.injectMainController(this);
			//// controller.injectStage(dialogStage);
			// }
		} catch (IOException e1) {
			log.error("Error loading dialog box.");
		}
		return controller;
	}

	/**
	 * Show the title and message in a pop-up dialog window.
	 * 
	 * @param title
	 * @param message
	 */
	public void show(String title, String message) {
		if (popupStage == null)
			throw new IllegalAccessError("Must set stage before use.");
		if (mainController == null)
			throw new IllegalAccessError("Must set main controller before use.");

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
		// dialogText.getChildren().add(new Text(message));

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
		// dialogText.getChildren().add(new Text(message));
	}

	public void close() {
		clear();
		popupStage.close();
	}

	@Override
	public ImageManager getImageManager() {
		return mainController.getImageManager();
	}

	@Override
	public void clear() {
		// dialogText.getChildren().clear();
		dialogTitle.getChildren().clear();
	}

	@Override
	public ReadOnlyObjectProperty<?> getSelectable() {
		return null;
	}

	@Override
	public OtmModelManager getModelManager() {
		return mainController.getModelManager();
	}

	@Override
	public void injectMainController(DexController mainController) {
		this.mainController = mainController;
	}

	@Override
	public void injectStage(Stage stage) {
		this.popupStage = stage;
	}

	@Override
	public void postStatus(String string) {
		// parentController.postStatus(string);
	}

	@Override
	public void postProgress(double percentDone) {
		// parentController.postProgress(percentDone);
	}

}