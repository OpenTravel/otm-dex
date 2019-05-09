/**
 * 
 */
package org.opentravel.dex.controllers.member.properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.DexRepeatMaxConverter;
import org.opentravel.common.cellfactories.AssignedTypePropertiesTreeTableCellFactory;
import org.opentravel.common.cellfactories.ValidationPropertiesTreeTableCellFactory;
import org.opentravel.dex.controllers.DexIncludedControllerBase;
import org.opentravel.dex.controllers.DexMainController;
import org.opentravel.dex.controllers.member.MemberDAO;
import org.opentravel.dex.events.DexMemberSelectionEvent;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.ChoiceBoxTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Manage a facets and properties in a tree table.
 * 
 * @author dmh
 *
 */
public class MemberPropertiesTreeTableController extends DexIncludedControllerBase<MemberDAO> {
	private static Log log = LogFactory.getLog(MemberPropertiesTreeTableController.class);

	@FXML
	protected TreeTableView<PropertiesDAO> propertiesTable;

	@FXML
	private VBox memberProperties;
	protected TreeItem<PropertiesDAO> root;
	// Table Columns
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

	private static final EventType[] publishedEvents = { DexMemberSelectionEvent.MEMBER_SELECTED };
	private static final EventType[] subscribedEvents = { DexMemberSelectionEvent.MEMBER_SELECTED };

	/**
	 * Create a facet and property treeTable with manager.
	 * 
	 */
	public MemberPropertiesTreeTableController() {
		super(subscribedEvents, publishedEvents);
	}

	/**
	 * Create Columns and set cell values
	 */
	@SuppressWarnings("unchecked")
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

		// Repeat: min and max Column
		minCol = new TreeTableColumn<>("min");
		setColumnProps(minCol, true, true, false, 75);
		minCol.setCellValueFactory(new TreeItemPropertyValueFactory<PropertiesDAO, String>("min"));
		minCol.setCellFactory(ChoiceBoxTreeTableCell.forTreeTableColumn(PropertiesDAO.minList()));

		maxCol = new TreeTableColumn<>("max");
		setColumnProps(minCol, true, true, false, 0);
		// javafx.geometry.Pos.
		maxCol.setStyle("-fx-alignment: CENTER;");
		maxCol.setCellValueFactory(new TreeItemPropertyValueFactory<PropertiesDAO, Integer>("max"));
		maxCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(new DexRepeatMaxConverter()));

		TreeTableColumn<PropertiesDAO, String> constraintCol = new TreeTableColumn<>("Repeat");
		constraintCol.getColumns().addAll(minCol, maxCol);

		// Validation column
		TreeTableColumn<PropertiesDAO, ImageView> valCol = new TreeTableColumn<>("");
		valCol.setPrefWidth(25);
		valCol.setEditable(false);
		valCol.setSortable(false);
		valCol.setCellFactory(c -> new ValidationPropertiesTreeTableCellFactory());

		exampleCol = new TreeTableColumn<>("Example");
		setColumnProps(exampleCol, false, false, false, 0);

		table.getColumns().addAll(nameCol, valCol, typeCol, roleCol, constraintCol, exampleCol, documentationCol);

		// Name Column
		setColumnProps(nameCol, true, true, false, 200, "name");

		// Assigned type column
		setColumnProps(typeCol, true, true, false, 150);
		typeCol.setCellValueFactory(new TreeItemPropertyValueFactory<PropertiesDAO, String>("assignedType"));
		typeCol.setCellFactory(c -> new AssignedTypePropertiesTreeTableCellFactory(this));

		// Role Column
		setColumnProps(roleCol, true, true, false, 100);
		roleCol.setCellValueFactory(new TreeItemPropertyValueFactory<PropertiesDAO, String>("role"));
		roleCol.setCellFactory(ChoiceBoxTreeTableCell.forTreeTableColumn(PropertiesDAO.getRoleList()));

		// Description Column
		setColumnProps(descCol, true, true, false, 150, "description");
		// Deprecation Column
		setColumnProps(deprecatedCol, true, true, false, 50, "deprecation");
		// Example Column
		setColumnProps(exampleCol, true, true, false, 0, "example");
	}

	@Override
	public void checkNodes() {
		if (propertiesTable == null)
			throw new IllegalStateException("Property table not injected by FXML");
	}

	/**
	 * Remove all items from the table
	 */
	@Override
	public void clear() {
		propertiesTable.getRoot().getChildren().clear();
	}

	@Override
	public void configure(DexMainController parent) {
		super.configure(parent);
		eventPublisherNode = propertiesTable;

		propertiesTable.getSelectionModel().selectedItemProperty()
				.addListener((v, old, newValue) -> propertySelectionListener(newValue));

		// Layout the table
		initializeTable(propertiesTable);
	}

	@Override
	public void handleEvent(Event e) {
		log.debug("event handler.");
		if (e instanceof DexMemberSelectionEvent)
			memberSelectionHandler((DexMemberSelectionEvent) e);
	}

	public void handleMaxEdit(TreeTableColumn.CellEditEvent<PropertiesDAO, String> event) {
		if (event != null && event.getTreeTablePosition() != null) {
			TreeItem<PropertiesDAO> currentItem = event.getRowValue();
			if (currentItem != null)
				currentItem.getValue().setMax(event.getNewValue());
		} else
			log.debug("ERROR - cell max edit handler has null.");
	}

	private void initializeTable(TreeTableView<PropertiesDAO> table) {
		// Set the hidden root item
		root = new TreeItem<>();
		root.setExpanded(true); // Start out fully expanded
		// Set up the TreeTable
		table.setRoot(root);
		table.setShowRoot(false);
		table.setEditable(true);
		table.getSelectionModel().setCellSelectionEnabled(true); // allow individual cells to be edited
		table.setTableMenuButtonVisible(true); // allow users to select columns

		// Enable context menus at the row level and add change listener for for applying style
		table.setRowFactory((TreeTableView<PropertiesDAO> p) -> new MemberPropertiesRowFactory(this));

		// Define Columns and cell content providers
		buildColumns(table);
	}

	public void memberSelectionHandler(DexMemberSelectionEvent event) {
		log.debug("Dex member selection event received.");
		post(event.getMember());
	}

	public void post(OtmLibraryMember member) {
		clear();
		if (member != null)
			new PropertiesDAO(member, this).createChildrenItems(root);
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
		nameCol.setEditable(item.getValue().isEditable());
		roleCol.setEditable(item.getValue().isEditable());
		typeCol.setEditable(item.getValue().isEditable());
		minCol.setEditable(item.getValue().isEditable());
		maxCol.setEditable(item.getValue().isEditable());
		exampleCol.setEditable(item.getValue().isEditable());
		descCol.setEditable(item.getValue().isEditable());
		deprecatedCol.setEditable(item.getValue().isEditable());
	}

	@Override
	public void refresh() {
		propertiesTable.refresh();
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

}
