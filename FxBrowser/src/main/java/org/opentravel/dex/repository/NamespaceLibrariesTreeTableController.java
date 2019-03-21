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
import org.opentravel.schemacompiler.repository.RepositoryItem;

import javafx.beans.property.ReadOnlyObjectProperty;
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
	protected TreeTableView<RepoItemDAO> librariesTreeTableView;
	@FXML
	private Label permissionLabel;
	@FXML
	private Label namespaceLabel;

	// private TreeTableView<RepoItemDAO> table;
	private TreeItem<RepoItemDAO> root;
	private NamespacesDAO currentNamespaceDAO = null;

	public NamespaceLibrariesTreeTableController() {
		log.debug("Constructing namespace libraries tree controller.");
	}

	@Override
	public void initialize() {
		log.debug("Initializing namespace libraries tree controller.");

		if (librariesTreeTableView == null)
			throw new IllegalArgumentException("Namespace libraries tree table view is null.");
		// table = librariesTreeTableView;

		// Initialize and build columns for library tree table
		root = initializeTree();
		buildColumns(librariesTreeTableView);
	}

	private TreeItem<RepoItemDAO> initializeTree() {
		// Set the hidden root item
		root = new TreeItem<>();
		root.setExpanded(true); // Startout fully expanded
		// Set up the TreeTable
		librariesTreeTableView.setRoot(root);
		librariesTreeTableView.setShowRoot(false);
		librariesTreeTableView.setEditable(false);

		// tree.getSelectionModel().setCellSelectionEnabled(true); // allow individual cells to be edited
		// tree.setTableMenuButtonVisible(true); // allow users to select columns
		// Enable context menus at the row level and add change listener for for applying style
		librariesTreeTableView.setRowFactory((TreeTableView<RepoItemDAO> p) -> new NamespaceLibrariesRowFactory(this));
		return root;
	}

	@Override
	public void clear() {
		librariesTreeTableView.getRoot().getChildren().clear();
	}

	public RepoItemDAO getSelectedItem() {
		return librariesTreeTableView.getSelectionModel().getSelectedItem().getValue();
	}

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

		Repository currentRepository = nsNode.getRepository();
		String namespace = nsNode.getFullPath();

		// Clear the table
		clear();

		// Display the namespace
		namespaceLabel.setText(namespace);

		// Display Permission enumeration value for this user in this namespace
		String permission = "unknown";
		try {
			permission = currentRepository.getUserAuthorization(namespace).toString();
		} catch (RepositoryException e) {
			// no op
		}
		permissionLabel.setText(permission);

		// Get a table of the latest of each library of any status
		HashMap<String, TreeItem<RepoItemDAO>> latestVersions = new HashMap<>();
		for (RepositoryItem ri : currentRepository.listItems(namespace, null, true)) {
			RepoItemDAO repoItemNode = new RepoItemDAO(ri);
			TreeItem<RepoItemDAO> treeItem = new TreeItem<>(repoItemNode);
			treeItem.setExpanded(true);
			root.getChildren().add(treeItem);
			latestVersions.put(ri.getLibraryName(), treeItem);
		}

		TreeItem<RepoItemDAO> item = null;
		for (RepositoryItem rItem : currentRepository.listItems(namespace, null, false)) {
			log.debug("Repo Item: " + rItem.getFilename());
			if (latestVersions.containsKey(rItem.getLibraryName())) {
				RepoItemDAO parent = latestVersions.get(rItem.getLibraryName()).getValue();
				if (!parent.versionProperty().get().equals(rItem.getVersion())) {
					RepoItemDAO repoItemNode = new RepoItemDAO(rItem);
					TreeItem<RepoItemDAO> treeItem = new TreeItem<>(repoItemNode);
					latestVersions.get(rItem.getLibraryName()).getChildren().add(treeItem);
				}
			}
		}
		// TODO - use progress indicator for history item retrieval
		// RepositoryViewerController.postStatus();
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

		// TODO - if WRITE status, post a lock/unlock button
		TreeTableColumn<RepoItemDAO, String> lockedCol = new TreeTableColumn<>("Locked By");
		lockedCol.setCellValueFactory(new TreeItemPropertyValueFactory<RepoItemDAO, String>("locked"));
		setColumnProps(lockedCol, true, false, true, 0);

		TreeTableColumn<RepoItemDAO, String> remarkCol = new TreeTableColumn<>("Last Remark");
		remarkCol.setCellValueFactory(new TreeItemPropertyValueFactory<RepoItemDAO, String>("history"));
		setColumnProps(remarkCol, true, false, true, 300);

		table.getColumns().setAll(fileCol, versionCol, statusCol, lockedCol, remarkCol);
		// table.getColumns().setAll(fileCol, versionCol, statusCol, lockedCol, lockedColB, remarkCol);

		// // Give all left over space to the last column
		// double width = fileCol.widthProperty().get();
		// width += versionCol.widthProperty().get();
		// width += statusCol.widthProperty().get();
		// width += lockedCol.widthProperty().get();
		// remarkCol.prefWidthProperty().bind(table.widthProperty().subtract(width));
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This exposes the library tree table's selected item.
	 */
	@Override
	public ReadOnlyObjectProperty<TreeItem<RepoItemDAO>> getSelectable() {
		return librariesTreeTableView.getSelectionModel().selectedItemProperty();
	}

	/**
	 * @return
	 */
	public RepositorySelectionController getRepositoryController() {
		// TODO - make this part of DexController interface
		return parentController.getRepositoryController();
	}

}
