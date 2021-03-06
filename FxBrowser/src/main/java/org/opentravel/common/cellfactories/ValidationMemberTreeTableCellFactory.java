/**
 * 
 */
package org.opentravel.common.cellfactories;

import org.opentravel.dex.controllers.member.MemberAndProvidersDAO;

import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableCell;
import javafx.scene.image.ImageView;

/**
 * @author dmh
 *
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class ValidationMemberTreeTableCellFactory extends TreeTableCell<MemberAndProvidersDAO, ImageView> {

	// TreeTableCell<PropertiesDAO, ImageView>() {
	@Override
	protected void updateItem(ImageView item, boolean empty) {
		super.updateItem(item, empty);
		// Provide imageView directly - does not update automatically as the observable property would
		// Provide tooltip showing validation results
		String name = "";
		if (!empty && getTreeTableRow() != null && getTreeTableRow().getItem() != null) {
			setGraphic(getTreeTableRow().getItem().getValue().validationImage());
			name = getTreeTableRow().getItem().getValue().getValidationFindingsAsString();
			if (!name.isEmpty())
				setTooltip(new Tooltip(name));
		} else {
			setGraphic(null);
			setTooltip(null);
		}
	}
}
