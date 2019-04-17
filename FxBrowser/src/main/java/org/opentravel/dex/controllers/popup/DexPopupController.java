/**
 * 
 */
package org.opentravel.dex.controllers.popup;

import org.opentravel.dex.controllers.DexController;
import org.opentravel.dex.controllers.popup.DexPopupControllerBase.Results;

/**
 * Abstract interface for all Otm-DE FX view controllers.
 * 
 * @author dmh
 *
 */
public interface DexPopupController extends DexController {

	/**
	 * Display this pop-up dialog to the user and post the message if supported. The GUI will not force wait for it to
	 * be closed.
	 * 
	 * @param message
	 */
	public void show(String message);

	/**
	 * Shows this pop-up dialog and waits for it to be hidden (closed) before returning to the caller.
	 * 
	 * @param message
	 */
	public Results showAndWait(String message);

	/**
	 * Set the title for the pop-up dialog. Set and override in sub-types to assure the same title is always used.
	 * 
	 * @param title
	 */
	void setTitle(String title);

}
