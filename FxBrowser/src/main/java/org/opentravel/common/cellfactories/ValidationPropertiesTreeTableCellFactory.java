/**
 * 
 */
package org.opentravel.common.cellfactories;

import org.opentravel.dex.controllers.member.properties.PropertiesDAO;

import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableCell;
import javafx.scene.image.ImageView;

/**
 * Provide cell with graphic and tool tip showing validation results
 * 
 * @author dmh
 *
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class ValidationPropertiesTreeTableCellFactory extends TreeTableCell<PropertiesDAO, ImageView> {

	@Override
	protected void updateItem(ImageView item, boolean empty) {
		super.updateItem(item, empty);
		String tip = "";
		if (!empty && getTreeTableRow() != null && getTreeTableRow().getItem() != null
				&& getTreeTableRow().getItem().getValue() != null) {
			setGraphic(getTreeTableRow().getItem().getValue().validationImage());
			tip = getTreeTableRow().getItem().getValue().getValidationFindingsAsString();
			if (!tip.isEmpty())
				setTooltip(new Tooltip(tip));
		} else {
			setGraphic(null);
			setTooltip(null);
		}
	}
}
