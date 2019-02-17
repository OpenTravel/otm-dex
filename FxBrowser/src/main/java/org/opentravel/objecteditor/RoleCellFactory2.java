/**
 * 
 */
package org.opentravel.objecteditor;

import org.opentravel.objecteditor.FacetTabTreeTableHandler.PropertyNode;

import javafx.scene.control.TreeTableCell;

/**
 * This is for the entire role column, not individual cells.
 * 
 * @author dmh
 *
 */
@SuppressWarnings("restriction")
public class RoleCellFactory2<P, S> extends TreeTableCell<PropertyNode, String> {

	public RoleCellFactory2() {
		System.out.println("Creating role cell factory ");
		// add context, style listeners and actions
	}

	@Override
	protected void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		if (item == null || empty) {
			setText(null);
		} else {
			setText(item);
		}
	}
}
