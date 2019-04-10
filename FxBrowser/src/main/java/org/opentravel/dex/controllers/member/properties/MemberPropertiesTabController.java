/**
 * 
 */
package org.opentravel.dex.controllers.member.properties;

import java.awt.IllegalComponentStateException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.controllers.DexIncludedController;
import org.opentravel.dex.controllers.DexMainController;
import org.opentravel.dex.controllers.DexMainControllerBase;

import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * Manage the properties tab.
 * 
 * @author dmh
 *
 */
public class MemberPropertiesTabController extends DexMainControllerBase {
	private static Log log = LogFactory.getLog(MemberPropertiesTabController.class);

	/**
	 * FXML Java FX Nodes this controller is dependent upon
	 */
	@FXML
	private MemberPropertiesTreeTableController memberPropertiesTreeTableController;

	private DexIncludedController<?> filter;

	public MemberPropertiesTabController() {
		log.debug("Repository Tab Controller constructed.");
	}

	/**
	 * @param primaryStage
	 */
	@Override
	public void setStage(Stage primaryStage, DexMainController parent) {
		super.setStage(primaryStage, parent);

		// Set up the repository selection
		addIncludedController(memberPropertiesTreeTableController);
		// repositorySelectionController.getSelectable().addListener((v, old, newV) ->
		// repositorySelectionChanged(newV));

		log.debug("Properties Tab Stage set.");
	}

	// TODO - only needed to set event handler...must be a better way!
	public MemberPropertiesTreeTableController getPropertiesTableController() {
		return memberPropertiesTreeTableController;
	}

	@Override
	public void checkNodes() {
		// Not needed - will be checked by addIncluded
		if (!(memberPropertiesTreeTableController instanceof MemberPropertiesTreeTableController))
			throw new IllegalComponentStateException("Member properties tree table Controller not injected by FXML.");

		log.debug("FXML Nodes checked OK.");
	}

}
