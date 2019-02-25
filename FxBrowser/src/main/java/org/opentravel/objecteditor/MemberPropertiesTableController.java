/**
 * 
 */
package org.opentravel.objecteditor;

import org.opentravel.common.DexIntegerStringConverter;
import org.opentravel.common.ImageManager;
import org.opentravel.model.OtmModelElement;
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.otmFacets.OtmFacet;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.ChoiceBoxTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.stage.Stage;

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
public class MemberPropertiesTableController implements DexController {
	private static final Logger LOGGER = LoggerFactory.getLogger(MemberPropertiesTableController.class);

	protected ImageManager imageMgr;
	protected TreeTableView<MemberPropertiesTableDAO> table;
	protected TreeItem<MemberPropertiesTableDAO> root;

	protected TreeTableColumn<MemberPropertiesTableDAO, String> nameCol;
	// protected TreeTableColumn<PropertyNode, ImageView> iconCol;
	protected TreeTableColumn<MemberPropertiesTableDAO, String> roleCol;
	protected TreeTableColumn<MemberPropertiesTableDAO, String> typeCol;
	protected TreeTableColumn<MemberPropertiesTableDAO, String> minCol;
	protected TreeTableColumn<MemberPropertiesTableDAO, Integer> maxCol;
	protected TreeTableColumn<MemberPropertiesTableDAO, String> exampleCol;
	protected TreeTableColumn<MemberPropertiesTableDAO, String> descCol;
	protected TreeTableColumn<MemberPropertiesTableDAO, String> deprecatedCol;
	protected TreeTableColumn<MemberPropertiesTableDAO, String> otherDocCol;

