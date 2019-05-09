/**
 * 
 */
package org.opentravel.dex.controllers.member.usage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.dex.controllers.DexController;
import org.opentravel.dex.controllers.DexIncludedControllerBase;
import org.opentravel.dex.controllers.DexMainController;
import org.opentravel.dex.controllers.member.properties.PropertiesDAO;
import org.opentravel.dex.events.DexMemberSelectionEvent;
import org.opentravel.dex.events.DexModelChangeEvent;
import org.opentravel.model.OtmChildrenOwner;
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.OtmTypeProvider;
import org.opentravel.model.otmFacets.OtmContributedFacet;
import org.opentravel.model.otmLibraryMembers.OtmContextualFacet;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

/**
 * Manage the library member where used view.
 * 
 * @author dmh
 *
 */
public class TypeUsersTreeController extends DexIncludedControllerBase<OtmModelManager> implements DexController {
	private static Log log = LogFactory.getLog(TypeUsersTreeController.class);

	/*
	 * FXML injected
	 */
	@FXML
	TreeView<PropertiesDAO> typeUsersTree;
	@FXML
	private VBox memberWhereUsed;

	TreeItem<PropertiesDAO> root; // Root of the navigation tree. Is displayed.
	OtmModelManager currentModelMgr; // this is postedData
	private boolean ignoreEvents = false;

	// All event types listened to by this controller's handlers
	private static final EventType[] subscribedEvents = { DexMemberSelectionEvent.MEMBER_SELECTED,
			DexModelChangeEvent.MODEL_CHANGED };
	private static final EventType[] publishedEvents = {};

	/**
	 * Construct a member tree table controller that can publish and receive events.
	 */
	public TypeUsersTreeController() {
		super(subscribedEvents, publishedEvents);
	}

	@Override
	public void checkNodes() {
		if (typeUsersTree == null)
			throw new IllegalStateException("Type Users Tree view is null.");
	}

	/**
	 * {@inheritDoc} Remove all items from the member tree.
	 */
	@Override
	public void clear() {
		typeUsersTree.getRoot().getChildren().clear();
	}

