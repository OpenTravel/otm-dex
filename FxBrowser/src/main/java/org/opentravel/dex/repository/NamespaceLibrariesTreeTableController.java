/**
 * 
 */
package org.opentravel.dex.repository;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.controllers.DexIncludedControllerBase;
import org.opentravel.dex.controllers.DexMainController;
import org.opentravel.dex.events.DexRepositoryItemSelectionEvent;
import org.opentravel.dex.events.DexRepositoryNamespaceSelectionEvent;
import org.opentravel.schemacompiler.repository.RepositoryItem;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

/**
 * Controller for a libraries in a namespace tree table view. Creates table containing repository item properties.
 * <p>
 * This class is designed to be injected into a parent controller by FXML loader. It has a VBOX containing the label
 * header and a tree table view.
 * 
 * @author dmh
 *
 */
public class NamespaceLibrariesTreeTableController extends DexIncludedControllerBase<NamespacesDAO> {
	private static Log log = LogFactory.getLog(NamespaceLibrariesTreeTableController.class);

	// Injected fields
	@FXML
	private TreeTableView<RepoItemDAO> nsLibrariesTreeTableView;
	@FXML
	private Label permissionLabel;
	@FXML
	private Label namespaceLabel;

	private TreeItem<RepoItemDAO> root;
	private NamespacesDAO currentNamespaceDAO = null;

	private static final EventType[] publishedEvents = { DexRepositoryItemSelectionEvent.REPOSITORY_ITEM_SELECTED };
	private static final EventType[] subscribedEvents = { DexRepositoryNamespaceSelectionEvent.REPOSITORY_NS_SELECTED };

	public NamespaceLibrariesTreeTableController() {
		super(subscribedEvents, publishedEvents);
	}

	@Override
	public void checkNodes() {
		if (!(nsLibrariesTreeTableView instanceof TreeTableView))
			throw new IllegalStateException("Libraries tree table not injected.");
		if (!(permissionLabel instanceof Label))
			throw new IllegalStateException("Permission label not injected.");
		if (!(namespaceLabel instanceof Label))
			throw new IllegalStateException("Namespace label not injected.");
		log.debug("Constructing namespace libraries tree controller.");
	}

	@Override
	public void configure(DexMainController main) {
		super.configure(main);
		eventPublisherNode = nsLibrariesTreeTableView;

		// Super.configure assures tree view is not null
		nsLibrariesTreeTableView.getSelectionModel().selectedItemProperty()
				.addListener((v, old, newValue) -> repoItemSelectionListener(newValue));

	}

	@Override
	public void initialize() {
		log.debug("Initializing namespace libraries tree controller.");

		// Initialize and build columns for library tree table
		root = initializeTree();
		buildColumns(nsLibrariesTreeTableView);

	}

	/**
	 * Respond to a selection in the table.
	 * 
	 * @param newValue
	 * @return
	 */
	private void repoItemSelectionListener(TreeItem<RepoItemDAO> newValue) {
		if (newValue != null && newValue.getValue() != null)
			nsLibrariesTreeTableView.fireEvent(new DexRepositoryItemSelectionEvent(this, newValue.getValue()));
	}

	@Override
	public void handleEvent(Event event) {
		if (event instanceof DexRepositoryNamespaceSelectionEvent)
			eventHandler((DexRepositoryNamespaceSelectionEvent) event);
	}

	private void eventHandler(DexRepositoryNamespaceSelectionEvent event) {
		log.debug("Namespace selected.");
		try {
			post(event.getValue());
		} catch (Exception e) {
			mainController.postError(e, "Error displaying repository namespace");
		}
	}

	private TreeItem<RepoItemDAO> initializeTree() {
		// Set the hidden root item
		root = new TreeItem<>();
		root.setExpanded(true); // Startout fully expanded
		// Set up the TreeTable
		nsLibrariesTreeTableView.setRoot(root);
		nsLibrariesTreeTableView.setShowRoot(false);
		nsLibrariesTreeTableView.setEditable(false);

		// Enable context menus at the row level and add change listener for for applying style
		nsLibrariesTreeTableView
				.setRowFactory((TreeTableView<RepoItemDAO> p) -> new NamespaceLibrariesRowFactory(this));
		return root;
	}

