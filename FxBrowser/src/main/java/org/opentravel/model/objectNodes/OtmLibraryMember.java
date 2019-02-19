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
package org.opentravel.model.objectNodes;

import org.opentravel.model.OtmModelElement;
import org.opentravel.model.facetNodes.OtmFacet;
import org.opentravel.schemacompiler.model.TLLibraryMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract OTM Object Node for Library Members.
 * 
 * @author Dave Hollander
 * 
 */
public abstract class OtmLibraryMember<TL extends TLLibraryMember> extends OtmModelElement<TLLibraryMember> {
	private static final Logger LOGGER = LoggerFactory.getLogger(OtmLibraryMember.class);

	// protected List<OtmFacet<TLFacet>> children = new ArrayList<>(); // leave empty if no children

	/**
	 * @param tlBusinessObject
	 */
	public OtmLibraryMember(TL tl) {
		super(tl);
	}

	@Override
	public String getNamespace() {
		return getTL().getNamespace();
	}

	@Override
	public String getName() {
		return tlObject.getLocalName();
		// return this.getClass().getSimpleName();
	}

	@Override
	public boolean isEditable() {
		return tlObject.getOwningLibrary() != null;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public String getPrefix() {
		return getTL().getOwningLibrary() != null ? getTL().getOwningLibrary().getPrefix() : "";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract String setName(String text);

	/**
	 * {@inheritDoc}
	 * <p>
	 * Add properties to the facets
	 * 
	 * @return this object
	 */
	public OtmLibraryMember<?> createTestChildren() {
		for (OtmModelElement<?> child : getChildren())
			if (child instanceof OtmFacet)
				((OtmFacet<?>) child).createTestChildren();
		return this;
	}

}
