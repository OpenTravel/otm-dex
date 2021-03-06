/**
 * Copyright (C) 2014 OpenTravel Alliance (info@opentravel.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 */
package org.opentravel.model.otmProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.common.ImageManager.Icons;
import org.opentravel.model.OtmPropertyOwner;
import org.opentravel.schemacompiler.model.TLIndicator;

/**
 * Abstract OTM Node for indicator attribute properties.
 * 
 * @author Dave Hollander
 * 
 */
public class OtmIndicator<TL extends TLIndicator> extends OtmProperty<TLIndicator> {
	private static Log log = LogFactory.getLog(OtmIndicator.class);

	/**
	 * @param tlBusinessObject
	 */
	public OtmIndicator(TL tl, OtmPropertyOwner parent) {
		super(tl, parent);

		if (!(tl instanceof TLIndicator))
			throw new IllegalArgumentException("OtmAttribute constructor not passed a tl attribute.");
	}

	@Override
	public TLIndicator getTL() {
		return (TLIndicator) tlObject;
	}

	@Override
	public String getName() {
		return getTL().getName();
	}

	@Override
	public String getRole() {
		return UserSelectablePropertyTypes.Attribute.toString();
	}

	@Override
	public boolean isInherited() {
		return getTL().getOwner() != getParent().getTL();
	}

	@Override
	public boolean isManditory() {
		return false;
	}

	@Override
	public void setManditory(boolean value) {
		// NO-OP
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String setName(String name) {
		getTL().setName(name);
		isValid(true);
		return getName();
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public Icons getIconType() {
		return ImageManager.Icons.INDICATOR;
	}
}
