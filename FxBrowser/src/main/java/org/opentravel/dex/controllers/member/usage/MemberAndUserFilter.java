/**
 * 
 */
package org.opentravel.dex.controllers.member.usage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.controllers.DexFilter;
import org.opentravel.model.OtmChildrenOwner;
import org.opentravel.model.OtmObject;
import org.opentravel.model.OtmTypeUser;
import org.opentravel.model.otmFacets.OtmAlias;
import org.opentravel.model.otmFacets.OtmContributedFacet;

/**
 * Filter that rejects empty children owners, non-children owners that are not type users, and aliases.
 * 
 * @author dmh
 *
 */
public class MemberAndUserFilter implements DexFilter<OtmObject> {
	private static Log log = LogFactory.getLog(MemberAndUserFilter.class);

	@Override
	public boolean isSelected(OtmObject obj) {
		// log.debug("Is " + obj + " selected?");
		if (obj instanceof OtmContributedFacet && ((OtmContributedFacet) obj).getContributor() != null)
			obj = ((OtmContributedFacet) obj).getContributor();

		if (obj instanceof OtmChildrenOwner) {
			if (((OtmChildrenOwner) obj).getChildren().isEmpty())
				return false;
		} else {
			if (!(obj instanceof OtmTypeUser))
				return false;
		}
		if (obj instanceof OtmAlias)
			return false;
		return true;
	}
}
