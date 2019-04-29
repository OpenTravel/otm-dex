/**
 * 
 */
package org.opentravel.common.cellfactories;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.dex.controllers.member.properties.MemberPropertiesTreeTableController;
import org.opentravel.dex.controllers.member.properties.PropertiesDAO;
import org.opentravel.dex.events.DexMemberSelectionEvent;
import org.opentravel.model.OtmModelElement;
import org.opentravel.model.OtmTypeProvider;
import org.opentravel.model.OtmTypeUser;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;
import org.opentravel.schemacompiler.model.TLModelElement;

import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * @author dmh
 *
 */
public class AssignedTypePropertiesTreeTableCellFactory extends TreeTableCell<PropertiesDAO, String> {
	private static Log log = LogFactory.getLog(AssignedTypePropertiesTreeTableCellFactory.class);
	private MemberPropertiesTreeTableController controller;
	private OtmTypeProvider assignedType = null;

	public AssignedTypePropertiesTreeTableCellFactory(MemberPropertiesTreeTableController controller) {
		this.controller = controller;
	}

	// TreeTableCell<PropertiesDAO, ImageView>() {
	@Override
	protected void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		if (!empty && getTreeTableRow() != null && getTreeTableRow().getItem() != null) {
			OtmModelElement<?> element = getTreeTableRow().getItem().getValue();
			ImageView graphic = null;

			setText(getTreeTableRow().getItem().assignedTypeProperty().get());

			if (element instanceof OtmTypeUser)
				assignedType = ((OtmTypeUser) element).getAssignedType();
			else
				assignedType = null;

			if (assignedType != null) {
				OtmModelElement<TLModelElement> type = (OtmModelElement<TLModelElement>) assignedType;
				setTooltip(new Tooltip(type.getObjectTypeName() + " in " + type.getLibrary().getName() + "library.\n"
						+ type.getDescription()));
				graphic = new ImageManager().getView(type.getIcon());
				if (graphic != null)
					setGraphic(graphic);
				setOnMouseClicked(this::mouseClick);
			} else {
				setGraphic(null);
				setTooltip(null);
			}
		} else {
			setGraphic(null);
			setTooltip(null);
			setText(null);
		}
	}

	public void mouseClick(MouseEvent event) {
		OtmLibraryMember lm = null;
		// this fires after the member selection listener
		if (assignedType != null)
			lm = ((OtmModelElement<?>) assignedType).getOwningMember();
		if (controller != null && lm != null && event.getButton().equals(MouseButton.PRIMARY)
				&& event.getClickCount() == 2) {
			log.debug("Double click selection: ");
			controller.fireEvent(new DexMemberSelectionEvent(lm));
		}
	}

}
