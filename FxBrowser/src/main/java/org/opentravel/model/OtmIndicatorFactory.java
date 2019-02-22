/**
 * 
 */
package org.opentravel.model;

import org.opentravel.model.otmProperties.OtmIndicator;
import org.opentravel.model.otmProperties.OtmIndicatorElement;
import org.opentravel.model.otmProperties.PropertyOwner;
import org.opentravel.schemacompiler.model.TLIndicator;

/**
 * @author dmh
 *
 */
public class OtmIndicatorFactory {

	public static OtmIndicator create(TLIndicator tlIndicator, PropertyOwner parent) {
		OtmIndicator<TLIndicator> indicator;
		if (tlIndicator.isPublishAsElement())
			indicator = new OtmIndicatorElement<>(tlIndicator, parent);
		else
			indicator = new OtmIndicator<>(tlIndicator, parent);

		return indicator;
	}
}
