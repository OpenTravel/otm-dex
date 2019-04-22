/**
 * 
 */
package org.opentravel.dex.controllers.member.properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.dex.controllers.DexDAO;
import org.opentravel.dex.controllers.DexIncludedController;
import org.opentravel.model.OtmModelElement;
import org.opentravel.model.OtmTypeUser;
import org.opentravel.model.otmFacets.OtmFacet;
import org.opentravel.model.otmProperties.OtmProperty;
import org.opentravel.model.otmProperties.UserSelectablePropertyTypes;
import org.opentravel.schemacompiler.model.TLProperty;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.ImageView;

/**
 * Manage a facets and properties in a tree table.
 * 
 * @author dmh
 *
 */
public class PropertiesDAO implements DexDAO<OtmModelElement<?>> {
	private static Log log = LogFactory.getLog(PropertiesDAO.class);

	static final String REQUIRED = "Required";
	static final String OPTIONAL = "Optional";

	protected OtmModelElement<?> element;
	protected DexIncludedController<?> controller;

	public PropertiesDAO(OtmFacet<?> property) {
		this.element = property;
	}

	public PropertiesDAO(OtmModelElement<?> element, MemberPropertiesTreeTableController controller) {
		this.element = element;
		this.controller = controller;
	}

	/**
	 * 
	 * @return an observable list of property roles
	 */
	public static ObservableList<String> getRoleList() {
		return UserSelectablePropertyTypes.getObservableList();
	}

	/**
	 * 
	 * @return an observable list of values for minimum repeat field
	 */
	public static ObservableList<String> minList() {
		ObservableList<String> list = FXCollections.observableArrayList();
		list.add(OPTIONAL);
		list.add(REQUIRED);
		return list;
	}

	/**
	 * If the property is a type user, create a simple string property with listener. Otherwise, create a read-only
	 * property.
	 * 
	 * @return
	 */
	public StringProperty assignedTypeProperty() {
		SimpleStringProperty ssp;
		if (element instanceof OtmTypeUser) {
			ssp = new SimpleStringProperty(((OtmTypeUser) element).getAssignedTypeName());
			ssp.addListener((v, o, n) -> {
				new AssignedTypesMenuHandler().handle(n, this);
				controller.refresh();
			});
		} else
			return new ReadOnlyStringWrapper("--");
		return ssp;
	}

	public StringProperty deprecationProperty() {
		String value = element.getDeprecation();

		if (element instanceof OtmFacet)
			return new ReadOnlyStringWrapper("");
		if (!element.isEditable())
			return new ReadOnlyStringWrapper(value);

		StringProperty desc = new SimpleStringProperty(value);
		// TODO - move to action handler
		desc.addListener((ObservableValue<? extends String> ov, String oldValue, String newValue) -> {
			// element.setDesc(newValue);
			log.debug("TODO: Set " + element + " deprecation to " + newValue);
		});
		return desc;
	}

	public StringProperty descriptionProperty() {
		return element.descriptionProperty();
		// if (!(element.getTL() instanceof TLDocumentationOwner))
		// return new ReadOnlyStringWrapper("");
		//
		// String value = element.getDescription();
		// if (!element.isEditable())
		// return new ReadOnlyStringWrapper(value);
		//
		// StringProperty desc = new SimpleStringProperty(value);
		// // TODO - move to action handler
		// desc.addListener(
		// (ObservableValue<? extends String> ov, String oldValue, String newValue) -> setDescription(newValue));
		// return desc;
	}

	public void setDescription(String description) {
		element.setDescription(description);
		log.debug("setDescription " + description + " on " + element);
	}

	public StringProperty exampleProperty() {
		String value = element.getExample();

		// Add empty for properties with complex types
		// if (element.isAssignedComplexType())
		if (element instanceof OtmFacet)
			return new ReadOnlyStringWrapper("");
		if (!element.isEditable())
			return new ReadOnlyStringWrapper(value);

		StringProperty desc = new SimpleStringProperty(value);
		// TODO - move to action handler
		desc.addListener((ObservableValue<? extends String> ov, String oldValue, String newValue) -> {
			// element.setDesc(newValue);
			log.debug("TODO: Set " + element + " example to " + newValue);
		});
		return desc;
	}

	@Override
	public ImageView getIcon(ImageManager imageMgr) {
		return imageMgr.getView(element.getIconType());
	}

	@Override
	public OtmModelElement<?> getValue() {
		return element;
	}

	public DexIncludedController<?> getController() {
		return controller;
	}

	public boolean isEditable() {
		return element.isEditable();
	}

	public IntegerProperty maxProperty() {
		// String value = "";
		// if (element.getTL() instanceof TLProperty)
		// value = String.valueOf(((TLProperty) element.getTL()).getRepeat());
		Integer value = 0;
		if (element.getTL() instanceof TLProperty)
			value = ((TLProperty) element.getTL()).getRepeat();
		return new SimpleIntegerProperty(value);
		// TODO - move to action handler
		// add listener
	}

	public StringProperty minProperty() {
		if (!(element instanceof OtmProperty))
			return new ReadOnlyStringWrapper("");

		String value = OPTIONAL;
		if (((OtmProperty<?>) element).isManditory())
			value = REQUIRED;

		SimpleStringProperty ssp = new SimpleStringProperty(value);
		if (element.isEditable())
			// TODO - move to action handler
			ssp.addListener((ObservableValue<? extends String> ov, String oldVal, String newVal) -> {
				((OtmProperty<?>) element).setManditory(newVal.equals(REQUIRED));
				log.debug("Set optional/manditory of " + element.getName() + " to " + newVal);
			});

		return ssp;
	}

	public StringProperty nameProperty() {
		if (element instanceof OtmProperty)
			return ((OtmProperty<?>) element).nameProperty();
		else
			// TODO - have facet return property
			return new ReadOnlyStringWrapper("" + element.getName());
	}

	public StringProperty roleProperty() {
		StringProperty ssp = new SimpleStringProperty(element.getRole());
		// TODO - move to action handler
		ssp.addListener((ObservableValue<? extends String> ov, String oldVal, String newVal) -> {
			// element.setName(newVal);
			log.debug("TODO - set role of " + element.getName() + " to " + newVal);
		});
		return ssp;
	}

	public void setMax(String newValue) {
		log.debug("TODO: Set max to: " + newValue);
	}

	public ObjectProperty<ImageView> validationImageProperty() {
		element.isValid(); // create findings if none existed
		return element.validationImageProperty();
	}

	@Override
	public String toString() {
		return element.toString();
	}

	// ((TLProperty)tl).getDocumentation().addImplementer(implementer);(null);
	// ((TLProperty)tl).getDocumentation().addMoreInfo(moreInfo);(null);
	// ((TLProperty)tl).getDocumentation().addOtherDoc(otherDoc);(null);

}
