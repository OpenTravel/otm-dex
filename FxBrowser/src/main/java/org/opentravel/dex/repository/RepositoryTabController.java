/**
 * 
 */
package org.opentravel.dex.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.controllers.DexMainController;
import org.opentravel.dex.controllers.DexMainControllerBase;
import org.opentravel.schemacompiler.repository.RepositoryException;

import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;

/**
 * Manage the repository tab.
 * 
 * @author dmh
 *
 */
public class RepositoryTabController extends DexMainControllerBase {
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

	/**
	 * @param primaryStage
	 */
	@Override
	public void setStage(Stage primaryStage, DexMainController parent) {
		super.setStage(primaryStage, parent);

		// repositorySearchController.setParent(this);
		// repositorySearchController.setStage();
		// repositorySearchController.setRepository(null);

		// Set up the repository selection
		addIncludedController(repositorySelectionController);
		repositorySelectionController.getSelectable().addListener((v, old, newV) -> repositorySelectionChanged(newV));

		// Set up repository namespaces tree
		addIncludedController(repositoryNamespacesTreeController);
		repositoryNamespacesTreeController.getSelectable()
				.addListener((v, old, newValue) -> namespaceSelectionListener(newValue));
		// repositoryNamespacesTreeController.setFilter(repositorySearchController);

		// Set up the libraries in a namespace table
		addIncludedController(namespaceLibrariesTreeTableController);
		namespaceLibrariesTreeTableController.getSelectable()
				.addListener((v, old, newValue) -> repoItemSelectionListener(newValue));

		// No set up needed, but add to list
		addIncludedController(repositoryItemCommitHistoriesController);

		log.debug("Repository Tab Stage set.");
	}

	@Override
	public void checkNodes() {
		// Not needed - will be checked by addIncluded
		if (!(repositoryItemCommitHistoriesController instanceof RepositoryItemCommitHistoriesController))
			throw new IllegalStateException("Commit Histories controller not injected by FXML.");
		if (!(namespaceLibrariesTreeTableController instanceof NamespaceLibrariesTreeTableController))
			throw new IllegalStateException("Libraries tree table controller not injected by FXML.");
		if (!(repositoryNamespacesTreeController instanceof RepositoryNamespacesTreeController))
			throw new IllegalStateException("repository namespaces controller not injected by FXML.");
		if (!(repositorySelectionController instanceof RepositorySelectionController))
			throw new IllegalStateException("repository selection controller not injected by FXML.");

		log.debug("FXML Nodes checked OK.");
	}

	private void repoItemSelectionListener(TreeItem<RepoItemDAO> item) {
		if (item == null)
			return;
		log.debug("Repository Item selected: " + item.getValue());
		try {
			repositoryItemCommitHistoriesController.post(item.getValue());
		} catch (Exception e) {
			parentController.postError(e, "Error retrieving history.");
		}
	}

	private void namespaceSelectionListener(TreeItem<NamespacesDAO> item) {
		if (item == null)
			return;
		log.debug("Namespace  selected: " + item.getValue());
		try {
			repositoryItemCommitHistoriesController.clear();
			namespaceLibrariesTreeTableController.post(item.getValue());
		} catch (Exception e) {
			parentController.postError(e, "Error retrieving libraries.");
		}
	}

	/**
	 * Called when the user modifies the selection of the 'repositoryChoice' control.
	 * 
	 * @throws RepositoryException
	 */
	private void repositorySelectionChanged(String newValue) {
		log.debug("Repository selection changed: " + newValue);
		clear();
		try {
			repositoryNamespacesTreeController.post(repositorySelectionController.getSelectedRepository());
		} catch (Exception e) {
			log.warn("Error posting repository: " + e.getLocalizedMessage());
			postError(e, "Error displaying repository.");
		}
	}

}
