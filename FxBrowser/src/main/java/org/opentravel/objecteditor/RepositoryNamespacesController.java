/**
 * 
 */
package org.opentravel.objecteditor;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.model.OtmModelManager;
import org.opentravel.objecteditor.repository.NamespaceLibrariesTreeTableController;
import org.opentravel.objecteditor.repository.NamespacesDAO;
import org.opentravel.schemacompiler.repository.Repository;
import org.opentravel.schemacompiler.repository.RepositoryException;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Controller for displaying repository namespaces in a tree view.
 * 
 * @author dmh
 *
 */
// FIXME - this should be replaced with the included RepositoryNamespacesTreeController
public class RepositoryNamespacesController implements DexController {
	private static Log log = LogFactory.getLog(RepositoryNamespacesController.class);

	protected ImageManager imageMgr;
	protected TreeView<NamespacesDAO> tree;
	protected TreeItem<NamespacesDAO> root;
	private HashMap<String, TreeItem<NamespacesDAO>> namespaceMap = new HashMap<>();
	private DexController parentController;

	@FXML
	private NamespaceLibrariesTreeTableController namespaceLibrariesTreeTableController;

	@FXML
	protected TreeView<NamespacesDAO> repositoryNamespacesTree;

	public RepositoryNamespacesController() {
		log.debug("Constructing repository tree controller.");
	}

	/**
	 * Create a tree of repository Namespaces with manager.
	 * 
	 * @param table
	 * @param stage
	 * @param nsLibraryTablePermissionField
	 */
	// Use FXML include and injection instead of constructor
	public RepositoryNamespacesController(DexController parent, TreeView<NamespacesDAO> tree) {
		log.debug("Initializing repository tab with parameters.");

		imageMgr = parent.getImageManager();
		parentController = parent;

		if (tree == null)
			throw new IllegalArgumentException("Repository tree view is null.");
		this.tree = tree;

		root = initializeTree(tree);
	}

	/**
	 * {@inheritDoc} Remove all items from the table
	 */
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

	// @Deprecated
	// public TreeView<NamespacesDAO> getTree() {
	// return repositoryNamespacesTree;
	// }

	@Override
	public ImageManager getImageManager() {
		if (imageMgr == null)
			throw new IllegalStateException("Image manger is null.");
		return imageMgr;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return null
	 */
	@Override
	public OtmModelManager getModelManager() {
		return null;
	}

	@Override
	public ReadOnlyObjectProperty<TreeItem<NamespacesDAO>> getSelectable() {
		return tree.getSelectionModel().selectedItemProperty();
	}

	// Have parent listen for selections
	// private void treeSelectionListener(TreeItem<NamespacesDAO> item) {
	// if (item == null)
	// return;
	// log.debug("New tree item selected: " + item.getValue());
	// NamespacesDAO nsNode = item.getValue();
	// // if (nsNode.getRepository() != null) {
	// // try {
	// // nsLibsController.post(nsNode.getRepository(), nsNode.getFullPath());
	// // libHistoryController.clear();
	// // } catch (RepositoryException e) {
	// // log.debug("Error accessing namespace: " + e.getLocalizedMessage());
	// // }
	// // }
	// }

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

	// @Override
	// public void initialize() {
	// log.debug("Initializing repository tree controller.");
	//
	// if (repositoryNamespacesTree == null)
	// throw new IllegalArgumentException("Repository tree view is null.");
	// this.tree = repositoryNamespacesTree;
	//
	// root = initializeTree(tree);
	//
	// }

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
	public void post(Repository repository) {
		// Clear existing content
		clear();

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

	@Override
	public void postProgress(double percentDone) {
		parentController.postProgress(percentDone);
	}

	@Override
	public void postStatus(String string) {
		parentController.postStatus(string);
	}

	// @Override
	// public void setParent(DexController parent) {
	// this.parentController = parent;
	// imageMgr = parent.getImageManager();
	// log.debug("Parent controller for tree controller set.");
	//
	// Parent parentNode = tree.getParent();
	// log.debug("parent is: " + parentNode);
	// // if (parentNode instanceof AnchorPane) {
	// // AnchorPane v = (AnchorPane) parentNode;
	// // double ph = v.getPrefHeight();
	// // v.setMaxHeight(1.0);
	// // ReadOnlyDoubleProperty wp = v.widthProperty();
	// // tree widthProperty is read only!
	// // tree.widthProperty().bind(wp.getValue());
	// // n.prefWidthProperty().bind(mainContent.widthProperty());
	// // n.prefHeightProperty().bind(mainContent.heightProperty());
	// }

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
