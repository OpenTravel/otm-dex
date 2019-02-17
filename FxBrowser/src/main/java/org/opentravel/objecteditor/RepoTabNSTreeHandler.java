/**
 * 
 */
package org.opentravel.objecteditor;

import java.util.HashMap;

import org.opentravel.common.RepositoryController;
import org.opentravel.objecteditor.NamespaceLibrariesTableController.RepoItemNode;
import org.opentravel.schemacompiler.repository.Repository;
import org.opentravel.schemacompiler.repository.RepositoryException;
import org.opentravel.schemacompiler.repository.RepositoryManager;
import org.opentravel.schemacompiler.repository.impl.RemoteRepositoryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

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
 * Manage a facets and properties in a tree table.
 * 
 * @author dmh
 *
 */
@SuppressWarnings("restriction")
public class RepoTabNSTreeHandler implements DexController {
	private static final Logger LOGGER = LoggerFactory.getLogger(RepoTabNSTreeHandler.class);

	private static final String LOCAL_REPO = "Local";

	private RepositoryManager repositoryManager;
	// private RepositoryAvailabilityChecker availabilityChecker;

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
			StringProperty ssp = new SimpleStringProperty(ns);
			return ssp;
			// ssp.addListener((ObservableValue<? extends String> ov, String oldVal, String newVal) -> {
			// // element.setName(newVal);
			// System.out.println("TODO - set role of " + element.getName() + " to " + newVal);
			// });
			// return ssp;
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
	}

	protected ImageManager imageMgr;
	protected TreeView<NamespaceNode> tree;
	protected ObservableList<?> nsList;
	protected TreeItem<NamespaceNode> root;
	protected Stage stage;
	protected RepositoryController repoController;

	private ChoiceBox<String> repositoryChoice;
	private ChoiceBox<String> namespaceChoice;
	private NamespaceLibrariesTableController nsLibsController;

	HashMap<String, TreeItem<NamespaceNode>> namespaceMap = new HashMap<>();

	/**
	 * Create a tree of repository Namespaces with manager.
	 * 
	 * @param table
	 * @param stage
	 * @param nsLibraryTablePermissionField
	 */
	public RepoTabNSTreeHandler(Stage stage, TreeView<NamespaceNode> tree, TreeTableView<RepoItemNode> repoTabNSContent,
			TextField nsLibraryTablePermissionField, ChoiceBox<String> repositoryChoice,
			ChoiceBox<String> namespaceChoice) {
		System.out.println("Initializing repository tab.");

		// Marshal and validate parameters
		//
		this.stage = stage;
		if (stage == null)
			throw new IllegalStateException("Stage is null.");
		imageMgr = new ImageManager(stage);

		this.repositoryChoice = repositoryChoice;
		this.namespaceChoice = namespaceChoice;
		if (repositoryChoice == null || namespaceChoice == null)
			throw new IllegalStateException("Null control nodes passed to repsitory tab handler.");

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
		// buildColumns(nsTable);

		// Set up repository Choice
		repoController = new RepositoryController();
		repositoryManager = repoController.getRepositoryManager(); // FIXME
		configureRepositoryChoice();

		// Add data items
		try {
			createTreeItems();
		} catch (RepositoryException e) {
			System.out.println("Error retrieving repository items: " + e.getLocalizedMessage());
		}

	}

	private void configureRepositoryChoice() {
		System.out.println("Configuring repository choice box.");
		stage.showingProperty().addListener((observable, oldValue, newValue) -> {
			ObservableList<String> repositoryIds = FXCollections.observableArrayList();
			repositoryIds.add(LOCAL_REPO);
			repositoryManager.listRemoteRepositories().forEach(r -> repositoryIds.add(r.getId()));
			repositoryChoice.setItems(repositoryIds);
			repositoryChoice.getSelectionModel().select(0);
		});

		// Configure listeners for choice boxes
		repositoryChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
			repositorySelectionChanged();
		});
		// namespaceChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
		// namespaceSelectionChanged();
		// });
	}

	/**
	 * Called when the user modifies the selection of the 'repositoryChoice' control.
	 * 
	 * @throws RepositoryException
	 */
	private void repositorySelectionChanged() {
		// FIXME - only get root namespaces in real time.
		// Create runable for others.
		//
		// Runnable r = new BackgroundTask("Updating namespaces from remote repository...", StatusType.INFO) {
		// public void execute() throws Throwable {
		// String rid = repositoryChoice.getSelectionModel().getSelectedItem();
		// Repository repository = repositoryManager.getRepository(rid);
		// try {
		// List<String> baseNamespaces = repository.listBaseNamespaces();
		// } catch (RepositoryException e) {
		// e.printStackTrace();
		// }
		System.out.println("Selected new repository");
		// Clear the existing namespace tree items
		clear();
		// Create new items
		try {
			createTreeItems();
		} catch (RepositoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// List<String> baseNamespaces;
		// try {
		// baseNamespaces = repository.listBaseNamespaces();
		// } catch (RepositoryException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// baseNamespaces = new ArrayList<>();
		// }
		// baseNamespaces.add(0, null);
		// // Platform.runLater(() -> {
		// namespaceChoice.setItems(FXCollections.observableList(baseNamespaces));
		// namespaceChoice.getSelectionModel().select(0);
		// // });
		// System.out.println("Added namespaces to choice.");
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
	 * Add tree items to ROOT for each child and grandchild of the member.
	 * 
	 * @param member
	 * @throws RepositoryException
	 */
	private void createTreeItems() throws RepositoryException {
		tree.getRoot().getChildren().clear();

		Repository repository = repoController.getLocalRepository();
		String rid = repositoryChoice.getSelectionModel().getSelectedItem();
		if (rid != null)
			if (rid.equals(LOCAL_REPO))
				repository = repoController.getLocalRepository();
			else
				// Use selected repository
				repository = repositoryManager.getRepository(rid);

		// FIXME - post the user
		String user = "";
		if (repository instanceof RemoteRepositoryClient)
			user = ((RemoteRepositoryClient) repository).getUserId();

		// Get the root namespaces in real time
		for (String rootNS : repository.listRootNamespaces()) {
			TreeItem<NamespaceNode> treeItem = createTreeItem(repository, null, rootNS, root);
			namespaceMap.put(rootNS, treeItem);

			startGetSubNamespaces(repository, rootNS);
			// getSubNamespaces(repository, rootNS);
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
	 * Thead task to get all children of a namespace and add to Map and Tree
	 * 
	 * @param repository
	 * @param parentNS
	 * @throws RepositoryException
	 */
	private void getSubNamespaces(Repository repository, String parentNS) throws RepositoryException {
		TreeItem<NamespaceNode> item;
		String fullNS;
		for (String childNS : repository.listNamespaceChildren(parentNS)) {
			TreeItem<NamespaceNode> parent = namespaceMap.get(parentNS);
			if (parent != null) {
				item = createTreeItem(repository, parentNS, childNS, parent);
				// Recurse to get all descendants
				fullNS = parentNS + "/" + childNS;
				namespaceMap.put(fullNS, item);
				startGetSubNamespaces(repository, fullNS);
			} else {
				System.out.println("ERROR - namespace not found in map.");
			}
		}
	}

	private TreeItem<NamespaceNode> createTreeItem(String ns, TreeItem<NamespaceNode> parent) {
		TreeItem<NamespaceNode> item = new TreeItem<>(new NamespaceNode(ns));
		item.setExpanded(false);
		parent.getChildren().add(item);
		// item.setGraphic(images.getView(element));
		return item;
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
	 * Remove all items from the table
	 */
	private void clear() {
		tree.getRoot().getChildren().clear();
	}

	// /**
	// * Add event listeners to passed tree table view.
	// *
	// * @param navTreeTableView
	// */
	// public void registerListeners(TreeTableView<OtmTreeTableNode> navTreeTableView) {
	// navTreeTableView.getSelectionModel().selectedItemProperty()
	// .addListener((v, old, newValue) -> newMemberSelectionListener(newValue));
	// }
	//
	// private void newMemberSelectionListener(TreeItem<OtmTreeTableNode> item) {
	// clear();
	// // if (item.getValue().getValue() instanceof OtmLibraryMember)
	// // createTreeItems((OtmLibraryMember<?>) item.getValue().getValue());
	// // System.out.println("Facet Table Selection Listener: " + item.getValue());
	// }

	/**
	 * Set edit-ability of columns
	 * 
	 * A note about selection: A TreeTableCell visually shows it is selected when two conditions are met: 1.The
	 * TableSelectionModel.isSelected(int, TableColumnBase) method returns true for the row / column that this cell
	 * represents, and 2.The cell selection mode property is set to true (to represent that it is allowable to select
	 * individual cells (and not just rows of cells)).
	 * 
	 * @param item
	 */
	private void namespaceSelectionListener(TreeItem<NamespaceNode> item) {
		if (item == null || item.getValue() == null)
			return;
		NamespaceNode nsNode = item.getValue();
		if (nsNode.repository != null) {
			try {
				nsLibsController.createTreeItems(nsNode.repository, nsNode.basePath + "/" + nsNode.ns);
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
