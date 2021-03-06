/**
 * 
 */
package org.opentravel.repositoryViewer;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.dex.controllers.DexMainController;
import org.opentravel.dex.controllers.DexMainControllerBase;
import org.opentravel.dex.controllers.DexStatusController;
import org.opentravel.dex.controllers.MenuBarWithProjectController;
import org.opentravel.dex.repository.NamespaceLibrariesTreeTableController;
import org.opentravel.dex.repository.NamespacesDAO;
import org.opentravel.dex.repository.RepoItemDAO;
import org.opentravel.dex.repository.RepositoryItemCommitHistoriesController;
import org.opentravel.dex.repository.RepositoryNamespacesTreeController;
import org.opentravel.dex.repository.RepositorySearchController;
import org.opentravel.dex.repository.RepositorySelectionController;
import org.opentravel.schemacompiler.repository.RepositoryException;
import org.opentravel.schemacompiler.repository.RepositoryItem;
import org.opentravel.schemacompiler.repository.RepositoryManager;

import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;

/**
 * Manage the repository viewer. Includes controllers for the trees and tree-tables.
 * 
 * @author dmh
 *
 */
public class RepositoryViewerController extends DexMainControllerBase implements DexMainController {
	private static Log log = LogFactory.getLog(RepositoryViewerController.class);

	// Let FXML inject controllers
	// @FXML
	// private RepositorySearchController repositorySearchController;
	@FXML
	private DexStatusController dexStatusController;
	@FXML
	private RepositoryNamespacesTreeController repositoryNamespacesTreeController;
	@FXML
	private NamespaceLibrariesTreeTableController namespaceLibrariesTreeTableController;
	@FXML
	private RepositoryItemCommitHistoriesController repositoryItemCommitHistoriesController;
	@FXML
	private RepositorySelectionController repositorySelectionController;
	@FXML
	private MenuBarWithProjectController menuBarWithProjectController;

	@Override
	public void checkNodes() {
		if (!(repositoryItemCommitHistoriesController instanceof RepositoryItemCommitHistoriesController))
			throw new IllegalStateException("Commit Histories controller not injected by FXML.");
		if (!(namespaceLibrariesTreeTableController instanceof NamespaceLibrariesTreeTableController))
			throw new IllegalStateException("Libraries tree table controller not injected by FXML.");
		if (!(repositoryNamespacesTreeController instanceof RepositoryNamespacesTreeController))
			throw new IllegalStateException("repository namespaces controller not injected by FXML.");
		if (!(repositorySelectionController instanceof RepositorySelectionController))
			throw new IllegalStateException("repository selection controller not injected by FXML.");
		if (!(dexStatusController instanceof DexStatusController))
			throw new IllegalStateException("Status controller not injected by FXML.");
		// if (!(repositorySearchController instanceof RepositorySearchController))
		// throw new IllegalStateException("Search controller not injected by FXML.");
		if (!(menuBarWithProjectController instanceof MenuBarWithProjectController))
			throw new IllegalStateException("Menu bar not injected by FXML.");

		log.debug("FXML Nodes checked OK.");
	}

	public RepositoryViewerController() {
		log.debug("Starting constructor.");
	}

	@Override
	@FXML
	public void initialize() {
		log.debug("Repository Viewer Controller initialized.");

		// Get user settings / preferences
		UserSettings settings = UserSettings.load();
		// settings.getWindowSize();
	}

	/**
	 * @param primaryStage
	 */
	@Override
	public void setStage(Stage primaryStage) {
		// These may be needed by sub-controllers
		this.stage = primaryStage;
		imageMgr = new ImageManager(primaryStage);
		checkNodes();

		// Hide the project combo
		addIncludedController(menuBarWithProjectController);
		menuBarWithProjectController.showCombo(false);

		// repositorySearchController.setParent(this);
		// repositorySearchController.setStage();
		// repositorySearchController.setRepository(null);

		// Set up the repository selection
		addIncludedController(repositorySelectionController);
		// FIXME - use Events
		// repositorySelectionController.getSelectable().addListener((v, old, newValue) ->
		// repositorySelectionChanged());

		// Inject this controller into sub-controllers
		addIncludedController(repositoryNamespacesTreeController);
		// FIXME - use Events
		// repositoryNamespacesTreeController.getSelectable()
		// .addListener((v, old, newValue) -> namespaceSelectionListener(newValue));
		// repositoryNamespacesTreeController.setFilter(repositorySearchController);

		// Set up the libraries in a namespace table
		addIncludedController(namespaceLibrariesTreeTableController);
		// FIXME - use events
		// namespaceLibrariesTreeTableController.getSelectable()
		// .addListener((v, old, newValue) -> librarySelectionListener(newValue));

		addIncludedController(dexStatusController);
		statusController = dexStatusController; // make available to base class

		log.debug("Stage set.");
	}

	private void librarySelectionListener(TreeItem<RepoItemDAO> item) {
		if (item == null)
			return;
		log.debug("Library selected: " + item.getValue());
		try {
			repositoryItemCommitHistoriesController.post(item.getValue());
		} catch (Exception e) {
			log.warn("Could not post commit history: " + e.getLocalizedMessage());
		}
	}

	/**
	 * Handle namespace tree item selection by sending namespace to library table and clearing history.
	 * 
	 * @param item
	 */
	private void namespaceSelectionListener(TreeItem<NamespacesDAO> item) {
		if (item == null)
			return;
		log.debug("New namespace tree item selected: " + item.getValue());
		NamespacesDAO nsNode = item.getValue();
		if (nsNode.getRepository() != null) {
			try {
				namespaceLibrariesTreeTableController.post(nsNode);
				repositoryItemCommitHistoriesController.clear();
			} catch (Exception e) {
				postRepoError(e);
			}
		}
	}

	/**
	 * Called when the user modifies the selection of the 'repositoryChoice' control.
	 * 
	 * @throws RepositoryException
	 */
	private void repositorySelectionChanged() {
		log.debug("Selected new repository");
		try {
			repositoryNamespacesTreeController.post(repositorySelectionController.getSelectedRepository());
			// repositorySearchController.setRepository(repositorySelectionController.getSelectedRepository());
		} catch (Exception e) {
			log.warn("Error posting repository: " + e.getLocalizedMessage());
			postRepoError(e);
		}
	}

	protected void postRepoError(Exception e) {
		postError(e, "Error accessing repository.");
	}

	/**
	 * @return
	 */
	public RepositorySelectionController getRepositoryController() {
		return repositorySelectionController;
	}

	public RepositorySearchController getRepositorySearchController() {
		// return repositorySearchController;
		return null;
	}

	public Map<String, RepositoryItem> getRepositorySearchFilter() {
		return null;
	}

	public RepositoryNamespacesTreeController getRepositoryNamespacesController() {
		return repositoryNamespacesTreeController;
	}

	@Override
	public RepositoryManager getRepositoryManager() {
		return repositorySelectionController.getRepositoryManager();
	}

}
