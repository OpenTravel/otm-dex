/**
 * 
 */
package org.opentravel.model.otmProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	private static Log log = LogFactory.getLog(OtmPropertyFactory.class);

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
		OtmProperty<?> p = null;
		if (tl instanceof TLIndicator)
			p = OtmPropertyFactory.create((TLIndicator) tl, parent);
		else if (tl instanceof TLProperty)
			p = OtmPropertyFactory.create((TLProperty) tl, parent);
		else if (tl instanceof TLAttribute)
			p = OtmPropertyFactory.create((TLAttribute) tl, parent);
		else
			log.debug("unknown/not-implemented property type.");
		log.debug("Created property " + p.getName() + " of " + p.getOwningMember().getName() + "  inherited? "
				+ p.isInherited());
		return p;
	}
}
