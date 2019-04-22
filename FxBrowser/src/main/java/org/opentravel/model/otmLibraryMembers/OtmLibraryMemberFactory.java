/**
 * 
 */
package org.opentravel.model.otmLibraryMembers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.otmFacets.OtmFacetFactory;
import org.opentravel.schemacompiler.model.LibraryMember;
import org.opentravel.schemacompiler.model.TLBusinessObject;
import org.opentravel.schemacompiler.model.TLChoiceObject;
import org.opentravel.schemacompiler.model.TLContextualFacet;
import org.opentravel.schemacompiler.model.TLCoreObject;

/**
 * @author dmh
 *
 */
public class OtmLibraryMemberFactory {
	private static Log log = LogFactory.getLog(OtmLibraryMemberFactory.class);

	private OtmLibraryMemberFactory() {
		// NO-OP - only static methods
	}

	public static OtmLibraryMember memberFactory(LibraryMember tlMember, OtmModelManager manager) {
		if (manager == null)
			throw new IllegalArgumentException("Member factory must be passed a non-null manager.");

		OtmLibraryMember otmMember = null;
		if (tlMember instanceof TLBusinessObject)
			otmMember = new OtmBusinessObject((TLBusinessObject) tlMember, manager);
		else if (tlMember instanceof TLChoiceObject)
			otmMember = new OtmChoiceObject((TLChoiceObject) tlMember, manager);
		else if (tlMember instanceof TLCoreObject)
			otmMember = new OtmCoreObject((TLCoreObject) tlMember, manager);
		else if (tlMember instanceof TLContextualFacet)
			otmMember = OtmFacetFactory.create((TLContextualFacet) tlMember, manager);

		manager.add(otmMember);
		return otmMember;
	}

}
