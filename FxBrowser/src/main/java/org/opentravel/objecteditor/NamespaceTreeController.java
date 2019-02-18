/**
 * 
 */
package org.opentravel.objecteditor;

import java.util.HashMap;

import org.opentravel.schemacompiler.repository.Repository;
import org.opentravel.schemacompiler.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

//import javafx.beans.value.ChangeListener;
//import javafx.scene.control.Label;
//import javafx.collections.FXCollections;
//import javafx.beans.value.ObservableValue;
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
	// protected Stage stage;

	private HashMap<String, TreeItem<NamespaceNode>> namespaceMap = new HashMap<>();

	/**
	 * Create a tree of repository Namespaces with manager.
	 * 
	 * @param table
	 * @param stage
	 * @param nsLibraryTablePermissionField
	 */
	public NamespaceTreeController(DexController parent, TreeView<NamespaceNode> tree) {
		System.out.println("Initializing repository tab.");

		imageMgr = parent.getImageManager();

		if (tree == null)
			throw new IllegalArgumentException("Repository tree view is null.");
		this.tree = tree;

		root = initializeTree(tree);
	}

	@Override
	public ReadOnlyObjectProperty<TreeItem<NamespaceNode>> getSelectable() {
		return tree.getSelectionModel().selectedItemProperty();
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

	@Override
	public ImageManager getImageManager() {
		if (imageMgr == null)
			throw new IllegalStateException("Image manger is null.");
		return imageMgr;
	}

}
