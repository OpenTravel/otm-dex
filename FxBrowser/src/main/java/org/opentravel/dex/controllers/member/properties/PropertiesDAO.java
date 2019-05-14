/**
 * 
 */
package org.opentravel.dex.controllers.member.properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.dex.controllers.DexDAO;
import org.opentravel.dex.controllers.DexIncludedController;
import org.opentravel.model.OtmChildrenOwner;
import org.opentravel.model.OtmObject;
import org.opentravel.model.OtmTypeUser;
import org.opentravel.model.otmFacets.OtmContributedFacet;
import org.opentravel.model.otmFacets.OtmFacet;
import org.opentravel.model.otmProperties.OtmElement;
import org.opentravel.model.otmProperties.OtmProperty;
import org.opentravel.model.otmProperties.UserSelectablePropertyTypes;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

/**
 * Manage a facets and properties in a tree table.
 * 
 * @author dmh
 *
 */
public class PropertiesDAO implements DexDAO<OtmObject> {
	private static Log log = LogFactory.getLog(PropertiesDAO.class);

	static final String REQUIRED = "Required";
	static final String OPTIONAL = "Optional";

	protected OtmObject element;
	protected DexIncludedController<?> controller;

	public PropertiesDAO(OtmFacet<?> property) {
		this.element = property;
	}

	public PropertiesDAO(OtmObject element, DexIncludedController<?> controller) {
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
		StringProperty ssp;
		if (element instanceof OtmTypeUser) {
			ssp = ((OtmTypeUser) element).assignedTypeProperty();
			// if (ssp instanceof SimpleStringProperty)
			// ssp.addListener((v, o, n) -> {
			// new AssignedTypesMenuHandler().handle(n, this);
			// controller.refresh();
			// });
		} else {
			ssp = new ReadOnlyStringWrapper("");
		}
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
	public OtmObject getValue() {
		return element;
	}

	public DexIncludedController<?> getController() {
		return controller;
	}

	public boolean isEditable() {
		return element.isEditable();
	}

	public IntegerProperty maxProperty() {
		Integer value = -1;
		if (element instanceof OtmElement)
			value = ((OtmElement<?>) element).getTL().getRepeat();
		return new SimpleIntegerProperty(value);
		// TODO - add listener
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
		if (element.nameProperty() != null)
			return element.nameProperty();
		// if (element instanceof OtmProperty)
		// return ((OtmProperty<?>) element).nameProperty();
		else
			// TODO - have facet return property
			return new ReadOnlyStringWrapper("" + element.getName());
	}

	public StringProperty roleProperty() {
		StringProperty ssp;
		if (element instanceof OtmProperty) {
			ssp = new SimpleStringProperty(((OtmProperty<?>) element).getRole());
			// TODO - create action handler
			ssp.addListener((ObservableValue<? extends String> ov, String oldVal, String newVal) -> {
				log.debug("TODO - set role of " + element.getName() + " to " + newVal);
			});
		} else {
			ssp = new ReadOnlyStringWrapper("");
		}
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

	/**
	 * Create a tree item for this DAO's element and add to parent. No business logic.
	 * 
	 * @param parent
	 *            to add item as child
	 * @return
	 */
	public TreeItem<PropertiesDAO> createTreeItem(TreeItem<PropertiesDAO> parent) {
		TreeItem<PropertiesDAO> item = new TreeItem<>(this);
		if (element instanceof OtmChildrenOwner)
			item.setExpanded(((OtmChildrenOwner) element).isExpanded());
		if (parent != null)
			parent.getChildren().add(item);
		// Decorate if possible
		if (controller != null && controller.getMainController() != null) {
			ImageManager imageMgr = controller.getMainController().getImageManager();
			if (imageMgr != null) {
				ImageView graphic = imageMgr.getView(element);
				item.setGraphic(graphic);
				Tooltip.install(graphic, new Tooltip(element.getObjectTypeName()));
			}
		}
		return item;
	}

	/**
	 * Add tree items to parent for each descendant of the child owner.
	 * 
	 * @param member
	 *            a child owning library member. Non-child owning properties are ignored.
	 */
	public void createChildrenItems(TreeItem<PropertiesDAO> parent) {
		OtmChildrenOwner member = null;
		if (element instanceof OtmChildrenOwner) {
			member = (OtmChildrenOwner) element;
			// create cells for member's facets and properties
			for (OtmObject child : member.getChildrenHierarchy()) {
				// Create item and add to tree at parent
				TreeItem<PropertiesDAO> item = new PropertiesDAO(child, getController()).createTreeItem(parent);

				// TODO - sort order

				// Contributor children list does not contain other contextual facets
				if (child instanceof OtmContributedFacet && ((OtmContributedFacet) child).getContributor() != null)
					child = ((OtmContributedFacet) child).getContributor();

				// Create tree items for children if any
				if (child instanceof OtmChildrenOwner)
					((OtmChildrenOwner) child).getChildrenHierarchy().forEach(c -> {
						TreeItem<PropertiesDAO> cfItem = new PropertiesDAO(c, getController()).createTreeItem(item);

						// Recurse to model nested contextual facets
						if (c instanceof OtmChildrenOwner)
							new PropertiesDAO(c, getController()).createChildrenItems(cfItem);
					});
			}
		}
	}

	// ((TLProperty)tl).getDocumentation().addImplementer(implementer);(null);
	// ((TLProperty)tl).getDocumentation().addMoreInfo(moreInfo);(null);
	// ((TLProperty)tl).getDocumentation().addOtherDoc(otherDoc);(null);

}
