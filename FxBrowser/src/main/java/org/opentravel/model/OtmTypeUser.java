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

	public OtmTypeProvider setAssignedType(OtmTypeProvider type);

	public String getAssignedTypeName();

	public TLPropertyType getAssignedTLType();

	public TLPropertyType setAssignedTLType(TLPropertyType type);

	/**
	 * @return the local name of the assigned type (no prefix)
	 */
	public String getAssignedTypeLocalName();

	/**
	 * Should only be used as last resort if Otm and TL objects are not available. Sometimes, only the name is known
	 * because the tl model does not have the type loaded.
	 * <p>
	 * <b>Warning:</b> The type is set to null so the name will be used in the compiler.
	 * 
	 * @param oldTLTypeName
	 */
	public void setTLTypeName(String oldTLTypeName);
}
