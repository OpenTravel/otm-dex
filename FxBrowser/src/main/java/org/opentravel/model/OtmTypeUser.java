/**
 * 
 */
package org.opentravel.model;

import org.opentravel.schemacompiler.model.NamedEntity;

import javafx.beans.property.StringProperty;

/**
 * OtmTypeUser is an interface and utility class. It is not part of the type hierarchy. Therefore, it has several method
 * declarations that duplicate other interfaces.
 * 
 * @author dmh
 *
 */
public interface OtmTypeUser extends OtmObject {

	/**
	 * FX Property with the type name. Adds prefix if the owner and type are in different libraries.
	 * 
	 * @return
	 */
	public StringProperty assignedTypeProperty();

	public NamedEntity getAssignedTLType();

	/**
	 * Get the type from the listener on the assigned TL Type.
	 * 
	 * @return
	 */
	public OtmTypeProvider getAssignedType();

	/**
	 * Get the "typeName" field from the TL object. Should only be used as last resort if Otm and TL objects are not
	 * available.
	 */
	public String getTlAssignedTypeName();

	public NamedEntity setAssignedTLType(NamedEntity type);

	public OtmTypeProvider setAssignedType(OtmTypeProvider type);

	/**
	 * Should only be used as last resort if Otm and TL objects are not available. Sometimes, only the name is known
	 * because the tl model does not have the type loaded.
	 * <p>
	 * <b>Warning:</b> The type is set to null so the name will be used in the compiler.
	 * 
	 * @param oldTLTypeName
	 */
	public void setTLTypeName(String name);
}