	/**
	 * Configure the controller for use by main controller.
	 */
	@Override
	public void configure(DexMainController parent) {
		super.configure(parent);
		log.debug("Configuring Member Tree Table.");
		eventPublisherNode = memberWhereUsed;
		configure(parent.getModelManager(), parent.getImageManager());
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
	public void configure(OtmModelManager modelMgr, ImageManager imageMgr) {
		if (modelMgr == null)
			throw new IllegalArgumentException("Model manager is null. Must configure member tree with model manager.");

		this.imageMgr = imageMgr;
		this.currentModelMgr = modelMgr;

		// Set the hidden root item
		root = new TreeItem<>();
		root.setExpanded(true); // Startout fully expanded

		// Set up the TreeTable
		typeUsersTree.setRoot(getRoot());
		typeUsersTree.setShowRoot(false);
		typeUsersTree.setEditable(true);
		// typeUsersTree.getSelectionModel().setCellSelectionEnabled(true); // allow individual cells to be edited
		// typeUsersTree.setTableMenuButtonVisible(true); // allow users to select columns
		// Enable context menus at the row level and add change listener for for applying style

		// TODO whereUsedTreeTable.setRowFactory((TreeTableView<PropertiesDAO> p) -> new MemberRowFactory(this));

		// buildColumns();

		// Add listeners and event handlers
		typeUsersTree.getSelectionModel().select(0);
		// whereUsedTreeTable.setOnKeyReleased(this::keyReleased);
		// whereUsedTreeTable.setOnMouseClicked(this::mouseClick);
		typeUsersTree.getSelectionModel().selectedItemProperty()
				.addListener((v, old, newValue) -> memberSelectionListener(newValue));

		log.debug("Where used table configured.");
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
	public void createTreeItem(OtmLibraryMember member, TreeItem<PropertiesDAO> parent) {
		// log.debug("Creating member tree item for: " + member + " of type " + member.getClass().getSimpleName());

		// Apply Filter
		// if (filter != null && !filter.isSelected(member))
		// return;
		// Skip over contextual facets that have been injected into an object. Their contributed facets will be modeled.
		if ((member instanceof OtmContextualFacet && ((OtmContextualFacet) member).getWhereContributed() != null))
			return;

		// Create item for the library member
		TreeItem<PropertiesDAO> item = createTreeItem((OtmTypeProvider) member, parent);

		// Create and add items for children
		if (member instanceof OtmChildrenOwner)
			createChildrenItems(member, item);
	}

	/**
	 * Create tree items for the type provider children of this child owning member
	 */
	private void createChildrenItems(OtmChildrenOwner member, TreeItem<PropertiesDAO> parentItem) {
		member.getChildrenTypeProviders().forEach(p -> {
			TreeItem<PropertiesDAO> cfItem = createTreeItem(p, parentItem);
			// Only user contextual facet for recursing
			if (p instanceof OtmContributedFacet && ((OtmContributedFacet) p).getContributor() != null)
				p = ((OtmContributedFacet) p).getContributor();
			// Recurse
			if (p instanceof OtmChildrenOwner)
				createChildrenItems((OtmChildrenOwner) p, cfItem);
		});
	}

	/**
	 * Create and add to tree with no conditional logic.
	 * 
	 * @return new tree item added to tree at the parent
	 */
	private TreeItem<PropertiesDAO> createTreeItem(OtmTypeProvider provider, TreeItem<PropertiesDAO> parent) {
		return new PropertiesDAO((provider), this).createTreeItem(parent, true);
		// TreeItem<PropertiesDAO> item = new TreeItem<>(new PropertiesDAO(provider));
		// item.setExpanded(false);
		// if (parent != null)
		// parent.getChildren().add(item);
		// if (imageMgr != null) {
		// ImageView graphic = imageMgr.getView(provider);
		// item.setGraphic(graphic);
		// Tooltip.install(graphic, new Tooltip(provider.getObjectTypeName()));
		// }
		// return item;
	}

	// public MemberFilterController getFilter() {
	// return filter;
	// }

	public TreeItem<PropertiesDAO> getRoot() {
		return root;
	}

	public PropertiesDAO getSelected() {
		return typeUsersTree.getSelectionModel().getSelectedItem() != null
				? typeUsersTree.getSelectionModel().getSelectedItem().getValue() : null;
	}

	// private void handleEvent(DexFilterChangeEvent event) {
	// if (!ignoreEvents)
	// refresh();
	// }

	private void handleEvent(DexMemberSelectionEvent event) {
		if (!ignoreEvents)
			post(event.getMember());
	}

	@Override
	public void handleEvent(Event event) {
		log.debug(event.getEventType() + " event received.  Ignore? " + ignoreEvents);
		if (!ignoreEvents) {
			if (event instanceof DexMemberSelectionEvent)
				handleEvent((DexMemberSelectionEvent) event);
			// if (event instanceof DexFilterChangeEvent)
			// handleEvent((DexFilterChangeEvent) event);
			if (event instanceof DexModelChangeEvent)
				clear();
			else
				refresh();
		}
	}

	public void keyReleased(KeyEvent event) {
		// TreeItem<PropertiesDAO> item = whereUsedTreeTable.getSelectionModel().getSelectedItem();
		// ObservableList<TreeTablePosition<PropertiesDAO, ?>> cells =
		// whereUsedTreeTable.getSelectionModel().getSelectedCells();
		int row = typeUsersTree.getSelectionModel().getSelectedIndex();
		log.debug("Selection row = " + row);
		if (event.getCode() == KeyCode.RIGHT) {
			typeUsersTree.getSelectionModel().getSelectedItem().setExpanded(true);
			typeUsersTree.getSelectionModel().select(row);
			// whereUsedTreeTable.getSelectionModel().focus(row);
			// Not sure how to: whereUsedTreeTable.getSelectionModel().requestFocus();
			// event.consume();
		} else if (event.getCode() == KeyCode.LEFT) {
			typeUsersTree.getSelectionModel().getSelectedItem().setExpanded(false);
			typeUsersTree.getSelectionModel().select(row);
			// whereUsedTreeTable.getSelectionModel().focus(row);
			// event.consume();
		}
	}

	/**
	 * Listener for selected library members in the tree table.
	 * 
	 * @param item
	 */
	private void memberSelectionListener(TreeItem<PropertiesDAO> item) {
		if (item == null)
			return;
		log.debug("Selection Listener: " + item.getValue());
		assert item != null;
		boolean editable = false;
		if (item.getValue() != null)
			editable = item.getValue().isEditable();
		// nameColumn.setEditable(editable); // TODO - is this still useful?
		ignoreEvents = true;
		// if (eventPublisherNode != null)
		// eventPublisherNode.fireEvent(new DexMemberSelectionEvent(this, item));
		ignoreEvents = false;
	}

	public void mouseClick(MouseEvent event) {
		// this fires after the member selection listener
		if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2)
			log.debug("Double click selection: ");
		// + whereUsedTreeTable.getSelectionModel().getSelectedItem().getValue().nameProperty().toString());
	}

	/**
	 * Get the library members from the model manager and put them into a cleared tree.
	 * 
	 * @param modelMgr
	 */
	// @Override
	public void post(OtmLibraryMember member) {
		clear();
		if (member != null)
			new PropertiesDAO(member, this).createChildrenItems(root);
	}

	@Override
	public void refresh() {
		// post(currentModelMgr);
		ignoreEvents = false;
	}

	public void select(OtmLibraryMember otm) {
		if (otm != null) {
			for (TreeItem<PropertiesDAO> item : typeUsersTree.getRoot().getChildren()) {
				if (item.getValue().getValue() == otm) {
					int row = typeUsersTree.getRow(item);
					// This may not highlight the row if the event comes from or goes to a different controller.
					Platform.runLater(() -> {
						// ignoreEvents = true;
						typeUsersTree.requestFocus();
						typeUsersTree.getSelectionModel().clearAndSelect(row);
						typeUsersTree.scrollTo(row);
						typeUsersTree.getFocusModel().focus(row);
						// ignoreEvents = false;
					});
					log.debug("Selected " + otm.getName() + " in member tree.");
					return;
				}
			}
			log.debug(otm.getName() + " not found in member tree.");
		}
	}

	public void setOnMouseClicked(EventHandler<? super MouseEvent> handler) {
		typeUsersTree.setOnMouseClicked(handler);
	}
}
