/**
 * 
 */
package org.opentravel.objecteditor;

import java.util.Set;

import org.opentravel.common.ImageManager;
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.otmContainers.OtmLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

/**
 * Manage the tree table view for libraries in projects (Library Tab)
 * 
 * @author dmh
 *
 */
@SuppressWarnings("restriction")
public class ProjectLibrariesTreeController implements DexController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectLibrariesTreeController.class);

	public static final String PREFIXCOLUMNLABEL = "Prefix";
	private static final String NAMELABEL = "Name";
	private static final String NAMESPACELABEL = "Namespace";
	private static final String VERSIONLABEL = "Version";
	private static final String EDITABLELABEL = "Editable";
	private static final String STATUSLABEL = "Status";
	private static final String REFERENCELABEL = "References";
	private static final String STATELABEL = "State";
	private static final String LOCKEDLABEL = "Locked-by";
	private static final String READONLYLABEL = "Read-only";

	TreeTableView<ProjectLibrariesTreeDAO> libraryTree;
	TreeItem<ProjectLibrariesTreeDAO> root; // Root of the navigation tree. Is displayed.

	// Editable Columns
	// TreeTableColumn<ProjectLibraryTreeDAO, String> nameColumn; // an editable column

	OtmModelManager modelMgr;
	ImageManager imageMgr;
	DexController parent;

	// TODO
	// 1. use style for latest version
	// 2. Add context menu to show where used based on references
	// 3. Add context menu for version uplift.
	//
	@SuppressWarnings("unchecked")
	public ProjectLibrariesTreeController(DexController parent, TreeTableView<ProjectLibrariesTreeDAO> view) {
		System.out.println("Initializing project-library tree table.");

		// remember and check the parameters
		this.parent = parent;
		this.libraryTree = view;
		if (parent == null)
			throw new IllegalArgumentException("Parent is null.");
		if (view == null)
			throw new IllegalArgumentException("Tree table view is null.");

		imageMgr = parent.getImageManager();
		modelMgr = parent.getModelManager();
		if (modelMgr == null)
			throw new IllegalStateException("Model manager is null.");

		// Set the hidden root item
		root = new TreeItem<>();
		root.setExpanded(true); // Startout fully expanded

		// Set up the TreeTable
		buildColumns();
		// Enable context menus at the row level and add change listener for for applying style
		libraryTree.setRowFactory((TreeTableView<ProjectLibrariesTreeDAO> p) -> new RowFactory());

		// create cells for members
		for (OtmLibrary lib : modelMgr.getLibraries()) {
			createTreeItem(lib, root);
		}
	}

	@Override
	public OtmModelManager getModelManager() {
		return modelMgr;
	}

	/**
	 * Get the library members from the model manager and put them into a cleared tree.
	 * 
	 * @param modelMgr
	 */
	public void post(OtmModelManager modelMgr) {
		if (modelMgr != null)
			this.modelMgr = modelMgr;
		refresh();
	}

	public void refresh() {
		// create cells for members
		libraryTree.getRoot().getChildren().clear();
		for (String baseNS : modelMgr.getBaseNamespaces()) {
			TreeItem<ProjectLibrariesTreeDAO> latestItem = null;
			OtmLibrary latest = null;
			Set<OtmLibrary> libs = modelMgr.getLibraryChain(baseNS);
			for (OtmLibrary lib : libs)
				if (lib.isLatestVersion()) {
					latestItem = createTreeItem(lib, root);
					latest = lib;
				}
			// Put 1st item at root, all rest under it.
			if (latest != null)
				for (OtmLibrary lib : libs)
					if (lib != latest)
						createTreeItem(lib, latestItem);

		}
	}

	/**
	 * Listener for selected library members.
	 * 
	 * @param item
	 */
	private void librarySelectionListener(TreeItem<ProjectLibrariesTreeDAO> item) {
		if (item == null || item.getValue() == null || item.getValue().getValue() == null)
			return;

		System.out.println("Selection Listener: " + item.getValue().getValue());

		if (parent instanceof ObjectEditorController)
			if (item.getValue().getValue() instanceof OtmLibrary)
				((ObjectEditorController) parent).handleLibrarySelectionEvent(item.getValue().getValue());
	}

	public TreeItem<ProjectLibrariesTreeDAO> getRoot() {
		return root;
	}

	private void buildColumns() {
		libraryTree.setRoot(getRoot());
		libraryTree.setShowRoot(false);
		libraryTree.setEditable(true);
		// libraryTree.getSelectionModel().setCellSelectionEnabled(true); // allow individual cells to be edited
		libraryTree.setTableMenuButtonVisible(true); // allow users to select columns

		// Enable context menus at the row level and add change listener for for applying style
		// libraryTree.setRowFactory((TreeTableView<ProjectLibraryTreeDAO> p) -> new NavRowFactory());

		// add a listener class with three parameters that invokes selection listener
		libraryTree.getSelectionModel().selectedItemProperty()
				.addListener((v, old, newValue) -> librarySelectionListener(newValue));

		//
		// Create columns
		//
		TreeTableColumn<ProjectLibrariesTreeDAO, String> prefixColumn = createStringColumn(PREFIXCOLUMNLABEL, "prefix",
				true, false, true, 0);
		TreeTableColumn<ProjectLibrariesTreeDAO, String> nameColumn = createStringColumn(NAMELABEL, "name", true, false,
				true, 200);
		TreeTableColumn<ProjectLibrariesTreeDAO, String> namespaceColumn = createStringColumn(NAMESPACELABEL, "namespace",
				true, false, true, 0);
		TreeTableColumn<ProjectLibrariesTreeDAO, String> versionColumn = createStringColumn(VERSIONLABEL, "version", true,
				false, true, 0);
		// TreeTableColumn<ProjectLibraryTreeDAO, String> editableColumn = createStringColumn(EDITABLELABEL, "edit",
		// true,
		// false, true, 0);
		TreeTableColumn<ProjectLibrariesTreeDAO, String> statusColumn = createStringColumn(STATUSLABEL, "status", true,
				false, true, 0);
		TreeTableColumn<ProjectLibrariesTreeDAO, String> stateColumn = createStringColumn(STATELABEL, "state", true,
				false, true, 0);
		// TreeTableColumn<ProjectLibraryTreeDAO, String> refColumn = createStringColumn(REFERENCELABEL, "reference",
		// true,
		// false, true, 0);
		TreeTableColumn<ProjectLibrariesTreeDAO, String> editColumn = createStringColumn(EDITABLELABEL, "edit", true,
				false, true, 0);
		TreeTableColumn<ProjectLibrariesTreeDAO, String> lockedColumn = createStringColumn(LOCKEDLABEL, "locked", true,
				false, true, 0);
		TreeTableColumn<ProjectLibrariesTreeDAO, Boolean> readonlyColumn = new TreeTableColumn<>(READONLYLABEL);
		readonlyColumn
				.setCellValueFactory(new TreeItemPropertyValueFactory<ProjectLibrariesTreeDAO, Boolean>("readonly"));
		TreeTableColumn<ProjectLibrariesTreeDAO, Integer> refColumn = new TreeTableColumn<>(REFERENCELABEL);
		refColumn.setCellValueFactory(new TreeItemPropertyValueFactory<ProjectLibrariesTreeDAO, Integer>("reference"));

		libraryTree.getColumns().addAll(nameColumn, prefixColumn, namespaceColumn, versionColumn, statusColumn,
				stateColumn, lockedColumn, refColumn, readonlyColumn, editColumn);
	}

	/**
	 * Create a treeTableColumn for a String and set properties.
	 * 
	 * @return
	 */
	private TreeTableColumn<ProjectLibrariesTreeDAO, String> createStringColumn(String label, String propertyName,
			boolean visable, boolean editable, boolean sortable, int width) {
		TreeTableColumn<ProjectLibrariesTreeDAO, String> c = new TreeTableColumn<>(label);
		c.setCellValueFactory(new TreeItemPropertyValueFactory<ProjectLibrariesTreeDAO, String>(propertyName));
		c.setVisible(visable);
		c.setEditable(editable);
		c.setSortable(sortable);
		if (width > 0)
			c.setPrefWidth(width);
		return c;
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
	private TreeItem<ProjectLibrariesTreeDAO> createTreeItem(OtmLibrary library, TreeItem<ProjectLibrariesTreeDAO> parent) {
		if (library != null) {
			TreeItem<ProjectLibrariesTreeDAO> item = new TreeItem<>(new ProjectLibrariesTreeDAO(library));
			item.setExpanded(false);
			parent.getChildren().add(item);
			return item;
		}
		return null;
	}

	// /**
	// * TreeTableRow is an IndexedCell, but rarely needs to be used by developers creating TreeTableView instances. The
	// * only time TreeTableRow is likely to be encountered at all by a developer is if they wish to create a custom
	// * rowFactory that replaces an entire row of a TreeTableView.
	// *
	// * https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/TreeTableRow.html
	// */
	private static final PseudoClass EDITABLE = PseudoClass.getPseudoClass("editable");

	private final class RowFactory extends TreeTableRow<ProjectLibrariesTreeDAO> {
		private final ContextMenu addMenu = new ContextMenu();

		public RowFactory() {
			// Create Context menu
			MenuItem addObject = new MenuItem("Show Where Used (future)");
			addMenu.getItems().add(addObject);
			setContextMenu(addMenu);
			//
			// // Create action for addObject event
			// addObject.setOnAction(this::addMemberEvent);
			//
			// // Set style listener (css class)
			// treeItemProperty().addListener((obs, oldTreeItem, newTreeItem) -> setCSSClass(this, newTreeItem));
			//
			// // Not sure this helps!
			// if (getTreeItem() != null && getTreeItem().getValue() != null) {
			// setEditable(getTreeItem().getValue().isEditable());
			// }
		}

		// /**
		// * Add a new member to the tree
		// *
		// * @param t
		// */
		// private void addMemberEvent(ActionEvent t) {
		// TreeItem<ProjectLibraryTreeDAO> item = createTreeItem(new OtmCoreObject("new", currentModelMgr),
		// getTreeItem().getParent());
		// super.updateTreeItem(item); // needed to apply stylesheet to new item
		// }

		/**
		 * @param tc
		 * @param newTreeItem
		 * @return
		 * @return
		 */
		// TODO - use style class for warning and error
		private void setCSSClass(TreeTableRow<ProjectLibrariesTreeDAO> tc, TreeItem<ProjectLibrariesTreeDAO> newTreeItem) {
			// if (newTreeItem != null) {
			// tc.pseudoClassStateChanged(EDITABLE, newTreeItem.getValue().editablePropety().getValue());
			// }
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
		libraryTree.getRoot().getChildren().clear();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return the member tree selected item property.
	 */
	@Override
	public ReadOnlyObjectProperty<TreeItem<ProjectLibrariesTreeDAO>> getSelectable() {
		return libraryTree.getSelectionModel().selectedItemProperty();
	}

	@Override
	public ImageManager getImageManager() {
		if (imageMgr == null)
			throw new IllegalStateException("Image manger is null.");
		return imageMgr;
	}
}
