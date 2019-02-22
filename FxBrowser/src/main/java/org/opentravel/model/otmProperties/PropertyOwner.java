/**
 * 
 */
package org.opentravel.model.otmProperties;

import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;

/**
 * All owners of properties (elements, attributes, idicators...) must implement this interface.
 * 
 * @author dmh
 *
 */
public interface PropertyOwner {

	/**
	 * 
	 * @return the library member that owns this property owner
	 */
	public OtmLibraryMember<?> getOwningMember();

}
