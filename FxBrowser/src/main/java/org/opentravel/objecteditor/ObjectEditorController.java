/**
 * 
 */
package org.opentravel.objecteditor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.controllers.DexMainControllerBase;
import org.opentravel.dex.controllers.DexStatusController;
import org.opentravel.dex.controllers.MenuBarWithProjectController;
import org.opentravel.dex.controllers.library.LibrariesTabController;
import org.opentravel.dex.controllers.member.MemberFilterController;
import org.opentravel.dex.controllers.member.MemberTreeTableController;
import org.opentravel.dex.controllers.member.properties.MemberPropertiesTabController;
import org.opentravel.dex.repository.RepositoryTabController;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * Main controller for OtmObjecEditorLayout.fxml (1 FXML = 1Controller).
 * 
 * @author dmh
 *
 */
public class ObjectEditorController extends DexMainControllerBase {
	private static Log log = LogFactory.getLog(ObjectEditorController.class);

	@FXML
	private MenuBarWithProjectController menuBarWithProjectController;
	@FXML
	private DexStatusController dexStatusController;
	@FXML
	private MemberFilterController memberFilterController;
	@FXML
	private RepositoryTabController repositoryTabController;
	@FXML
	private MemberTreeTableController memberTreeTableController;
	@FXML
	private MemberPropertiesTabController memberPropertiesTabController;
	@FXML
	private LibrariesTabController librariesTabController;

	// TODO - preferences (improve as i use it)
	// Uses java beans to read/write to file
	// 1. Abstract User Settings class (application common)
	// 1a. Add fields, getters, setters for app specific preferences
	// 2. Add load to main controller initialize

	@Override
	public void checkNodes() {
		if (!(repositoryTabController instanceof RepositoryTabController))
			throw new IllegalStateException("Repository tab not injected by FXML.");
		if (!(memberPropertiesTabController instanceof MemberPropertiesTabController))
			throw new IllegalStateException("Member properties tab not injected by FXML.");
		if (!(librariesTabController instanceof LibrariesTabController))
			throw new IllegalStateException("Libraries tab not injected by FXML.");
	}

	/**
	 * Set up this FX controller
	 * 
	 * @param stage
	 */
	@Override
	public void setStage(Stage stage) {
		super.setStage(stage);
		log.debug("Controller - Initializing Object Editor Controller");

		// Set up menu bar and show the project combo
		addIncludedController(menuBarWithProjectController);
		menuBarWithProjectController.showCombo(true);
		menuBarController = menuBarWithProjectController; // Make available to base class

		// Setup status controller
		addIncludedController(dexStatusController);
		statusController = dexStatusController; // Make available to base class

		repositoryTabController.configure(this); // TODO - this is slow!
		librariesTabController.configure(this);

		// Include controllers that are not in tabs
		addIncludedController(memberFilterController);
		addIncludedController(memberTreeTableController);
		memberTreeTableController.setFilter(memberFilterController);

		memberPropertiesTabController.configure(this);

		// Now that all controller's event requirements are known
		configureEventHandlers();
	}

	@Override
	public void initialize() {
		log.debug("Object Editor Controller - Initialize w/params is now loading!");
		checkNodes();
	}

	// Fires whenever a tab is selected. Fires on closed tab and opened tab.
	@FXML
	public void whereUsedTabSelection(Event e) {
		log.debug("Where used tab selection event");
	}

	@FXML
	public void memberTabSelection(Event e) {
		log.debug("memberTab selection event");
	}
}
