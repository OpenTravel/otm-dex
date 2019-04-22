/**
 * 
 */
package org.opentravel.dex.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ValidationUtils;
import org.opentravel.model.OtmModelElement;
import org.opentravel.model.otmLibraryMembers.OtmComplexObject;
import org.opentravel.schemacompiler.validate.ValidationFindings;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

public class NameChangeAction extends DexStringAction {
	private static Log log = LogFactory.getLog(NameChangeAction.class);
	private OtmModelElement<?> otm;
	private boolean outcome = false;

	private ObservableValue<? extends String> observable;
	private String oldName;
	private String modifiedName;
	// private String name;
	private boolean ignore;

	private static final String VETO1 = "org.opentravel.schemacompiler.TLProperty.name.ELEMENT_REF_NAME_MISMATCH";
	private static final String VETO2 = "org.opentravel.schemacompiler.TLAttribute.name.INVALID_REFERENCE_NAME";
	private static final String VETO3 = "org.opentravel.schemacompiler.TLProperty.name.PATTERN_MISMATCH";
	private static final String[] VETOKEYS = { VETO1, VETO2, VETO3 };

	public NameChangeAction(OtmModelElement<?> otm) {
		this.otm = otm;
	}

	@Override
	public String doIt(ObservableValue<? extends String> o, String oldName, String name) {
		log.debug("Ready to set name to " + name + "  from: " + oldName + " on: " + otm.getClass().getSimpleName() + " "
				+ ignore);
		if (ignore)
			return "";
		if (otm.getActionManager() == null)
			return "";
		// TODO - should we allow empty name?
		if (name == null || name.isEmpty())
			return "";

		this.observable = o;
		this.oldName = oldName;
		this.modifiedName = name;
		// Force upper case
		if (otm instanceof OtmComplexObject)
			modifiedName = name.substring(0, 1).toUpperCase() + name.substring(1);

		// Set value into model and GUI
		otm.setName(modifiedName);

		// Validate results. Note: TL will not veto (prevent) change.
		if (isValid())
			outcome = true;

		if (!name.equals(modifiedName))
			otm.getActionManager().postWarning("Changed name from " + name + " to " + modifiedName);

		// Record action to allow undo
		otm.getActionManager().push(this);

		log.debug("Set name to " + name + "  success: " + outcome);
		return otm.getName();
	}

	@Override
	public String undo() {
		ignore = true;
		log.debug("Undo-ing change");
		otm.setName(oldName);
		if (observable instanceof SimpleStringProperty)
			((SimpleStringProperty) observable).set(oldName);

		if (!isValid()) {
			// You will get a loop if the old name is not valid!
			otm.setName("");
			if (observable instanceof SimpleStringProperty)
				((SimpleStringProperty) observable).set("");
		}
		ignore = false;
		return otm.getName();
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAllowed(String value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ValidationFindings getVetoFindings() {
		return ValidationUtils.getRelevantFindings(VETOKEYS, otm.getFindings());
	}

	@Override
	public boolean isValid() {
		// validation does not catch:
		// incorrect case
		// elements assigned to type provider
		return otm.isValid(true) ? true : ValidationUtils.getRelevantFindings(VETOKEYS, otm.getFindings()).isEmpty();
	}

	@Override
	public String toString() {
		return "Changed name from " + oldName + " to " + modifiedName;
	}

}
