/**
 * 
 */
package org.opentravel.model.otmLibraryMembers;

import java.util.Collection;
import java.util.List;

import org.opentravel.common.ImageManager.Icons;
import org.opentravel.dex.actions.DexActionManager;
import org.opentravel.model.OtmModelElement;
import org.opentravel.model.OtmTypeProvider;
import org.opentravel.model.otmContainers.OtmLibrary;
import org.opentravel.schemacompiler.model.LibraryMember;
import org.opentravel.schemacompiler.model.TLModelElement;

import javafx.scene.Node;

/**
 * Interface implemented by all library members, including complex objects and contextual facets.
 * 
 * @author dmh
 *
 */
public interface OtmLibraryMember {

	public DexActionManager getActionManager();

	public Collection<OtmTypeProvider> getChildren_TypeProviders();

	public List<OtmModelElement<?>> getChildren();

	/**
	 * TLContextualFacet or TLLibraryMember
	 * 
	 * @return
	 */
	public LibraryMember getLM();

	public TLModelElement getTL();

	public OtmLibrary getLibrary();

	public String getNamespace();

	public OtmLibraryMember getOwningMember();

	public String getName();

	public String getLibraryName();

	public boolean isEditable();

	public String getPrefix();

	public void modelChildren();

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
}
