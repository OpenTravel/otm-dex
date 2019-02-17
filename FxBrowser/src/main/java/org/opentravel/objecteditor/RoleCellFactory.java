/**
 * 
 */
package org.opentravel.objecteditor;

import org.opentravel.model.propertyNodes.UserSelectablePropertyTypes;
import org.opentravel.objecteditor.FacetTabTreeTableHandler.PropertyNode;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.cell.ComboBoxTreeTableCell;
import javafx.util.Callback;

@SuppressWarnings("restriction")
public class RoleCellFactory<P, S>
		implements Callback<TreeTableColumn<PropertyNode, String>, TreeTableCell<PropertyNode, String>> {

	private EventHandler<ActionEvent> deletePersonsHandler;

	@Override
	public TreeTableCell<PropertyNode, String> call(TreeTableColumn<PropertyNode, String> param) {
		return new ComboBoxTreeTableCell<PropertyNode, String>(UserSelectablePropertyTypes.getObservableList()) {
			// Define inner class to handle cell
			{
				// Note - this context menu is specific to the role column.
				ContextMenu cm = new ContextMenu();
				MenuItem deletePersonsMenuItem = new MenuItem("Delete");
				// deletePersonsMenuItem.setOnAction( PersonTypeCellFactory.this.deletePersonsHandler );
				cm.getItems().add(deletePersonsMenuItem);
				this.setContextMenu(cm);
				//
				// this.getItems().addAll( "Friend", "Co-worker", "Other" );
				//
				this.setEditable(true);
			}

			@Override
			public void updateItem(String arg0, boolean empty) {
				super.updateItem(arg0, empty);
				if (!empty) {
					this.setText(arg0);
				} else {
					this.setText(null); // clear from recycled obj
				}
			}

			@SuppressWarnings("unchecked")
			@Override
			public void commitEdit(String newValue) {
				super.commitEdit(newValue);
				TreeTableRow<PropertyNode> row = this.getTreeTableRow();
				PropertyNode p = row.getItem();

				System.out.println("TODO - make " + p + " into a " + newValue);

				// TODO - study TASK!!!
				// Task<Void> task = new Task<Void>() {
				// @Override
				// protected Void call() {
				//// dao.updatePerson(p); // updates AR too
				// return null;
				// }
				// };
				// new Thread(task).start();
			}
		};
	}

	public void setDeletePersonsHandler(EventHandler<ActionEvent> handler) {
		this.deletePersonsHandler = handler;
	}
}