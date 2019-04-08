/**
 * 
 */
package org.opentravel.dex.controllers;

import javafx.beans.property.ReadOnlyObjectProperty;

/**
 * Abstract interface for all Otm-DE FX view controllers.
 * 
 * @author dmh
 *
 */
public interface DexController {

	/**
	 * Check all the injected FXML nodes and throw IllegalStateException if missing.
	 */
	void checkNodes();

	/**
	 * Remove all items from the controlled view(s).
	 */
	public void clear();

	/**
	 * Get the observable property so that others can register a listener.
	 * <p>
	 * Example usage: nsTreeController.getSelectable().addListener((v, old, newValue) ->
	 * treeSelectionListener(newValue));
	 * 
	 * @return a property or NULL if no fxNodes are of interest outside of this controller
	 */
	// TODO - move to DexIncludedController after controllers are refactored.
	@Deprecated
	public ReadOnlyObjectProperty<?> getSelectable();

	/**
	 * Used by FXML when controller is loaded.
	 */
	public void initialize();

	/**
	 * Refresh the view(s) with current data.
	 * <p>
	 * Note: some controllers will do nothing on refresh.
	 */
	public void refresh();

}
