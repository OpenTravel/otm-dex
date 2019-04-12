/**
 * 
 */
package org.opentravel.dex.controllers;

import javafx.fxml.FXML;

/**
 * Interface for all Otm-DE FX tab controllers.
 * <p>
 * Tab controllers simply pass the included controllers to the parent.
 * 
 * @author dmh
 *
 */
public interface DexTabController {

	/**
	 * Used by FXML when controller is loaded.
	 */
	@FXML
	public void initialize();

	/**
	 * Add included controllers to parent.
	 */
	public void configure(DexMainController parent);

}
