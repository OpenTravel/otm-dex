/**
 * 
 */
package org.opentravel.dex.controllers.member.usage;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.dex.controllers.DexController;
import org.opentravel.dex.controllers.DexIncludedControllerBase;
import org.opentravel.dex.controllers.DexMainController;
import org.opentravel.dex.events.DexMemberSelectionEvent;
import org.opentravel.dex.events.DexModelChangeEvent;
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;

/**
 * Manage where-used tree for the posted library member. (1st column in tab)
 * 
 * @author dmh
 *
 */
public class UsersTreeController extends DexIncludedControllerBase<OtmModelManager> implements DexController {
	private static Log log = LogFactory.getLog(UsersTreeController.class);

	/*
	 * FXML injected
	 */
	@FXML
	TreeView<MemberAndUsersDAO> usersTree;
	@FXML
	private VBox memberWhereUsed;

	TreeItem<MemberAndUsersDAO> root; // Root of the navigation tree. Is displayed.
	private boolean ignoreEvents = false;
	private OtmLibraryMember postedMember = null;

	// All event types listened to by this controller's handlers
	private static final EventType[] subscribedEvents = { DexMemberSelectionEvent.MEMBER_SELECTED,
			DexModelChangeEvent.MODEL_CHANGED };
	private static final EventType[] publishedEvents = { DexMemberSelectionEvent.MEMBER_SELECTED };

	/**
	 * Construct a member tree table controller that can publish and receive events.
	 */
	public UsersTreeController() {
		super(subscribedEvents, publishedEvents);
	}

	@Override
	public void checkNodes() {
		if (usersTree == null)
			throw new IllegalStateException("Type Users Tree view is null.");
	}

	/**
	 * {@inheritDoc} Remove all items from the member tree.
	 */
	@Override
	public void clear() {
		usersTree.getRoot().getChildren().clear();
		postedMember = null;
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
		// this.currentModelMgr = modelMgr;

		// Set the hidden root item
		root = new TreeItem<>();
		root.setExpanded(true); // Startout fully expanded

		// Set up the TreeTable
		usersTree.setRoot(getRoot());
		usersTree.setShowRoot(false);
		usersTree.setEditable(true);

		// Add listeners and event handlers
		usersTree.getSelectionModel().select(0);
		// usersTree.getSelectionModel().selectedItemProperty()
		// .addListener((v, old, newValue) -> memberSelectionListener(newValue));

		log.debug("Where used table configured.");
		refresh();
	}

	/**
	 * For the member, get all members that use this member or any of its descendants as types.
	 * <p>
	 * Note: TreeItem class does not extend the Node class.
	 * 
	 * @param member
	 *            post the where-used types for the member.
	 * @param parent
	 *            the tree root or parent member
	 * @return
	 */
	public void createTreeItems(OtmLibraryMember member) {
		if (member == null)
			return;
		log.debug("Creating member tree item for: " + member + " of type " + member.getClass().getSimpleName());

		// Get all providers for this member
		Collection<OtmLibraryMember> whereUsed = member.getWhereUsed();
		whereUsed.forEach(wu -> new MemberAndUsersDAO(wu).createTreeItem(imageMgr, root));

	}

	// /**
	// * Create tree items for the type provider children of this child owning member
	// */
	// private void createChildrenItems(OtmChildrenOwner childOwner, TreeItem<MemberAndUsersDAO> parentItem) {
	// log.debug("Creating member children for: " + childOwner + " of type " + childOwner.getClass().getSimpleName());
	// Collection<OtmTypeUser> users = childOwner.getDescendantsTypeUsers();
	// childOwner.getDescendantsTypeUsers().forEach(u -> {
	// TreeItem<MemberAndUsersDAO> cfItem = new MemberAndUsersDAO(u).createTreeItem(imageMgr, parentItem);
	// // contextual facet are not type users and will not be listed here
	//
	// // Recurse
	// if (u instanceof OtmChildrenOwner && !((OtmChildrenOwner) u).getChildren().isEmpty())
	// createChildrenItems((OtmChildrenOwner) u, cfItem);
	// });
	// }

	public TreeItem<MemberAndUsersDAO> getRoot() {
		return root;
	}

	public MemberAndUsersDAO getSelected() {
		return usersTree.getSelectionModel().getSelectedItem() != null
				? usersTree.getSelectionModel().getSelectedItem().getValue() : null;
	}

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
			if (event instanceof DexModelChangeEvent)
				clear();
			else
				refresh();
		}
	}

	// public void keyReleased(KeyEvent event) {
	// // TreeItem<MemberDAO> item = whereUsedTreeTable.getSelectionModel().getSelectedItem();
	// // ObservableList<TreeTablePosition<MemberDAO, ?>> cells =
	// // whereUsedTreeTable.getSelectionModel().getSelectedCells();
	// int row = usersTree.getSelectionModel().getSelectedIndex();
	// log.debug("Selection row = " + row);
	// if (event.getCode() == KeyCode.RIGHT) {
	// usersTree.getSelectionModel().getSelectedItem().setExpanded(true);
	// usersTree.getSelectionModel().select(row);
	// // whereUsedTreeTable.getSelectionModel().focus(row);
	// // Not sure how to: whereUsedTreeTable.getSelectionModel().requestFocus();
	// // event.consume();
	// } else if (event.getCode() == KeyCode.LEFT) {
	// usersTree.getSelectionModel().getSelectedItem().setExpanded(false);
	// usersTree.getSelectionModel().select(row);
	// // whereUsedTreeTable.getSelectionModel().focus(row);
	// // event.consume();
	// }
	// }

	// /**
	// * Listener for selected library members in the tree table.
	// *
	// * @param item
	// */
	// private void memberSelectionListener(TreeItem<MemberAndUsersDAO> item) {
	// if (item == null)
	// return;
	// log.debug("Selection Listener: " + item.getValue());
	// ignoreEvents = true;
	// if (eventPublisherNode != null)
	// eventPublisherNode.fireEvent(new DexMemberSelectionEvent(this, item));
	// ignoreEvents = false;
	// }

	// public void mouseClick(MouseEvent event) {
	// // this fires after the member selection listener
	// if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2)
	// log.debug("Double click selection: ");
	// // + whereUsedTreeTable.getSelectionModel().getSelectedItem().getValue().nameProperty().toString());
	// }

	/**
	 * Get the library members from the model manager and put them into a cleared tree.
	 * 
	 * @param modelMgr
	 */
	@Override
	public void post(OtmModelManager modelMgr) {
		// No-op
	}

	public void post(OtmLibraryMember member) {
		clear();
		postedMember = member;
		createTreeItems(postedMember);
	}

	@Override
	public void refresh() {
		post(postedMember);
		ignoreEvents = false;
	}

	public void select(OtmLibraryMember otm) {
		if (otm != null) {
			for (TreeItem<MemberAndUsersDAO> item : usersTree.getRoot().getChildren()) {
				if (item.getValue().getValue() == otm) {
					int row = usersTree.getRow(item);
					// This may not highlight the row if the event comes from or goes to a different controller.
					Platform.runLater(() -> {
						// ignoreEvents = true;
						usersTree.requestFocus();
						usersTree.getSelectionModel().clearAndSelect(row);
						usersTree.scrollTo(row);
						usersTree.getFocusModel().focus(row);
						// ignoreEvents = false;
					});
					log.debug("Selected " + otm.getName() + " in member tree.");
					return;
				}
			}
			log.debug(otm.getName() + " not found in member tree.");
		}
	}

	// public void setOnMouseClicked(EventHandler<? super MouseEvent> handler) {
	// usersTree.setOnMouseClicked(handler);
	// }
}
