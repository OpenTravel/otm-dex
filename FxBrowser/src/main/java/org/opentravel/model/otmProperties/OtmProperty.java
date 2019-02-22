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

import org.opentravel.model.OtmModelElement;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;
import org.opentravel.schemacompiler.model.TLModelElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract OTM Node for properties.
 * 
 * @author Dave Hollander
 * 
 */
public abstract class OtmProperty<TL extends TLModelElement> extends OtmModelElement<TLModelElement> {
	private static final Logger LOGGER = LoggerFactory.getLogger(OtmProperty.class);
	PropertyOwner parent;

	/**
	 * @param tlBusinessObject
	 */
	public OtmProperty(TL tl, PropertyOwner parent) {
		super(tl);
		this.parent = parent;

		if (parent == null)
			throw new IllegalArgumentException("Missing property owner parent.");
	}

	// Needs to be abstract because getTL() is of type TLModelElement
	@Override
	public abstract TLModelElement getTL();

	/**
	 * Property Factory
	 * 
	 * @param tl
	 * @return OtmFacet<?> based on type or null.
	 */
	public static OtmProperty<?> propertyFactory(TLModelElement tl) {
		return null;
	}

	@Override
	public OtmLibraryMember<?> getOwningMember() {
		return parent.getOwningMember();
	}

	@Override
	public String getNamespace() {
		return getOwningMember().getNamespace();
	}

	@Override
	public abstract String getName();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract String setName(String name);

	@Override
	public boolean isEditable() {
		return getOwningMember() != null && getOwningMember().isEditable();
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * @return
	 */
	public abstract boolean isManditory();

	/**
	 * @param value
	 */
	public abstract void setManditory(boolean value);
}
