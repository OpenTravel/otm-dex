/**
 * 
 */
package org.opentravel.objecteditor.modelMembers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.dex.controllers.DexController;
import org.opentravel.dex.controllers.DexMainController;
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.OtmTypeProvider;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableColumn.SortType;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.ImageView;

/**
 * Manage the library member navigation tree.
 * 
 * @author dmh
 *
 */
@SuppressWarnings("restriction")
public class MemberTreeController implements DexController {
	private static Log log = LogFactory.getLog(MemberTreeController.class);

	public static final String PREFIXCOLUMNLABEL = "Prefix";
	private static final String NAMECOLUMNLABEL = "Member";

	private static final String VERSIONCOLUMNLABEL = "Version";

	private static final String LIBRARYLABEL = "Library";
	/**
	 * TreeTableRow is an IndexedCell, but rarely needs to be used by developers creating TreeTableView instances. The
	 * only time TreeTableRow is likely to be encountered at all by a developer is if they wish to create a custom
	 * rowFactory that replaces an entire row of a TreeTableView.
	 * 
	 * https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/TreeTableRow.html
	 */
	private static final PseudoClass EDITABLE = PseudoClass.getPseudoClass("editable");
	TreeTableView<MemberDAO> memberTree;
	TreeItem<MemberDAO> root; // Root of the navigation tree. Is displayed.

	TreeTableColumn<MemberDAO, String> nameColumn; // an editable column
	MemberFilterController filter = null;

	OtmModelManager currentModelMgr;

	ImageManager imageMgr;

	private DexMainController parentController;

	@SuppressWarnings("unchecked")
	public MemberTreeController(DexMainController parent, TreeTableView<MemberDAO> navTreeTableView,
			OtmModelManager model) {
		log.debug("Initializing navigation tree table.");

		if (navTreeTableView == null)
			throw new IllegalStateException("Tree table view is null.");

		// remember the view, and get an image manager for the stage.
		this.memberTree = navTreeTableView;
		imageMgr = parent.getImageManager();
		parentController = parent;

		// Set the hidden root item
		root = new TreeItem<>();
		root.setExpanded(true); // Startout fully expanded

		// Set up the TreeTable
		buildColumns();

		// create cells for members
		currentModelMgr = model;
		for (OtmLibraryMember<?> member : model.getMembers()) {
			createTreeItem(member, root);
		}

		navTreeTableView.getSelectionModel().select(0);
	}

