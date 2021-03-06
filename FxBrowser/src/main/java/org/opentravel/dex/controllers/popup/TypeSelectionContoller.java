/**
 * 
 */
package org.opentravel.dex.controllers.popup;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.dex.controllers.member.MemberAndProvidersDAO;
import org.opentravel.dex.controllers.member.MemberFilterController;
import org.opentravel.dex.controllers.member.MemberTreeTableController;
import org.opentravel.model.OtmModelManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextFlow;
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
			throw new IllegalStateException(
					"Error loading dialog box. " + e1.getLocalizedMessage() + "\n" + e1.getCause().toString());
		}
		return controller;
	}

	@FXML
	private Button cancelButton;
	@FXML
	private Button selectButton;
	@FXML
	private TextFlow dialogHelp;
	@FXML
	private MemberTreeTableController memberTreeTableController;
	@FXML
	private MemberFilterController memberFilterController;

	private OtmModelManager modelManager;

	private ImageManager imageMgr;

	@Override
	public void checkNodes() {
		if (dialogStage == null)
			throw new IllegalStateException("Missing stage.");
		if (cancelButton == null || selectButton == null)
			throw new IllegalStateException("Null FXML injected node.");
	}

	// @Override
	// public void doOK() {
	// super.doOK();
	// // Handle selection
	// }

	public MemberAndProvidersDAO getSelected() {
		return memberTreeTableController.getSelected();
	}

	public void mouseClick(MouseEvent event) {
		// this fires after the member selection listener
		log.debug("Double click selection");
		if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
			doOK();
		}
	}

	@Override
	public void refresh() {
		memberTreeTableController.refresh();
	}

	public void setManagers(OtmModelManager model, ImageManager image) {
		this.modelManager = model;
		this.imageMgr = image;
	}

	@Override
	protected void setup(String message) {
		super.setStage(dialogTitle, dialogStage);
		postHelp(helpText, dialogHelp);
		cancelButton.setOnAction(e -> doCancel());
		selectButton.setOnAction(e -> doOK());

		memberTreeTableController.configure(modelManager, imageMgr, false);
		memberFilterController.configure(modelManager, this);
		memberTreeTableController.setFilter(memberFilterController);

		memberTreeTableController.post(modelManager);
		memberTreeTableController.setOnMouseClicked(this::mouseClick);

	}

}