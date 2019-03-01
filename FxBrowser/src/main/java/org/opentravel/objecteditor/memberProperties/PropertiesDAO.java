/**
 * 
 */
package org.opentravel.objecteditor.memberProperties;

import org.opentravel.common.DialogBox;
import org.opentravel.common.ImageManager;
import org.opentravel.model.OtmModelElement;
import org.opentravel.model.OtmTypeUser;
import org.opentravel.model.otmFacets.OtmFacet;
import org.opentravel.model.otmProperties.OtmProperty;
import org.opentravel.model.otmProperties.UserSelectablePropertyTypes;
import org.opentravel.objecteditor.DexDAO;
import org.opentravel.schemacompiler.model.TLProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.ImageView;

//import javafx.util.converter.IntegerStringConverter;
//javafx.beans.property.SimpleBooleanProperty
// import javafx.beans.property.ReadOnlyStringWrapper;
//javafx.beans.property.ReadOnlyBooleanWrapper
//javafx.beans.property.SimpleintegerProperty
//javafx.beans.property.ReadOnlyintegerWrapper
//javafx.beans.property.SimpleDoubleProperty
//javafx.beans.property.ReadOnlyDoubleWrapper
//javafx.beans.property.SimpleStringProperty
//javafx.beans.property.ReadOnlyStringWrapper

/**
 * Manage a facets and properties in a tree table.
 * 
 * @author dmh
 *
 */
@SuppressWarnings("restriction")
public class PropertiesDAO implements DexDAO<OtmModelElement<?>> {
	private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesDAO.class);
	static final String REQUIRED = "Required";
	static final String OPTIONAL = "Optional";

	static final String CHANGE = "Change (future)";
	static final String GOTO = "Go To (Experimental)";
	static final String REMOVE = "Remove (future)";
	static final String STRING = "xsd:String (future)";

	protected OtmModelElement<?> element;
	protected PropertiesTableController controller;

	public PropertiesDAO(OtmFacet<?> property) {
		this.element = property;
	}

	public PropertiesDAO(OtmModelElement<?> element, PropertiesTableController controller) {
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
	 * 
	 * @return an observable list of values for minimum repeat field
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
				System.out.println("TODO - Set " + element + " type to " + newValue);

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

	public StringProperty deprecationProperty() {
		String value = element.getDeprecation();

		if (element instanceof OtmFacet)
			return new ReadOnlyStringWrapper("");
		if (!element.isEditable())
			return new ReadOnlyStringWrapper(value);

		StringProperty desc = new SimpleStringProperty(value);
		desc.addListener((ObservableValue<? extends String> ov, String oldValue, String newValue) -> {
			// element.setDesc(newValue);
			System.out.println("TODO - Set " + element + " deprecation to " + newValue);
		});
		return desc;
	}

	public StringProperty descriptionProperty() {
		String value = element.getDescription();

		if (element instanceof OtmFacet)
			return new ReadOnlyStringWrapper("");
		if (!element.isEditable())
			return new ReadOnlyStringWrapper(value);

		StringProperty desc = new SimpleStringProperty(value);
		desc.addListener((ObservableValue<? extends String> ov, String oldValue, String newValue) -> {
			// element.setDesc(newValue);
			System.out.println("TODO - Set " + element + " description to " + newValue);
		});
		return desc;
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
		desc.addListener((ObservableValue<? extends String> ov, String oldValue, String newValue) -> {
			// element.setDesc(newValue);
			System.out.println("TODO - Set " + element + " example to " + newValue);
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
	}

	public StringProperty minProperty() {
		if (!(element instanceof OtmProperty))
			return new ReadOnlyStringWrapper("");

		String value = OPTIONAL;
		if (((OtmProperty<?>) element).isManditory())
			value = REQUIRED;

		SimpleStringProperty ssp = new SimpleStringProperty(value);
		if (element.isEditable())
			ssp.addListener((ObservableValue<? extends String> ov, String oldVal, String newVal) -> {
				((OtmProperty<?>) element).setManditory(newVal.equals(REQUIRED));
				System.out.println("Set optional/manditory of " + element.getName() + " to " + newVal);
			});

		return ssp;
	}

	public StringProperty nameProperty() {
		StringProperty nameProperty;
		if (element.isEditable()) {
			nameProperty = new SimpleStringProperty(element.getName());
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
		ssp.addListener((ObservableValue<? extends String> ov, String oldVal, String newVal) -> {
			// element.setName(newVal);
			System.out.println("TODO - set role of " + element.getName() + " to " + newVal);
		});
		return ssp;
	}

	public void setMax(String newValue) {
		System.out.println("Setting max to: " + newValue);
	}

	@Override
	public String toString() {
		return element.toString();
	}

	// ((TLProperty)tl).getDocumentation().addImplementer(implementer);(null);
	// ((TLProperty)tl).getDocumentation().addMoreInfo(moreInfo);(null);
	// ((TLProperty)tl).getDocumentation().addOtherDoc(otherDoc);(null);

}
