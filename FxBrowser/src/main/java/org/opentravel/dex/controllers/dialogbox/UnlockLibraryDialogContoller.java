/**
 * 
 */
package org.opentravel.dex.controllers.dialogbox;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller for unlock library dialog box pop-up menu.
 * <p>
 * This MUST be constructed by passing an FXMLLoader instance which needs access to default constructor.
 * 
 * @author dmh
 *
 */
public class UnlockLibraryDialogContoller implements DexPopupController {
	private static Log log = LogFactory.getLog(UnlockLibraryDialogContoller.class);

	public static final String LAYOUT_FILE = "/UnlockLibraryDialog.fxml";

	public enum Results {
		OK, CANCEL;
	}

	private Results result = Results.OK;

	@FXML
	BorderPane dialogBox;
	@FXML
	Label dialogTitleLabel;
	@FXML
	TextFlow dialogHelp;
	@FXML
	TextArea dialogText;
	@FXML
	RadioButton ulCommitButton;
	@FXML
	Button dialogButtonCancel;
	@FXML
	Button dialogButtonOK;

	private static Stage popupStage;

	Parent root;
	Scene scene;

	private static String helpText = "Unlock in the repository using the current credentials. "
			+ "If the 'commit' is selected, the Work-In-Process will be "
			+ "committed with the remarks to the remote repository before the existing lock is released. "
			+ "If not selected, any changes in the library's Work-In-Progress will be discarded.";

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
	public static UnlockLibraryDialogContoller init(FXMLLoader loader) {
		UnlockLibraryDialogContoller controller = null;
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
			if (!(controller instanceof UnlockLibraryDialogContoller))
				log.error("Error creating dialog box controller.");
		} catch (IOException e1) {
			log.error("Error loading dialog box. " + e1.getLocalizedMessage());
		}
		return controller;
	}

	public void setup(String title, String message) {
		if (popupStage == null)
			throw new IllegalAccessError("Must set stage before use.");

		if (dialogButtonCancel == null || dialogHelp == null || dialogText == null)
			throw new IllegalStateException("Missing dialog FXML fields.");

		popupStage.setTitle(title);

		dialogButtonCancel.setOnAction(e -> doCancel());
		dialogButtonOK.setOnAction(e -> doOK());
		ulCommitButton.setOnAction(e -> doCommitButton());

		dialogHelp.getChildren().add(new Text(helpText));

		dialogText.setText(message);
		// dialogText.getChildren().add(new Text(message));

	}

	@Override
	public void show(String title, String message) {
		setup(title, message);
		popupStage.show();
	}

	public void showAndWait(String title, String message) {
		setup(title, message);
		popupStage.showAndWait();
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

	public void doOK() {
		clear();
		popupStage.close();
		result = Results.OK;
	}

	public void doCommitButton() {
		// Hide the text if not selected
		dialogText.setVisible(ulCommitButton.isSelected());
	}

	public boolean getCommitState() {
		return ulCommitButton.isSelected();
	}

	public String getCommitRemarks() {
		return dialogText.getText();
	}

	public Results getResult() {
		return result;
	}

	public void doCancel() {
		clear();
		popupStage.close();
		result = Results.CANCEL;
	}

	@Override
	public void clear() {
		// dialogText.getChildren().clear();
		dialogHelp.getChildren().clear();
	}

	@Override
	public ReadOnlyObjectProperty<?> getSelectable() {
		return null;
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
	}

}