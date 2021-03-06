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
import org.opentravel.model.OtmPropertyOwner;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;
import org.opentravel.schemacompiler.model.TLModelElement;

/**
 * Abstract OTM Node for properties.
 * 
 * @author Dave Hollander
 * 
 */
public abstract class OtmProperty<T extends TLModelElement> extends OtmModelElement<TLModelElement> {

	private OtmPropertyOwner parent;

	/**
	 * @param tl
	 *            property owner
	 */
	public OtmProperty(T tl, OtmPropertyOwner parent) {
		super(tl, parent.getActionManager());
		this.parent = parent;
	}

	@Override
	public abstract String getName();

	@Override
	public String getNamespace() {
		return getOwningMember().getNamespace();
	}

	@Override
	public OtmLibraryMember getOwningMember() {
		return parent.getOwningMember();
	}

	public OtmPropertyOwner getParent() {
		return parent;
	}

	@Override
	public boolean isEditable() {
		return getOwningMember() != null && getOwningMember().isEditable();
	}

	@Override
	public abstract boolean isInherited();

	/**
	 * @return
	 */
	public abstract boolean isManditory();

	/**
	 * @param value
	 */
	public abstract void setManditory(boolean value);

	@Override
	public String toString() {
		return getName();
	}
}
