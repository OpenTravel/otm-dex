/**
 * 
 */
package org.opentravel.dex.controllers;

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
