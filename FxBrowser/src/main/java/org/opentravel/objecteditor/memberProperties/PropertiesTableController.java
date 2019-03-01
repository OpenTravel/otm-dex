/**
 * 
 */
package org.opentravel.objecteditor.memberProperties;

import org.opentravel.common.DexIntegerStringConverter;
import org.opentravel.common.ImageManager;
import org.opentravel.model.OtmModelElement;
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.otmFacets.OtmFacet;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;
import org.opentravel.objecteditor.DexController;
import org.opentravel.objecteditor.ObjectEditorController;
import org.opentravel.objecteditor.modelMembers.MemberDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.ChoiceBoxTreeTableCell;
import javafx.scene.control.cell.ComboBoxTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

//import javafx.util.converter.IntegerStringConverter;
//javafx.beans.property.SimpleBooleanProperty
// import javafx.beans.property.ReadOnlyStringWrapper;
//javafx.beans.property.ReadOnlyBooleanWrapper
//javafx.beans.property.SimpleintegerProperty
//javafx.beans.property.ReadOnlyintegerWrapper
//javafx.beans.property.SimpleDoubleProperty
//javafx.beans.property.ReadOnlyDoubleWrapper
//javafx.beans.property.SimpleStringProperty
//javafx.beans.property.ReadOnlyStringWrapper

/**
 * Manage a facets and properties in a tree table.
 * 
 * @author dmh
 *
 */
