/**
 * 
 */
package org.opentravel.dex.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.controllers.DexMainController;
import org.opentravel.dex.controllers.DexTabController;

import javafx.fxml.FXML;

/**
 * Manage the repository tab.
 * 
 * @author dmh
 *
 */
public class RepositoryTabController implements DexTabController {
	private static Log log = LogFactory.getLog(RepositoryTabController.class);

	/**
	 * FXML Java FX Nodes this controller is dependent upon
	 */
	@FXML
	private RepositoryNamespacesTreeController repositoryNamespacesTreeController;
	@FXML
	private NamespaceLibrariesTreeTableController namespaceLibrariesTreeTableController;
	@FXML
	private RepositoryItemCommitHistoriesController repositoryItemCommitHistoriesController;
	@FXML
	private RepositorySelectionController repositorySelectionController;

	// protected RepositoryController repoController;
	// private RepositoryAvailabilityChecker availabilityChecker;

	public RepositoryTabController() {
		log.debug("Repository Tab Controller constructed.");
	}

	@Override
	@FXML
	public void initialize() {
		// no-op
	}

	/**
	 * @param primaryStage
	 */
	@Override
	public void configure(DexMainController parent) {
		// super.setParent(parent);

		// repositorySearchController.setParent(this);
		// repositorySearchController.setStage();
		// repositorySearchController.setRepository(null);

		// Set up the repository selection
		parent.addIncludedController(repositorySelectionController);

		// Set up repository namespaces tree
		parent.addIncludedController(repositoryNamespacesTreeController);

		// Set up the libraries in a namespace table
		parent.addIncludedController(namespaceLibrariesTreeTableController);

		// No set up needed, but add to list
		parent.addIncludedController(repositoryItemCommitHistoriesController);

		log.debug("Repository Tab Stage set.");
	}
}
