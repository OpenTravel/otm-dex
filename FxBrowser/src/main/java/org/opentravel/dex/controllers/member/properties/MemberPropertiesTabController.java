/**
 * 
 */
package org.opentravel.dex.controllers.member.properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.controllers.DexMainController;
import org.opentravel.dex.controllers.DexTabController;

import javafx.fxml.FXML;

/**
 * Manage the properties tab.
 * 
 * @author dmh
 *
 */
public class MemberPropertiesTabController implements DexTabController {
	private static Log log = LogFactory.getLog(MemberPropertiesTabController.class);

	/**
	 * FXML Java FX Nodes this controller is dependent upon
	 */
	@FXML
	private MemberPropertiesTreeTableController memberPropertiesTreeTableController;

	public MemberPropertiesTabController() {
		log.debug("Repository Tab Controller constructed.");
	}

	@FXML
	@Override
	public void initialize() {
		// no-op
	}

	@Override
	public void configure(DexMainController parent) {
		parent.addIncludedController(memberPropertiesTreeTableController);
	}

}
