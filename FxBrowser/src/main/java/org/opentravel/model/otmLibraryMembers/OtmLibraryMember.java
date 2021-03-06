/**
 * 
 */
package org.opentravel.model.otmLibraryMembers;

import java.util.List;

import org.opentravel.model.OtmChildrenOwner;
import org.opentravel.model.OtmObject;
import org.opentravel.model.OtmTypeProvider;
import org.opentravel.schemacompiler.model.LibraryMember;
import org.opentravel.schemacompiler.model.TLAlias;

import javafx.beans.property.StringProperty;

/**
 * Interface implemented by all library members, including complex objects and contextual facets.
 * 
 * @author dmh
 *
 */
public interface OtmLibraryMember extends OtmChildrenOwner {

	/**
	 * Add this facet alias to the appropriate alias
	 * 
	 * @param tla
	 *            must be an alias on a tlFacet
	 */
	public void addAlias(TLAlias tla);

	/**
	 * @return
	 */
	public StringProperty baseTypeProperty();

	/**
	 * @param o
	 * @return true if object is direct child of member
	 */
	boolean contains(OtmObject o);

	/**
	 * Get the object that is extended by this object -- its base type.
	 * 
	 * @return an OtmObject that is extended by this object or null.
	 */
	public OtmObject getBaseType();

	public String getLibraryName();

	/**
	 * @return
	 */
	@Override
	public String getObjectTypeName();

	/**
	 * TLContextualFacet or TLLibraryMember
	 * <p>
	 * Not all library members implement the TLLibraryMember class. This method makes it easy to get a LibraryMember
	 * regardless of which type hierarchy it belongs.
	 * 
	 * @return
	 */
	public LibraryMember getTlLM();

	/**
	 * @return non-null, sorted list of type providers used by all descendants of this member.
	 */
	public List<OtmTypeProvider> getUsedTypes();

	/**
	 * Get all members that contain type users that use this member or any of its descendants as assigned types.
	 * 
	 * @return list of where used or empty list
	 */
	public List<OtmLibraryMember> getWhereUsed();

	/**
	 * @return fx property for library name
	 */
	public StringProperty libraryProperty();

	/**
	 * @return fx property for library prefix
	 */
	public StringProperty prefixProperty();

	public StringProperty versionProperty();

}
