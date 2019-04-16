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
	}

}
