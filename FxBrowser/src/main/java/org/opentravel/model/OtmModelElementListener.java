/**
 * 
 */
package org.opentravel.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.schemacompiler.event.ModelElementListener;
import org.opentravel.schemacompiler.event.OwnershipEvent;
import org.opentravel.schemacompiler.event.ValueChangeEvent;
import org.opentravel.schemacompiler.model.TLModelElement;

/**
 * @author dmh
 *
 */
public class OtmModelElementListener implements ModelElementListener {
	private static Log log = LogFactory.getLog(OtmModelElementListener.class);

	OtmModelElement<TLModelElement> otm;

	public OtmModelElementListener(OtmModelElement<TLModelElement> otmModelElement) {
		otm = otmModelElement;
	}

	public OtmModelElement<TLModelElement> get() {
		return otm;
	}

	@Override
	public void processOwnershipEvent(OwnershipEvent<?, ?> event) {
		log.debug(otm.getName() + " ownership event: " + event.getType());
	}

	@Override
	public void processValueChangeEvent(ValueChangeEvent<?, ?> event) {
		log.debug(otm.getName() + " value change event: " + event.getType());
		switch (event.getType()) {
		case NAME_MODIFIED:
			if (event.getNewValue() instanceof String && otm.nameProperty() != null)
				otm.nameProperty().setValue((String) event.getNewValue());
			break;
		case DOCUMENTATION_MODIFIED:
			// Only happens when the documentation container is changed, not it's contents.
			// Description and other documentation types must update their own observable properties in setters.
			break;
		default:
		}
	}

}
