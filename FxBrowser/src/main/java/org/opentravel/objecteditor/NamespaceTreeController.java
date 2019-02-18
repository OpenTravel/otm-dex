/**
 * 
 */
package org.opentravel.objecteditor;

import java.util.HashMap;

import org.opentravel.objecteditor.NamespaceLibrariesTableController.RepoItemNode;
import org.opentravel.schemacompiler.repository.Repository;
import org.opentravel.schemacompiler.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

//import javafx.scene.control.Label;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.control.TreeView;
//import javafx.util.converter.IntegerStringConverter;
//javafx.beans.property.SimpleBooleanProperty
// import javafx.beans.property.ReadOnlyStringWrapper;
//javafx.beans.property.ReadOnlyBooleanWrapper
//javafx.beans.property.SimpleintegerProperty
//javafx.beans.property.ReadOnlyintegerWrapper
//javafx.beans.property.SimpleDoubleProperty
//javafx.beans.property.ReadOnlyDoubleWrapper
//javafx.beans.property.ReadOnlyStringWrapper
//import javafx.beans.property.StringProperty;
//import javafx.beans.property.SimpleStringProperty;

/**
 * Controller for displaying repository namespaces in a tree view.
 * 
 * @author dmh
 *
 */
@SuppressWarnings("restriction")
public class NamespaceTreeController implements DexController {
	private static final Logger LOGGER = LoggerFactory.getLogger(NamespaceTreeController.class);

	// Create a javafx node for namespace tree
	public class NamespaceNode {
		protected String ns;
		protected String basePath;
		protected Repository repository;

		public NamespaceNode(String ns) {
			this(ns, null, null);
		}

		public NamespaceNode(String ns, String basePath, Repository repo) {
			this.ns = ns;
			this.basePath = basePath;
			this.repository = repo;
		}

		public StringProperty nsProperty() {
			return new SimpleStringProperty(ns);
		}

		//
		@Override
		public String toString() {
			return ns;
		}

		public String getValue() {
			return ns;
		}
		//
		// public ImageView getIcon() {
		// return images.getView(element.getIconType());
		// }
		//

		/**
		 * @return
		 */
		public String getFullPath() {
			return basePath + "/" + ns;
		}
	}

	protected ImageManager imageMgr;
	protected TreeView<NamespaceNode> tree;
	protected TreeItem<NamespaceNode> root;
	protected Stage stage;

	private NamespaceLibrariesTableController nsLibsController;
	private HashMap<String, TreeItem<NamespaceNode>> namespaceMap = new HashMap<>();

	/**
	 * Create a tree of repository Namespaces with manager.
	 * 
	 * @param table
	 * @param stage
	 * @param nsLibraryTablePermissionField
	 */
	public NamespaceTreeController(Stage stage, TreeView<NamespaceNode> tree,
			TreeTableView<RepoItemNode> repoTabNSContent, TextField nsLibraryTablePermissionField,
			ChoiceBox<String> repositoryChoice, ChoiceBox<String> namespaceChoice) {
		System.out.println("Initializing repository tab.");

		// Marshal and validate parameters
		//
		this.stage = stage;
		if (stage == null)
			throw new IllegalStateException("Stage is null.");
		imageMgr = new ImageManager(stage);
		// TODO - get imageManager from parent.

		// Create namespace library Table
		// TODO - move to tab controller
		if (repoTabNSContent == null)
			throw new IllegalArgumentException("Namespace Library tree table view is null.");
		nsLibsController = new NamespaceLibrariesTableController(this, repoTabNSContent, nsLibraryTablePermissionField);

		if (tree == null)
			throw new IllegalArgumentException("Repository tree view is null.");
		this.tree = tree;

		// Layout the table
		tree.getSelectionModel().selectedItemProperty()
				.addListener((v, old, newValue) -> namespaceSelectionListener(newValue));
		root = initializeTree(tree);
	}

	private TreeItem<NamespaceNode> initializeTree(TreeView<NamespaceNode> tree) {
		// Set the hidden root item
		TreeItem<NamespaceNode> root = new TreeItem<>();
		root.setExpanded(true); // Startout fully expanded
		// Set up the TreeTable
		tree.setRoot(root);
		tree.setShowRoot(false);
		tree.setEditable(true);
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
				TreeItem<NamespaceNode> treeItem = createTreeItem(repository, null, rootNS, root);
				namespaceMap.put(rootNS, treeItem);
				// Get sub-namespaces in background thread
				startGetSubNamespaces(repository, rootNS);
			}
		} catch (RepositoryException e) {
			System.out.println("Error: " + e.getLocalizedMessage());
		}
	}

	private void startGetSubNamespaces(final Repository repository, final String rootNS) {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				try {
					getSubNamespaces(repository, rootNS);
				} catch (RepositoryException e) {
					System.out.println("Repository error: " + e.getLocalizedMessage());
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

	/**
	 * Get all children of a namespace and add to Map and Tree
	 * 
	 * @param repository
	 * @param parentNS
	 * @throws RepositoryException
	 */
	private void getSubNamespaces(Repository repository, String parentNS) throws RepositoryException {
		TreeItem<NamespaceNode> item;
		for (String childNS : repository.listNamespaceChildren(parentNS)) {
			TreeItem<NamespaceNode> parent = namespaceMap.get(parentNS);
			if (parent != null) {
				item = createTreeItem(repository, parentNS, childNS, parent);
				namespaceMap.put(item.getValue().getFullPath(), item);
				// Recurse to get all descendants
				startGetSubNamespaces(repository, item.getValue().getFullPath());
			} else {
				System.out.println("ERROR - namespace not found in map.");
			}
		}
	}

	private TreeItem<NamespaceNode> createTreeItem(Repository repo, String basePath, String ns,
			TreeItem<NamespaceNode> parent) {
		TreeItem<NamespaceNode> item = new TreeItem<>(new NamespaceNode(ns, basePath, repo));
		item.setExpanded(false);
		parent.getChildren().add(item);
		// item.setGraphic(images.getView(element));
		return item;
	}

	/**
	 * {@inheritDoc} Remove all items from the table
	 */
	@Override
	public void clear() {
		tree.getRoot().getChildren().clear();
	}

	/**
	 * Listener for namespace selection events. Informs children to post item.
	 * 
	 * @param item
	 */
	private void namespaceSelectionListener(TreeItem<NamespaceNode> item) {
		if (item == null || item.getValue() == null)
			return;
		NamespaceNode nsNode = item.getValue();
		if (nsNode.repository != null) {
			try {
				nsLibsController.createTreeItems(nsNode.repository, nsNode.getFullPath());
			} catch (RepositoryException e) {
				System.out.println("Error accessing namespace: " + e.getLocalizedMessage());
			}
		}
	}

	@Override
	public ImageManager getImageManager() {
		return imageMgr;
	}

}
