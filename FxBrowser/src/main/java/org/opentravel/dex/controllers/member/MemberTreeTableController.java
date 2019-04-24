/**
 * 
 */
package org.opentravel.dex.controllers.member;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.dex.controllers.DexController;
import org.opentravel.dex.controllers.DexIncludedControllerBase;
import org.opentravel.dex.controllers.DexMainController;
import org.opentravel.dex.events.DexFilterChangeEvent;
import org.opentravel.dex.events.DexMemberSelectionEvent;
import org.opentravel.model.OtmChildrenOwner;
import org.opentravel.model.OtmModelElement;
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.OtmTypeProvider;
import org.opentravel.model.otmFacets.OtmContributedFacet;
import org.opentravel.model.otmLibraryMembers.OtmContextualFacet;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.SortType;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

/**
 * Manage the library member navigation tree.
 * 
 * @author dmh
 *
 */
public class MemberTreeTableController extends DexIncludedControllerBase<OtmModelManager> implements DexController {
	private static Log log = LogFactory.getLog(MemberTreeTableController.class);

	// Column labels
	// TODO - externalize strings
	public static final String PREFIXCOLUMNLABEL = "Prefix";
	private static final String NAMECOLUMNLABEL = "Member";
	private static final String VERSIONCOLUMNLABEL = "Version";
	private static final String LIBRARYLABEL = "Library";
	private static final String ERRORLABEL = "Errors";

	// All event types listened to by this controller's handlers
	private static final EventType[] subscribedEvents = { DexFilterChangeEvent.FILTER_CHANGED,
			DexMemberSelectionEvent.MEMBER_SELECTED };
	private static final EventType[] publishedEvents = { DexMemberSelectionEvent.MEMBER_SELECTED };

	/*
	 * FXML injected
	 */
	@FXML
	TreeTableView<MemberDAO> memberTree;
	@FXML
	private VBox memberTreeController;

	//
	TreeItem<MemberDAO> root; // Root of the navigation tree. Is displayed.
	TreeTableColumn<MemberDAO, String> nameColumn; // an editable column

	OtmModelManager currentModelMgr; // this is postedData

	MemberFilterController filter = null;

	private boolean ignoreEvents = false;
	// By default, the tree is editable. Setting this to false will prevent edits.
	private boolean treeEditingEnabled = true;

	/**
	 * Construct a member tree table controller that can publish and receive events.
	 */
	public MemberTreeTableController() {
		super(subscribedEvents, publishedEvents);
	}

	/**
	 * Create columns
	 */
	private void buildColumns() {

		TreeTableColumn<MemberDAO, String> prefixColumn = new TreeTableColumn<>(PREFIXCOLUMNLABEL);
		prefixColumn.setCellValueFactory(new TreeItemPropertyValueFactory<MemberDAO, String>("prefix"));
		setColumnProps(prefixColumn, true, false, true, 100);
		prefixColumn.setStyle("-fx-alignment: CENTER-RIGHT;");

		nameColumn = new TreeTableColumn<>(NAMECOLUMNLABEL);
		nameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<MemberDAO, String>("name"));
		setColumnProps(nameColumn, true, true, true, 200);
		nameColumn.setSortType(SortType.ASCENDING);

		TreeTableColumn<MemberDAO, String> versionColumn = new TreeTableColumn<>(VERSIONCOLUMNLABEL);
		versionColumn.setCellValueFactory(new TreeItemPropertyValueFactory<MemberDAO, String>("version"));

		TreeTableColumn<MemberDAO, String> libColumn = new TreeTableColumn<>(LIBRARYLABEL);
		libColumn.setCellValueFactory(new TreeItemPropertyValueFactory<MemberDAO, String>("library"));

		TreeTableColumn<MemberDAO, String> errTextColumn = new TreeTableColumn<>(ERRORLABEL);
		errTextColumn.setCellValueFactory(new TreeItemPropertyValueFactory<MemberDAO, String>("error"));

		TreeTableColumn<MemberDAO, ImageView> valColumn = new TreeTableColumn<>("");
		valColumn.setCellFactory(c -> {
			return new TreeTableCell<MemberDAO, ImageView>() {
				@Override
				protected void updateItem(ImageView item, boolean empty) {
					super.updateItem(item, empty);
					// Provide imageView directly - does not update automatically as the observable property would
					// Provide tooltip showing validation results
					String name = "";
					if (!empty && getTreeTableRow() != null && getTreeTableRow().getItem() != null) {
						setGraphic(getTreeTableRow().getItem().getValue().validationImage());
						name = getTreeTableRow().getItem().getValue().getValidationFindingsAsString();
						if (!name.isEmpty())
							setTooltip(new Tooltip(name));
					} else {
						setGraphic(null);
						setTooltip(null);
					}
				}
			};
		});
		setColumnProps(valColumn, true, false, false, 25);

