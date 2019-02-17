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
package org.opentravel.model.propertyNodes;

import org.opentravel.model.objectNodes.OtmLibraryMember;
import org.opentravel.objecteditor.ImageManager;
import org.opentravel.objecteditor.ImageManager.Icons;
import org.opentravel.schemacompiler.model.TLProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract OTM Node for attribute properties.
 * 
 * @author Dave Hollander
 * 
 */
public class OtmElement<TL extends TLProperty> extends OtmProperty<TLProperty> {
	private static final Logger LOGGER = LoggerFactory.getLogger(OtmElement.class);

	/**
	 * @param tlBusinessObject
	 */
	public OtmElement(TL tl) {
		super(tl);

		if (!(tl instanceof TLProperty))
			throw new IllegalArgumentException("OtmElement constructor not passed a tl property.");
		if (tl.isReference())
			throw new IllegalArgumentException("OtmElement constructor passed a property reference.");
	}

	@Override
	public TLProperty getTL() {
		return (TLProperty) tlObject;
	}

	@Override
	public String getName() {
		return getTL().getName();
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
	public Icons getIconType() {
		return ImageManager.Icons.ELEMENT;
	}

	@Override
	public OtmLibraryMember<?> getOwningComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isManditory() {
		return getTL().isMandatory();
	}

	@Override
	public void setManditory(boolean value) {
		getTL().setMandatory(value);
	}

}
