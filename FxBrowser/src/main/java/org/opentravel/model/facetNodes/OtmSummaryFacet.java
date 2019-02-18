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
package org.opentravel.model.facetNodes;

import org.opentravel.schemacompiler.model.TLFacet;
import org.opentravel.schemacompiler.model.TLFacetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract OTM Node for Facets.
 * 
 * @author Dave Hollander
 * 
 */
public class OtmSummaryFacet extends OtmFacet<TLFacet> {
	private static final Logger LOGGER = LoggerFactory.getLogger(OtmSummaryFacet.class);

	/**
	 * @param tlBusinessObject
	 */
	public OtmSummaryFacet(TLFacet tl) {
		super(tl);

		if (tl.getFacetType() != TLFacetType.SUMMARY)
			throw new IllegalArgumentException(
					"Tried to create summary facet from wrong facet type: " + tl.getFacetType());
	}

	@Override
	public TLFacet getTL() {
		return tlObject;
	}

}