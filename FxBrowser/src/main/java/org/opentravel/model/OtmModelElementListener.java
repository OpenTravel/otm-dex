/**
 * 
 */
package org.opentravel.model;

import org.opentravel.schemacompiler.event.ModelElementListener;
import org.opentravel.schemacompiler.event.OwnershipEvent;
import org.opentravel.schemacompiler.event.ValueChangeEvent;
import org.opentravel.schemacompiler.model.TLModelElement;

/**
 * @author dmh
 *
 */
public class OtmModelElementListener implements ModelElementListener {

	OtmModelElement<TLModelElement> otm;

	public OtmModelElementListener(OtmModelElement<TLModelElement> otmModelElement) {
		otm = otmModelElement;
	}

	public OtmModelElement<TLModelElement> get() {
		return otm;
	}

	@Override
	public void processOwnershipEvent(OwnershipEvent<?, ?> event) {
		// No-op
	}

	@Override
	public void processValueChangeEvent(ValueChangeEvent<?, ?> event) {
		// No-op
	}

}
