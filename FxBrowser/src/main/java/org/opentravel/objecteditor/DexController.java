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
	 * Remove all items from the controlled view(s).
	 */
	public void clear();

	/**
	 * @return the image manager used by this controller
	 */
	public ImageManager getImageManager();

	/**
	 * @return the model manager used by this controller
	 */
	public OtmModelManager getModelManager();

	/**
	 * Get the observable property so that others can register a listener.
	 * <p>
	 * Example usage: nsTreeController.getSelectable().addListener((v, old, newValue) ->
	 * treeSelectionListener(newValue));
	 * 
	 * @return a property or NULL if no fxNodes are of interest outside of this controller
	 */
	public ReadOnlyObjectProperty<?> getSelectable();

	/**
	 * Update the progress indicator displayed value.
	 * 
	 * @param percentDone
	 */
	public void postProgress(double percentDone);

	/**
	 * Put the status string into the status label.
	 * 
	 * @param string
	 */
	public void postStatus(String string);

	// /**
	// * @return
	// */
	// public RepositoryManager getRepositoryManager();
	//
}
