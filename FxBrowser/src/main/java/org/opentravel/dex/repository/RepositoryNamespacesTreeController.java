/**
 * 
 */
package org.opentravel.dex.repository;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.objecteditor.DexIncludedControllerBase;
import org.opentravel.schemacompiler.repository.Repository;
import org.opentravel.schemacompiler.repository.RepositoryException;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Controller for posting a repository to display its namespaces in a tree view.
 * 
 * @author dmh
 *
 */
public class RepositoryNamespacesTreeController extends DexIncludedControllerBase<Repository> {
	private static Log log = LogFactory.getLog(RepositoryNamespacesTreeController.class);

	protected TreeView<NamespacesDAO> tree;
	protected TreeItem<NamespacesDAO> root;
	private HashMap<String, TreeItem<NamespacesDAO>> namespaceMap = new HashMap<>();

	// @FXML
	// private NamespaceLibrariesTreeTableController namespaceLibrariesTreeTableController;
	@FXML
	protected TreeView<NamespacesDAO> repositoryNamespacesTree;

	public RepositoryNamespacesTreeController() {
		super();
	}

	@Override
	public void clear() {
		if (tree != null && tree.getRoot() != null)
			tree.getRoot().getChildren().clear();
	}

	private TreeItem<NamespacesDAO> createTreeItem(Repository repo, String basePath, String ns,
			TreeItem<NamespacesDAO> parent) {
		TreeItem<NamespacesDAO> item = new TreeItem<>(new NamespacesDAO(ns, basePath, repo));
		item.setExpanded(false);
		parent.getChildren().add(item);
		// item.setGraphic(images.getView(element));
		return item;
	}

	@Override
	public ReadOnlyObjectProperty<TreeItem<NamespacesDAO>> getSelectable() {
		return tree.getSelectionModel().selectedItemProperty();
	}

	/**
	 * Get all children of a namespace and add to Map and Tree
	 * 
	 * @param repository
	 * @param parentNS
	 * @throws RepositoryException
	 */
	private void getSubNamespaces(Repository repository, String parentNS) throws RepositoryException {
		TreeItem<NamespacesDAO> item;
		for (String childNS : repository.listNamespaceChildren(parentNS)) {
			TreeItem<NamespacesDAO> parent = namespaceMap.get(parentNS);
			if (parent != null) {
				item = createTreeItem(repository, parentNS, childNS, parent);
				namespaceMap.put(item.getValue().getFullPath(), item);
				// Recurse to get all descendants
				startGetSubNamespaces(repository, item.getValue().getFullPath());
			} else {
				log.debug("ERROR - namespace not found in map.");
			}
		}
	}

	@Override
	public void initialize() {
		log.debug("Initializing repository tree controller.");

		if (repositoryNamespacesTree == null)
			throw new IllegalArgumentException("Repository tree view is null.");
		this.tree = repositoryNamespacesTree;

		root = initializeTree(tree);
	}

	private TreeItem<NamespacesDAO> initializeTree(TreeView<NamespacesDAO> tree) {
		// Set the hidden root item
		TreeItem<NamespacesDAO> root = new TreeItem<>();
		root.setExpanded(true); // Startout fully expanded
		// Set up the TreeTable
		tree.setRoot(root);
		tree.setShowRoot(false);
		tree.setEditable(true);

		// nsTreeController.getSelectable().addListener((v, old, newValue) -> treeSelectionListener(newValue));
		// tree.getSelectionModel().selectedItemProperty()
		// .addListener((v, old, newValue) -> treeSelectionListener(newValue));
		return root;
	}

	/**
	 * Post the contents from the repository into the tree.
	 * 
	 * @param repository
	 */
	@Override
	public void post(Repository repository) throws Exception {
		super.post(repository);

		// Get the root namespaces in real time
		try {
			for (String rootNS : repository.listRootNamespaces()) {
				TreeItem<NamespacesDAO> treeItem = createTreeItem(repository, null, rootNS, root);
				namespaceMap.put(rootNS, treeItem);
				// Get sub-namespaces in background thread
				startGetSubNamespaces(repository, rootNS);
			}
		} catch (RepositoryException e) {
			log.debug("Error: " + e.getLocalizedMessage());
		}
	}

	// TODO - use fx task with repository selection controller's progress bar and status
	private void startGetSubNamespaces(final Repository repository, final String rootNS) {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				try {
					getSubNamespaces(repository, rootNS);
				} catch (RepositoryException e) {
					log.debug("Repository error: " + e.getLocalizedMessage());
				}
			}
		};
		// Run the task in a background thread
		Thread backgroundThread = new Thread(task);
		// Terminate the running thread if the application exits
		backgroundThread.setDaemon(true);
		// Start the thread
		backgroundThread.start();
	}

}
