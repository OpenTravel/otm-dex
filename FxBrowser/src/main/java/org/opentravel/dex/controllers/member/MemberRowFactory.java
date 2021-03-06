/**
 * 
 */
package org.opentravel.dex.controllers.member;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
@SuppressWarnings("squid:MaximumInheritanceDepth")
public final class MemberRowFactory extends TreeTableRow<MemberAndProvidersDAO> {
	private static Log log = LogFactory.getLog(MemberRowFactory.class);

	private final ContextMenu addMenu = new ContextMenu();
	private static final PseudoClass EDITABLE = PseudoClass.getPseudoClass("editable");
	private MemberTreeTableController controller;

	public MemberRowFactory(MemberTreeTableController controller) {
		this.controller = controller;

		// Create Context menu
		MenuItem addObject = new MenuItem("Add Object (Future)");
		addMenu.getItems().add(addObject);
		setContextMenu(addMenu);

		// Create action for addObject event
		addObject.setOnAction(this::addMemberEvent);

		// Set style listener (css class)
		treeItemProperty().addListener((obs, oldTreeItem, newTreeItem) -> setCSSClass(this, newTreeItem));

		// Not sure this helps!
		if (getTreeItem() != null && getTreeItem().getValue() != null) {
			setEditable(getTreeItem().getValue().isEditable());
		}
	}

	/**
	 * Add a new member to the tree
	 * 
	 * @param t
	 */
	private void addMemberEvent(ActionEvent t) {
		// Works - but business logic is wrong.
		// TreeItem<LibraryMemberTreeDAO> item = controller
		// .createTreeItem(new OtmCoreObject("new", controller.getModelManager()), getTreeItem().getParent());
		// super.updateTreeItem(item); // needed to apply stylesheet to new item
	}

	/**
	 * @param tc
	 * @param newTreeItem
	 * @return
	 * @return
	 */
	// TODO - use style class for warning and error
	private void setCSSClass(TreeTableRow<MemberAndProvidersDAO> tc, TreeItem<MemberAndProvidersDAO> newTreeItem) {
		if (newTreeItem != null) {
			tc.pseudoClassStateChanged(EDITABLE, newTreeItem.getValue().isEditable());
		}
	}
	// TODO - investigate using ControlsFX for decoration
	// TODO - Dragboard db = r.startDragAndDrop(TransferMode.MOVE);
	// https://www.programcreek.com/java-api-examples/index.php?api=javafx.scene.control.TreeTableRow
}
