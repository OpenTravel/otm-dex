/**
 * 
 */
package org.opentravel.objecteditor.repository;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.model.OtmModelManager;
import org.opentravel.objecteditor.DexController;
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
public class NamespaceLibrariesTreeTableController implements DexController {
	private static Log log = LogFactory.getLog(NamespaceLibrariesTreeTableController.class);
	protected ImageManager imageMgr;

	// Injected fields
	@FXML
	protected TreeTableView<RepoItemDAO> librariesTreeTableView;
	private TreeTableView<RepoItemDAO> table;
	@FXML
	private Label permissionLabel;

	private TreeItem<RepoItemDAO> root;
	private DexController parentController;

	public NamespaceLibrariesTreeTableController() {
		log.debug("Constructing namespace libraries tree controller.");
	}

	public void initialize() {
		log.debug("Initializing namespace libraries tree controller.");

		if (librariesTreeTableView == null)
			throw new IllegalArgumentException("Namespace libraries tree table view is null.");

		// Initialize and build columns for library tree table
		root = initializeTree();
		buildColumns(librariesTreeTableView);

	}

	public void setParent(DexController parent) {
		this.parentController = parent;
		imageMgr = parent.getImageManager();
		log.debug("Parent controller for tree controller set.");

		// n.prefWidthProperty().bind(mainContent.widthProperty());
		// n.prefHeightProperty().bind(mainContent.heightProperty());
	}

	/**
	 * Create a view for the libraries described by repository items in the passed namespace.
	 * 
	 * @param nsLibraryTablePermissionField
	 */
	public NamespaceLibrariesTreeTableController(DexController parent, TreeTableView<RepoItemDAO> libTable,
			Label permissionField) {

		log.debug("Initializing repository library table view.");

		parentController = parent;

		// // Marshal and validate the parameters
		// imageMgr = parent.getImageManager();
		// if (imageMgr == null)
		// throw new IllegalStateException("Image manger is null.");

		// this.librariesTreeTableView = libTable;
		// if (libTable == null)
		// throw new IllegalStateException("Namespace libraries view is null.");
		//
		// this.permissionLabel = permissionField;
		// if (permissionField == null)
		// throw new IllegalStateException("Namespace permission field is null.");

		// // Initialize and build columns for library tree table
		// root = initializeTree();
		// buildColumns(libTable);
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
		// tree.setRowFactory((TreeTableView<NamespaceNode> p) -> new PropertyRowFactory());
		return root;
	}

	@Override
	public void clear() {
		librariesTreeTableView.getRoot().getChildren().clear();
	}

	/**
	 * Add tree items to ROOT for each Library with the same name.
	 * 
	 * @param namespace
	 * @param repository
	 * @throws RepositoryException
	 */
	public void post(Repository repository, String namespace) throws RepositoryException {
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
		permissionLabel.setText(permission);

		// Get a table of the latest of each library of any status
		HashMap<String, TreeItem<RepoItemDAO>> latestVersions = new HashMap<>();
		for (RepositoryItem ri : repository.listItems(namespace, null, true)) {
			RepoItemDAO repoItemNode = new RepoItemDAO(ri);
			TreeItem<RepoItemDAO> treeItem = new TreeItem<>(repoItemNode);
			treeItem.setExpanded(true);
			root.getChildren().add(treeItem);
			latestVersions.put(ri.getLibraryName(), treeItem);
		}

		TreeItem<RepoItemDAO> item = null;
		for (RepositoryItem rItem : repository.listItems(namespace, null, false)) {
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
	 * {@inheritDoc}
	 * <p>
	 * This exposes the library tree table's selected item.
	 */
	@Override
	public ReadOnlyObjectProperty<TreeItem<RepoItemDAO>> getSelectable() {
		return librariesTreeTableView.getSelectionModel().selectedItemProperty();
	}

	@Override
	public ImageManager getImageManager() {
		if (imageMgr == null)
			throw new IllegalStateException("Image manger is null.");
		return imageMgr;
	}

	@Override
	public void postStatus(String string) {
		parentController.postStatus(string);
	}

	@Override
	public void postProgress(double percentDone) {
		parentController.postProgress(percentDone);
	}

}
