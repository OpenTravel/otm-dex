/**
 * 
 */
package org.opentravel.dex.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.controllers.DexMainController;
import org.opentravel.model.OtmModelElement;
import org.opentravel.schemacompiler.validate.ValidationFindings;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

public class DescriptionChangeAction extends DexStringAction {
	private static Log log = LogFactory.getLog(DescriptionChangeAction.class);
	private OtmModelElement<?> otm;
	private boolean outcome = false;
	private DexMainController controller;

	public DescriptionChangeAction(OtmModelElement<?> otm) {
		this.otm = otm;
	}

	@Override
	public String doIt(ObservableValue<? extends String> o, String old, String description) {
		if (description == null)
			return "";

		// Set value into model
		otm.setDescription(description);
		if (o instanceof SimpleStringProperty)
			((SimpleStringProperty) o).set(description);

		if (otm.getActionManager() != null) {
			otm.getActionManager().postWarning("Set description to " + description);
			otm.getActionManager().push(this);
		}

		log.debug("Set description to " + description + "  success: " + outcome);
		return otm.getName();
	}

	@Override
	public String undo() {
		// TODO
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
	public boolean isValid() {
		// validation does not catch:
		// incorrect case
		// elements assigned to type provider
		return otm.isValid(true);

		// DONE - move isValid to OtmModelElement
		// names applied to elements assigned the type
		// boolean deep = false;
		// ValidationFindings findings = null;
		// try {
		// findings = TLModelCompileValidator.validateModelElement(otm.getTL(), deep);
		// } catch (Exception e) {
		// // LOGGER.debug("Validation threw error: " + e.getLocalizedMessage());
		// }
		// log.debug(findings != null ? findings.count() : " null" + " findings found.");
		// return findings == null || findings.isEmpty();
	}

	@Override
	public ValidationFindings getVetoFindings() {
		return null;
	}
}
