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
public interface OtmChildrenOwner {

	/**
	 * Get a list of all the children of this object. To allow lazy evaluation, implementations are expected to attempt
	 * to model the children if the list is empty.
	 * 
	 * @return list of children or empty list.
	 */
	public List<OtmModelElement<?>> getChildren();

	public Collection<OtmTypeProvider> getChildren_TypeProviders();

	/**
	 * Model the children of this object from its' tlObject(s).
	 */
	public void modelChildren();
}