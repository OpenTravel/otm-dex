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

import javafx.beans.property.ReadOnlyObjectProperty;
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
public class LibraryMemberTreeController implements DexController {
	private static final Logger LOGGER = LoggerFactory.getLogger(LibraryMemberTreeController.class);

	public static final String PREFIXCOLUMNLABEL = "Prefix";
	private static final String NAMECOLUMNLABEL = "Member";

	TreeTableView<LibraryMemberTreeDAO> memberTree;
	TreeItem<LibraryMemberTreeDAO> root; // Root of the navigation tree. Is displayed.
	TreeTableColumn<LibraryMemberTreeDAO, String> nameColumn; // an editable column
	ImageManager imageMgr;
	Stage stage;

	@SuppressWarnings("unchecked")
	public LibraryMemberTreeController(Stage stage, TreeTableView<LibraryMemberTreeDAO> navTreeTableView,
			OtmModelManager model) {
		System.out.println("Initializing navigation tree table.");

		if (navTreeTableView == null)
			throw new IllegalStateException("Tree table view is null.");

		// remember the stage and view, and get an image manager for the stage.
		this.stage = stage;
		this.memberTree = navTreeTableView;
		imageMgr = new ImageManager(stage);

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
		navTreeTableView.setRowFactory((TreeTableView<LibraryMemberTreeDAO> p) -> new NavRowFactory());

		// add a listener class with three parameters that invokes selection listener
		navTreeTableView.getSelectionModel().selectedItemProperty()
				.addListener((v, old, newValue) -> memberSelectionListener(newValue));

		//
		// Create columns
		//
		TreeTableColumn<LibraryMemberTreeDAO, String> prefixColumn = new TreeTableColumn<>(PREFIXCOLUMNLABEL);
		prefixColumn.setPrefWidth(100);
		prefixColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
		prefixColumn.setVisible(false); // Works - is true by default

		TreeTableColumn<LibraryMemberTreeDAO, ImageView> iconColumn = new TreeTableColumn<>("");
		iconColumn.setPrefWidth(50);
		iconColumn.setSortable(false);

		nameColumn = new TreeTableColumn<>(NAMECOLUMNLABEL);
		nameColumn.setPrefWidth(150);
		nameColumn.setEditable(true);
		nameColumn.setSortable(true);
		nameColumn.setSortType(TreeTableColumn.SortType.DESCENDING);

		// Add columns to table
		navTreeTableView.getColumns().addAll(iconColumn, nameColumn, prefixColumn);

		// Define cell content
		prefixColumn.setCellValueFactory(new TreeItemPropertyValueFactory<LibraryMemberTreeDAO, String>("prefix"));

		iconColumn.setCellValueFactory((CellDataFeatures<LibraryMemberTreeDAO, ImageView> p) -> {
			if (p.getValue() != null)
				p.getValue().setGraphic(p.getValue().getValue().getIcon(imageMgr));
			return null;
		});

		nameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<LibraryMemberTreeDAO, String>("name"));
		nameColumn.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());

		// create cells for members
		for (OtmLibraryMember<?> member : model.getMembers()) {
			createTreeItem(member, root);
		}

		navTreeTableView.getSelectionModel().select(0);
	}

	/**
	 * Set the name value of this object.
	 * 
	 * @param event
	 */
	public void handleNameCellEdit(TreeTableColumn.CellEditEvent<LibraryMemberTreeDAO, String> event) {
		// NPE happens if position can't be read
		// if (event.getTreeTablePosition() != null) {
		// TreeItem<LibraryMemberTreeDAO> currentItem = event.getRowValue();
		// currentItem.getValue().setName(event.getNewValue());
		// } else
		// System.out.println("ERROR - cell edit handler has null tree table position.");
	}

	/**
	 * Listener for selected library members.
	 * 
	 * @param item
	 */
	private void memberSelectionListener(TreeItem<LibraryMemberTreeDAO> item) {
		System.out.println("Selection Listener: " + item.getValue());
		assert item != null;
		boolean editable = false;
		if (item.getValue() != null)
			editable = item.getValue().isEditable();
		nameColumn.setEditable(editable);
	}

	public TreeItem<LibraryMemberTreeDAO> getRoot() {
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
	private TreeItem<LibraryMemberTreeDAO> createTreeItem(OtmLibraryMember<?> member,
			TreeItem<LibraryMemberTreeDAO> parent) {
		LibraryMemberTreeDAO tn = new LibraryMemberTreeDAO(member);
		TreeItem<LibraryMemberTreeDAO> item = new TreeItem<>(tn);
		item.setExpanded(false);
		parent.getChildren().add(item);

		// Post the children as children of this item
		for (OtmModelElement<?> ele : member.getChildren()) {
			// TODO - this should test for TypeProvider not facet
			if (ele instanceof OtmFacet<?>) {
				LibraryMemberTreeDAO tnF = new LibraryMemberTreeDAO((OtmFacet<?>) ele);
				TreeItem<LibraryMemberTreeDAO> itemF = new TreeItem<>(tnF);
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

	private final class NavRowFactory extends TreeTableRow<LibraryMemberTreeDAO> {
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
			TreeItem<LibraryMemberTreeDAO> item = createTreeItem(new OtmCoreObject("new"), getTreeItem().getParent());
			super.updateTreeItem(item); // needed to apply stylesheet to new item
		}

		/**
		 * @param tc
		 * @param newTreeItem
		 * @return
		 * @return
		 */
		// TODO - use style class for warning and error
		private void setCSSClass(TreeTableRow<LibraryMemberTreeDAO> tc, TreeItem<LibraryMemberTreeDAO> newTreeItem) {
			if (newTreeItem != null) {
				tc.pseudoClassStateChanged(EDITABLE, newTreeItem.getValue().isEditable());
			}
		}
		// TODO - investigate using ControlsFX for decoration
		// TODO - Dragboard db = r.startDragAndDrop(TransferMode.MOVE);
		// https://www.programcreek.com/java-api-examples/index.php?api=javafx.scene.control.TreeTableRow
	}

	/**
	 * {@inheritDoc} Remove all items from the member tree.
	 */
	@Override
	public void clear() {
		memberTree.getRoot().getChildren().clear();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns the member tree selected item property.
	 */
	@Override
	public ReadOnlyObjectProperty<TreeItem<LibraryMemberTreeDAO>> getSelectable() {
		return memberTree.getSelectionModel().selectedItemProperty();
	}

	@Override
	public ImageManager getImageManager() {
		if (imageMgr == null)
			throw new IllegalStateException("Image manger is null.");
		return imageMgr;
	}
}
