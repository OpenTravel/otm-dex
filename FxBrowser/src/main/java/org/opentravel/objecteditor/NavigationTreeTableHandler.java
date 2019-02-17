/**
 * 
 */
package org.opentravel.objecteditor;

import org.opentravel.model.OtmModelElement;
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.facetNodes.OtmFacet;
import org.opentravel.model.objectNodes.OtmCoreObject;
import org.opentravel.model.objectNodes.OtmLibraryMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Manage the node navigation tree.
 * 
 * @author dmh
 *
 */
@SuppressWarnings("restriction")
public class NavigationTreeTableHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(NavigationTreeTableHandler.class);

	public static final String PREFIXCOLUMNLABEL = "Prefix";
	private static final String NAMECOLUMNLABEL = "Member";

	/**
	 * The type of the TreeItem instances used in this TreeTableView. Simple POJO that contains and provides gui access
	 * to the OTM model object.
	 * 
	 * @author dmh
	 *
	 */
	public class OtmTreeTableNode {
		protected OtmModelElement<?> otmObject;

		public OtmTreeTableNode(OtmLibraryMember<?> member) {
			this.otmObject = member;
		}

		public OtmTreeTableNode(OtmFacet<?> facet) {
			this.otmObject = facet;
		}

		public String getPrefix() {
			if (otmObject instanceof OtmLibraryMember<?>)
				return otmObject.getClass().getSimpleName();
			return "";
		}

		public StringProperty nameProperty() {
			return new SimpleStringProperty(otmObject.getName());
		}

		public ImageView getIcon() {
			return images.getView(otmObject.getIconType());
		}

		public String getName() {
			return otmObject.getName();
		}

		public boolean isEditable() {
			return otmObject.isEditable();
		}

		public void setName(String name) {
			if (otmObject instanceof OtmLibraryMember<?>)
				((OtmLibraryMember<?>) otmObject).setName(name);
		}

		@Override
		public String toString() {
			return otmObject.toString();
		}

		/**
		 * @return
		 */
		public OtmModelElement<?> getValue() {
			return otmObject;
		}

	}

	TreeTableView<OtmTreeTableNode> navTreeTableView;
	// TreeTableColumn<OtmTreeTableNode, String> nameColumn;
	TreeItem<OtmTreeTableNode> root; // Root of the navigation tree. Is displayed.
	ImageManager images;
	Stage stage;

	@SuppressWarnings("unchecked")
	public NavigationTreeTableHandler(Stage stage, TreeTableView<OtmTreeTableNode> navTreeTableView,
			OtmModelManager model) {
		System.out.println("Initializing navigation tree table.");

		if (navTreeTableView == null)
			throw new IllegalStateException("Tree table view is null.");

		// remember the stage and view, and get an image manager for the stage.
		this.stage = stage;
		this.navTreeTableView = navTreeTableView;
		images = new ImageManager(stage);

		// Set the hidden root item
		root = new TreeItem<>();
		root.setExpanded(true); // Startout fully expanded

		// Set up the TreeTable
		navTreeTableView.setRoot(getRoot());
		navTreeTableView.setShowRoot(false);
		navTreeTableView.setEditable(true);
		navTreeTableView.getSelectionModel().setCellSelectionEnabled(true); // allow individual cells to be edited
		navTreeTableView.setTableMenuButtonVisible(true); // allow users to select columns

		// Enable context menus at the row level and add change listener for for applying style
		navTreeTableView.setRowFactory((TreeTableView<OtmTreeTableNode> p) -> new NavRowFactory());

		// add a listener class with three parameters that invokes selection listener
		navTreeTableView.getSelectionModel().selectedItemProperty()
				.addListener((v, old, newValue) -> selectionListener(newValue));

		//
		// Create columns
		//
		TreeTableColumn<OtmTreeTableNode, String> prefixColumn = new TreeTableColumn<>(PREFIXCOLUMNLABEL);
		prefixColumn.setPrefWidth(100);
		prefixColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
		prefixColumn.setVisible(false); // Works - is true by default

		TreeTableColumn<OtmTreeTableNode, ImageView> iconColumn = new TreeTableColumn<>("");
		iconColumn.setPrefWidth(50);
		iconColumn.setSortable(false);

		TreeTableColumn<OtmTreeTableNode, String> nameColumn = new TreeTableColumn<>(NAMECOLUMNLABEL);
		// nameColumn = new TreeTableColumn<>(NAMECOLUMNLABEL);
		nameColumn.setPrefWidth(150);
		nameColumn.setEditable(true);
		nameColumn.setSortable(true);
		nameColumn.setSortType(TreeTableColumn.SortType.DESCENDING);

		// Add columns to table
		navTreeTableView.getColumns().addAll(iconColumn, nameColumn, prefixColumn);

		// TODO - figure out how to preset the sort order
		// ObservableList<TreeTableColumn<OtmTreeTableNode, ?>> order = navTreeTableView.getSortOrder();
		// order.add(iconColumn);
		// order.add(nameColumn);
		// navTreeTableView.getColumns().add(prefixColumn);
		// TODO - change icon column to sort based on node type - need wrapper on icon
		// prefixColumn.setComparator((p, q) -> {
		// return p.compareTo(q);
		// });

		// Define cell content
		prefixColumn.setCellValueFactory(new TreeItemPropertyValueFactory<OtmTreeTableNode, String>("prefix"));

		iconColumn.setCellValueFactory((CellDataFeatures<OtmTreeTableNode, ImageView> p) -> {
			if (p.getValue() != null)
				p.getValue().setGraphic(p.getValue().getValue().getIcon());
			return null;
		});

		nameColumn.setCellValueFactory((CellDataFeatures<OtmTreeTableNode, String> p) -> {
			if (p.getValue() != null)
				return p.getValue().getValue().nameProperty();
			return null;
		});

		// Make column editable - set the factory and commit function.
		//
		// Note - can use lots of controls, not just text field. Try ChoiceBox.
		nameColumn.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		// // TODO - use only if can stop edit - nameColumn.setOnEditStart(this::handleNameCellEditStart);
		nameColumn.setOnEditCommit(this::handleNameCellEdit);
		// method reference is the same as this lambda: nameColumn.setOnEditCommit(e -> handleNameCellEdit(e));

		// create cells for members
		for (OtmLibraryMember<?> member : model.getMembers()) {
			createTreeItem(member, root);
		}

		navTreeTableView.getSelectionModel().select(0);
	}

	// TODO - try extending TextFieldTreeTableCell to call cancel on start if not editable.
	// Will require using something but string for item
	//

	// TODO - prevent or stop edit for non-editable rows
	public void handleNameCellEditStart(TreeTableColumn.CellEditEvent<OtmTreeTableNode, String> event) {
		if (event.getTreeTablePosition() != null) {
			// EventTarget cv = event.getTarget();
			// TreeTablePosition<OtmTreeTableNode, String> cc = event.getTreeTablePosition();
			// EventHandler<CellEditEvent<OtmTreeTableNode, String>> cy = event.getTableColumn().getOnEditCancel();
			// TreeTableColumn src = (TreeTableColumn) event.getSource();
			// int row = event.getTreeTablePosition().getRow();

			TreeItem<OtmTreeTableNode> rv = event.getRowValue();
			OtmTreeTableNode currentItem = rv.getValue();
			if (!currentItem.isEditable())
				System.out.println("How to stop edit?");
		} else
			System.out.println("ERROR - cell edit handler start has null tree table position.");

	}

	/**
	 * Set the name value of this object.
	 * 
	 * @param event
	 */
	public void handleNameCellEdit(TreeTableColumn.CellEditEvent<OtmTreeTableNode, String> event) {
		if (event.getTreeTablePosition() != null) {
			TreeItem<OtmTreeTableNode> currentItem = event.getRowValue();
			currentItem.getValue().setName(event.getNewValue());
			// TODO - make sure it is editable.
			// TreeTablePosition<OtmTreeTableNode, String> p = event.getTreeTablePosition();
			// int row = event.getTreeTablePosition().getRow();
			// TreeItem<OtmTreeTableNode> currentItem = navTreeTableView.getTreeItem(row);
			// TreeItem<OtmTreeTableNode> currentItem =
			// navTreeTableView.getTreeItem(event.getTreeTablePosition().getRow());
			// TreeTableColumn<OtmTreeTableNode, String> col = event.getTableColumn();
			// OtmTreeTableNode otmTTNode;
			// String ov = event.getOldValue();
			// String nv = event.getNewValue();
		} else
			System.out.println("ERROR - cell edit handler has null tree table position.");
	}

	/**
	 * Listener for changes to tree items.
	 * 
	 * @param item
	 */
	private void selectionListener(TreeItem<OtmTreeTableNode> item) {
		System.out.println("Selection Listener: " + item.getValue());
	}

	public TreeItem<OtmTreeTableNode> getRoot() {
		return root;
	}

	/**
	 * TreeItem class does not extend the Node class.
	 * 
	 * Therefore, you cannot apply any visual effects or add menus to the tree items. Use the cell factory mechanism to
	 * overcome this obstacle and define as much custom behavior for the tree items as your application requires.
	 * 
	 * @param item
	 * @return
	 */
	private TreeItem<OtmTreeTableNode> createTreeItem(OtmLibraryMember<?> member, TreeItem<OtmTreeTableNode> parent) {
		OtmTreeTableNode tn = new OtmTreeTableNode(member);
		TreeItem<OtmTreeTableNode> item = new TreeItem<>(tn);
		item.setExpanded(false);
		parent.getChildren().add(item);

		// Post the children as children of this item
		for (OtmModelElement<?> ele : member.getChildren()) {
			// TODO - this should test for TypeProvider not facet
			if (ele instanceof OtmFacet<?>) {
				OtmTreeTableNode tnF = new OtmTreeTableNode((OtmFacet<?>) ele);
				TreeItem<OtmTreeTableNode> itemF = new TreeItem<>(tnF);
				itemF.setExpanded(false);
				item.getChildren().add(itemF);
			}
		}
		return item;
	}

	/**
	 * TreeTableRow is an IndexedCell, but rarely needs to be used by developers creating TreeTableView instances. The
	 * only time TreeTableRow is likely to be encountered at all by a developer is if they wish to create a custom
	 * rowFactory that replaces an entire row of a TreeTableView.
	 * 
	 * https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/TreeTableRow.html
	 */
	private static final PseudoClass EDITABLE = PseudoClass.getPseudoClass("editable");

	private final class NavRowFactory extends TreeTableRow<OtmTreeTableNode> {
		private final ContextMenu addMenu = new ContextMenu();

		public NavRowFactory() {
			// Create Context menu
			MenuItem addObject = new MenuItem("Add Object");
			addMenu.getItems().add(addObject);
			setContextMenu(addMenu);

			// Create action for addObject event
			addObject.setOnAction(this::addMemberEvent);

			// Set style listener (css class)
			treeItemProperty().addListener((obs, oldTreeItem, newTreeItem) -> setCSSClass(this, newTreeItem));

			// Not sure this helps!
			if (getTreeItem() != null && getTreeItem().getValue() != null) {
				setEditable(getTreeItem().getValue().isEditable());
			}
		}

		/**
		 * Add a new member to the tree
		 * 
		 * @param t
		 */
		private void addMemberEvent(ActionEvent t) {
			TreeItem<OtmTreeTableNode> item = createTreeItem(new OtmCoreObject("new"), getTreeItem().getParent());
			super.updateTreeItem(item); // needed to apply stylesheet to new item
		}

		/**
		 * @param tc
		 * @param newTreeItem
		 * @return
		 * @return
		 */
		// TODO - use style class for warning and error
		private void setCSSClass(TreeTableRow<OtmTreeTableNode> tc, TreeItem<OtmTreeTableNode> newTreeItem) {
			if (newTreeItem != null) {
				tc.pseudoClassStateChanged(EDITABLE, newTreeItem.getValue().isEditable());
				tc.setEditable(newTreeItem.getValue().isEditable());
				// if (newTreeItem.getValue().isEditable())
				// System.out.println(newTreeItem.getValue().getName() + " set editable." + tc.isEditable());
				// else
				// System.out.println(newTreeItem.getValue().getName() + " set NOT editable." + tc.isEditable());
			}
		}
		// TODO - investigate using ControlsFX for decoration
		// TODO - Dragboard db = r.startDragAndDrop(TransferMode.MOVE);
		// https://www.programcreek.com/java-api-examples/index.php?api=javafx.scene.control.TreeTableRow

		// startEdit, commitEdit, cancelEdit do not run on row

		// Runs often, but no access to cells in the row to act upon them
		// @Override
		// public void updateItem(OtmTreeTableNode item, boolean empty) {
		// super.updateItem(item, empty);
		// }
	}

}
