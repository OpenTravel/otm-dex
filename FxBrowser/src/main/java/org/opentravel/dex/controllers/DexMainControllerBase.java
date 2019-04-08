/**
 * 
 */
package org.opentravel.dex.controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.application.common.AbstractMainWindowController;
import org.opentravel.application.common.StatusType;
import org.opentravel.common.ImageManager;
import org.opentravel.dex.controllers.dialogbox.DialogBoxContoller;
import org.opentravel.model.OtmModelManager;
import org.opentravel.schemacompiler.repository.RepositoryManager;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * Abstract base controller for main controllers.
 * 
 * @author dmh
 *
 */
public abstract class DexMainControllerBase extends AbstractMainWindowController implements DexMainController {
	private static Log log = LogFactory.getLog(DexMainControllerBase.class);

	protected DexMainController parentController;
	protected ImageManager imageMgr;
	protected OtmModelManager modelMgr;
	protected Stage stage;
	private List<DexIncludedController<?>> includedControllers = new ArrayList<>();
	protected DexStatusController statusController;
	protected DialogBoxContoller dialogBoxController;

	public DexMainControllerBase() {
		log.debug("Constructing controller.");
	}

	@Override
	public void addIncludedController(DexIncludedController<?> controller) {
		if (controller == null)
			throw new IllegalStateException("Included controller is null.");
		checkNodes();

		includedControllers.add(controller);
		controller.configure(this);
	}

	@Override
	public void clear() {
		includedControllers.forEach(DexIncludedController::clear);
	}

	public DialogBoxContoller getDialogBoxController() {
		if (dialogBoxController == null)
			dialogBoxController = DialogBoxContoller.init();
		return dialogBoxController;
	}

	@Override
	public ImageManager getImageManager() {
		if (imageMgr != null)
			return imageMgr;
		return parentController != null ? parentController.getImageManager() : null;
	}

	@Override
	public OtmModelManager getModelManager() {
		if (modelMgr != null)
			return modelMgr;
		return parentController != null ? parentController.getModelManager() : null;
	}

	@Override
	public RepositoryManager getRepositoryManager() {
		return null;
	}

	@Override
	public ReadOnlyObjectProperty<?> getSelectable() {
		return null;
	}

	@Override
	public DexStatusController getStatusController() {
		if (statusController != null)
			return statusController;
		return parentController != null ? parentController.getStatusController() : null;
	}

	@FXML
	@Override
	public void initialize() {
		log.debug("Initializing controller: " + this.getClass().getSimpleName());
	}

	@Override
	public void postError(Exception e, String title) {
		log.debug(title + e.getLocalizedMessage());
		if (getDialogBoxController() != null) {
			if (e.getCause() != null) {
				log.debug(title + e.getCause().toString());
				getDialogBoxController().show(title,
						e.getLocalizedMessage() + " \n\n(" + e.getCause().toString() + ")");
			} else {
				getDialogBoxController().show(title, e.getLocalizedMessage());
			}
		}
	}

	@Override
	public void postProgress(double percentDone) {
		if (getStatusController() != null)
			getStatusController().postProgress(percentDone);
	}

	@Override
	public void postStatus(String string) {
		if (getStatusController() != null)
			getStatusController().postStatus(string);
	}

	@Override
	public void refresh() {
		includedControllers.forEach(DexIncludedController::refresh);
	}

	public void setStage(Stage primaryStage) {
		// These may be needed by sub-controllers
		this.stage = primaryStage;
		this.parentController = null;
		imageMgr = new ImageManager(primaryStage);
		checkNodes();
	}

	/**
	 * Create a main controller that has a main controller parent.
	 * 
	 * @param primaryStage
	 * @param parent
	 */
	public void setStage(Stage primaryStage, DexMainController parent) {
		this.stage = primaryStage;
		this.parentController = parent;
		if (parentController.getImageManager() == null)
			imageMgr = new ImageManager(primaryStage);
	}

	// Required by AbstractApp...
	@Override
	protected void setStatusMessage(String message, StatusType statusType, boolean disableControls) {
		if (getStatusController() != null)
			getStatusController().postStatus(message);
	}

	@Override
	protected void updateControlStates() {
		// Platform.runLater(() -> {
		// // boolean exDisplayDisabled = (originalDocument == null);
		// // boolean exControlsDisabled = (model == null) || (originalDocument == null);
		// //
		// // libraryText.setText( (modelFile == null) ? "" : modelFile.getName() );
		// // libraryTooltip.setText( (modelFile == null) ? "" : modelFile.getAbsolutePath() );
		// // exampleText.setText( (exampleFile == null) ? "" : exampleFile.getName() );
		// // exampleTooltip.setText( (exampleFile == null) ? "" : exampleFile.getAbsolutePath() );
	}

}
