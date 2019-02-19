/**
 * 
 */
package org.opentravel.objecteditor;

import org.opentravel.model.OtmModelElement;
import org.opentravel.model.facetNodes.OtmFacet;
import org.opentravel.model.objectNodes.OtmLibraryMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.ImageView;

/**
 * The type of the TreeItem instances used in this TreeTableView. Simple Data Access Object that contains and provides
 * gui access to OTM model library members.
 *
 * @author dmh
 *
 * 
 * @author dmh
 *
 */
// TODO - Should this be NamedEntityTreeDAO ??
// TODO - should this be TypeProviderTreeDAO ??
@SuppressWarnings("restriction")
public class LibraryMemberTreeDAO {
	private static final Logger LOGGER = LoggerFactory.getLogger(LibraryMemberTreeDAO.class);

	protected OtmModelElement<?> otmObject;

	public LibraryMemberTreeDAO(OtmLibraryMember<?> member) {
		this.otmObject = member;
	}

	public LibraryMemberTreeDAO(OtmFacet<?> facet) {
		this.otmObject = facet;
	}

	public String getPrefix() {
		return otmObject.getPrefix();
		// if (otmObject instanceof OtmLibraryMember<?>)
		// return otmObject.getClass().getSimpleName();
		// return "";
	}

	public StringProperty nameProperty() {
		SimpleStringProperty ssp = new SimpleStringProperty(otmObject.getName());
		ssp.addListener((ov, old, newValue) -> setName(newValue)); // Track changes
		return ssp;
	}

	public ImageView getIcon(ImageManager imageMgr) {
		return imageMgr.getView(otmObject.getIconType());
	}

	public String getName() {
		return otmObject.getName();
	}

	public boolean isEditable() {
		return otmObject.isEditable();
	}

	public void setName(String name) {
		if (otmObject instanceof OtmLibraryMember<?>)
			((OtmLibraryMember<?>) otmObject).setName(name);
		// TODO - update children
	}

	@Override
	public String toString() {
		return otmObject.toString();
	}

	/**
	 * @return
	 */
	public OtmModelElement<?> getValue() {
		return otmObject;
	}

}
