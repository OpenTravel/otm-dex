/**
 * 
 */
package org.opentravel.model;

import org.opentravel.model.otmFacets.OtmDetailFacet;
import org.opentravel.model.otmFacets.OtmFacet;
import org.opentravel.model.otmFacets.OtmIdFacet;
import org.opentravel.model.otmFacets.OtmSharedFacet;
import org.opentravel.model.otmFacets.OtmSummaryFacet;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;
import org.opentravel.schemacompiler.model.TLFacet;

/**
 * @author dmh
 *
 */
public class OtmFacetFactory {

	public static OtmFacet<?> create(TLFacet tlFacet, OtmLibraryMember<?> parent) {
		OtmFacet<?> facet = null;
		switch (tlFacet.getFacetType()) {
		case SUMMARY:
			facet = new OtmSummaryFacet(tlFacet, parent);
			break;
		case DETAIL:
			facet = new OtmDetailFacet(tlFacet, parent);
			break;
		case SHARED:
			facet = new OtmSharedFacet(tlFacet, parent);
			break;
		case ID:
			facet = new OtmIdFacet(tlFacet, parent);
			break;
		case CHOICE:
		case CUSTOM:
		case QUERY:
		case SIMPLE:
		default:
			System.out.println("Un-handled facet factory case: " + tlFacet.getFacetType());
			break;
		}

		if (facet != null)
			facet.modelChildren();

		return facet;
	}
}
