/**
 * 
 */
package org.opentravel.model;

import org.opentravel.schemacompiler.model.TLPropertyType;

/**
 * @author dmh
 *
 */
public interface OtmTypeUser {

	public OtmTypeProvider getAssignedType();

	public String getAssignedTypeName();

	public TLPropertyType getAssignedTLType();

	/**
	 * @return the local name of the assigned type (no prefix)
	 */
	String getAssignedTypeLocalName();
}
