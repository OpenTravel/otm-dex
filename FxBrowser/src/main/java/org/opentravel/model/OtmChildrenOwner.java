/**
 * 
 */
package org.opentravel.model;

import java.util.Collection;
import java.util.List;

/**
 * All owners of children must implement this interface.
 * 
 * @author dmh
 *
 */
public interface OtmChildrenOwner extends OtmObject {

	/**
	 * Get a list of all the children of this object. To allow lazy evaluation, implementations are expected to attempt
	 * to model the children if the list is empty.
	 * 
	 * @return list of children or empty list.
	 */
	public List<OtmObject> getChildren();

	/**
	 * Get a list of children organized by inheritance. For example, a business object will only report out the ID facet
	 * and the ID facet will include the summary facet in this list.
	 * <p>
	 * To allow lazy evaluation, implementations are expected to attempt to model the children if the list is empty.
	 * 
	 * @return list of children or empty list.
	 */
	public Collection<OtmObject> getChildrenHierarchy();

	/**
	 * Get a list of children that are type providers.
	 * 
	 * @return new list of children or empty list.
	 */
	public Collection<OtmTypeProvider> getChildrenTypeProviders();

	/**
	 * @return new list of all descendants that are children owners or empty list, never null
	 */
	public Collection<OtmChildrenOwner> getDescendantsChildrenOwners();

	/**
	 * Get a list of children and their descendants that are type providers.
	 * 
	 * @return list of children or empty list.
	 */
	public Collection<OtmTypeProvider> getDescendantsTypeProviders();

	/**
	 * @return
	 */
	public Collection<OtmTypeUser> getDescendantsTypeUsers();

	/**
	 * Model the children of this object from its' tlObject(s).
	 */
	public void modelChildren();
}