	private void buildColumns() {
		memberTree.setRoot(getRoot());
		memberTree.setShowRoot(false);
		memberTree.setEditable(true);
		memberTree.getSelectionModel().setCellSelectionEnabled(true); // allow individual cells to be edited
		memberTree.setTableMenuButtonVisible(true); // allow users to select columns

		// Enable context menus at the row level and add change listener for for applying style
		memberTree.setRowFactory((TreeTableView<MemberDAO> p) -> new MemberRowFactory(this));

		// add a listener class with three parameters that invokes selection listener
		memberTree.getSelectionModel().selectedItemProperty()
				.addListener((v, old, newValue) -> memberSelectionListener(newValue));

		//
		// Create columns
		//
		TreeTableColumn<MemberDAO, String> prefixColumn = new TreeTableColumn<>(PREFIXCOLUMNLABEL);
		prefixColumn.setPrefWidth(100);
		prefixColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
		prefixColumn.setVisible(false); // Works - is true by default

		TreeTableColumn<MemberDAO, ImageView> iconColumn = new TreeTableColumn<>("");
		iconColumn.setPrefWidth(50);
		iconColumn.setSortable(false);

		nameColumn = new TreeTableColumn<>(NAMECOLUMNLABEL);
		nameColumn.setPrefWidth(150);
		nameColumn.setEditable(true);
		nameColumn.setSortable(true);
		nameColumn.setSortType(TreeTableColumn.SortType.DESCENDING);

		TreeTableColumn<MemberDAO, String> versionColumn = new TreeTableColumn<>(VERSIONCOLUMNLABEL);
		versionColumn.setCellValueFactory(new TreeItemPropertyValueFactory<MemberDAO, String>("version"));

		TreeTableColumn<MemberDAO, String> libColumn = new TreeTableColumn<>(LIBRARYLABEL);
		libColumn.setCellValueFactory(new TreeItemPropertyValueFactory<MemberDAO, String>("library"));

		// Add columns to table
		memberTree.getColumns().addAll(iconColumn, nameColumn, libColumn, versionColumn, prefixColumn);

		// Define cell content
		prefixColumn.setCellValueFactory(new TreeItemPropertyValueFactory<MemberDAO, String>("prefix"));

		iconColumn.setCellValueFactory((CellDataFeatures<MemberDAO, ImageView> p) -> {
			if (p.getValue() != null)
				p.getValue().setGraphic(p.getValue().getValue().getIcon(imageMgr));
			return null;
		});

		nameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<MemberDAO, String>("name"));
		nameColumn.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());

		// Start out sorted on names
		// nameColumn.setSortType(SortType.DESCENDING);
		nameColumn.setSortType(SortType.ASCENDING);
		memberTree.getSortOrder().add(nameColumn);
	}

	/**
	 * {@inheritDoc} Remove all items from the member tree.
	 */
	@Override
	public void clear() {
		memberTree.getRoot().getChildren().clear();
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
	TreeItem<MemberDAO> createTreeItem(OtmLibraryMember<?> member, TreeItem<MemberDAO> parent) {
		// Apply Filter
		if (filter != null && !filter.isSelected(member))
			return null;

		// Create item for the library member
		TreeItem<MemberDAO> item = new TreeItem<>(new MemberDAO(member));
		item.setExpanded(false);
		parent.getChildren().add(item);

		// Create items for the type provider children of this member
		for (OtmTypeProvider ele : member.getChildren_TypeProviders())
			createTreeItem(ele, item);

		return item;
	}

	private TreeItem<MemberDAO> createTreeItem(OtmTypeProvider ele, TreeItem<MemberDAO> parent) {
		TreeItem<MemberDAO> item = new TreeItem<>(new MemberDAO(ele));
		item.setExpanded(false);
		parent.getChildren().add(item);
		return item;
	}

	public MemberFilterController getFilter() {
		return filter;
	}

	public ImageManager getImageManager() {
		if (imageMgr == null)
			throw new IllegalStateException("Image manger is null.");
		return imageMgr;
	}

	public OtmModelManager getModelManager() {
		return currentModelMgr;
	}

	public TreeItem<MemberDAO> getRoot() {
		return root;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return the member tree selected item property.
	 */
	@Override
	public ReadOnlyObjectProperty<TreeItem<MemberDAO>> getSelectable() {
		return memberTree.getSelectionModel().selectedItemProperty();
	}

	/**
	 * Listener for selected library members.
	 * 
	 * @param item
	 */
	private void memberSelectionListener(TreeItem<MemberDAO> item) {
		if (item == null)
			return;
		// log.debug("Selection Listener: " + item.getValue());
		assert item != null;
		boolean editable = false;
		if (item.getValue() != null)
			editable = item.getValue().isEditable();
		nameColumn.setEditable(editable);
	}

	/**
	 * Get the library members from the model manager and put them into a cleared tree.
	 * 
	 * @param modelMgr
	 */
	public void post(OtmModelManager modelMgr) {
		if (modelMgr != null)
			currentModelMgr = modelMgr;
		if (getFilter() != null)
			getFilter().clear();
		refresh();
	}

	public void select(OtmLibraryMember<?> otm) {
		if (otm != null)
			select(otm.getName());
	}

	public void select(String name) {
		log.debug("Selecting member: " + name);
		// Find the row to select
		// TODO - how to strip prefix that can be in the name
		for (TreeItem<MemberDAO> item : memberTree.getRoot().getChildren()) {
			String testName = item.getValue().getValue().getName();
			if (item.getValue().getValue().getName().equals(name)) {
				memberTree.getSelectionModel().select(item);
				memberTree.scrollTo(memberTree.getRow(item));
				return;
			}
		}
		log.debug(name + " not found.");
	}

	@Override
	public void refresh() {
		// create cells for members
		memberTree.getRoot().getChildren().clear();
		for (OtmLibraryMember<?> member : currentModelMgr.getMembers()) {
			createTreeItem(member, root);
		}
		try {
			memberTree.sort();
		} catch (Exception e) {
			// FIXME - why does first sort always throw exception?
			log.debug("Exception sorting: " + e.getLocalizedMessage());
		}
	}

	public void setFilter(MemberFilterController filter) {
		this.filter = filter;
	}

	public void postStatus(String string) {
		parentController.postStatus(string);
	}

	public void postProgress(double percentDone) {
		parentController.postProgress(percentDone);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opentravel.objecteditor.DexController#initialize()
	 */
	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opentravel.objecteditor.DexController#checkNodes()
	 */
	@Override
	public void checkNodes() {
		// TODO Auto-generated method stub

	}

}
