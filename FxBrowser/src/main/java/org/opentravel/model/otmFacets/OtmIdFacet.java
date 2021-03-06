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

import java.util.ArrayList;
import java.util.Collection;

import org.opentravel.model.OtmChildrenOwner;
import org.opentravel.model.OtmObject;
import org.opentravel.model.otmLibraryMembers.OtmComplexObjects;
import org.opentravel.model.otmProperties.OtmProperty;
import org.opentravel.schemacompiler.model.TLFacet;
import org.opentravel.schemacompiler.model.TLFacetType;

/**
 * @author Dave Hollander
 * 
 */
public class OtmIdFacet extends OtmFacet<TLFacet> {
	// private static final Logger LOGGER = LoggerFactory.getLogger(OtmIdFacet.class);

	/**
	 */
	public OtmIdFacet(TLFacet tl, OtmComplexObjects<?> parent) {
		super(tl, parent);

		if (tl.getFacetType() != TLFacetType.ID)
			throw new IllegalArgumentException("Tried to create id facet from wrong facet type: " + tl.getFacetType());
	}

	// @Override
	// public TLFacet getTL() {
	// return tlObject;
	// }

	@Override
	public Collection<OtmObject> getChildrenHierarchy() {
		Collection<OtmObject> hierarchy = new ArrayList<>();
		// TODO - add inherited properties
		getInheritedChildren().forEach(c -> {
			if (c instanceof OtmProperty)
				hierarchy.add(c);
		});
		children.forEach(c -> {
			if (c instanceof OtmProperty)
				hierarchy.add(c);
		});
		if (getParent() instanceof OtmChildrenOwner)
			getParent().getChildren().forEach(c -> {
				if (c instanceof OtmSummaryFacet)
					hierarchy.add(c);
			});
		return hierarchy;
	}

}
