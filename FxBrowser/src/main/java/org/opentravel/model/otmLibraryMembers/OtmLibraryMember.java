/**
 * 
 */
package org.opentravel.model.otmLibraryMembers;

import org.opentravel.common.ImageManager.Icons;
import org.opentravel.dex.actions.DexActionManager;
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
public interface OtmLibraryMember {

	public DexActionManager getActionManager();

	// public Collection<OtmTypeProvider> getChildren_TypeProviders();

	// public List<OtmModelElement<?>> getChildren();

	/**
	 * TLContextualFacet or TLLibraryMember
	 * <p>
	 * Not all library members implement the TLLibraryMember class. This method makes it easy to get a LibraryMember
	 * regardless of which type hierarchy it belongs.
	 * 
	 * @return
	 */
	public LibraryMember getTlLM();

	public TLModelElement getTL();

	public OtmLibrary getLibrary();

	public String getNamespace();

	public OtmLibraryMember getOwningMember();

	public String getName();

	public String getLibraryName();

	public boolean isEditable();

	public String getPrefix();

	// public void modelChildren();

	public String setName(String text);

	/**
	 * Are there any warnings or errors in the findings?
	 * 
	 * @param force
	 *            regenerating findings by validating with the compiler
	 * @return
	 */
	public boolean isValid(boolean force);

	/**
	 * @return
	 */
	public Icons getIconType();

	/**
	 * @return
	 */
	public Node validationImage();

	/**
	 * @return
	 */
	public String getValidationFindingsAsString();

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
