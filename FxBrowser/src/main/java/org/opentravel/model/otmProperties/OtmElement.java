/**
 * Copyright (C) 2014 OpenTravel Alliance (info@opentravel.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 */
package org.opentravel.model.otmProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.common.ImageManager.Icons;
import org.opentravel.dex.controllers.member.properties.MemberPropertiesTreeTableController;
import org.opentravel.model.OtmModelElement;
import org.opentravel.model.OtmPropertyOwner;
import org.opentravel.model.OtmTypeProvider;
import org.opentravel.model.OtmTypeUser;
import org.opentravel.schemacompiler.model.TLModelElement;
import org.opentravel.schemacompiler.model.TLProperty;
import org.opentravel.schemacompiler.model.TLPropertyType;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Abstract OTM Node for attribute properties.
 * 
 * @author Dave Hollander
 * 
 */
public class OtmElement<TL extends TLProperty> extends OtmProperty<TLProperty> implements OtmTypeUser {
	private static Log log = LogFactory.getLog(MemberPropertiesTreeTableController.class);

	private StringProperty assignedTypeProperty;

	/**
	 */
	public OtmElement(TL tl, OtmPropertyOwner parent) {
		super(tl, parent);

		if (!(tl instanceof TLProperty))
			throw new IllegalArgumentException("OtmElement constructor not passed a tl property.");
		// if (tl.isReference())
		// throw new IllegalArgumentException("OtmElement constructor passed a property reference.");
	}

	@Override
	public StringProperty assignedTypeProperty() {
		if (assignedTypeProperty == null && isEditable() && !getAssignedTypeName().isEmpty())
			assignedTypeProperty = new SimpleStringProperty(getAssignedTypeName());
		else
			assignedTypeProperty = new ReadOnlyStringWrapper(getAssignedTypeName());
		return assignedTypeProperty;
	}

	@Override
	public TLProperty getTL() {
		return (TLProperty) tlObject;
	}

	/**
	 * Get the "typeName" field from the TL object.
	 */
	@Override
	public String getAssignedTypeName() {
		return getTL().getTypeName();
	}

	@Override
	public String getAssignedTypeLocalName() {
		if (getTL() == null || getTL().getType() == null) {
			log.warn("Missing TL assigned type.");
			return "";
		}
		return getTL().getType().getLocalName();
	}

	@Override
	public OtmTypeProvider getAssignedType() {
		OtmModelElement<TLModelElement> tp = OtmModelElement.get((TLModelElement) getTL().getType());
		return tp instanceof OtmTypeProvider ? (OtmTypeProvider) tp : null;
	}

	@Override
	public String getName() {
		return getTL().getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String setName(String name) {
		getTL().setName(name);
		isValid(true);
		return getName();
	}

	@Override
	public Icons getIconType() {
		return ImageManager.Icons.ELEMENT;
	}

	@Override
	public boolean isManditory() {
		return getTL().isMandatory();
	}

	@Override
	public void setManditory(boolean value) {
		getTL().setMandatory(value);
	}

	@Override
	public TLPropertyType getAssignedTLType() {
		return getTL().getType();
	}

	@Override
	public void setTLTypeName(String typeName) {
		getTL().setType(null);
		getTL().setTypeName(typeName);
	}

	/**
	 * Useful for types that are not in the model manager.
	 */
	@Override
	public TLPropertyType setAssignedTLType(TLPropertyType type) {
		if (type == null)
			return null;
		getTL().setType(type);

		log.debug("Set assigned TL type to: " + type.getLocalName());
		return getTL().getType();
	}

	@Override
	public OtmTypeProvider setAssignedType(OtmTypeProvider type) {
		if (type == null)
			return null; // May not be a modeled type on ondo
		@SuppressWarnings("unchecked")
		TLModelElement tlType = ((OtmModelElement<TLModelElement>) type).getTL();
		if (tlType instanceof TLPropertyType)
			getTL().setType((TLPropertyType) tlType);

		if (type.isNameControlled())
			setName(type.getName());

		log.debug("Set assigned type to: " + type);
		return getAssignedType() == type ? type : null;
	}

}
