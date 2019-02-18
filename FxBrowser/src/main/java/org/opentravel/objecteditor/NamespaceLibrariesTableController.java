/**
 * 
 */
package org.opentravel.objecteditor;

import java.util.HashMap;

import org.opentravel.common.RepositoryController;
import org.opentravel.objecteditor.NavigationTreeTableHandler.OtmTreeTableNode;
import org.opentravel.schemacompiler.repository.Repository;
import org.opentravel.schemacompiler.repository.RepositoryException;
import org.opentravel.schemacompiler.repository.RepositoryItem;
import org.opentravel.schemacompiler.repository.RepositoryItemHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

//import javafx.concurrent.Task;
//import javafx.scene.control.cell.TreeItemPropertyValueFactory;
//import javafx.scene.control.cell.TextFieldTreeTableCell;
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
 * Controller for a library table. Creates table containing repository item properties.
 * 
 * @author dmh
 *
 */
@SuppressWarnings("restriction")
public class NamespaceLibrariesTableController implements DexController {
	private static final Logger LOGGER = LoggerFactory.getLogger(NamespaceLibrariesTableController.class);

	public class RepoItemNode {
		protected RepositoryItem repoItem;
		SimpleStringProperty lastHistory = new SimpleStringProperty(":> working...");
		RepositoryItemHistory history = null;

		public RepoItemNode(RepositoryItem item) {
			this.repoItem = item;

			Runnable task = new Runnable() {
				@Override
				public void run() {
					getHistory();
				}
			};
			// Run the task in a background thread
			Thread backgroundThread = new Thread(task);
			// Terminate the running thread if the application exits
			backgroundThread.setDaemon(true);
			// Start the thread
			backgroundThread.start();
		}

		public StringProperty libraryNameProperty() {
			return new SimpleStringProperty(repoItem.getLibraryName());
		}

		public StringProperty versionProperty() {
			return new SimpleStringProperty(repoItem.getVersion());
		}

		public StringProperty statusProperty() {
			return new SimpleStringProperty(repoItem.getStatus().toString());
		}

		public StringProperty lockedProperty() {
			return new SimpleStringProperty(repoItem.getLockedByUser());
		}

		public StringProperty historyProperty() {
			return lastHistory;
		}

		public void setHistory() {
			if (history == null)
				return;
			StringBuilder remark = new StringBuilder(history.getCommitHistory().get(0).getUser());
			remark.append(" - ");
			remark.append(history.getCommitHistory().get(0).getRemarks());
			lastHistory.set(remark.toString());
		}

		/**
		 * Background thread ready getter for the history of this repository item.
		 * 
		 * @param repoItem
		 * @param value
		 */
		public void getHistory() {
			System.out.println("Finding history item for " + repoItem.getFilename());
			try {
				history = repoItem.getRepository().getHistory(repoItem);
				setHistory();
			} catch (RepositoryException e) {
			}
		}
	}

	protected ImageManager imageMrg;
	protected TreeTableView<RepoItemNode> nsTable;
	protected ObservableList<?> nsList;
	protected RepositoryController repoController;

	private TreeItem<RepoItemNode> root;
	private TextField permissionField;

