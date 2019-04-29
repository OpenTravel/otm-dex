/**
 * 
 */
package org.opentravel.model.otmLibraryMembers;

import java.util.List;

import org.opentravel.common.ImageManager.Icons;
import org.opentravel.dex.actions.DexActionManager;
import org.opentravel.model.OtmChildrenOwner;
import org.opentravel.model.OtmTypeProvider;
import org.opentravel.model.otmContainers.OtmLibrary;
import org.opentravel.schemacompiler.model.LibraryMember;
import org.opentravel.schemacompiler.model.TLModelElement;

import javafx.beans.property.StringProperty;
import javafx.scene.Node;

/**
 * Interface implemented by all library members, including complex objects and contextual facets.
 * 
 * @author dmh
 *
 */
public interface OtmLibraryMember extends OtmChildrenOwner {

	/**
	 * @return fx property for description
	 */
	public StringProperty descriptionProperty();

	public DexActionManager getActionManager();

	/**
	 * @return
	 */
	public Icons getIconType();

	public OtmLibrary getLibrary();

	public String getLibraryName();

	public String getName();

	public String getNamespace();

	/**
	 * @return
	 */
	public String getObjectTypeName();

	public OtmLibraryMember getOwningMember();

	public String getPrefix();

	// public void modelChildren();

	public TLModelElement getTL();

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
	 * @return
	 */
	public String getValidationFindingsAsString();

	public boolean isEditable();

	/**
	 * Are there any warnings or errors in the findings?
	 * 
	 * @param force
	 *            regenerating findings by validating with the compiler
	 * @return
	 */
	public boolean isValid(boolean force);

	/**
	 * @return fx property for library name
	 */
	public StringProperty libraryProperty();

	/**
	 * @return fx property for object name
	 */
	public StringProperty nameProperty();

	/**
	 * @return fx property for library prefix
	 */
	public StringProperty prefixProperty();

	public String setName(String text);

	/**
	 * @return
	 */
	public Node validationImage();

	public StringProperty versionProperty();

	/**
	 * @return list of type providers used by all descendants of this member.
	 */
	public List<OtmTypeProvider> getUsedTypes();

}
