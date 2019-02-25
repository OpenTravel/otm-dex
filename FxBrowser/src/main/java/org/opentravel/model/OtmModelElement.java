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
package org.opentravel.model;

import java.util.ArrayList;
import java.util.List;

import org.opentravel.common.ImageManager;
import org.opentravel.model.otmContainers.OtmLibrary;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;
import org.opentravel.schemacompiler.model.NamedEntity;
import org.opentravel.schemacompiler.model.TLModelElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.image.Image;

/**
 * Abstract base for all OTM libraries, objects, facets and properties.
 * 
 * @author Dave Hollander
 * 
 */
public abstract class OtmModelElement<TL extends TLModelElement> {
	private static final Logger LOGGER = LoggerFactory.getLogger(OtmModelElement.class);

	protected TL tlObject;

	// leave empty if object can have children but does not or has not been modeled yet.
	// leave null if the element can not have children.
	protected List<OtmModelElement<?>> children = new ArrayList<>();

	private static final String NONAMESPACE = "no-namespace-for-for-this-object";
	private static final String NONAME = "no-name-for-for-this-object";

	public Image getIcon() {
		return new ImageManager().get(this.getIconType());
	}

	/**
	 * @param
	 */
	public OtmModelElement(TL tl) {
		tlObject = tl;
	}

	public abstract ImageManager.Icons getIconType();

	public String getNamespace() {
		if (tlObject instanceof NamedEntity)
			return ((NamedEntity) tlObject).getNamespace();
		return NONAMESPACE;
	}

	public String getName() {
		if (tlObject instanceof NamedEntity)
			return ((NamedEntity) tlObject).getLocalName();
		return NONAME;
	}

	/**
	 * Set the name if possible.
	 * 
	 * @param name
	 * @return the actual name after assignment attempted
	 */
	public String setName(String name) {
		return getName();
	}

	public abstract TL getTL();

	public boolean isEditable() {
		return getOwningMember() != null ? getOwningMember().isEditable() : false;
	}

	/**
	 * Get Children. To allow lazy evaluation, children will be modeled if list is empty.
	 * 
	 * @return the live list of children for this library member.
	 */
	public List<OtmModelElement<?>> getChildren() {
		// Create OtmNodes for all the children of this member
		if (children != null && children.isEmpty())
			modelChildren();

		return children;
	}

	/**
	 * Model the children of this object from its' tlObject.
	 */
	public void modelChildren() {
		// Override if the element has children
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * @return
	 */
	public String getRole() {
		return getClass().getSimpleName();
	}

	public abstract OtmLibraryMember<?> getOwningMember();

	/**
	 * 
	 */
	public String getPrefix() {
		return getOwningMember() != null && getOwningMember().getLibrary() != null
				? getOwningMember().getLibrary().getPrefix() : "---";
	}

	/**
	 * @return this library, owning library or null
	 */
	public OtmLibrary getLibrary() {
		// if (this instanceof OtmLibraryMember<?>) return getLibrary();
		if (getOwningMember() != null)
			return getOwningMember().getLibrary();
		return null;
	}
}
