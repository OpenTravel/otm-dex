/**
 * 
 */
package org.opentravel.dex.controllers.member;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.dex.controllers.DexDAO;
import org.opentravel.model.OtmModelElement;
import org.opentravel.model.OtmTypeProvider;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.ImageView;

/**
 * The TreeItem properties used in ModelMembersTreeController TreeTableView. Simple Data Access Object that contains and
 * provides gui access to OTM model library members and type provider children.
 *
 * @author dmh
 * @param <T>
 *
 */
public class MemberDAO implements DexDAO<OtmModelElement<?>> {
	private static Log log = LogFactory.getLog(MemberDAO.class);

	protected OtmModelElement<?> otmObject;

	public MemberDAO(OtmLibraryMember<?> member) {
		this.otmObject = member;
	}

	public MemberDAO(OtmTypeProvider provider) {
		this.otmObject = (OtmModelElement<?>) provider;
	}

	@Override
	public ImageView getIcon(ImageManager imageMgr) {
		return imageMgr.getView(otmObject.getIconType());
	}

	@Override
	public OtmModelElement<?> getValue() {
		return otmObject;
	}

	public boolean isEditable() {
		return otmObject.isEditable();
	}

	public StringProperty libraryProperty() {
		String libName = "";
		if (otmObject instanceof OtmLibraryMember)
			if (((OtmLibraryMember<?>) otmObject).getTL().getOwningLibrary() != null)
				libName = ((OtmLibraryMember<?>) otmObject).getTL().getOwningLibrary().getName();
		return new SimpleStringProperty(libName);
	}

	public StringProperty nameProperty() {
		SimpleStringProperty ssp = new SimpleStringProperty(otmObject.getName());
		ssp.addListener((ov, old, newValue) -> setName(newValue)); // Track changes
		return ssp;
	}

	public void setName(String name) {
		if (otmObject instanceof OtmLibraryMember<?>)
			((OtmLibraryMember<?>) otmObject).setName(name);
	}

	@Override
	public String toString() {
		return otmObject.toString();
	}

	public StringProperty versionProperty() {
		String v = "";
		if (otmObject instanceof OtmLibraryMember)
			if (((OtmLibraryMember<?>) otmObject).getTL().getOwningLibrary() != null)
				v = ((OtmLibraryMember<?>) otmObject).getTL().getOwningLibrary().getVersion();
		return new SimpleStringProperty(v);
	}

}
