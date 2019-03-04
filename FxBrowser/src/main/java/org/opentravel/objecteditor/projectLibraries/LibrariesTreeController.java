/**
 * 
 */
package org.opentravel.objecteditor.projectLibraries;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.otmContainers.OtmLibrary;
import org.opentravel.objecteditor.DexController;
import org.opentravel.objecteditor.ObjectEditorController;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.SortType;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

/**
 * Manage the tree table view for libraries in projects (Library Tab)
 * 
 * @author dmh
 *
 */
@SuppressWarnings("restriction")
public class LibrariesTreeController implements DexController {
	private static Log log = LogFactory.getLog(LibrariesTreeController.class);

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

	private TreeTableView<LibraryDAO> libraryTree;
	private TreeItem<LibraryDAO> root; // Root of the tree.

	// Editable Columns
	// None

	private OtmModelManager modelMgr;
	private ImageManager imageMgr;
	private DexController parent;

	@SuppressWarnings("unchecked")
	public LibrariesTreeController(DexController parent, TreeTableView<LibraryDAO> view) {
		log.debug("Initializing project-library tree table.");

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
		libraryTree.setRoot(root);
		libraryTree.setShowRoot(false);
		libraryTree.setEditable(true);
		// libraryTree.getSelectionModel().setCellSelectionEnabled(true); // allow individual cells to be edited
		libraryTree.setTableMenuButtonVisible(true); // allow users to select columns

		// add a listener class with three parameters that invokes selection listener
		libraryTree.getSelectionModel().selectedItemProperty()
				.addListener((v, old, newValue) -> librarySelectionListener(newValue));

		// Set up the TreeTable
		buildColumns();

		// Enable context menus at the row level and add change listener for for applying style
		libraryTree.setRowFactory((TreeTableView<LibraryDAO> p) -> new LibraryRowFactory(this));

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
			TreeItem<LibraryDAO> latestItem = null;
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
	private void librarySelectionListener(TreeItem<LibraryDAO> item) {
		if (item == null || item.getValue() == null || item.getValue().getValue() == null)
			return;

		log.debug("Selection Listener: " + item.getValue().getValue());

		if (parent instanceof ObjectEditorController)
			if (item.getValue().getValue() instanceof OtmLibrary)
				((ObjectEditorController) parent).handleLibrarySelectionEvent(item.getValue().getValue());
	}

	public TreeItem<LibraryDAO> getRoot() {
		return root;
	}

	//
	// Create columns
	//
	private void buildColumns() {
		TreeTableColumn<LibraryDAO, String> prefixColumn = createStringColumn(PREFIXCOLUMNLABEL, "prefix", true, false,
				true, 0);
		TreeTableColumn<LibraryDAO, String> nameColumn = createStringColumn(NAMELABEL, "name", true, false, true, 200);
		TreeTableColumn<LibraryDAO, String> namespaceColumn = createStringColumn(NAMESPACELABEL, "namespace", true,
				false, true, 0);
		TreeTableColumn<LibraryDAO, String> versionColumn = createStringColumn(VERSIONLABEL, "version", true, false,
				true, 0);
		TreeTableColumn<LibraryDAO, String> statusColumn = createStringColumn(STATUSLABEL, "status", true, false, true,
				0);
		TreeTableColumn<LibraryDAO, String> stateColumn = createStringColumn(STATELABEL, "state", true, false, true, 0);
		TreeTableColumn<LibraryDAO, String> editColumn = createStringColumn(EDITABLELABEL, "edit", true, false, true,
				0);
		TreeTableColumn<LibraryDAO, String> lockedColumn = createStringColumn(LOCKEDLABEL, "locked", true, false, true,
				0);
		TreeTableColumn<LibraryDAO, Boolean> readonlyColumn = new TreeTableColumn<>(READONLYLABEL);
		readonlyColumn.setCellValueFactory(new TreeItemPropertyValueFactory<LibraryDAO, Boolean>("readonly"));
		TreeTableColumn<LibraryDAO, Integer> refColumn = new TreeTableColumn<>(REFERENCELABEL);
		refColumn.setCellValueFactory(new TreeItemPropertyValueFactory<LibraryDAO, Integer>("reference"));

		libraryTree.getColumns().addAll(nameColumn, prefixColumn, namespaceColumn, versionColumn, statusColumn,
				stateColumn, lockedColumn, refColumn, readonlyColumn, editColumn);

		// Start out sorted on names
		nameColumn.setSortType(SortType.ASCENDING);
		libraryTree.getSortOrder().add(nameColumn);
	}

	/**
	 * Create a treeTableColumn for a String and set properties.
	 * 
	 * @return
	 */
	private TreeTableColumn<LibraryDAO, String> createStringColumn(String label, String propertyName, boolean visable,
			boolean editable, boolean sortable, int width) {
		TreeTableColumn<LibraryDAO, String> c = new TreeTableColumn<>(label);
		c.setCellValueFactory(new TreeItemPropertyValueFactory<LibraryDAO, String>(propertyName));
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
	private TreeItem<LibraryDAO> createTreeItem(OtmLibrary library, TreeItem<LibraryDAO> parent) {
		if (library != null) {
			TreeItem<LibraryDAO> item = new TreeItem<>(new LibraryDAO(library));
			item.setExpanded(false);
			parent.getChildren().add(item);
			return item;
		}
		return null;
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
	public ReadOnlyObjectProperty<TreeItem<LibraryDAO>> getSelectable() {
		return libraryTree.getSelectionModel().selectedItemProperty();
	}

	@Override
	public ImageManager getImageManager() {
		if (imageMgr == null)
			throw new IllegalStateException("Image manger is null.");
		return imageMgr;
	}
}
