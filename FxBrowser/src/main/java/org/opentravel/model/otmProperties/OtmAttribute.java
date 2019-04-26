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
import org.opentravel.model.OtmModelElement;
import org.opentravel.model.OtmPropertyOwner;
import org.opentravel.model.OtmTypeProvider;
import org.opentravel.model.OtmTypeUser;
import org.opentravel.schemacompiler.model.TLAttribute;
import org.opentravel.schemacompiler.model.TLModelElement;
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
public class OtmAttribute<TL extends TLAttribute> extends OtmProperty<TLAttribute> implements OtmTypeUser {
	private static Log log = LogFactory.getLog(OtmAttribute.class);

	private StringProperty assignedTypeProperty;

	/**
	 * @param tlBusinessObject
	 */
	public OtmAttribute(TL tl, OtmPropertyOwner parent) {
		super(tl, parent);

		if (!(tl instanceof TLAttribute))
			throw new IllegalArgumentException("OtmAttribute constructor not passed a tl attribute.");
		// if (tl.isReference())
		// throw new IllegalArgumentException("OtmAttribute constructor passed a attribute reference.");
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
	public TLAttribute getTL() {
		return (TLAttribute) tlObject;
	}

	@Override
	public String getAssignedTypeName() {
		return getTL().getTypeName();
	}

	@Override
	public String getAssignedTypeLocalName() {
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

	@Override
	public String getRole() {
		return UserSelectablePropertyTypes.Attribute.toString();
	}

	@Override
	public boolean isManditory() {
		return getTL().isMandatory();
	}

	@Override
	public void setManditory(boolean value) {
		getTL().setMandatory(value);
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
	public void setTLTypeName(String typeName) {
		getTL().setTypeName(typeName);
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public Icons getIconType() {
		return ImageManager.Icons.ATTRIBUTE;
	}

	@Override
	public TLPropertyType getAssignedTLType() {
		return getTL().getType();
	}

	/**
	 * Useful for types that are not in the model manager.
	 */
	@Override
	public TLPropertyType setAssignedTLType(TLPropertyType type) {
		getTL().setType(type);
		return getTL().getType();
	}

	@Override
	public OtmTypeProvider setAssignedType(OtmTypeProvider type) {
		@SuppressWarnings("unchecked")
		TLModelElement tlType = ((OtmModelElement<TLModelElement>) type).getTL();
		if (tlType instanceof TLPropertyType)
			getTL().setType((TLPropertyType) tlType);
		return getAssignedType() == type ? type : null;
	}

}