	/**
	 * Create a facet and property treeTable with manager.
	 * 
	 * @param member
	 * @param table
	 * @param stage
	 */
	public MemberPropertiesTableController(OtmLibraryMember<?> member, TreeTableView<MemberPropertiesTableDAO> table,
			Stage stage) {
		System.out.println("Initializing property table for " + member + "member.");

		if (stage == null)
			throw new IllegalStateException("Stage is null.");
		imageMgr = new ImageManager(stage);

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
			TreeItem<MemberPropertiesTableDAO> item = createTreeItem(element, root);
			item.setExpanded(true);
			if (element instanceof OtmFacet) {
				for (OtmModelElement<?> child : ((OtmFacet<?>) element).getChildren())
					createTreeItem(child, item);
			}
		}
	}

	private TreeItem<MemberPropertiesTableDAO> createTreeItem(OtmModelElement<?> element,
			TreeItem<MemberPropertiesTableDAO> parent) {
		TreeItem<MemberPropertiesTableDAO> item = new TreeItem<>(new MemberPropertiesTableDAO(element));
		item.setExpanded(false);
		parent.getChildren().add(item);
		item.setGraphic(imageMgr.getView(element));
		return item;
	}

	private TreeItem<MemberPropertiesTableDAO> initializeTable(TreeTableView<MemberPropertiesTableDAO> table) {
		// Set the hidden root item
		TreeItem<MemberPropertiesTableDAO> root = new TreeItem<>();
		root.setExpanded(true); // Startout fully expanded
		// Set up the TreeTable
		table.setRoot(root);
		table.setShowRoot(false);
		table.setEditable(true);
		table.getSelectionModel().setCellSelectionEnabled(true); // allow individual cells to be edited
		table.setTableMenuButtonVisible(true); // allow users to select columns

		// Enable context menus at the row level and add change listener for for applying style
		table.setRowFactory((TreeTableView<MemberPropertiesTableDAO> p) -> new MemberPropertiesTableRowFactory());

		// Define Columns and cell content providers
		buildColumns(table);

		return root;
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
	private void buildColumns(TreeTableView<MemberPropertiesTableDAO> table) {
		nameCol = new TreeTableColumn<>("Name");
		// iconCol = new TreeTableColumn<>("");
		roleCol = new TreeTableColumn<>("Role");
		typeCol = new TreeTableColumn<>("Assigned Type");

		TreeTableColumn<MemberPropertiesTableDAO, String> documentationCol = new TreeTableColumn<>("Documentation");
		descCol = new TreeTableColumn<>("Description");
		deprecatedCol = new TreeTableColumn<>("Deprecation");
		otherDocCol = new TreeTableColumn<>("Other");
		documentationCol.getColumns().addAll(descCol, deprecatedCol, otherDocCol);
		setColumnProps(descCol, true, true, false, 0);
		setColumnProps(deprecatedCol, false, false, false, 0);
		setColumnProps(otherDocCol, false, false, false, 0);

		TreeTableColumn<MemberPropertiesTableDAO, String> constraintCol = new TreeTableColumn<>("Repeat");
		minCol = new TreeTableColumn<>("min");
		maxCol = new TreeTableColumn<>("max");
		constraintCol.getColumns().addAll(minCol, maxCol);

		exampleCol = new TreeTableColumn<>("Example");
		setColumnProps(exampleCol, false, false, false, 0);
		table.getColumns().addAll(nameCol, roleCol, typeCol, constraintCol, documentationCol, exampleCol);

		// nameCol.setStyle("-fx-alignment: CENTER-RIGHT;");

		// Define cell content management
		// 1. Format column
		// 2. set the cell value factory
		// 3. set the cell factory
		//

		// Name Column
		setColumnProps(nameCol, true, true, false, 200, "name");

		setColumnProps(typeCol, true, true, false, 0);
		typeCol.setCellValueFactory(new TreeItemPropertyValueFactory<MemberPropertiesTableDAO, String>("assignedType"));
		// typeCol.setCellFactory(ChoiceBoxTreeTableCell.forTreeTableColumn(getRoleList()));

		// Role Column
		setColumnProps(roleCol, true, true, false, 0);
		minCol.setCellValueFactory(new TreeItemPropertyValueFactory<MemberPropertiesTableDAO, String>("role"));
		minCol.setCellFactory(ChoiceBoxTreeTableCell.forTreeTableColumn(MemberPropertiesTableDAO.getRoleList()));

		// Min Column
		setColumnProps(minCol, true, true, false, 0);
		minCol.setCellValueFactory(new TreeItemPropertyValueFactory<MemberPropertiesTableDAO, String>("min"));
		minCol.setCellFactory(ChoiceBoxTreeTableCell.forTreeTableColumn(MemberPropertiesTableDAO.minList()));

		// Maximum Column
		setColumnProps(minCol, true, true, false, 0);
		maxCol.setCellValueFactory(new TreeItemPropertyValueFactory<MemberPropertiesTableDAO, Integer>("max"));
		maxCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(new DexIntegerStringConverter()));

		// Description Column
		setColumnProps(descCol, true, true, false, 0, "description");
		// Deprecation Column
		setColumnProps(deprecatedCol, true, true, false, 0, "deprecation");
		// Example Column
		setColumnProps(exampleCol, true, true, false, 0, "example");
	}

	/**
	 * Set String column properties and set value to named field.
	 */
	private void setColumnProps(TreeTableColumn<MemberPropertiesTableDAO, String> c, boolean visable, boolean editable,
			boolean sortable, int width, String field) {
		setColumnProps(c, visable, editable, sortable, width);
		c.setCellValueFactory(new TreeItemPropertyValueFactory<MemberPropertiesTableDAO, String>(field));
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
	public void registerListeners(TreeTableView<ModelMembersTreeDAO> navTreeTableView) {
		navTreeTableView.getSelectionModel().selectedItemProperty()
				.addListener((v, old, newValue) -> newMemberSelectionListener(newValue));
	}

	private void newMemberSelectionListener(TreeItem<ModelMembersTreeDAO> item) {
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
	private void propertySelectionListener(TreeItem<MemberPropertiesTableDAO> item) {
		if (item == null || item.getValue() == null)
			return;
		nameCol.setEditable(item.getValue().isEditable());
		roleCol.setEditable(item.getValue().isEditable());
		minCol.setEditable(item.getValue().isEditable());
		maxCol.setEditable(item.getValue().isEditable());
		descCol.setEditable(item.getValue().isEditable());
	}

	public void handleMaxEdit(TreeTableColumn.CellEditEvent<MemberPropertiesTableDAO, String> event) {
		if (event != null && event.getTreeTablePosition() != null) {
			TreeItem<MemberPropertiesTableDAO> currentItem = event.getRowValue();
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
