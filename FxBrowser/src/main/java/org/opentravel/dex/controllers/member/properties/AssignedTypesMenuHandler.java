/**
 * 
 */
package org.opentravel.dex.controllers.member.properties;

import org.opentravel.common.DialogBox;
import org.opentravel.dex.controllers.member.MemberDAO;
import org.opentravel.dex.controllers.popup.DexPopupControllerBase.Results;
import org.opentravel.dex.controllers.popup.TypeSelectionContoller;
import org.opentravel.dex.events.DexMemberSelectionEvent;
import org.opentravel.model.OtmModelElement;
import org.opentravel.model.OtmTypeUser;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;
import org.opentravel.schemacompiler.model.TLModelElement;
import org.opentravel.schemacompiler.model.TLPropertyType;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Add handle to a listener to a combo box or choice box.
 * 
 * @author dmh
 *
 */
public class AssignedTypesMenuHandler {
	static final String CHANGE = "Change (future)";
	static final String GOTO = "Go To";
	static final String REMOVE = "Remove (future)";
	static final String STRING = "xsd:String (future)";

	/**
	 * @return an observable list of values for the assigned type actions
	 */
	public static ObservableList<String> getAssignedTypeList() {
		ObservableList<String> list = FXCollections.observableArrayList();
		list.add(GOTO);
		list.add(CHANGE);
		list.add(REMOVE);
		list.add(STRING);
		return list;
	}

	public void handle(String selection, PropertiesDAO prop) {
		if (selection.equals(CHANGE)) {
			TypeSelectionContoller controller = TypeSelectionContoller.init();
			controller.setModelManager(prop.getController().getMainController().getModelManager());
			if (controller.showAndWait("MSG") == Results.OK) {
				MemberDAO selected = controller.getSelected();
				DialogBox.display("Set Assigned Type", "TODO - action on: " + selected.getValue().getName());
			}
		}
		// TODO
		// set combo to different value
		// Done - Make tree non-editable
		// Get selection on double-click
		// Done - Direct - QUESTION - how should dialogs communicate with controllers?
		// Need to fire off an action handler.
		//
		else if (selection.equals(GOTO)) {
			OtmLibraryMember<?> otm = findAssignedType(prop);
			if (otm != null)
				prop.getController().fireEvent(new DexMemberSelectionEvent(otm));
			else
				prop.getController().getMainController().postError(null, "The type assigned could not be found.");
		} else
			DialogBox.notify("Assigned Type Menu", selection + " is not implemented yet.");
	}

	public OtmLibraryMember<?> findAssignedType(PropertiesDAO prop) {
		if (prop.getValue() instanceof OtmTypeUser) {
			OtmTypeUser user = (OtmTypeUser) prop.getValue();
			TLPropertyType propertyType = user.getAssignedTLType();
			OtmModelElement<?> otm = OtmModelElement.get((TLModelElement) propertyType);
			if (otm != null && !(otm instanceof OtmLibraryMember))
				otm = otm.getOwningMember();
			return (OtmLibraryMember<?>) otm;
		}
		return null;
	}

}
