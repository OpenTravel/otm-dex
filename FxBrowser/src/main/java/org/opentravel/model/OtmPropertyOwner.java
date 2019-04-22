/**
 * 
 */
package org.opentravel.model;

import org.opentravel.dex.actions.DexActionManager;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;
import org.opentravel.model.otmProperties.OtmProperty;
import org.opentravel.schemacompiler.model.TLModelElement;

/**
 * All owners of properties (elements, attributes, indicators...) must implement this interface.
 * 
 * @author dmh
 *
 */
public interface OtmPropertyOwner extends OtmChildrenOwner {

	/**
	 * 
	 * @return the library member that owns this property owner
	 */
	public OtmLibraryMember getOwningMember();

	/**
	 * Add the passed TL property/attribute/indicator then create OtmProperty
	 * 
	 * @return the new OtmProperty
	 */
	public OtmProperty<?> add(TLModelElement newTL);

	/**
	 * @return
	 */
	public DexActionManager getActionManager();
}
