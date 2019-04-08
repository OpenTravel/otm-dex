/**
 * 
 */
package org.opentravel.objecteditor;

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
	 * Refresh the view(s) with current data.
	 * <p>
	 * Note: some controllers will do nothing on refresh.
	 */
	public void refresh();

	/**
	 * Get the observable property so that others can register a listener.
	 * <p>
	 * Example usage: nsTreeController.getSelectable().addListener((v, old, newValue) ->
	 * treeSelectionListener(newValue));
	 * 
	 * @return a property or NULL if no fxNodes are of interest outside of this controller
	 */
	@Deprecated
	public ReadOnlyObjectProperty<?> getSelectable();

}
