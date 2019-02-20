/**
 * 
 */
package org.opentravel.objecteditor;

import org.opentravel.model.OtmModelManager;
import org.opentravel.objecteditor.NamespaceLibrariesTableController.RepoItemNode;
import org.opentravel.schemacompiler.repository.RepositoryException;
import org.opentravel.schemacompiler.repository.RepositoryItemCommit;
import org.opentravel.schemacompiler.repository.RepositoryItemHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.PropertyValueFactory;

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
 * Controller for a library history table. Creates table containing library history properties.
 * 
 * @author dmh
 *
 */
@SuppressWarnings("restriction")
public class LibraryHistoryController implements DexController {
	private static final Logger LOGGER = LoggerFactory.getLogger(LibraryHistoryController.class);

	public class CommitNode {
		private RepositoryItemCommit item;

		// private int commitNumber;
		// private Date effectiveOn;
		// private String user;
		// private String remarks;

		public CommitNode(RepositoryItemCommit item) {
			this.item = item;
		}

		public StringProperty numberProperty() {
			return new SimpleStringProperty(Integer.toString(item.getCommitNumber()));
		}

		public StringProperty effectiveProperty() {
			return new SimpleStringProperty(item.getEffectiveOn().toString());
		}

		public StringProperty userProperty() {
			return new SimpleStringProperty(item.getUser());
		}

		public StringProperty remarksProperty() {
			return new SimpleStringProperty(item.getRemarks());
		}
	}

	private TableView<CommitNode> historyTable;
	private ObservableList<CommitNode> commitList = FXCollections.observableArrayList();

	/**
	 * Create a view for the libraries described by repository items in the passed namespace.
	 * 
	 * @param nsLibraryTablePermissionField
	 */
	public LibraryHistoryController(DexController parent, TableView<CommitNode> table) {

		System.out.println("Initializing repository library table view.");

		this.historyTable = table;
		if (historyTable == null)
			throw new IllegalStateException("Library History Table view is null.");

		// Initialize and build columns for library tree table
		// root = initializeTree();
		buildColumns(historyTable);

		// Have table listen to list.
		historyTable.setItems(commitList);
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

	// private TreeItem<RepoItemNode> initializeTree() {
	// // Set the hidden root item
	// root = new TreeItem<>();
	// root.setExpanded(true); // Startout fully expanded
	// // Set up the TreeTable
	// libTable.setRoot(root);
	// libTable.setShowRoot(false);
	// libTable.setEditable(false);
	//
	// // tree.getSelectionModel().setCellSelectionEnabled(true); // allow individual cells to be edited
	// // tree.setTableMenuButtonVisible(true); // allow users to select columns
	// // Enable context menus at the row level and add change listener for for applying style
	// // tree.setRowFactory((TreeTableView<NamespaceNode> p) -> new PropertyRowFactory());
	// return root;
	// }

	@Override
	public void clear() {
		historyTable.getItems().clear();
	}

	/**
	 * Add tree items to ROOT for each Library with the same name.
	 * 
	 * @param namespace
	 * @param repository
	 * @throws RepositoryException
	 */
	public void post(RepoItemNode repoItem) {
		clear();

		if (repoItem == null)
			throw new IllegalArgumentException("Missing repo item.");
		RepositoryItemHistory history = repoItem.getHistory();
		if (history == null) {
			System.out.println("OOPS - need to wait for history to be retrieved.");
			return; // FIXME
		}
		for (RepositoryItemCommit cItem : history.getCommitHistory()) {
			commitList.add(new CommitNode(cItem));
		}
	}

	/**
	 * Create Columns and set cell values
	 */
	private void buildColumns(TableView<CommitNode> table) {
		TableColumn<CommitNode, String> numCol = new TableColumn<>("Number");
		numCol.setCellValueFactory(new PropertyValueFactory<CommitNode, String>("number"));
		setColumnProps(numCol, true, false, true, 0);

		TableColumn<CommitNode, String> dateCol = new TableColumn<>("Date");
		dateCol.setCellValueFactory(new PropertyValueFactory<CommitNode, String>("effective"));
		setColumnProps(dateCol, true, false, true, 0);

		TableColumn<CommitNode, String> userCol = new TableColumn<>("User");
		userCol.setCellValueFactory(new PropertyValueFactory<CommitNode, String>("user"));
		setColumnProps(userCol, true, false, true, 0);

		TableColumn<CommitNode, String> remarksCol = new TableColumn<>("Remarks");
		remarksCol.setCellValueFactory(new PropertyValueFactory<CommitNode, String>("remarks"));
		setColumnProps(remarksCol, true, false, true, 0);

		table.getColumns().setAll(numCol, dateCol, userCol, remarksCol);

		// Give all left over space to the last column
		double width = numCol.widthProperty().get();
		width += dateCol.widthProperty().get();
		width += userCol.widthProperty().get();
		remarksCol.prefWidthProperty().bind(table.widthProperty().subtract(width));

	}

	/**
	 * Set String column properties and set value to named field.
	 */
	private void setColumnProps(TableColumn<?, ?> c, boolean visable, boolean editable, boolean sortable, int width) {
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
	public ReadOnlyObjectProperty<TreeItem<CommitNode>> getSelectable() {
		// return historyTable.getItems();
		return null;
	}

	@Override
	public ImageManager getImageManager() {
		// if (imageMgr == null)
		// throw new IllegalStateException("Image manger is null.");
		return null;
	}

}
