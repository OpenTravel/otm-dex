/**
 * 
 */
package org.opentravel.objecteditor.memberProperties;

import org.opentravel.model.otmFacets.OtmFacet;

import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
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
@SuppressWarnings("restriction")
public final class PropertiesRowFactory extends TreeTableRow<PropertiesDAO> {
	private static final PseudoClass EDITABLE = PseudoClass.getPseudoClass("editable");
	private static final PseudoClass DIVIDER = PseudoClass.getPseudoClass("divider");
	private final ContextMenu addMenu = new ContextMenu();

	public PropertiesRowFactory() {
		// Create Context menu
		MenuItem addObject = new MenuItem("Add Property (Future)");
		MenuItem upObject = new MenuItem("Move Up (Future)");
		MenuItem downObject = new MenuItem("Move Down (Future)");
		addMenu.getItems().addAll(addObject, upObject, downObject);
		setContextMenu(addMenu);

		// Create action for addObject event
		addObject.setOnAction(this::addMemberEvent);

		// // Set editable style listener (css class)
		treeItemProperty().addListener((obs, oldTreeItem, newTreeItem) -> setCSSClass(this, newTreeItem));

		// treeItemProperty().getValue() is always null!
		// getItem() is always null!

		// System.out.println("");
	}

	/**
	 * Add a new member to the tree
	 * 
	 * @param t
	 */
	private void addMemberEvent(ActionEvent t) {
		System.out.println("TODO - implement add member event in Property Row Factory.");
		// TreeItem<OtmTreeTableNode> item = createTreeItem(new OtmCoreObject("new"), getTreeItem().getParent());
		// super.updateTreeItem(item); // needed to apply stylesheet to new item
	}

	/**
	 * @param tc
	 * @param newTreeItem
	 * @return
	 */
	// TODO - use style class for warning and error
	private void setCSSClass(TreeTableRow<PropertiesDAO> tc,
			TreeItem<PropertiesDAO> newTreeItem) {
		if (newTreeItem != null) {
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
