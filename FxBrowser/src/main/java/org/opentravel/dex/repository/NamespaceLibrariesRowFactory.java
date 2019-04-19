/**
 * 
 */
package org.opentravel.dex.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.controllers.DexMainController;
import org.opentravel.dex.controllers.popup.UnlockLibraryDialogContoller;
import org.opentravel.dex.tasks.repository.LockItemTask;
import org.opentravel.dex.tasks.repository.UnlockItemTask;
import org.opentravel.schemacompiler.repository.RepositoryItem;

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
@SuppressWarnings("squid:MaximumInheritanceDepth")
public final class NamespaceLibrariesRowFactory extends TreeTableRow<RepoItemDAO> {
	private static Log log = LogFactory.getLog(NamespaceLibrariesRowFactory.class);

	private static final PseudoClass EDITABLE = PseudoClass.getPseudoClass("editable");
	private static final PseudoClass DIVIDER = PseudoClass.getPseudoClass("divider");
	private NamespaceLibrariesTreeTableController controller;
	private final ContextMenu contextMenu = new ContextMenu();
	MenuItem lockLibrary;
	MenuItem unlockLibrary;
	MenuItem promoteLibrary;

	private DexMainController mainController;

	public NamespaceLibrariesRowFactory(NamespaceLibrariesTreeTableController controller) {
		this.controller = controller;
		mainController = controller.getMainController();

		// Create Context menu
		lockLibrary = new MenuItem("Lock");
		unlockLibrary = new MenuItem("Unlock");
		promoteLibrary = new MenuItem("Promote (Future)");
		contextMenu.getItems().addAll(lockLibrary, unlockLibrary, promoteLibrary);
		setContextMenu(contextMenu);

		// The item behind this row - NOT Available!
		// TreeItem<PropertiesDAO> x = this.getTreeItem();
		// PropertiesDAO y = getItem();
		// OtmModelElement<?> otm = getItem().getValue();
		// this.setUserData(otm);

		// Create action for events
		lockLibrary.setOnAction((e) -> lockLibraryEventHandler());
		unlockLibrary.setOnAction((e) -> unlockLibraryEventHandler());
		promoteLibrary.setOnAction(this::promoteLibraryEventHandler);

		// // Set editable style listener (css class)
		treeItemProperty().addListener((obs, oldTreeItem, newTreeItem) -> setCSSClass(this, newTreeItem));

		// log.debug("");
	}

	/**
	 * Add a new member to the tree
	 * 
	 */
	private void lockLibraryEventHandler() {
		log.debug("Lock in Row Factory.");
		new LockItemTask(controller.getSelectedItem().getValue(), new RepositoryResultHandler(mainController),
				mainController.getStatusController()).go();
	}

	private void unlockLibraryEventHandler() {
		log.debug("Unlock in Row Factory.");
		UnlockLibraryDialogContoller uldc = UnlockLibraryDialogContoller.init();
		uldc.showAndWait("");
		boolean commitWIP = uldc.getCommitState();
		String remarks = uldc.getCommitRemarks();

		new UnlockItemTask(controller.getSelectedItem().getValue(), commitWIP, remarks,
				new RepositoryResultHandler(mainController), mainController.getStatusController()).go();
	}

	private void promoteLibraryEventHandler(ActionEvent t) {
		log.debug("TODO - implement Promote in Row Factory.");
	}

	/**
	 * 
	 * @return true if user can write the namespace
	 */
	// TODO - how to find out if the user has WRITE permission?
	private boolean userCanWrite() {
		return true; //
	}

	/**
	 * @param tc
	 * @param newTreeItem
	 * @return
	 */
	// TODO - use style class for warning and error
	private void setCSSClass(TreeTableRow<RepoItemDAO> tc, TreeItem<RepoItemDAO> newTreeItem) {
		// TODO - how to determine if the user is the LockedBy user?
		//
		if (newTreeItem != null) {
			lockLibrary.setDisable(true);
			unlockLibrary.setDisable(true);
			promoteLibrary.setDisable(true);

			RepoItemDAO repoItem = newTreeItem.getValue();
			RepositoryItem item = newTreeItem.getValue().getValue();
			if (userCanWrite()) {
				String user = item.getLockedByUser();
				if (user != null && !user.isEmpty()) {
					// Make unlock inactive
					lockLibrary.setDisable(true);
					unlockLibrary.setDisable(false);
				} else {
					lockLibrary.setDisable(false);
					unlockLibrary.setDisable(true);
				}
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
