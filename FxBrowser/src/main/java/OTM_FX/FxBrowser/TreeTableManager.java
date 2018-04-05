/**
 * 
 */
package OTM_FX.FxBrowser;

import java.util.Collection;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * @author dmh
 *
 */
@SuppressWarnings("restriction")
public class TreeTableManager {

	TreeItem<DemoNode> root, branch1, branch2, branch3;
	private TreeTableView<DemoNode> table;

	public TreeTableManager(TreeTableView<DemoNode> nodeTreeTableView) {
		table = nodeTreeTableView;

		// Enable cell-level selection
		table.getSelectionModel().setCellSelectionEnabled(true);

		// Get or create root node
		if (table.getRoot() == null) {
			root = new TreeItem<DemoNode>();
			table.setRoot(root);
		} else
			root = table.getRoot();

		// Make columns and add factories
		makeColumns(table);
		root.setExpanded(true);
		root.setValue(new DemoNode("Root", "Int", "Attribute", "RootNode"));
	}

	public void build(Collection<DemoNode> nodes) {
		// TODO - study how to make cells editable
		// https://stackoverflow.com/questions/28414825/make-individual-cell-editable-in-javafx-tableview
		//
		for (DemoNode n : nodes) {
			TreeItem<DemoNode> item = new TreeItem<>(n);
			root.getChildren().add(item);
		}
		System.out.println("Added " + nodes.size() + " children to root.");
	}

	public void build() {

		// Make columns if necessary
		makeColumns(table);

		// Add row selection listener
		table.getSelectionModel().selectedItemProperty()
				.addListener((observableValue, oldValue, newValue) -> rowSelectionHandler(newValue));
		// branch1 = makeBranch("Item1", root);
		// makeBranch("Leaf1", branch1);
		// makeBranch("Leaf2", branch1);
		// branch2 = makeBranch("Item2", root);
		// makeBranch("Leaf1", branch2);
		// branch3 = makeBranch("Item3", root);
		// makeBranch("Leaf1", branch3);
		// makeBranch("Leaf2", branch3);
		// makeBranch("Leaf3", branch3);
	}

	public TreeItem<DemoNode> getRoot() {
		return root;
	}

	public void makeColumns(TreeTableView<DemoNode> table) {
		// TODO - make into an enum with Column name and property name
		for (TreeTableColumn<DemoNode, ?> column : table.getColumns()) {
			if (column.getId() == null)
				System.out.println("GIVE ME an Id please.");
			else if (column.getId().equals(NodeTableColumns.NAME.id())) {
				column.setCellValueFactory(new TreeItemPropertyValueFactory<>(NodeTableColumns.NAME.property()));
				column.setText(NodeTableColumns.NAME.label());
			} else if (column.getId().equals("RoleColumn"))
				column.setCellValueFactory(new TreeItemPropertyValueFactory<>("nodeType"));
			else if (column.getId().equals("FacetColumn"))
				column.setCellValueFactory(new TreeItemPropertyValueFactory<>("nodeType"));
			else if (column.getId().equals("TypeColumn"))
				column.setCellValueFactory(new TreeItemPropertyValueFactory<>("nodeType"));
			else if (column.getId().equals("DescriptionColumn"))
				column.setCellValueFactory(new TreeItemPropertyValueFactory<>("description"));
			column.setSortable(false);
		}
		// TreeTableColumn<DemoNode, String> lastNameCol = new TreeTableColumn<>("Name");
		// lastNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));

		// Not Needed, done in scene builder
		// TreeTableColumn<DemoNode, ?> c1, c2, c3;
		// if (table != null && table.getColumns().isEmpty()) {
		// c1 = makeColumn("Name", "name", 100);
		// c2 = makeColumn("Type", "nodeType", 100);
		// c3 = makeColumn("Assigned Type", "assignedType", 100);
		// table.getColumns().addAll(c1, c2, c3);
		// }
	}

	private TreeTableColumn<DemoNode, String> makeColumn(String name, String propertyName, int width) {
		TreeTableColumn<DemoNode, String> column = new TreeTableColumn<>(name);
		column.setMinWidth(width);
		column.setCellValueFactory(new TreeItemPropertyValueFactory<>(propertyName));
		column.setSortable(false);
		return column;
	}

	/**
	 * @param newValue
	 * @return
	 */
	private void rowSelectionHandler(TreeItem<DemoNode> newValue) {
		if (newValue != null) {
			if (newValue.isLeaf())
				System.out.println(newValue.getValue().getName() + " selected.");
			else
				System.out.println("Selected row has " + newValue.getChildren().size() + "children.");
			// nameInput.setPromptText(node.getName());
			// typeInput.setValue(node.getNodeType());
			// assignedInput.setPromptText(node.getAssignedType());
		}
	}

	private TreeItem<String> makeBranch(String label, TreeItem<String> parent) {
		Image imageOk = new Image(getClass().getResourceAsStream("/icons/BusinessObject.png"));
		Image error = new Image(getClass().getResourceAsStream("/icons/error.gif"));
		ImageView iv = new ImageView(imageOk);
		ImageView ie = new ImageView(error);
		// iv.setBlendMode(BLENDMODE.);
		// GraphicDecoration decoration = new GraphicDecoration(ie, Pos.TOP_LEFT);

		TreeItem<String> item = new TreeItem<String>(label);
		// Decorator.addDecoration( item, decoration);
		// Platform.runLater(new Runnable() {
		// @Override
		// public void run() {
		// try {
		// Decorator.addDecoration(item.graphicProperty().get(), decoration);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// });
		item.setGraphic(iv);
		item.setExpanded(true);
		parent.getChildren().add(item);
		return item;
	}
}