@SuppressWarnings("restriction")
public class PropertiesTableController implements DexController {
	private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesTableController.class);

	protected ImageManager imageMgr;
	protected TreeTableView<PropertiesDAO> table;
	protected TreeItem<PropertiesDAO> root;
	protected ObjectEditorController parent;

	protected TreeTableColumn<PropertiesDAO, String> nameCol;
	// protected TreeTableColumn<PropertyNode, ImageView> iconCol;
	protected TreeTableColumn<PropertiesDAO, String> roleCol;
	protected TreeTableColumn<PropertiesDAO, String> typeCol;
	protected TreeTableColumn<PropertiesDAO, String> minCol;
	protected TreeTableColumn<PropertiesDAO, Integer> maxCol;
	protected TreeTableColumn<PropertiesDAO, String> exampleCol;
	protected TreeTableColumn<PropertiesDAO, String> descCol;
	protected TreeTableColumn<PropertiesDAO, String> deprecatedCol;
	protected TreeTableColumn<PropertiesDAO, String> otherDocCol;

	/**
	 * Create a facet and property treeTable with manager.
	 * 
	 * @param member
	 * @param table
	 * @param stage
	 */
	public PropertiesTableController(OtmLibraryMember<?> member, TreeTableView<PropertiesDAO> table,
			ObjectEditorController parent) {
		System.out.println("Initializing property table for " + member + "member.");

		if (parent == null)
			throw new IllegalArgumentException("Missing parent contoller.");
		this.parent = parent;
		imageMgr = parent.getImageManager();

		if (table == null)
			throw new IllegalStateException("Tree view is null.");
		this.table = table;

		// table.setEventDispatcher(new MyEventDispatcher());
		table.getSelectionModel().selectedItemProperty()
				.addListener((v, old, newValue) -> propertySelectionListener(newValue));

		// Layout the table
		root = initializeTable(table);

		// Add data items
		if (member != null)
			createTreeItems(member);
	}

	/**
	 * Add tree items to ROOT for each child and grandchild of the member.
	 * 
	 * @param member
	 */
	private void createTreeItems(OtmLibraryMember<?> member) {
		// create cells for member's facets and properties
		for (OtmModelElement<?> element : member.getChildren()) {
			TreeItem<PropertiesDAO> item = createTreeItem(element, root);
			item.setExpanded(true);
			if (element instanceof OtmFacet) {
				for (OtmModelElement<?> child : ((OtmFacet<?>) element).getChildren())
					createTreeItem(child, item);
			}
		}
	}

	public void refresh() {
		table.refresh();
	}

	protected TreeItem<PropertiesDAO> createTreeItem(OtmModelElement<?> element, TreeItem<PropertiesDAO> parent) {
		TreeItem<PropertiesDAO> item = new TreeItem<>(new PropertiesDAO(element, this));
		item.setExpanded(false);
		parent.getChildren().add(item);
		item.setGraphic(imageMgr.getView(element));
		return item;
	}

	private TreeItem<PropertiesDAO> initializeTable(TreeTableView<PropertiesDAO> table) {
		// Set the hidden root item
		TreeItem<PropertiesDAO> root = new TreeItem<>();
		root.setExpanded(true); // Startout fully expanded
		// Set up the TreeTable
		table.setRoot(root);
		table.setShowRoot(false);
		table.setEditable(true);
		table.getSelectionModel().setCellSelectionEnabled(true); // allow individual cells to be edited
		table.setTableMenuButtonVisible(true); // allow users to select columns

		// Enable context menus at the row level and add change listener for for applying style
		table.setRowFactory((TreeTableView<PropertiesDAO> p) -> new PropertiesRowFactory(this));

		// Define Columns and cell content providers
		buildColumns(table);

		return root;
	}

	public void select(OtmModelElement<?> otm) {
		System.out.println("TODO - select " + otm);
		if (otm != null) {
			if (!(otm instanceof OtmLibraryMember))
				otm = otm.getOwningMember();
			select(otm.getName());
		}
	}

	public void select(String name) {
		System.out.println("TODO - select " + name);
		parent.select(name);
	}

	/**
	 * Remove all items from the table
	 */
	@Override
	public void clear() {
		table.getRoot().getChildren().clear();
	}

	/**
	 * Create Columns and set cell values
	 */
	private void buildColumns(TreeTableView<PropertiesDAO> table) {
		nameCol = new TreeTableColumn<>("Name");
		// iconCol = new TreeTableColumn<>("");
		roleCol = new TreeTableColumn<>("Role");
		typeCol = new TreeTableColumn<>("Assigned Type");

		TreeTableColumn<PropertiesDAO, String> documentationCol = new TreeTableColumn<>("Documentation");
		descCol = new TreeTableColumn<>("Description");
		deprecatedCol = new TreeTableColumn<>("Deprecation");
		otherDocCol = new TreeTableColumn<>("Other");
		documentationCol.getColumns().addAll(descCol, deprecatedCol, otherDocCol);
		setColumnProps(descCol, true, true, false, 0);
		setColumnProps(deprecatedCol, false, false, false, 0);
		setColumnProps(otherDocCol, false, false, false, 0);

		TreeTableColumn<PropertiesDAO, String> constraintCol = new TreeTableColumn<>("Repeat");
		minCol = new TreeTableColumn<>("min");
		maxCol = new TreeTableColumn<>("max");
		constraintCol.getColumns().addAll(minCol, maxCol);

		exampleCol = new TreeTableColumn<>("Example");
		setColumnProps(exampleCol, false, false, false, 0);
		table.getColumns().addAll(nameCol, roleCol, typeCol, constraintCol, exampleCol, documentationCol);

		// nameCol.setStyle("-fx-alignment: CENTER-RIGHT;");

		// Define cell content management
		// 1. Format column
		// 2. set the cell value factory
		// 3. set the cell factory
		//

		// Name Column
		setColumnProps(nameCol, true, true, false, 200, "name");

		// Assigned type column
		setColumnProps(typeCol, true, true, false, 150);
		typeCol.setCellValueFactory(new TreeItemPropertyValueFactory<PropertiesDAO, String>("assignedType"));
		typeCol.setCellFactory(ComboBoxTreeTableCell.forTreeTableColumn(PropertiesDAO.getAssignedTypeList()));

		// Role Column
		setColumnProps(roleCol, true, true, false, 100);
		roleCol.setCellValueFactory(new TreeItemPropertyValueFactory<PropertiesDAO, String>("role"));
		roleCol.setCellFactory(ChoiceBoxTreeTableCell.forTreeTableColumn(PropertiesDAO.getRoleList()));

		// Min Column
		setColumnProps(minCol, true, true, false, 75);
		minCol.setCellValueFactory(new TreeItemPropertyValueFactory<PropertiesDAO, String>("min"));
		minCol.setCellFactory(ChoiceBoxTreeTableCell.forTreeTableColumn(PropertiesDAO.minList()));

		// Maximum Column
		setColumnProps(minCol, true, true, false, 0);
		maxCol.setCellValueFactory(new TreeItemPropertyValueFactory<PropertiesDAO, Integer>("max"));
		maxCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(new DexIntegerStringConverter()));

		// Description Column
		setColumnProps(descCol, true, true, false, 150, "description");
		// Deprecation Column
		setColumnProps(deprecatedCol, true, true, false, 50, "deprecation");
		// Example Column
		setColumnProps(exampleCol, true, true, false, 0, "example");
	}

	/**
	 * Set String column properties and set value to named field.
	 */
	private void setColumnProps(TreeTableColumn<PropertiesDAO, String> c, boolean visable, boolean editable,
			boolean sortable, int width, String field) {
		setColumnProps(c, visable, editable, sortable, width);
		c.setCellValueFactory(new TreeItemPropertyValueFactory<PropertiesDAO, String>(field));
		c.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
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
	public void registerListeners(TreeTableView<MemberDAO> navTreeTableView) {
		navTreeTableView.getSelectionModel().selectedItemProperty()
				.addListener((v, old, newValue) -> newMemberSelectionListener(newValue));
	}

	private void newMemberSelectionListener(TreeItem<MemberDAO> item) {
		clear();
		if (item == null || item.getValue() == null || item.getValue().getValue() == null)
			return;
		if (item.getValue().getValue() instanceof OtmLibraryMember)
			createTreeItems((OtmLibraryMember<?>) item.getValue().getValue());
		System.out.println("Facet Table Selection Listener: " + item.getValue());
	}

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
	private void propertySelectionListener(TreeItem<PropertiesDAO> item) {
		if (item == null || item.getValue() == null)
			return;
		// TODO - set name editable IFF ...
		nameCol.setEditable(item.getValue().isEditable());
		roleCol.setEditable(item.getValue().isEditable());
		typeCol.setEditable(item.getValue().isEditable());
		minCol.setEditable(item.getValue().isEditable());
		maxCol.setEditable(item.getValue().isEditable());
		// TODO - set example editable IFF ...
		exampleCol.setEditable(item.getValue().isEditable());
		descCol.setEditable(item.getValue().isEditable());
		deprecatedCol.setEditable(item.getValue().isEditable());
	}

	public void handleMaxEdit(TreeTableColumn.CellEditEvent<PropertiesDAO, String> event) {
		if (event != null && event.getTreeTablePosition() != null) {
			TreeItem<PropertiesDAO> currentItem = event.getRowValue();
			if (currentItem != null)
				currentItem.getValue().setMax(event.getNewValue());
		} else
			System.out.println("ERROR - cell max edit handler has null.");
	}

	@Override
	public ReadOnlyObjectProperty<TreeItem<?>> getSelectable() {
		return null;
	}

	@Override
	public ImageManager getImageManager() {
		return imageMgr;
	}

	@Override
	public OtmModelManager getModelManager() {
		return null;
	}

}
