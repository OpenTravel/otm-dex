/**
 * 
 */
package org.opentravel.dex.controllers.library;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.controllers.DexMainController;
import org.opentravel.dex.controllers.DexTabController;
import org.opentravel.model.OtmModelManager;

import javafx.fxml.FXML;

/**
 * Manage the Libraries tab.
 * 
 * @author dmh
 *
 */
public class LibrariesTabController implements DexTabController {
	private static Log log = LogFactory.getLog(LibrariesTabController.class);

	/**
	 * FXML Java FX Nodes this controller is dependent upon
	 */
	@FXML
	private LibrariesTreeTableController librariesTreeTableController;

	public LibrariesTabController() {
		log.debug("Library Tab Controller constructed.");
	}

	@Override
	@FXML
	public void initialize() {
		log.debug("Library Tab Controller constructed.");
		// do nothing
	}

	/**
	 */
	@Override
	public void configure(DexMainController parent) {
		// Add included controllers to parent.
		parent.addIncludedController(librariesTreeTableController);
		log.debug("Library Tab configured.");
	}

	@Deprecated
	public void post(OtmModelManager modelMgr) {
		librariesTreeTableController.post(modelMgr);
	}
}
