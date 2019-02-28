/**
 * 
 */
package org.opentravel.model.otmProperties;

import org.opentravel.model.OtmPropertyOwner;
import org.opentravel.schemacompiler.model.TLAttribute;
import org.opentravel.schemacompiler.model.TLIndicator;
import org.opentravel.schemacompiler.model.TLModelElement;
import org.opentravel.schemacompiler.model.TLProperty;

/**
 * Factory that resolves which type of indicator to create.
 * 
 * @author dmh
 *
 */
public class OtmPropertyFactory {

	private OtmPropertyFactory() {
		// NO-OP - only static methods
	}

	public static OtmAttribute<TLAttribute> create(TLAttribute tlAttribute, OtmPropertyOwner parent) {
		OtmAttribute<TLAttribute> attribute;
		attribute = new OtmAttribute<>(tlAttribute, parent);
		return attribute;
	}

	public static OtmElement<TLProperty> create(TLProperty tlProperty, OtmPropertyOwner parent) {
		OtmElement<TLProperty> property;
		if (tlProperty.isReference())
			property = new OtmElementReference<>(tlProperty, parent);
		else
			property = new OtmElement<>(tlProperty, parent);
		return property;
	}

	public static OtmIndicator<TLIndicator> create(TLIndicator tlIndicator, OtmPropertyOwner parent) {
		OtmIndicator<TLIndicator> indicator;
		if (tlIndicator.isPublishAsElement())
			indicator = new OtmIndicatorElement<>(tlIndicator, parent);
		else
			indicator = new OtmIndicator<>(tlIndicator, parent);

		return indicator;
	}

	/**
	 * @param tl
	 * @param parent
	 */
	public static OtmProperty<?> create(TLModelElement tl, OtmPropertyOwner parent) {
		if (tl instanceof TLIndicator)
			return OtmPropertyFactory.create((TLIndicator) tl, parent);
		else if (tl instanceof TLProperty)
			return OtmPropertyFactory.create((TLProperty) tl, parent);
		else if (tl instanceof TLAttribute)
			return OtmPropertyFactory.create((TLAttribute) tl, parent);
		return null;
	}
}
