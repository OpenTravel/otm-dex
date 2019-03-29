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
import org.opentravel.schemacompiler.repository.RemoteRepository;
import org.opentravel.schemacompiler.repository.RepositoryException;
import org.opentravel.schemacompiler.repository.RepositoryManager;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
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
public class RepositoryLoginDialogContoller implements DexPopupController {
	public enum Results {
		OK, CANCEL;
	}

	private static Log log = LogFactory.getLog(RepositoryLoginDialogContoller.class);

	public static String LAYOUT_FILE = "/RepositoryLoginDialog.fxml";

	private static Stage popupStage;

	private static DexController mainController;
	private static String helpText = "Login to repository using provided credentials.";

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
	public static RepositoryLoginDialogContoller init(FXMLLoader loader, DexController mainController) {
		RepositoryLoginDialogContoller controller = null;
		RepositoryLoginDialogContoller.mainController = mainController;

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
			if (!(controller instanceof RepositoryLoginDialogContoller))
				log.error("Error creating dialog box controller.");
		} catch (IOException e1) {
			log.error("Error loading dialog box. " + e1.getLocalizedMessage() + "\n" + e1.getCause().toString());
		}
		return controller;
	}

	private Results result = Results.OK;
	@FXML
	TextField loginURL;
	@FXML
	TextField loginUser;
	@FXML
	TextField loginPassword;

	@FXML
	Button dialogButtonCancel;
	@FXML
	Button dialogButtonOK;

	@FXML
	Button dialogButtonTest;
	@FXML
	TextArea testResults;
	@FXML
	ProgressIndicator dialogProgress;

	Parent root;

	Scene scene;

	@Override
	public void clear() {
		loginURL.setText("");
		loginUser.setText("");
		loginPassword.setText("");
		testResults.setText("");
	}

	public void doCancel() {
		clear();
		popupStage.close();
		result = Results.CANCEL;
		testResults.setText("");
	}

	public void doOK() {
		clear();
		popupStage.close();
		result = Results.OK;
	}

	public void doTest() {
		log.debug("Test: " + loginURL.getText() + " : " + loginUser.getText() + " : " + loginPassword.getText());

		String url = loginURL.getText();
		String user = loginUser.getText();
		String pwd = loginPassword.getText();

		dialogProgress.progressProperty().set(-1.0);
		RepositoryManager rMgr;
		try {
			rMgr = RepositoryManager.getDefault();
			// FIXME - if there is already an known repo with URL do not throw error
			RemoteRepository repo = rMgr.addRemoteRepository(url); // throws error if can not be added
			rMgr.setCredentials(repo, user, pwd);
		} catch (RepositoryException e) {
			StringBuilder errMsg = new StringBuilder("Error: ");
			errMsg.append(e.getLocalizedMessage());
			if (e.getCause() != null)
				errMsg.append("\n" + e.getCause().toString());

			log.error(errMsg.toString());
			testResults.setWrapText(true);
			testResults.setText(errMsg.toString());
		}
		dialogProgress.progressProperty().set(1.0);
	}

	@Override
	public ImageManager getImageManager() {
		return mainController.getImageManager();
	}

	@Override
	public OtmModelManager getModelManager() {
		return mainController.getModelManager();
	}

	public Results getResult() {
		return result;
	}

	@Override
	public ReadOnlyObjectProperty<?> getSelectable() {
		return null;
	}

	/**
	 * Is run when the associated .fxml file is loaded.
	 */
	@FXML
	public void initialize() {
		log.debug("Initialize injection point.");
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
	public void postProgress(double percentDone) {
		// parentController.postProgress(percentDone);
	}

	@Override
	public void postStatus(String string) {
		// parentController.postStatus(string);
	}

	public void setup(String title, String message) {
		if (popupStage == null)
			throw new IllegalAccessError("Must set stage before use.");
		if (mainController == null)
			throw new IllegalAccessError("Must set main controller before use.");

		if (dialogProgress == null || testResults == null || dialogButtonCancel == null || loginURL == null
				|| loginUser == null || loginPassword == null)
			throw new IllegalStateException("Missing dialog FXML fields.");

		popupStage.setTitle(title);

		dialogButtonCancel.setOnAction(e -> doCancel());
		dialogButtonOK.setOnAction(e -> doOK());
		dialogButtonTest.setOnAction(e -> doTest());

		// dialogHelp.getChildren().add(new Text(helpText));
	}

	public void show(String title, String message) {
		setup(title, message);
		popupStage.show();
	}

	public void showAndWait(String title, String message) {
		setup(title, message);
		popupStage.showAndWait();
	}

}