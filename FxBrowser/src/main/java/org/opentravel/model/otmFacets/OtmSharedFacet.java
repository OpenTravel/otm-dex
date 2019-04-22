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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.model.otmLibraryMembers.OtmComplexObject;
import org.opentravel.schemacompiler.model.TLFacet;
import org.opentravel.schemacompiler.model.TLFacetType;

/**
 * Abstract OTM Node for Detail Facets.
 * 
 * @author Dave Hollander
 * 
 */
public class OtmSharedFacet extends OtmFacet<TLFacet> {
	private static Log log = LogFactory.getLog(OtmSharedFacet.class);

	/**
	 */
	public OtmSharedFacet(TLFacet tl, OtmComplexObject<?> parent) {
		super(tl, parent);

		if (tl.getFacetType() != TLFacetType.SHARED)
			throw new IllegalArgumentException(
					"Tried to create shared facet from wrong facet type: " + tl.getFacetType());
	}

	@Override
	public TLFacet getTL() {
		return tlObject;
	}

}
