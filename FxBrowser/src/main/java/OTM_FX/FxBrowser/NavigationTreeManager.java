/**
 * 
 */
package OTM_FX.FxBrowser;

import org.opentravel.common.ImageManager;
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.otmLibraryMembers.OtmChoiceObject;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * Manage the node navigation tree.
 * 
 * @author dmh
 *
 */
@SuppressWarnings("restriction")
public class NavigationTreeManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(NavigationTreeManager.class);

	// @FXML
	public TreeView<TreeNode> navigationTreeView;
	// public TreeView<OtmLibraryMember<?>> treeView;

	// TreeView<?> treeView;
	TreeItem<TreeNode> root; // Root of the navigation tree. Is displayed.
	// TreeItem<OtmLibraryMember<?>> branch1, branch2, branch3;
	ImageManager images;
	Stage stage;
	private OtmModelManager modelMgr;

	// Obscure generic from tree view and tree item and cell
	public class TreeNode {
		protected String name;
		protected OtmLibraryMember<?> member;

		public TreeNode(OtmLibraryMember<?> member) {
			this.member = member;
		}

		public boolean isEditable() {
			return member.isEditable();
		}

		@Override
		public String toString() {
			return member.toString();
		}
	}

	public NavigationTreeManager(Stage stage, TreeView<TreeNode> navigationTreeView, OtmModelManager model) {
		System.out.println("Initializing navigation tree.");

		if (navigationTreeView == null)
			throw new IllegalStateException("Tree view is null.");

		root = new TreeItem<>();
		root.setExpanded(true); // Startout fully expanded
		this.stage = stage;
		images = new ImageManager(stage);
		this.navigationTreeView = navigationTreeView;
		modelMgr = model;

		// Wire in the overridden Tree View Cell factory
		navigationTreeView.setCellFactory((TreeView<TreeNode> p) -> new NavCellFactory());

		for (OtmLibraryMember<?> member : model.getMembers()) {
			makeBranch(member, root);
		}
		// makeTestBranches(root);

		navigationTreeView.setRoot(getRoot());
		navigationTreeView.setShowRoot(false);
		navigationTreeView.setEditable(true);
		// add a listener class with three parameters that invokes selection listener
		navigationTreeView.getSelectionModel().selectedItemProperty()
				.addListener((v, old, newValue) -> selectionListener(v, newValue));
	}

	/**
	 * Listener for changes to tree items.
	 * 
	 * @param v
	 * 
	 * @param item
	 */
	private void selectionListener(ObservableValue<? extends TreeItem<TreeNode>> v, TreeItem<TreeNode> item) {
		System.out.println("Listener: " + item.getValue() + " from parent " + item.getParent().getValue());
	}

	public TreeItem<TreeNode> getRoot() {
		return root;
	}

	/**
	 * TreeItem class does not extend the Node class.
	 * 
	 * Therefore, you cannot apply any visual effects or add menus to the tree items. Use the cell factory mechanism to
	 * overcome this obstacle and define as much custom behavior for the tree items as your application requires.
	 * 
	 * @param item
	 */
	protected TreeItem<TreeNode> createItem(OtmLibraryMember<?> member) {
		TreeNode tn = new TreeNode(member);
		TreeItem<TreeNode> item = new TreeItem<>(tn);
		// applyCellFactory(item);
		item.setGraphic(images.getView(member.getIconType()));
		item.setExpanded(true);
		return item;
	}

	private void makeBranch(OtmLibraryMember<?> member, TreeItem<TreeNode> parent) {
		parent.getChildren().add(createItem(member));
	}

	private void applyCellFactory(TreeItem<TreeNode> cell) {
		// PseudoClass leaf = PseudoClass.getPseudoClass("leaf");
		navigationTreeView.setCellFactory(tv -> {
			TreeCell<TreeNode> tc = new TreeCell<>();
			tc.itemProperty().addListener((obs, oldValue, newValue) -> {
				tc.setGraphic(images.getView(newValue.member.getIconType()));
				tc.setText(newValue.toString());
				// if (newValue == null)
				// tc.setText("missing");
				// else
				// tc.setText(newValue.name);

			});
			tc.treeItemProperty().addListener((obs, oldTreeItem, newTreeItem) -> setClass(tc, newTreeItem));
			// tc.treeItemProperty().addListener((obs, oldTreeItem, newTreeItem) -> tc.pseudoClassStateChanged(leaf,
			// newTreeItem != null && newTreeItem.getValue().isEditable()));
			return tc;
		});
	}

	private final class NavCellFactory extends TreeCell<TreeNode> {
		private final ContextMenu addMenu = new ContextMenu();
		private TextField textField;

		public NavCellFactory() {
			// Create Context menu
			MenuItem addObject = new MenuItem("Add Object");
			addMenu.getItems().add(addObject);
			addObject.setOnAction((ActionEvent t) -> {
				TreeItem<TreeNode> newObject = new TreeItem<>(new TreeNode(new OtmChoiceObject("new Guy", modelMgr)));
				newObject.setGraphic(images.getView(newObject.getValue().member.getIconType()));
				if (getTreeItem().getParent() != null)
					getTreeItem().getParent().getChildren().add(newObject);
				else
					getTreeItem().getChildren().add(newObject);
			});
			setContextMenu(addMenu);

			// Give it text and icon
			itemProperty().addListener((obs, oldValue, newValue) -> {
				if (newValue != null) {
					if (newValue.member != null)
						setGraphic(images.getView(newValue.member.getIconType()));
					setText(newValue.toString());
				}
			});

			// Set font (css class)
			treeItemProperty().addListener((obs, oldTreeItem, newTreeItem) -> setClass(this, newTreeItem));
		}

		// @Override
		// public void commitEdit(TreeNode newValue) {
		// super.commitEdit(newValue);
		// }

		/** {@inheritDoc} */
		@Override
		public void startEdit() {
			super.startEdit();

			if (textField == null) {
				createTextField();
			}
			setText(null);
			setGraphic(textField);
			textField.selectAll();
		}

		@Override
		public void cancelEdit() {
			super.cancelEdit();
			setText(getItem().toString());
			setGraphic(getTreeItem().getGraphic());
		}

		@Override
		public void updateItem(TreeNode item, boolean empty) {
			super.updateItem(item, empty);
			String text = "";
			if (item != null)
				text = item.toString();
			if (textField != null)
				text = textField.getText();

			System.out.println("Update Item: " + text);

			if (empty || item == null) {
				setText(null);
				setGraphic(null);
			} else {
				if (isEditing()) {
					if (textField != null) {
						textField.setText(item.toString());
					}
					setText(null);
					setGraphic(textField);
				} else {
					item.member.setName(text);
					setText(item.toString());
					setGraphic(getTreeItem().getGraphic());
				}
			}
		}

		private void createTextField() {
			textField = new TextField(getItem().toString());
			textField.setOnKeyReleased((KeyEvent t) -> {
				if (t.getCode() == KeyCode.ENTER) {
					commitEdit(getItem());
					// getItem().name = textField.getText();
				} else if (t.getCode() == KeyCode.ESCAPE) {
					cancelEdit();
				}
			});
		}

	}

	/**
	 * @param tc
	 * @param newTreeItem
	 * @return
	 */
	private Object setClass(TreeCell<TreeNode> tc, TreeItem<TreeNode> newTreeItem) {
		PseudoClass leaf = PseudoClass.getPseudoClass("leaf");
		tc.pseudoClassStateChanged(leaf, newTreeItem != null && !newTreeItem.getValue().isEditable());
		return tc;
	}

	// private void applyCellFactoryI(TreeView<Integer> tree) {
	// PseudoClass leaf = PseudoClass.getPseudoClass("leaf");
	// tree.setCellFactory(tv -> {
	// TreeCell<Integer> cell = new TreeCell<>();
	// cell.itemProperty().addListener((obs, oldValue, newValue) -> {
	// if (newValue == null) {
	// cell.setText("");
	// } else {
	// cell.setText(newValue.toString());
	// }
	// });
	// cell.treeItemProperty().addListener((obs, oldTreeItem, newTreeItem) -> cell.pseudoClassStateChanged(leaf,
	// newTreeItem != null && newTreeItem.isLeaf()));
	// return cell;
	// });
	// }

	// TODO - add context menus
	// https://docs.oracle.com/javase/8/javafx/user-interface-tutorial/tree-view.htm#BABDEADA

	// TODO - investigate using ControlsFX for decoration
	private void addDecorator(TreeItem<OtmLibraryMember<?>> item) {
		// Image error = images.get(Icons.Error);
		// ImageView ie = new ImageView(error);

		// https://github.com/jinghai/controlsfx
		// iv.setBlendMode(BLENDMODE.);
		// GraphicDecoration decoration = new GraphicDecoration(ie, Pos.TOP_LEFT);

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

	}

	// private void makeTestBranches(TreeItem<OtmLibraryMember<?>> root) {
	// branch1 = makeBranch("Item1", root);
	// makeBranch("Leaf1", branch1);
	// makeBranch("Leaf2", branch1);
	// branch2 = makeBranch("Item2", root);
	// makeBranch("Leaf1", branch2);
	// branch3 = makeBranch("Item3", branch2);
	// makeBranch("Leaf1", branch3);
	// makeBranch("Leaf2", branch3);
	// makeBranch("Leaf3", branch3);
	// }
}
