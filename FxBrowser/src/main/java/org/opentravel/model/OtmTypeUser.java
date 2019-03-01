/**
 * 
 */
package org.opentravel.model;

/**
 * @author dmh
 *
 */
public interface OtmTypeUser {

	public OtmTypeProvider getAssignedType();

	public String getAssignedTypeName();

	/**
	 * @return the local name of the assigned type (no prefix)
	 */
	String getAssignedTypeLocalName();
}
