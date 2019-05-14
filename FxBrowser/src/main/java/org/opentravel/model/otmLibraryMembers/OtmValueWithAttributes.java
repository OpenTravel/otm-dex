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
package org.opentravel.model.otmLibraryMembers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.common.ImageManager.Icons;
import org.opentravel.common.OtmTypeUserUtils;
import org.opentravel.model.OtmChildrenOwner;
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.OtmPropertyOwner;
import org.opentravel.model.OtmTypeProvider;
import org.opentravel.model.OtmTypeUser;
import org.opentravel.model.otmProperties.OtmProperty;
import org.opentravel.model.otmProperties.OtmPropertyFactory;
import org.opentravel.schemacompiler.model.NamedEntity;
import org.opentravel.schemacompiler.model.TLAttribute;
import org.opentravel.schemacompiler.model.TLAttributeType;
import org.opentravel.schemacompiler.model.TLIndicator;
import org.opentravel.schemacompiler.model.TLModelElement;
import org.opentravel.schemacompiler.model.TLPropertyType;
import org.opentravel.schemacompiler.model.TLValueWithAttributes;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * OTM Object Node for Core objects.
 * 
 * @author Dave Hollander
 * 
 */
public class OtmValueWithAttributes extends OtmLibraryMemberBase<TLValueWithAttributes>
		implements OtmTypeProvider, OtmChildrenOwner, OtmTypeUser, OtmPropertyOwner {
	private static Log log = LogFactory.getLog(OtmValueWithAttributes.class);

	private StringProperty assignedTypeProperty;

	public OtmValueWithAttributes(String name, OtmModelManager mgr) {
		super(new TLValueWithAttributes(), mgr);
		setName(name);
	}

	public OtmValueWithAttributes(TLValueWithAttributes tlo, OtmModelManager mgr) {
		super(tlo, mgr);
	}

	@Override
	public OtmProperty<?> add(TLModelElement tl) {
		if (tl instanceof TLIndicator)
			getTL().addIndicator((TLIndicator) tl);
		else if (tl instanceof TLAttribute)
			getTL().addAttribute((TLAttribute) tl);
		else
			log.debug("unknown/not-implemented property type.");

		return OtmPropertyFactory.create(tl, this);
	}

	private void addChild(OtmProperty<?> child) {
		if (child != null)
			children.add(child);
	}

	@Override
	public StringProperty assignedTypeProperty() {
		if (assignedTypeProperty == null) {
			if (isEditable())
				assignedTypeProperty = new SimpleStringProperty(OtmTypeUserUtils.formatAssignedType(this));
			else
				assignedTypeProperty = new ReadOnlyStringWrapper(OtmTypeUserUtils.formatAssignedType(this));
		}
		return assignedTypeProperty;
	}

	@Override
	public TLPropertyType getAssignedTLType() {
		return getTL().getParentType();
	}

	@Override
	public OtmTypeProvider getAssignedType() {
		return OtmTypeUserUtils.getAssignedType(this);
	}

	@Override
	public Icons getIconType() {
		return ImageManager.Icons.VWA;
	}

	@Override
	public OtmValueWithAttributes getOwningMember() {
		return this;
	}

	@Override
	public TLValueWithAttributes getTL() {
		return (TLValueWithAttributes) tlObject;
	}

	@Override
	public String getTlAssignedTypeName() {
		return getTL().getParentTypeName();
	}

	@Override
	public boolean isNameControlled() {
		return false;
	}

	@Override
	public boolean isExpanded() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Creates properties for attributes and indicators in the TL object.
	 */
	@Override
	public void modelChildren() {
		getTL().getAttributes().forEach(tla -> addChild(OtmPropertyFactory.create(tla, this)));
		getTL().getIndicators().forEach(tli -> addChild(OtmPropertyFactory.create(tli, this)));
	}

	@Override
	public TLPropertyType setAssignedTLType(NamedEntity type) {
		if (type instanceof TLAttributeType)
			getTL().setParentType((TLAttributeType) type);
		assignedTypeProperty = null;
		log.debug("Set assigned TL type");
		return getAssignedTLType();
	}

	@Override
	public OtmTypeProvider setAssignedType(OtmTypeProvider type) {
		if (type != null && type.getTL() instanceof TLAttributeType)
			setAssignedTLType((TLAttributeType) type.getTL());
		return getAssignedType();
	}

	@Override
	public String setName(String name) {
		getTL().setName(name);
		isValid(true);
		return getName();
	}

	@Override
	public void setTLTypeName(String name) {
		getTL().setParentType(null);
		getTL().setParentTypeName(name);
	}

}
