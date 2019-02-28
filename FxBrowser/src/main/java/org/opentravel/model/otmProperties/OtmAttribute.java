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

import org.opentravel.common.ImageManager;
import org.opentravel.common.ImageManager.Icons;
import org.opentravel.model.OtmTypeProvider;
import org.opentravel.model.OtmTypeUser;
import org.opentravel.model.OtmPropertyOwner;
import org.opentravel.schemacompiler.model.TLAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract OTM Node for attribute properties.
 * 
 * @author Dave Hollander
 * 
 */
public class OtmAttribute<TL extends TLAttribute> extends OtmProperty<TLAttribute> implements OtmTypeUser {
	private static final Logger LOGGER = LoggerFactory.getLogger(OtmAttribute.class);

	/**
	 * @param tlBusinessObject
	 */
	public OtmAttribute(TL tl, OtmPropertyOwner parent) {
		super(tl, parent);

		if (!(tl instanceof TLAttribute))
			throw new IllegalArgumentException("OtmAttribute constructor not passed a tl attribute.");
		// if (tl.isReference())
		// throw new IllegalArgumentException("OtmAttribute constructor passed a attribute reference.");
	}

	@Override
	public TLAttribute getTL() {
		return (TLAttribute) tlObject;
	}

	@Override
	public String getAssignedTypeName() {
		return getTL().getTypeName();
	}

	@Override
	public OtmTypeProvider getAssignedType() {
		return null; // FIXME
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
	public boolean isManditory() {
		return getTL().isMandatory();
	}

	@Override
	public void setManditory(boolean value) {
		getTL().setMandatory(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String setName(String name) {
		getTL().setName(name);
		return getName();
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public Icons getIconType() {
		return ImageManager.Icons.ATTRIBUTE;
	}
}
