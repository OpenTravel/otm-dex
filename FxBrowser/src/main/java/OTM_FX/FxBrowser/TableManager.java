/**
 * 
 */
package OTM_FX.FxBrowser;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/**
 * @author dmh
 *
 */
// @SuppressWarnings("restriction")
public class TableManager {

	TableColumn<DemoNode, String> c1, c2, c3, c4;
	TextField nameInput, assignedInput;
	ChoiceBox<String> typeInput;
	private TableView<DemoNode> table;

	public ObservableList<DemoNode> getNodes() {
		ObservableList<DemoNode> list = FXCollections.observableArrayList();
		list.add(new DemoNode("A", "String", "Element"));
		list.add(new DemoNode("b", "String", "attribute"));
		list.add(new DemoNode("C", "String", "Element"));
		list.add(new DemoNode("D", "String", "ElementRef"));
		list.add(new DemoNode("E", "String", "Indicator"));
		return list;
	}

	/**
	 * Build a table using the default nodes and columns
	 * 
	 * @return
	 */
	public TableView<DemoNode> build() {
		table = new TableView<>();
		// c1 = makeColumn("Name", "name", 100);
		// c2 = makeColumn("Type", "nodeType", 100);
		// c3 = makeColumn("Assigned Type", "assignedType", 100);
		// table.getColumns().addAll(c1, c2, c3);
		build(table);
		return table;
	}

	public void build(TableView<DemoNode> table) {
		if (table != null) {
			table.setEditable(true);

			c1 = makeColumn("Name", "name", 100);
			c2 = makeColumn("Type", "nodeType", 100);
			c3 = makeColumn("Assigned Type", "assignedType", 100);
			c4 = makeColumn("Description", "description", 100);

			makeEditable();
			table.getColumns().addAll(c1, c2, c3, c4);
		}
		// Add row listener
		table.getSelectionModel().selectedItemProperty()
				.addListener((observableValue, oldValue, newValue) -> rowSelectionHandler(newValue));
		this.table = table;
	}

	/**
	 * @param newValue
	 * @return
	 */
	private void rowSelectionHandler(DemoNode node) {
		if (node != null) {
			nameInput.setPromptText(node.getName());
			typeInput.setValue(node.getNodeType());
			assignedInput.setPromptText(node.getAssignedType());
		}
	}

	public Pane getEditPane() {
		HBox inputScene = new HBox();
		getEditPane(inputScene);
		return inputScene;
	}

	public Pane getEditPane(HBox inputScene) {
		nameInput = new TextField();
		nameInput.setPromptText("Field Name");
		assignedInput = new TextField();
		assignedInput.setPromptText("Assigned Type");

		typeInput = new ChoiceBox<>();
		for (UserSelectablePropertyTypes p : UserSelectablePropertyTypes.values())
			typeInput.getItems().add(p.label());

		typeInput.setValue("Element");
		typeInput.setOnAction(e -> choiceHandler(e, typeInput));

		Button addB = new Button("Add");
		addB.setOnAction(e -> addNode(e));
		Button deleteB = new Button("Delete");
		deleteB.setOnAction(e -> deleteNode(e));

		inputScene.setPadding(new Insets(10, 10, 10, 10));
		inputScene.setSpacing(10);
		inputScene.getChildren().addAll(nameInput, typeInput, assignedInput, addB, deleteB);
		return inputScene;
	}

	/**
	 * @param e
	 * @return
	 */
	private void addNode(ActionEvent e) {
		DemoNode n = new DemoNode(nameInput.getText(), assignedInput.getText(), typeInput.getValue());
		table.getItems().add(n);
		nameInput.clear();
		assignedInput.clear();
		typeInput.setValue("Element");
	}

	/**
	 * @param e
	 * @return
	 */
	private void deleteNode(ActionEvent e) {
		ObservableList<DemoNode> selected = table.getSelectionModel().getSelectedItems();
		selected.forEach(table.getItems()::remove);
	}

	/**
	 * Make a column editable
	 */
	private void makeEditable() {
		// TODO - something about demoNode makes it unsuitable.
		//
		// https://docs.oracle.com/javase/8/javafx/user-interface-tutorial/table-view.htm#CJAGAAEE
		//
		c4.setCellFactory(TextFieldTableCell.<DemoNode> forTableColumn());
		c4.setOnEditCommit((CellEditEvent<DemoNode, String> t) -> {
			t.getTableView().getItems().get(t.getTablePosition().getRow()).setDescription(t.getNewValue());
		});
	}

	/**
	 * @param e
	 * @param cBox
	 * @return
	 */
	private void choiceHandler(ActionEvent e, ChoiceBox<String> cBox) {
		if (table.getSelectionModel().getSelectedItem() != null) {
			System.out.println("Set property Role on " + table.getSelectionModel().getSelectedItem().getName() + " to: "
					+ cBox.getValue());
			table.getSelectionModel().getSelectedItem().setNodeType(cBox.getValue());
			table.refresh();
		}
	}

	private TableColumn<DemoNode, String> makeColumn(String name, String propertyName, int width) {
		TableColumn<DemoNode, String> column = new TableColumn<>(name);
		column.setMinWidth(width);
		column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
		column.setSortable(false);
		return column;
	}
}