	@Override
	public void clear() {
		nsLibrariesTreeTableView.getRoot().getChildren().clear();
	}

	public RepoItemDAO getSelectedItem() {
		return nsLibrariesTreeTableView.getSelectionModel().getSelectedItem().getValue();
	}

	@Override
	public void refresh() {
		try {
			post(currentNamespaceDAO);
		} catch (Exception e) {
			log.error("Error refreshing namespace libraries tree table: " + e.getLocalizedMessage());
		}
	}

	@Override
	public void post(NamespacesDAO nsNode) throws Exception {
		super.post(nsNode);
		currentNamespaceDAO = nsNode;
		if (nsNode == null || nsNode.getFullPath() == null || nsNode.getFullPath().isEmpty())
			throw new IllegalArgumentException("Missing repository and namespace.");

		// Clear the table
		clear();

		// Display the namespace and permission
		namespaceLabel.textProperty().bind(nsNode.fullPathProperty());
		permissionLabel.textProperty().bind(nsNode.permissionProperty());

		// Get a table of the latest of each library of any status
		HashMap<String, TreeItem<RepoItemDAO>> latestVersions = new HashMap<>();
		if (nsNode.getLatestItems() != null)
			for (RepositoryItem ri : nsNode.getLatestItems()) {
				RepoItemDAO repoItemNode = new RepoItemDAO(ri, mainController.getStatusController());
				TreeItem<RepoItemDAO> treeItem = new TreeItem<>(repoItemNode);
				treeItem.setExpanded(true);
				root.getChildren().add(treeItem);
				latestVersions.put(ri.getLibraryName(), treeItem);
			}

		if (nsNode.getAllItems() != null)
			for (RepositoryItem rItem : nsNode.getAllItems()) {
				if (latestVersions.containsKey(rItem.getLibraryName())) {
					RepoItemDAO parent = latestVersions.get(rItem.getLibraryName()).getValue();
					if (!parent.versionProperty().get().equals(rItem.getVersion())) {
						RepoItemDAO repoItemNode = new RepoItemDAO(rItem, mainController.getStatusController());
						TreeItem<RepoItemDAO> treeItem = new TreeItem<>(repoItemNode);
						latestVersions.get(rItem.getLibraryName()).getChildren().add(treeItem);
					}
				}
			}
	}

	/**
	 * Create Columns and set cell values
	 */
	private void buildColumns(TreeTableView<RepoItemDAO> table) {
		TreeTableColumn<RepoItemDAO, String> fileCol = new TreeTableColumn<>("Library");
		fileCol.setCellValueFactory(new TreeItemPropertyValueFactory<RepoItemDAO, String>("libraryName"));
		setColumnProps(fileCol, true, false, true, 250);

		TreeTableColumn<RepoItemDAO, String> versionCol = new TreeTableColumn<>("Version");
		versionCol.setCellValueFactory(new TreeItemPropertyValueFactory<RepoItemDAO, String>("version"));
		setColumnProps(versionCol, true, false, true, 0);

		TreeTableColumn<RepoItemDAO, String> statusCol = new TreeTableColumn<>("Status");
		statusCol.setCellValueFactory(new TreeItemPropertyValueFactory<RepoItemDAO, String>("status"));
		setColumnProps(statusCol, true, false, true, 0);

		TreeTableColumn<RepoItemDAO, String> lockedCol = new TreeTableColumn<>("Locked By");
		lockedCol.setCellValueFactory(new TreeItemPropertyValueFactory<RepoItemDAO, String>("locked"));
		setColumnProps(lockedCol, true, false, true, 0);

		TreeTableColumn<RepoItemDAO, String> remarkCol = new TreeTableColumn<>("Last Remark");
		remarkCol.setCellValueFactory(new TreeItemPropertyValueFactory<RepoItemDAO, String>("history"));
		setColumnProps(remarkCol, true, false, true, 300);

		table.getColumns().setAll(fileCol, versionCol, statusCol, lockedCol, remarkCol);
	}

	// public RepositoryViewerController getRepositoryViewerController() {
	// if (mainController instanceof RepositoryViewerController)
	// return (RepositoryViewerController) mainController;
	// return null;
	// }

}
