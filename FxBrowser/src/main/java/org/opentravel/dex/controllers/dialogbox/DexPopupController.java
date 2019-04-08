/**
 * 
 */
package org.opentravel.dex.controllers.dialogbox;

import org.opentravel.dex.controllers.DexController;

/**
 * Abstract interface for all Otm-DE FX view controllers.
 * 
 * @author dmh
 *
 */
public interface DexPopupController extends DexController {

	// TODO - make init a shared implementation
	// public static DexPopupController init(FXMLLoader loader);

	public void show(String title, String message);

	// TODO - put Results in base class and return
	// public Results showAndWait(String title, String message);

	// /**
	// * Set the main controller so this controller can get data and send events.
	// *
	// * @param mainController
	// */
	// public void injectMainController(DexController mainController);

	// /**
	// * Set the stage for this pop-up window.
	// *
	// * @param stage
	// */
	// public void injectStage(Stage stage);
}
