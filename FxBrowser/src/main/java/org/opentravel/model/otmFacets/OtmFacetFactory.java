/**
 * 
 */
package org.opentravel.model.otmFacets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.otmLibraryMembers.OtmComplexObject;
import org.opentravel.model.otmLibraryMembers.OtmContextualFacet;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;
import org.opentravel.schemacompiler.model.TLContextualFacet;
import org.opentravel.schemacompiler.model.TLFacet;

/**
 * @author dmh
 *
 */
public class OtmFacetFactory {
	private static Log log = LogFactory.getLog(OtmFacetFactory.class);

	private OtmFacetFactory() {
		// NO-OP - only static methods
	}

	/**
	 * Create a new library member from the contextual facet.
	 * 
	 * @param tlFacet
	 * @param manager
	 * @return
	 */
	public static OtmLibraryMember create(TLContextualFacet tlFacet, OtmModelManager manager) {
		OtmContextualFacet facet = null;
		switch (tlFacet.getFacetType()) {
		case CHOICE:
			facet = new OtmChoiceFacet(tlFacet, manager);
			break;
		case CUSTOM:
			facet = new OtmCustomFacet(tlFacet, manager);
			break;
		case QUERY:
			facet = new OtmQueryFacet(tlFacet, manager);
			break;
		case UPDATE:
			facet = new OtmUpdateFacet(tlFacet, manager);
			break;

		case SUMMARY:
		case DETAIL:
		case SHARED:
		case ID:
		case SIMPLE:
		default:
			log.debug("Un-handled facet factory case: " + tlFacet.getFacetType());
			break;
		}

		if (facet != null)
			facet.modelChildren();

		return facet;
	}

	public static OtmFacet<?> create(TLFacet tlFacet, OtmLibraryMember parent) {
		OtmFacet<?> facet = null;
		switch (tlFacet.getFacetType()) {
		case SUMMARY:
			if (parent instanceof OtmComplexObject)
				facet = new OtmSummaryFacet(tlFacet, (OtmComplexObject) parent);
			break;
		case DETAIL:
			if (parent instanceof OtmComplexObject)
				facet = new OtmDetailFacet(tlFacet, (OtmComplexObject) parent);
			break;
		case SHARED:
			if (parent instanceof OtmComplexObject)
				facet = new OtmSharedFacet(tlFacet, (OtmComplexObject) parent);
			break;
		case ID:
			if (parent instanceof OtmComplexObject)
				facet = new OtmIdFacet(tlFacet, (OtmComplexObject) parent);
			break;
		case CHOICE:
		case CUSTOM:
		case UPDATE:
		case QUERY:
			if (parent instanceof OtmLibraryMember && tlFacet instanceof TLContextualFacet)
				facet = new OtmContributedFacet((TLContextualFacet) tlFacet, parent);
			break;
		case SIMPLE:
		default:
			log.debug("Un-handled facet factory case: " + tlFacet.getFacetType());
			break;
		}

		if (facet != null)
			facet.modelChildren();

		return facet;
	}
}
