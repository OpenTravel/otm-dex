/**
 * 
 */
package org.opentravel.dex.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.model.OtmModelElement;
import org.opentravel.schemacompiler.validate.ValidationFindings;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

public class DescriptionChangeAction extends DexStringAction {
	private static Log log = LogFactory.getLog(DescriptionChangeAction.class);

	private OtmModelElement<?> otm;
	private boolean outcome = false;
	private String oldDescription;
	private String newDescription;

	public DescriptionChangeAction(OtmModelElement<?> otm) {
		this.otm = otm;
		this.oldDescription = otm.getDescription();
		if (oldDescription == null)
			oldDescription = "";
	}

	@Override
	public String doIt(ObservableValue<? extends String> o, String old, String description) {
		if (description == null)
			return "";

		newDescription = description;

		// Set value into model
		otm.setDescription(description);
		// TODO - DOCUMENTATION_MODIFIED in listener
		if (o instanceof SimpleStringProperty)
			((SimpleStringProperty) o).set(description);

		if (otm.getActionManager() != null) {
			otm.getActionManager().push(this);
		}

		log.debug("Set description to: " + description);
		return otm.getName();
	}

	@Override
	public String undo() {
		otm.setDescription(oldDescription);
		log.debug("Undo - restored description to " + oldDescription);
		return oldDescription;
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
	public boolean isValid() {
		return otm.isValid(true);
	}

	@Override
	public ValidationFindings getVetoFindings() {
		return null;
	}

	@Override
	public String toString() {
		return "Changed description to " + newDescription;
	}

}
