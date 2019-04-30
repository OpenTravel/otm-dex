/**
 * 
 */
package org.opentravel.dex.controllers.member.properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.actions.DexActionManager.DexActions;
import org.opentravel.model.OtmObject;
import org.opentravel.model.OtmPropertyOwner;
import org.opentravel.model.OtmTypeUser;
import org.opentravel.model.otmFacets.OtmFacet;
import org.opentravel.model.otmProperties.OtmProperty;
import org.opentravel.schemacompiler.model.TLProperty;

import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableRow;

/**
 * @author dmh
 *
 */
/**
 * TreeTableRow is an IndexedCell, but rarely needs to be used by developers creating TreeTableView instances. The only
 * time TreeTableRow is likely to be encountered at all by a developer is if they wish to create a custom rowFactory
 * that replaces an entire row of a TreeTableView.
 * 
 * https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/TreeTableRow.html
 */
public final class MemberPropertiesRowFactory extends TreeTableRow<PropertiesDAO> {
	private static Log log = LogFactory.getLog(MemberPropertiesRowFactory.class);

	private static final PseudoClass EDITABLE = PseudoClass.getPseudoClass("editable");
	private static final PseudoClass DIVIDER = PseudoClass.getPseudoClass("divider");
	private final ContextMenu addMenu = new ContextMenu();
	private MemberPropertiesTreeTableController controller;

	// Constructor does not have access to content, just the empty row
	public MemberPropertiesRowFactory(MemberPropertiesTreeTableController controller) {
		this.controller = controller;

		// Create Context menu
		MenuItem addObject = new MenuItem("Add Property (Demo)");
		MenuItem changeType = new MenuItem("Change Assigned Type");
		MenuItem upObject = new MenuItem("Move Up (Future)");
		MenuItem downObject = new MenuItem("Move Down (Future)");
		SeparatorMenuItem separator = new SeparatorMenuItem();
		addMenu.getItems().addAll(addObject, changeType, separator, upObject, downObject);
		setContextMenu(addMenu);

		// Create action for addObject event
		addObject.setOnAction(this::addMemberEvent);
		changeType.setOnAction(e -> changeAssignedType());

		// // Set editable style listener (css class)
		treeItemProperty().addListener((obs, oldTreeItem, newTreeItem) -> setCSSClass(this, newTreeItem));

		// treeItemProperty().getValue() is always null!
		// getItem() is always null!

		// log.debug("");
	}

	/**
	 * Add a new member to the tree
	 * 
	 * @param t
	 */
	private void addMemberEvent(ActionEvent t) {
		log.debug("TODO - implement add member event in Properties Row Factory.");
		// TreeItem<OtmTreeTableNode> item = createTreeItem(new OtmCoreObject("new"), getTreeItem().getParent());
		// super.updateTreeItem(item); // needed to apply stylesheet to new item

		TreeItem<PropertiesDAO> treeItem = getTreeItem();
		if (treeItem != null) {
			OtmObject otm = treeItem.getValue().getValue();

			// TODO - move to action handler
			//
			// Find child owning parent
			OtmPropertyOwner owner = null;
			if (otm instanceof OtmPropertyOwner)
				owner = (OtmPropertyOwner) otm;
			else if (otm instanceof OtmProperty<?>)
				owner = ((OtmProperty<?>) otm).getParent();

			if (owner instanceof OtmPropertyOwner) {
				TLProperty newTL = new TLProperty();
				newTL.setName("New");
				OtmProperty newP = owner.add(newTL);
				if (newP != null) {
					controller.createTreeItem(newP, getTreeItem().getParent(), true);
					controller.refresh();
					// Post this row
				}
			}
		}

	}

	private void changeAssignedType() {
		TreeItem<PropertiesDAO> treeItem = getTreeItem();
		if (treeItem != null && treeItem.getValue() != null && treeItem.getValue().getValue() instanceof OtmTypeUser) {
			OtmTypeUser user = (OtmTypeUser) treeItem.getValue().getValue();
			user.getActionManager().addAction(DexActions.TYPECHANGE, user);
		}
		controller.getMainController().refresh();
	}

	/**
	 * @param tc
	 * @param newTreeItem
	 * @return
	 */
	// TODO - use style class for warning and error
	private void setCSSClass(TreeTableRow<PropertiesDAO> tc, TreeItem<PropertiesDAO> newTreeItem) {
		if (newTreeItem != null) {
			// Disable context menu items
			getContextMenu().getItems().forEach(i -> i.setDisable(!newTreeItem.getValue().isEditable()));

			if (newTreeItem.getValue().getValue() instanceof OtmFacet) {
				// Make facets dividers
				tc.pseudoClassStateChanged(DIVIDER, true);
				tc.setEditable(false);
			} else {
				// Set Editable style and state
				tc.pseudoClassStateChanged(DIVIDER, false);
				tc.pseudoClassStateChanged(EDITABLE, newTreeItem.getValue().isEditable());
				tc.setEditable(newTreeItem.getValue().isEditable());
			}
		}
	}
	// TODO - investigate using ControlsFX for decoration
	// TODO - Dragboard db = r.startDragAndDrop(TransferMode.MOVE);
	// https://www.programcreek.com/java-api-examples/index.php?api=javafx.scene.control.TreeTableRow

	// startEdit, commitEdit, cancelEdit do not run on row

	// Runs often, but no access to cells in the row to act upon them
	// @Override
	// public void updateItem(OtmTreeTableNode item, boolean empty) {
	// super.updateItem(item, empty);
	// }
}