		// Add columns to table
		memberTree.getColumns().addAll(nameColumn, valColumn, libColumn, versionColumn, prefixColumn, errTextColumn);
		memberTree.getSortOrder().add(nameColumn);
	}

	@Override
	public void checkNodes() {
		if (memberTree == null)
			throw new IllegalStateException("Tree table view is null.");
	}

	/**
	 * {@inheritDoc} Remove all items from the member tree.
	 */
	@Override
	public void clear() {
		memberTree.getRoot().getChildren().clear();
	}

	/**
	 * Configure the controller for use by main controller.
	 */
	@Override
	public void configure(DexMainController parent) {
		super.configure(parent);
		log.debug("Configuring Member Tree Table.");
		eventPublisherNode = memberTreeController;
		configure(parent.getModelManager(), parent.getImageManager(), treeEditingEnabled);
	}

	/**
	 * Configure controller for use by non-main controllers.
	 * 
	 * @param modelMgr
	 *            must not be null
	 * @param imageMgr
	 *            may be null if no graphics are to presented.
	 * @param editable
	 *            sets tree editing enables
	 */
	public void configure(OtmModelManager modelMgr, ImageManager imageMgr, boolean editable) {
		if (modelMgr == null)
			throw new IllegalArgumentException("Model manager is null. Must configure member tree with model manager.");

		this.imageMgr = imageMgr;
		this.currentModelMgr = modelMgr;
		this.treeEditingEnabled = editable;

		// Set the hidden root item
		root = new TreeItem<>();
		root.setExpanded(true); // Startout fully expanded

		// Set up the TreeTable
		memberTree.setRoot(getRoot());
		memberTree.setShowRoot(false);
		memberTree.setEditable(true);
		memberTree.getSelectionModel().setCellSelectionEnabled(true); // allow individual cells to be edited
		memberTree.setTableMenuButtonVisible(true); // allow users to select columns
		// Enable context menus at the row level and add change listener for for applying style
		memberTree.setRowFactory((TreeTableView<MemberDAO> p) -> new MemberRowFactory(this));
		buildColumns();

		// Add listeners and event handlers
		memberTree.getSelectionModel().select(0);
		memberTree.setOnKeyReleased(this::keyReleased);
		memberTree.setOnMouseClicked(this::mouseClick);
		memberTree.getSelectionModel().selectedItemProperty()
				.addListener((v, old, newValue) -> memberSelectionListener(newValue));

		refresh();
	}

	/**
	 * Note: TreeItem class does not extend the Node class. Therefore, you cannot apply any visual effects or add menus
	 * to the tree items. Use the cell factory mechanism to overcome this obstacle and define as much custom behavior
	 * for the tree items as your application requires.
	 * 
	 * @param member
	 *            the Otm Library Member to add to the tree
	 * @param parent
	 *            the tree root or parent member
	 * @return
	 */
	public void createTreeItem(OtmLibraryMember member, TreeItem<MemberDAO> parent) {
		// log.debug("Creating member tree item for: " + member + " of type " + member.getClass().getSimpleName());

		// Apply Filter
		if (filter != null && !filter.isSelected(member))
			return;
		// Skip over contextual facets that have been injected into an object. Their contributed facets will be modeled.
		if ((member instanceof OtmContextualFacet && ((OtmContextualFacet) member).getWhereContributed() != null))
			return;

		// Create item for the library member
		TreeItem<MemberDAO> item = createTreeItem((OtmTypeProvider) member, parent);

		// Create and add items for children
		if (member instanceof OtmChildrenOwner)
			createChildrenItems((OtmChildrenOwner) member, item);
	}

	/**
	 * Create tree items for the type provider children of this child owning member
	 */
	private void createChildrenItems(OtmChildrenOwner member, TreeItem<MemberDAO> parentItem) {
		member.getChildren_TypeProviders().forEach(p -> {
			TreeItem<MemberDAO> cfItem = createTreeItem(p, parentItem);
			// Recurse for the contextual facet contributor which may have children that are also contextual facets
			if (p instanceof OtmContributedFacet && ((OtmContributedFacet) p).getContributor() != null)
				createChildrenItems(((OtmContributedFacet) p).getContributor(), cfItem);
		});
	}

	/**
	 * Create and add to tree with no conditional logic.
	 * 
	 * @return new tree item added to tree at the parent
	 */
	private TreeItem<MemberDAO> createTreeItem(OtmTypeProvider provider, TreeItem<MemberDAO> parent) {
		TreeItem<MemberDAO> item = new TreeItem<>(new MemberDAO(provider));
		item.setExpanded(false);
		if (parent != null)
			parent.getChildren().add(item);
		if (imageMgr != null)
			item.setGraphic(imageMgr.getView((OtmModelElement<?>) provider));
		return item;
	}

	public MemberFilterController getFilter() {
		return filter;
	}

	public TreeItem<MemberDAO> getRoot() {
		return root;
	}

	public MemberDAO getSelected() {
		return memberTree.getSelectionModel().getSelectedItem() != null
				? memberTree.getSelectionModel().getSelectedItem().getValue() : null;
	}

	private void handleEvent(DexFilterChangeEvent event) {
		if (!ignoreEvents)
			refresh();
	}

	private void handleEvent(DexMemberSelectionEvent event) {
		if (!ignoreEvents)
			select(event.getMember());
	}

	@Override
	public void handleEvent(Event event) {
		log.debug(event.getEventType() + " event received.");
		if (!ignoreEvents) {
			if (event instanceof DexMemberSelectionEvent)
				handleEvent((DexMemberSelectionEvent) event);
			if (event instanceof DexFilterChangeEvent)
				handleEvent((DexFilterChangeEvent) event);
			else
				refresh();
		}
	}

	public void keyReleased(KeyEvent event) {
		TreeItem<MemberDAO> item = memberTree.getSelectionModel().getSelectedItem();
		ObservableList<TreeTablePosition<MemberDAO, ?>> cells = memberTree.getSelectionModel().getSelectedCells();
		int row = memberTree.getSelectionModel().getSelectedIndex();
		log.debug("Selection row = " + row);
		if (event.getCode() == KeyCode.RIGHT) {
			memberTree.getSelectionModel().getSelectedItem().setExpanded(true);
			memberTree.getSelectionModel().select(row);
			// memberTree.getSelectionModel().focus(row);
			// Not sure how to: memberTree.getSelectionModel().requestFocus();
			// event.consume();
		} else if (event.getCode() == KeyCode.LEFT) {
			memberTree.getSelectionModel().getSelectedItem().setExpanded(false);
			memberTree.getSelectionModel().select(row);
			// memberTree.getSelectionModel().focus(row);
			// event.consume();
		}
	}

	/**
	 * Listener for selected library members in the tree table.
	 * 
	 * @param item
	 */
	private void memberSelectionListener(TreeItem<MemberDAO> item) {
		if (item == null)
			return;
		log.debug("Selection Listener: " + item.getValue());
		assert item != null;
		boolean editable = false;
		if (treeEditingEnabled && item.getValue() != null)
			editable = item.getValue().isEditable();
		nameColumn.setEditable(editable); // TODO - is this still useful?
		ignoreEvents = true;
		if (eventPublisherNode != null)
			eventPublisherNode.fireEvent(new DexMemberSelectionEvent(this, item));
		ignoreEvents = false;
	}

	public void mouseClick(MouseEvent event) {
		// this fires after the member selection listener
		if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2)
			log.debug("Double click selection: "
					+ memberTree.getSelectionModel().getSelectedItem().getValue().nameProperty().toString());
	}

	/**
	 * Get the library members from the model manager and put them into a cleared tree.
	 * 
	 * @param modelMgr
	 */
	@Override
	public void post(OtmModelManager modelMgr) {
		if (modelMgr != null && memberTree != null) {
			currentModelMgr = modelMgr;
			// if (getFilter() != null)
			// getFilter().clear();
			// create cells for members
			memberTree.getRoot().getChildren().clear();
			currentModelMgr.getMembers().forEach(m -> createTreeItem(m, root));
			try {
				memberTree.sort();
			} catch (Exception e) {
				// why does first sort always throw exception?
				log.debug("Exception sorting: " + e.getLocalizedMessage());
			}
		}
	}

	@Override
	public void refresh() {
		post(currentModelMgr);
	}

	public void select(OtmLibraryMember otm) {
		if (otm != null) {
			for (TreeItem<MemberDAO> item : memberTree.getRoot().getChildren()) {
				if (item.getValue().getValue() == otm) {
					int row = memberTree.getRow(item);
					// This may not highlight the row if the event comes from or goes to a different controller.
					Platform.runLater(() -> {
						ignoreEvents = true;
						memberTree.requestFocus();
						memberTree.getSelectionModel().clearAndSelect(row);
						memberTree.scrollTo(row);
						memberTree.getFocusModel().focus(row);
						ignoreEvents = false;
					});
					log.debug("Selected " + otm.getName() + " in member tree.");
					return;
				}
			}
			log.debug(otm.getName() + " not found in member tree.");
		}
	}

	public void setFilter(MemberFilterController filter) {
		this.filter = filter;
	}

	public void setOnMouseClicked(EventHandler<? super MouseEvent> handler) {
		memberTree.setOnMouseClicked(handler);
	}
}
