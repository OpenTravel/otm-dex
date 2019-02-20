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
package org.opentravel.model.otmFacets;

import org.opentravel.model.OtmModelElement;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;
import org.opentravel.model.otmProperties.OtmAttribute;
import org.opentravel.model.otmProperties.OtmElement;
import org.opentravel.model.otmProperties.OtmProperty;
import org.opentravel.model.otmProperties.PropertyOwner;
import org.opentravel.objecteditor.ImageManager;
import org.opentravel.objecteditor.ImageManager.Icons;
import org.opentravel.schemacompiler.model.TLAttribute;
import org.opentravel.schemacompiler.model.TLFacet;
import org.opentravel.schemacompiler.model.TLProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract OTM Node for Facets.
 * 
 * @author Dave Hollander
 * 
 */
public abstract class OtmFacet<TL extends TLFacet> extends OtmModelElement<TLFacet> implements PropertyOwner {
	private static final Logger LOGGER = LoggerFactory.getLogger(OtmFacet.class);

	private OtmLibraryMember parent;

	/**
	 * @param TLFacet
	 *            to model
	 */
	@Deprecated
	public OtmFacet(TL tl) {
		super(tl);
		assert false;
	}

	public OtmFacet(TL tl, OtmLibraryMember parent) {
		super(tl);
		this.parent = parent;

		if (parent == null)
			throw new IllegalArgumentException("No parent library member set.");
	}

	/**
	 * Facet Factory
	 * 
	 * @param tl
	 * @return OtmFacet<?> based on facet type or null.
	 */
	public static OtmFacet<?> facetFactory(TLFacet tl, OtmLibraryMember parent) {
		switch (tl.getFacetType()) {
		case SUMMARY:
			return new OtmSummaryFacet(tl, parent);
		case DETAIL:
			return new OtmDetailFacet(tl, parent);
		default:
			LOGGER.debug("Missing Facet Type case: " + tl.getFacetType());
			return null;
		}
	}

	@Override
	public String getNamespace() {
		return tlObject.getNamespace();
	}

	@Override
	public String getName() {
		return tlObject.getLocalName();
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	public String getRole() {
		return getTL().getFacetType().getIdentityName();
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public Icons getIconType() {
		return ImageManager.Icons.FACET;
	}

	@Override
	public OtmLibraryMember<?> getOwningMember() {
		return parent;
	}

	public void createTestChildren() {
		// TODO - add name, type and constraints
		OtmProperty<?> prop;
		prop = new OtmAttribute<>(new TLAttribute(), this);
		children.add(prop);
		prop.setName(getName() + "a1");
		prop = new OtmElement<>(new TLProperty(), this);
		children.add(prop);
		prop.setName(getName() + "e1");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Creates facets to represent facets in the TL business object.
	 */
	@Override
	public void modelChildren() {
		// TODO
	}
}
