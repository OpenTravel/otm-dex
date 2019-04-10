/**
 * 
 */
package org.opentravel.dex.controllers.library;

import java.awt.IllegalComponentStateException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.controllers.DexMainController;
import org.opentravel.dex.controllers.DexMainControllerBase;
import org.opentravel.dex.events.DexLibrarySelectionEvent;
import org.opentravel.model.OtmModelManager;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * Manage the Libraries tab.
 * 
 * @author dmh
 *
 */
public class LibrariesTabController extends DexMainControllerBase {
	private static Log log = LogFactory.getLog(LibrariesTabController.class);

	/**
	 * FXML Java FX Nodes this controller is dependent upon
	 */
	@FXML
	private LibrariesTreeTableController librariesTreeTableController;

	// private DexIncludedController<?> filter;

	public LibrariesTabController() {
		log.debug("Repository Tab Controller constructed.");
	}

	/**
	 * @param primaryStage
	 */
	@Override
	public void setStage(Stage primaryStage, DexMainController parent) {
		super.setStage(primaryStage, parent);

		// Set up the repository selection
		addIncludedController(librariesTreeTableController);
		// repositorySelectionController.getSelectable().addListener((v, old, newV) ->
		// repositorySelectionChanged(newV));

		log.debug("Properties Tab Stage set.");
	}

	public void setLibrarySelectionEventHandler(EventHandler<DexLibrarySelectionEvent> handler) {
		librariesTreeTableController.setLibrarySelectionEventHandler(handler);
	}

	// public MemberFilterController getMemberFilterController() {
	// return parentController.getMemberFilterController();
	// }

	@Override
	public void checkNodes() {
		// Not needed - will be checked by addIncluded
		if (!(librariesTreeTableController instanceof LibrariesTreeTableController))
			throw new IllegalComponentStateException("Library tree table Controller not injected by FXML.");

		log.debug("FXML Nodes checked OK.");
	}

	public void post(OtmModelManager modelMgr) {
		librariesTreeTableController.post(modelMgr);
	}
}
