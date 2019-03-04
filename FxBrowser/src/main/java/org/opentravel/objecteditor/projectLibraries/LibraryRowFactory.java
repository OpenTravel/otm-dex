/**
 * 
 */
package org.opentravel.objecteditor.projectLibraries;

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
// @SuppressWarnings("restriction")
public final class LibraryRowFactory extends TreeTableRow<LibraryDAO> {
	private static Log log = LogFactory.getLog(LibraryRowFactory.class);

	private final ContextMenu addMenu = new ContextMenu();
	private static final PseudoClass EDITABLE = PseudoClass.getPseudoClass("editable");
	private LibrariesTreeController controller;

	public LibraryRowFactory(LibrariesTreeController controller) {
		this.controller = controller;

		// Create Context menu
		MenuItem addObject = new MenuItem("Show Where Used (future)");
		addMenu.getItems().add(addObject);
		addObject.setOnAction(this::addMemberEvent);

		addObject = new MenuItem("Update (Future)");
		addMenu.getItems().add(addObject);
		addObject.setOnAction(this::addMemberEvent);

		setContextMenu(addMenu);

		// Create action for addObject event

		// Set style listener (css class)
		treeItemProperty().addListener((obs, oldTreeItem, newTreeItem) -> setCSSClass(this, newTreeItem));
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
	private void setCSSClass(TreeTableRow<LibraryDAO> tc, TreeItem<LibraryDAO> newTreeItem) {
		if (newTreeItem != null) {
			tc.pseudoClassStateChanged(EDITABLE, newTreeItem.getValue().getValue().isEditable());
		}
	}
}
