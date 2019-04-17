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
import org.opentravel.schemacompiler.validate.FindingType;

import javafx.beans.property.ReadOnlyStringWrapper;
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
		return imageMgr != null ? imageMgr.getView(otmObject.getIconType()) : null;
	}

	@Override
	public OtmModelElement<?> getValue() {
		return otmObject;
	}

	public boolean isEditable() {
		return otmObject.isEditable();
	}

	public StringProperty errorProperty() {
		// Move to OtmModelElement
		otmObject.isValid(true); // consider moving to task
		String errMsg = "-/-";
		if (otmObject.getFindings() != null) {
			int warnings = otmObject.getFindings().count(FindingType.WARNING);
			int errors = otmObject.getFindings().count(FindingType.ERROR);
			errMsg = Integer.toString(warnings) + "/" + Integer.toString(errors);
		}
		return new ReadOnlyStringWrapper(errMsg);
	}

	public StringProperty libraryProperty() {
		String libName = "";
		if (otmObject instanceof OtmLibraryMember)
			if (((OtmLibraryMember<?>) otmObject).getTL().getOwningLibrary() != null)
				libName = ((OtmLibraryMember<?>) otmObject).getTL().getOwningLibrary().getName();
		return new SimpleStringProperty(libName);
	}

	public StringProperty nameProperty() {
		StringProperty p = otmObject.nameProperty();
		// p.addListener((v, o, n) -> otmObject.nameProperty().set(n));
		// SimpleStringProperty ssp = new SimpleStringProperty(otmObject.getName());
		// ssp.addListener((ov, old, newValue) -> setName(newValue)); // Track changes
		return (otmObject.nameProperty());
		// return ssp;
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
