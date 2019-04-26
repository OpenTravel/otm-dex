/**
 * 
 */
package org.opentravel.dex.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.common.ValidationUtils;
import org.opentravel.dex.controllers.member.MemberDAO;
import org.opentravel.dex.controllers.member.properties.PropertiesDAO;
import org.opentravel.dex.controllers.popup.DexPopupControllerBase.Results;
import org.opentravel.dex.controllers.popup.TypeSelectionContoller;
import org.opentravel.model.OtmModelElement;
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.OtmTypeProvider;
import org.opentravel.model.OtmTypeUser;
import org.opentravel.schemacompiler.model.TLPropertyType;
import org.opentravel.schemacompiler.validate.ValidationFindings;

public class AssignedTypeChangeAction implements DexAction<OtmTypeProvider> {
	private static Log log = LogFactory.getLog(AssignedTypeChangeAction.class);

	private OtmModelElement<?> otm;
	private OtmTypeUser user = null;
	private boolean outcome = false;

	private PropertiesDAO propertiesDAO;
	private OtmTypeProvider oldProvider;
	private TLPropertyType oldTLType;
	private String oldName;
	private OtmTypeProvider newProvider;
	private boolean ignore;

	private OtmModelManager modelMgr = null;
	private ImageManager imageMgr = null;

	private String oldTLTypeName;

	private static final String VETO1 = "org.opentravel.schemacompiler.TLProperty.name.ELEMENT_REF_NAME_MISMATCH";
	private static final String VETO2 = ".OBSOLETE_TYPE_REFERENCE";
	private static final String VETO3 = ".ILLEGAL_REFERENCE";
	private static final String[] VETOKEYS = { VETO1, VETO2, VETO3 };

	public AssignedTypeChangeAction(PropertiesDAO prop) {
		this.otm = prop.getValue();
		this.propertiesDAO = prop;

		// TODO - simplify access to model manager
		// Must have access to model manager
		if (propertiesDAO.getController() != null && propertiesDAO.getController().getMainController() != null) {
			this.modelMgr = propertiesDAO.getController().getMainController().getModelManager();
			this.imageMgr = propertiesDAO.getController().getMainController().getImageManager();
		}
	}

	public OtmTypeProvider doIt() {
		log.debug("Ready to set assigned type to " + otm + " " + ignore);
		if (ignore)
			return null;
		if (modelMgr == null)
			return null;
		if (!isEnabled())
			return null;
		if (otm.getActionManager() == null)
			return null;

		// Hold onto old value
		user = (OtmTypeUser) otm;
		oldProvider = user.getAssignedType();
		oldTLType = user.getAssignedTLType();
		oldName = otm.getName();
		oldTLTypeName = user.assignedTypeProperty().get();

		// Get the user's selected new provider
		MemberDAO selected = null;
		TypeSelectionContoller controller = TypeSelectionContoller.init();
		controller.setManagers(modelMgr, imageMgr);
		if (controller.showAndWait("MSG") == Results.OK) {
			selected = controller.getSelected();
			if (selected == null || selected.getValue() == null)
				log.error("Missing selection from Type Selection Controller"); // cancel?
			else
				log.debug("action - Set Assigned Type on: " + selected.getValue().getName());
		}

		// Make the change and test the results
		if (selected != null && selected.getValue() instanceof OtmTypeProvider) {
			newProvider = (OtmTypeProvider) selected.getValue();
			// Set value into model
			user.setAssignedType(newProvider);

			// Validate results. Note: TL will not veto (prevent) change.
			if (isValid())
				outcome = true;

			// Record action to allow undo. Will validate results and warn user.
			otm.getActionManager().push(this);

			log.debug("Set type to " + newProvider + "  success: " + outcome);
		}
		return newProvider;
	}

	@Override
	public OtmTypeProvider undo() {
		log.debug(" TODO -Undo-ing change");
		if (oldProvider != null) {
			if (oldProvider != user.setAssignedType(oldProvider))
				otm.getActionManager().postWarning("Error undoing change.");
		} else if (oldTLType != null) {
			// If provider was not in model manager
			if (oldTLType != user.setAssignedTLType(oldTLType))
				otm.getActionManager().postWarning("Error undoing change.");
		} else {
			// Sometimes, only the name is known because the tl model does not have the type loaded.
			user.setTLTypeName(oldTLTypeName);
			otm.setName(oldName);
		}
		otm.setName(oldName); // May have been changed by assignment
		return oldProvider;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Assure the object is a type user and editable.
	 */
	@Override
	public boolean isEnabled() {
		if (!(otm instanceof OtmTypeUser))
			return false;
		if (!otm.isEditable())
			return false;
		return true;
	}

	@Override
	public boolean isAllowed(OtmTypeProvider value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ValidationFindings getVetoFindings() {
		return ValidationUtils.getRelevantFindings(VETOKEYS, otm.getFindings());
	}

	@Override
	public boolean isValid() {
		return otm.isValid(true) ? true : ValidationUtils.getRelevantFindings(VETOKEYS, otm.getFindings()).isEmpty();
	}

	@Override
	public String toString() {
		return "Assigned Type: " + newProvider;
	}
}