	/**
	 * Create a view for the libraries described by repository items in the passed namespace.
	 * 
	 * @param nsLibraryTablePermissionField
	 */
	public NamespaceLibrariesTableController(DexController parent, TreeTableView<RepoItemNode> view,
			TextField nsLibraryTablePermissionField) {

		System.out.println("Initializing repository library table view.");

		// Marshal and validate the parameters
		imageMrg = parent.getImageManager();
		if (imageMrg == null)
			throw new IllegalStateException("Image manger is null.");

		this.nsTable = view;
		if (nsTable == null)
			throw new IllegalStateException("Namespace libraries view is null.");

		this.permissionField = nsLibraryTablePermissionField;
		permissionField.setEditable(false);

		// Create and layout tree table
		root = initializeTree(nsTable);
		buildColumns(nsTable);

		// Add data items
		// try {
		// createTreeItems(root, repository, namespace);
		// } catch (RepositoryException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

	private TreeItem<RepoItemNode> initializeTree(TreeTableView<RepoItemNode> tree) {
		// Set the hidden root item
		TreeItem<RepoItemNode> root = new TreeItem<>();
		root.setExpanded(true); // Startout fully expanded
		// Set up the TreeTable
		tree.setRoot(root);
		tree.setShowRoot(false);
		tree.setEditable(false);

		// tree.getSelectionModel().setCellSelectionEnabled(true); // allow individual cells to be edited
		// tree.setTableMenuButtonVisible(true); // allow users to select columns
		// Enable context menus at the row level and add change listener for for applying style
		// tree.setRowFactory((TreeTableView<NamespaceNode> p) -> new PropertyRowFactory());
		return root;
	}

	@Override
	public void clear() {
		nsTable.getRoot().getChildren().clear();
	}

	/**
	 * Add tree items to ROOT for each Library with the same name.
	 * 
	 * @param namespace
	 * @param repository
	 * @throws RepositoryException
	 */
	public void createTreeItems(Repository repository, String namespace) throws RepositoryException {
		if (repository == null || namespace == null || namespace.isEmpty())
			throw new IllegalArgumentException("Missing repository and namespace.");

		// Clear the table
		clear();

		// Display Permission enumeration value for this user in this namespace
		String permission = "unknown";
		try {
			permission = repository.getUserAuthorization(namespace).toString();
		} catch (RepositoryException e) {
			// no op
		}
		permissionField.setText(permission);

		// Get a table of the latest of each library of any status
		HashMap<String, TreeItem<RepoItemNode>> latestVersions = new HashMap<>();
		for (RepositoryItem ri : repository.listItems(namespace, null, true)) {
			RepoItemNode repoItemNode = new RepoItemNode(ri);
			TreeItem<RepoItemNode> treeItem = new TreeItem<>(repoItemNode);
			treeItem.setExpanded(true);
			root.getChildren().add(treeItem);
			latestVersions.put(ri.getLibraryName(), treeItem);
		}

		TreeItem<RepoItemNode> item = null;
		for (RepositoryItem rItem : repository.listItems(namespace, null, false)) {
			System.out.println("Repo Item: " + rItem.getFilename());
			if (latestVersions.containsKey(rItem.getLibraryName())) {
				RepoItemNode parent = latestVersions.get(rItem.getLibraryName()).getValue();
				if (!parent.versionProperty().get().equals(rItem.getVersion())) {
					RepoItemNode repoItemNode = new RepoItemNode(rItem);
					TreeItem<RepoItemNode> treeItem = new TreeItem<>(repoItemNode);
					latestVersions.get(rItem.getLibraryName()).getChildren().add(treeItem);
				}
			}
			// RepoItemNode repoItemNode = new RepoItemNode(rItem);
			// root.getChildren().add(new TreeItem<>(repoItemNode));
		}
		// FIXME - use library name to organize parent/child relationships

	}

	/**
	 * Create Columns and set cell values
	 */
	private void buildColumns(TreeTableView<RepoItemNode> table) {
		TreeTableColumn<RepoItemNode, String> fileCol = new TreeTableColumn<>("Library");
		fileCol.setCellValueFactory(new TreeItemPropertyValueFactory<RepoItemNode, String>("libraryName"));
		setColumnProps(fileCol, true, false, true, 250);

		TreeTableColumn<RepoItemNode, String> versionCol = new TreeTableColumn<>("Version");
		versionCol.setCellValueFactory(new TreeItemPropertyValueFactory<RepoItemNode, String>("version"));
		setColumnProps(versionCol, true, false, true, 0);

		TreeTableColumn<RepoItemNode, String> statusCol = new TreeTableColumn<>("Status");
		statusCol.setCellValueFactory(new TreeItemPropertyValueFactory<RepoItemNode, String>("status"));
		setColumnProps(statusCol, true, false, true, 0);

		TreeTableColumn<RepoItemNode, String> lockedCol = new TreeTableColumn<>("Locked By");
		lockedCol.setCellValueFactory(new TreeItemPropertyValueFactory<RepoItemNode, String>("locked"));
		setColumnProps(lockedCol, true, false, true, 0);

		// TreeTableColumn<RepoItemNode, String> permissionCol = new TreeTableColumn<>("Permissions");
		// permissionCol.setCellValueFactory(new TreeItemPropertyValueFactory<RepoItemNode, String>("permission"));
		// setColumnProps(permissionCol, true, false, true, 0);
		//
		TreeTableColumn<RepoItemNode, String> remarkCol = new TreeTableColumn<>("Last Remark");
		remarkCol.setCellValueFactory(new TreeItemPropertyValueFactory<RepoItemNode, String>("history"));
		setColumnProps(remarkCol, true, false, true, 0);

		table.getColumns().setAll(fileCol, versionCol, statusCol, lockedCol, remarkCol);

		// Give all left over space to the last column
		double width = fileCol.widthProperty().get();
		width += versionCol.widthProperty().get();
		width += statusCol.widthProperty().get();
		width += lockedCol.widthProperty().get();
		remarkCol.prefWidthProperty().bind(table.widthProperty().subtract(width));

	}

	/**
	 * Set String column properties and set value to named field.
	 */
	private void setColumnProps(TreeTableColumn<?, ?> c, boolean visable, boolean editable, boolean sortable,
			int width) {
		c.setVisible(visable);
		c.setEditable(editable);
		c.setSortable(sortable);
		if (width > 0)
			c.setPrefWidth(width);
	}

	/**
	 * Add event listeners to passed tree table view.
	 * 
	 * @param navTreeTableView
	 */
	public void registerListeners(TreeTableView<OtmTreeTableNode> navTreeTableView) {
		navTreeTableView.getSelectionModel().selectedItemProperty()
				.addListener((v, old, newValue) -> newMemberSelectionListener(newValue));
	}

	private void newMemberSelectionListener(TreeItem<OtmTreeTableNode> item) {
		// clear();
		// if (item.getValue().getValue() instanceof OtmLibraryMember)
		// createTreeItems((OtmLibraryMember<?>) item.getValue().getValue());
		// System.out.println("Facet Table Selection Listener: " + item.getValue());
	}

	@Override
	public ImageManager getImageManager() {
		return imageMrg;
	}

}
