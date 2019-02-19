/**
 * 
 */
package org.opentravel.objecteditor;

import org.opentravel.common.DexIntegerStringConverter;
import org.opentravel.model.OtmModelElement;
import org.opentravel.model.facetNodes.OtmFacet;
import org.opentravel.model.objectNodes.OtmLibraryMember;
import org.opentravel.model.propertyNodes.OtmProperty;
import org.opentravel.model.propertyNodes.UserSelectablePropertyTypes;
import org.opentravel.schemacompiler.model.TLProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.ChoiceBoxTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.ImageView;
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
public class FacetTabTreeTableHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(FacetTabTreeTableHandler.class);

	private static final String REQUIRED = "Required";
	private static final String OPTIONAL = "Optional";

	public static ObservableList<String> minList() {
		ObservableList<String> list = FXCollections.observableArrayList();
		list.add(OPTIONAL);
		list.add(REQUIRED);
		return list;
	}

	public static ObservableList<String> getRoleList() {
		return UserSelectablePropertyTypes.getObservableList();
	}

	// Obscure generic from table view
	// Create a javafx properties and add change listeners
	public class PropertyNode {
		protected OtmModelElement<?> element;

		public PropertyNode(OtmModelElement<?> element) {
			this.element = element;
		}

		public PropertyNode(OtmFacet<?> property) {
			this.element = property;
		}

		public StringProperty nameProperty() {
			StringProperty nameProperty;
			if (element.isEditable()) {
				nameProperty = new SimpleStringProperty(element.getName());
				// Adding a change listener with lambda expression
				nameProperty.addListener((ObservableValue<? extends String> ov, String oldVal, String newVal) -> {
					element.setName(newVal);
				});
			} else {
				nameProperty = new ReadOnlyStringWrapper("" + element.getName());
			}
			return nameProperty;
		}

		public StringProperty roleProperty() {
			StringProperty ssp = new SimpleStringProperty(element.getRole());
			ssp.addListener((ObservableValue<? extends String> ov, String oldVal, String newVal) -> {
				// element.setName(newVal);
				System.out.println("TODO - set role of " + element.getName() + " to " + newVal);
			});
			return ssp;
		}

		public StringProperty minProperty() {
			if (!(element instanceof OtmProperty))
				return new ReadOnlyStringWrapper("");

			String value = OPTIONAL;
			if (((OtmProperty<?>) element).isManditory())
				value = REQUIRED;

			SimpleStringProperty ssp = new SimpleStringProperty(value);
			if (element.isEditable())
				ssp.addListener((ObservableValue<? extends String> ov, String oldVal, String newVal) -> {
					((OtmProperty<?>) element).setManditory(newVal.equals(REQUIRED));
					System.out.println("Set optional/manditory of " + element.getName() + " to " + newVal);
				});

			return ssp;
		}

		public IntegerProperty maxProperty() {
			// String value = "";
			// if (element.getTL() instanceof TLProperty)
			// value = String.valueOf(((TLProperty) element.getTL()).getRepeat());
			Integer value = 0;
			if (element.getTL() instanceof TLProperty)
				value = ((TLProperty) element.getTL()).getRepeat();
			return new SimpleIntegerProperty(value);
		}

		public boolean isEditable() {
			return element.isEditable();
		}

		@Override
		public String toString() {
			return element.toString();
		}

		public StringProperty descriptionProperty() {
			// String value = element.getDescription();
			String value = "Now is the time for a description.";

			if (element instanceof OtmFacet)
				return new ReadOnlyStringWrapper("");
			if (!element.isEditable())
				return new ReadOnlyStringWrapper(value);

			StringProperty desc = new SimpleStringProperty(value);
			desc.addListener((ObservableValue<? extends String> ov, String oldValue, String newValue) -> {
				// element.setDesc(newValue);
				System.out.println("TODO - Set " + element + " description to " + newValue);
			});
			return desc;
		}

		public StringProperty deprecationProperty() {
			// String value = element.getDescription();
			String value = "Don't use this!";

			if (element instanceof OtmFacet)
				return new ReadOnlyStringWrapper("");
			if (!element.isEditable())
				return new ReadOnlyStringWrapper(value);

			StringProperty desc = new SimpleStringProperty(value);
			desc.addListener((ObservableValue<? extends String> ov, String oldValue, String newValue) -> {
				// element.setDesc(newValue);
				System.out.println("TODO - Set " + element + " deprecation to " + newValue);
			});
			return desc;
		}

		public StringProperty exampleProperty() {
			// String value = element.getExample();
			String value = "This is a flintstone.";

			// Add empty for properties with complex types
			// if (element.isAssignedComplexType())
			if (element instanceof OtmFacet)
				return new ReadOnlyStringWrapper("");
			if (!element.isEditable())
				return new ReadOnlyStringWrapper(value);

			StringProperty desc = new SimpleStringProperty(value);
			desc.addListener((ObservableValue<? extends String> ov, String oldValue, String newValue) -> {
				// element.setDesc(newValue);
				System.out.println("TODO - Set " + element + " example to " + newValue);
			});
			return desc;
		}

		/**
		 * @return
		 */
		public OtmModelElement<?> getValue() {
			return element;
		}

		/**
		 * @return
		 */
		public ImageView getIcon() {
			return images.getView(element.getIconType());
		}

		public void setMax(String newValue) {
			System.out.println("Setting max to: " + newValue);
		}

		// ((TLProperty)tl).getDocumentation().addImplementer(implementer);(null);
		// ((TLProperty)tl).getDocumentation().addMoreInfo(moreInfo);(null);
		// ((TLProperty)tl).getDocumentation().addOtherDoc(otherDoc);(null);

	}

	protected ImageManager images;
	protected TreeTableView<PropertyNode> table;
	protected TreeItem<PropertyNode> root;

	protected TreeTableColumn<PropertyNode, String> nameCol;
	// protected TreeTableColumn<PropertyNode, ImageView> iconCol;
	protected TreeTableColumn<PropertyNode, String> roleCol;
	protected TreeTableColumn<PropertyNode, String> typeCol;
	protected TreeTableColumn<PropertyNode, String> minCol;
	protected TreeTableColumn<PropertyNode, Integer> maxCol;
	protected TreeTableColumn<PropertyNode, String> exampleCol;
	protected TreeTableColumn<PropertyNode, String> descCol;
	protected TreeTableColumn<PropertyNode, String> deprecatedCol;
	protected TreeTableColumn<PropertyNode, String> otherDocCol;

	/**
	 * Create a facet and property treeTable with manager.
	 * 
	 * @param member
	 * @param table
	 * @param stage
	 */
	public FacetTabTreeTableHandler(OtmLibraryMember<?> member, TreeTableView<PropertyNode> table, Stage stage) {
		System.out.println("Initializing property table for " + member + "member.");

		if (stage == null)
			throw new IllegalStateException("Stage is null.");
		images = new ImageManager(stage);

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
			TreeItem<PropertyNode> item = createTreeItem(element, root);
			item.setExpanded(true);
			if (element instanceof OtmFacet) {
				for (OtmModelElement<?> child : ((OtmFacet<?>) element).getChildren())
					createTreeItem(child, item);
			}
		}
	}

	private TreeItem<PropertyNode> createTreeItem(OtmModelElement<?> element, TreeItem<PropertyNode> parent) {
		TreeItem<PropertyNode> item = new TreeItem<>(new PropertyNode(element));
		item.setExpanded(false);
		parent.getChildren().add(item);
		item.setGraphic(images.getView(element));
		return item;
	}

	private TreeItem<PropertyNode> initializeTable(TreeTableView<PropertyNode> table) {
		// Set the hidden root item
		TreeItem<PropertyNode> root = new TreeItem<>();
		root.setExpanded(true); // Startout fully expanded
		// Set up the TreeTable
		table.setRoot(root);
		table.setShowRoot(false);
		table.setEditable(true);
		table.getSelectionModel().setCellSelectionEnabled(true); // allow individual cells to be edited
		table.setTableMenuButtonVisible(true); // allow users to select columns

		// Enable context menus at the row level and add change listener for for applying style
		table.setRowFactory((TreeTableView<PropertyNode> p) -> new PropertyRowFactory());

		// Define Columns and cell content providers
		buildColumns(table);

		return root;
	}

	/**
	 * Remove all items from the table
	 */
	public void clear() {
		table.getRoot().getChildren().clear();
	}

	/**
	 * Create Columns and set cell values
	 */
	private void buildColumns(TreeTableView<PropertyNode> table) {
		nameCol = new TreeTableColumn<>("Name");
		// iconCol = new TreeTableColumn<>("");
		roleCol = new TreeTableColumn<>("Role");
		typeCol = new TreeTableColumn<>("Assigned Type");

		TreeTableColumn<PropertyNode, String> documentationCol = new TreeTableColumn<>("Documentation");
		descCol = new TreeTableColumn<>("Description");
		deprecatedCol = new TreeTableColumn<>("Deprecation");
		otherDocCol = new TreeTableColumn<>("Other");
		documentationCol.getColumns().addAll(descCol, deprecatedCol, otherDocCol);
		setColumnProps(descCol, true, true, false, 0);
		setColumnProps(deprecatedCol, false, false, false, 0);
		setColumnProps(otherDocCol, false, false, false, 0);

		TreeTableColumn<PropertyNode, String> constraintCol = new TreeTableColumn<>("Repeat");
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
		// Role Column
		setColumnProps(roleCol, true, true, false, 0);
		minCol.setCellValueFactory(new TreeItemPropertyValueFactory<PropertyNode, String>("role"));
		minCol.setCellFactory(ChoiceBoxTreeTableCell.forTreeTableColumn(getRoleList()));

		// Min Column
		setColumnProps(minCol, true, true, false, 0);
		minCol.setCellValueFactory(new TreeItemPropertyValueFactory<PropertyNode, String>("min"));
		minCol.setCellFactory(ChoiceBoxTreeTableCell.forTreeTableColumn(minList()));

		// Maximum Column
		setColumnProps(minCol, true, true, false, 0);
		maxCol.setCellValueFactory(new TreeItemPropertyValueFactory<PropertyNode, Integer>("max"));
		maxCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(new DexIntegerStringConverter()));

		// Description Column
		setColumnProps(descCol, true, true, false, 0, "description");
		// Deprecation Column
		setColumnProps(deprecatedCol, true, true, false, 0, "deprecation");
		// Deprecation Column
		setColumnProps(exampleCol, true, true, false, 0, "example");
	}

	/**
	 * Set String column properties and set value to named field.
	 */
	private void setColumnProps(TreeTableColumn<PropertyNode, String> c, boolean visable, boolean editable,
			boolean sortable, int width, String field) {
		setColumnProps(c, visable, editable, sortable, width);
		c.setCellValueFactory(new TreeItemPropertyValueFactory<PropertyNode, String>(field));
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
	public void registerListeners(TreeTableView<LibraryMemberTreeDAO> navTreeTableView) {
		navTreeTableView.getSelectionModel().selectedItemProperty()
				.addListener((v, old, newValue) -> newMemberSelectionListener(newValue));
	}

	private void newMemberSelectionListener(TreeItem<LibraryMemberTreeDAO> item) {
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
	private void propertySelectionListener(TreeItem<PropertyNode> item) {
		if (item == null || item.getValue() == null)
			return;
		nameCol.setEditable(item.getValue().isEditable());
		roleCol.setEditable(item.getValue().isEditable());
		minCol.setEditable(item.getValue().isEditable());
		maxCol.setEditable(item.getValue().isEditable());
		descCol.setEditable(item.getValue().isEditable());
	}

	public void handleMaxEdit(TreeTableColumn.CellEditEvent<PropertyNode, String> event) {
		if (event != null && event.getTreeTablePosition() != null) {
			TreeItem<PropertyNode> currentItem = event.getRowValue();
			if (currentItem != null)
				currentItem.getValue().setMax(event.getNewValue());
		} else
			System.out.println("ERROR - cell max edit handler has null.");
	}

	// public class MyEventDispatcher implements EventDispatcher {
	//
	// // I am ONLY capturing Mouse Events ??Why??
	// @Override
	// public Event dispatchEvent(Event event, EventDispatchChain tail) {
	// // capturing phase, can handle / modify / substitute / divert the event
	// boolean notHandledYet = true;
	// if (!(event instanceof javafx.scene.input.MouseEvent)) {
	// if (event instanceof ActionEvent) {
	// System.out.println("Action Event type = " + event.getEventType());
	// }
	// System.out.println("Event type = " + event.getEventType());
	// System.out.println("Key Event: " + event.getClass().getSimpleName());
	// if (event instanceof javafx.scene.input.KeyEvent)
	// System.out.println("Key Event trapped.");
	// }
	//
	// if (notHandledYet) {
	// // forward the event to the rest of the chain
	// event = tail.dispatchEvent(event);
	//
	// if (event != null) {
	// // bubbling phase, can handle / modify / substitute / divert
	// // the event
	// }
	// }
	//
	// return notHandledYet ? event : null;
	// }
	//
	// }
}
