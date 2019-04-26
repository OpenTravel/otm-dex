/**
 * 
 */
package org.opentravel.model;

import java.util.Collection;
import java.util.List;

import org.opentravel.schemacompiler.model.TLModelElement;

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

	/**
	 * Get a list of children organized by inheritance. For example, a business object will only report out the ID facet
	 * and the ID facet will include the summary facet in this list.
	 * <p>
	 * To allow lazy evaluation, implementations are expected to attempt to model the children if the list is empty.
	 * 
	 * @return list of children or empty list.
	 */
	public Collection<OtmModelElement<TLModelElement>> getChildrenHierarchy();

	public Collection<OtmTypeProvider> getChildren_TypeProviders();

	/**
	 * Model the children of this object from its' tlObject(s).
	 */
	public void modelChildren();
}