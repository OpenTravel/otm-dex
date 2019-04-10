/**
 * 
 */
package org.opentravel.dex.controllers.member.properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.DialogBox;
import org.opentravel.common.ImageManager;
import org.opentravel.dex.controllers.DexDAO;
import org.opentravel.model.OtmModelElement;
import org.opentravel.model.OtmTypeUser;
import org.opentravel.model.otmFacets.OtmFacet;
import org.opentravel.model.otmProperties.OtmProperty;
import org.opentravel.model.otmProperties.UserSelectablePropertyTypes;
import org.opentravel.schemacompiler.model.TLDocumentationOwner;
import org.opentravel.schemacompiler.model.TLProperty;

import javafx.beans.property.IntegerProperty;
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
// @SuppressWarnings("restriction")
public class PropertiesDAO implements DexDAO<OtmModelElement<?>> {
	private static Log log = LogFactory.getLog(PropertiesDAO.class);

	static final String REQUIRED = "Required";
	static final String OPTIONAL = "Optional";

	static final String CHANGE = "Change (future)";
	static final String GOTO = "Go To (Experimental)";
	static final String REMOVE = "Remove (future)";
	static final String STRING = "xsd:String (future)";

	protected OtmModelElement<?> element;
	protected MemberPropertiesTreeTableController controller;

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
	 * @return an observable list of values for the assigned type actions
	 */
	public static ObservableList<String> getAssignedTypeList() {
		ObservableList<String> list = FXCollections.observableArrayList();
		list.add(GOTO);
		list.add(CHANGE);
		list.add(REMOVE);
		list.add(STRING);
		return list;
	}

	public StringProperty assignedTypeProperty() {
		SimpleStringProperty ssp;
		if (element instanceof OtmTypeUser) {
			ssp = new SimpleStringProperty(((OtmTypeUser) element).getAssignedTypeName());
			ssp.addListener((ObservableValue<? extends String> ov, String oldValue, String newValue) -> {
				log.debug("TODO: Set " + element + " type to " + newValue);

				if (newValue.equals(CHANGE))
					DialogBox.display("Set Assigned Type", "TODO - view to select type.");
				else if (newValue.equals(GOTO)) {
					if (element instanceof OtmTypeUser)
						controller.select(((OtmTypeUser) element).getAssignedTypeLocalName());
					// controller.select((OtmModelElement<?>) ((OtmTypeUser) element).getAssignedType());
				} else
					DialogBox.notify("Set Assigned Type", newValue + " is not implemented yet.");
				// How to set value without firing the listener?
			});
		}

		else
			return new ReadOnlyStringWrapper("--");
		// TODO - add listener and change wizard
		return ssp;
	}

	// TODO
	// 1. Where does the combo behavior belong?
	// 2. Who is responsible for finding the assigned type
	// 3. How to inform consumers without event loop
	// 4. Where does editing action behavior belong
	//
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
		if (!(element.getTL() instanceof TLDocumentationOwner))
			return new ReadOnlyStringWrapper("");

		String value = element.getDescription();
		if (!element.isEditable())
			return new ReadOnlyStringWrapper(value);

		StringProperty desc = new SimpleStringProperty(value);
		// TODO - move to action handler
		desc.addListener(
				(ObservableValue<? extends String> ov, String oldValue, String newValue) -> setDescription(newValue));
		return desc;
	}

	public void setDescription(String description) {
		// element.setDescription(description);
		log.debug("TODO: setDescription " + description + " on " + element);
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
		StringProperty nameProperty;
		if (element.isEditable()) {
			nameProperty = new SimpleStringProperty(element.getName());
			// TODO - move to action handler
			// Adding a change listener with lambda expression
			nameProperty.addListener((ObservableValue<? extends String> ov, String oldVal, String newVal) -> {
				element.setName(newVal);
			});
		} else {
			nameProperty = new ReadOnlyStringWrapper("" + element.getName());
		}
		return nameProperty;
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

	@Override
	public String toString() {
		return element.toString();
	}

	// ((TLProperty)tl).getDocumentation().addImplementer(implementer);(null);
	// ((TLProperty)tl).getDocumentation().addMoreInfo(moreInfo);(null);
	// ((TLProperty)tl).getDocumentation().addOtherDoc(otherDoc);(null);

}
