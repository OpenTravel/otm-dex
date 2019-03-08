/**
 * 
 */
package org.opentravel.objecteditor;

import org.opentravel.common.ImageManager;
import org.opentravel.model.OtmModelManager;

import javafx.beans.property.ReadOnlyObjectProperty;

/**
 * Abstract interface for all Otm-DE FX view controllers.
 * 
 * @author dmh
 *
 */
public interface DexController {

	/**
	 * @return the model manager used by this controller
	 */
	ImageManager getImageManager();

	/**
	 * Remove all items from the controlled view.
	 */
	void clear();

	/**
	 * Get the observable property so that others can register a listener.
	 * <p>
	 * Example usage: nsTreeController.getSelectable().addListener((v, old, newValue) ->
	 * treeSelectionListener(newValue));
	 * 
	 * @return a property or NULL if no fxNodes are of interest outside of this controller
	 */
	ReadOnlyObjectProperty<?> getSelectable();

	/**
	 * @return
	 */
	OtmModelManager getModelManager();

	/**
	 * Put the status string into the status label.
	 * 
	 * @param string
	 */
	void postStatus(String string);

	/**
	 * Update the progress indicator displayed value.
	 * 
	 * @param percentDone
	 */
	void postProgress(double percentDone);

}
