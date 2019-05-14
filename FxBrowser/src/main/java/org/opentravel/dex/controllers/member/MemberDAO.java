/**
 * 
 */
package org.opentravel.dex.controllers.member;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.dex.controllers.DexDAO;
import org.opentravel.model.OtmObject;
import org.opentravel.model.OtmTypeProvider;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
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
public class MemberDAO implements DexDAO<OtmObject> {
	private static Log log = LogFactory.getLog(MemberDAO.class);

	protected OtmObject otmObject;

	public MemberDAO(OtmLibraryMember member) {
		this.otmObject = member;
	}

	public MemberDAO(OtmTypeProvider provider) {
		this.otmObject = provider;
	}

	@Override
	public ImageView getIcon(ImageManager imageMgr) {
		return imageMgr != null ? imageMgr.getView(otmObject.getIconType()) : null;
	}

	@Override
	public OtmObject getValue() {
		return otmObject;
	}

	public boolean isEditable() {
		return otmObject.isEditable();
	}

	public StringProperty usedTypesProperty() {
		String usedTypeCount = "";
		if (otmObject instanceof OtmLibraryMember) {
			List<OtmTypeProvider> u = ((OtmLibraryMember) otmObject).getUsedTypes();
			if (u != null)
				usedTypeCount = Integer.toString(u.size());
		}
		return new ReadOnlyStringWrapper(usedTypeCount);
	}

	public StringProperty errorProperty() {
		return otmObject.validationProperty();
	}

	public ObjectProperty<ImageView> errorImageProperty() {
		return otmObject.validationImageProperty();
	}

	public StringProperty libraryProperty() {
		if (otmObject instanceof OtmLibraryMember)
			return ((OtmLibraryMember) otmObject).libraryProperty();
		return new ReadOnlyStringWrapper(otmObject.getLibrary().getName());
	}

	public StringProperty prefixProperty() {
		if (otmObject instanceof OtmLibraryMember)
			return ((OtmLibraryMember) otmObject).prefixProperty();
		return new ReadOnlyStringWrapper(otmObject.getPrefix());
	}

	public StringProperty nameProperty() {
		return (otmObject.nameProperty());
	}

	public void setName(String name) {
		if (otmObject instanceof OtmLibraryMember)
			otmObject.setName(name);
	}

	@Override
	public String toString() {
		return otmObject != null ? otmObject.toString() : "";
	}

	public StringProperty versionProperty() {
		if (otmObject instanceof OtmLibraryMember)
			return ((OtmLibraryMember) otmObject).versionProperty();
		return new ReadOnlyStringWrapper("");
	}

}
