/**
 * 
 */
package org.opentravel.model.otmProperties;

import org.opentravel.schemacompiler.model.TLAttribute;
import org.opentravel.schemacompiler.model.TLIndicator;
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

	public static OtmAttribute<TLAttribute> create(TLAttribute tlAttribute, PropertyOwner parent) {
		OtmAttribute<TLAttribute> attribute;
		attribute = new OtmAttribute<>(tlAttribute, parent);
		return attribute;
	}

	public static OtmElement<TLProperty> create(TLProperty tlProperty, PropertyOwner parent) {
		OtmElement<TLProperty> property;
		if (tlProperty.isReference())
			property = new OtmElementReference<>(tlProperty, parent);
		else
			property = new OtmElement<>(tlProperty, parent);
		return property;
	}

	public static OtmIndicator<TLIndicator> create(TLIndicator tlIndicator, PropertyOwner parent) {
		OtmIndicator<TLIndicator> indicator;
		if (tlIndicator.isPublishAsElement())
			indicator = new OtmIndicatorElement<>(tlIndicator, parent);
		else
			indicator = new OtmIndicator<>(tlIndicator, parent);

		return indicator;
	}
}
