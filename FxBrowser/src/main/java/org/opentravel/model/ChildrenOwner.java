/**
 * 
 */
package org.opentravel.model;

import java.util.List;

/**
 * All owners of children must implement this interface.
 * 
 * @author dmh
 *
 */
public interface ChildrenOwner {

	/**
	 * Get a list of all the children of this object. To allow lazy evaluation, implementations are expected to attempt
	 * to model the children if the list is empty.
	 * 
	 * @return list of children or empty list.
	 */
	public List<OtmModelElement<?>> getChildren();

	/**
	 * Model the children of this object from its' tlObject(s).
	 */
	public void modelChildren();
}