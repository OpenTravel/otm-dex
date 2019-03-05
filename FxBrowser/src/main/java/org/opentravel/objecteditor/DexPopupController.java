/**
 * 
 */
package org.opentravel.objecteditor;

import javafx.stage.Stage;

/**
 * Abstract interface for all Otm-DE FX view controllers.
 * 
 * @author dmh
 *
 */
public interface DexPopupController extends DexController {

	/**
	 * Set the main controller so this controller can get data and send events.
	 * 
	 * @param mainController
	 */
	public void injectMainController(DexController mainController);

	/**
	 * Set the stage for this pop-up window.
	 * 
	 * @param stage
	 */
	public void injectStage(Stage stage);
}
