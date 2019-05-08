/**
 * 
 */
package org.opentravel.dex.controllers.member.usage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.controllers.DexMainController;
import org.opentravel.dex.controllers.DexTabController;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

/**
 * Manage the properties tab.
 * 
 * @author dmh
 *
 */
public class WhereUsedTabController implements DexTabController {
	private static Log log = LogFactory.getLog(WhereUsedTabController.class);

	/**
	 * FXML Java FX Nodes this controller is dependent upon
	 */
	@FXML
	private WhereUsedTableController whereUsedTableController;
	@FXML
	private Tab whereUsedTab;
	@FXML
	private VBox whereUsedTabVbox;

	public WhereUsedTabController() {
		log.debug("Where Used Controller constructed.");
	}

	@FXML
	@Override
	public void initialize() {
		// no-op
	}

	@Override
	public void configure(DexMainController parent) {
		parent.addIncludedController(whereUsedTableController);
		// parent.addIncludedController(memberDetailsController);
	}

}
