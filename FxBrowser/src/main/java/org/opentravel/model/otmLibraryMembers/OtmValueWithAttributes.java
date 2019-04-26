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
import org.opentravel.model.OtmChildrenOwner;
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.OtmPropertyOwner;
import org.opentravel.model.OtmTypeProvider;
import org.opentravel.model.OtmTypeUser;
import org.opentravel.model.otmProperties.OtmProperty;
import org.opentravel.model.otmProperties.OtmPropertyFactory;
import org.opentravel.schemacompiler.model.TLAttribute;
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

	public OtmValueWithAttributes(TLValueWithAttributes tlo, OtmModelManager mgr) {
		super(tlo, mgr);
	}

	public OtmValueWithAttributes(String name, OtmModelManager mgr) {
		super(new TLValueWithAttributes(), mgr);
		setName(name);
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
	public Icons getIconType() {
		return ImageManager.Icons.VWA;
	}

	// @Override
	// public OtmLibrary getLibrary() {
	// return mgr.get(getTL().getOwningLibrary());
	// }
	//
	// @Override
	// public boolean isEditable() {
	// return getLibrary() != null && getLibrary().isEditable();
	// }

	@Override
	public boolean isNameControlled() {
		return false;
	}

	@Override
	public TLValueWithAttributes getTL() {
		return (TLValueWithAttributes) tlObject;
	}

	@Override
	public String setName(String name) {
		getTL().setName(name);
		isValid(true);
		return getName();
	}

	// @Override
	// public String getLibraryName() {
	// String libName = "";
	// if (getTL().getOwningLibrary() != null)
	// libName = getTL().getOwningLibrary().getName();
	// return libName;
	// }

	@Override
	public OtmValueWithAttributes getOwningMember() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Creates properties for children in the TL object.
	 */
	@Override
	public void modelChildren() {
		getTL().getAttributes().forEach(tla -> addChild(OtmPropertyFactory.create(tla, this)));
		getTL().getIndicators().forEach(tli -> addChild(OtmPropertyFactory.create(tli, this)));
	}

	private void addChild(OtmProperty<?> child) {
		if (child != null)
			children.add(child);
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

	@Override
	public OtmTypeProvider getAssignedType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OtmTypeProvider setAssignedType(OtmTypeProvider type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAssignedTypeName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TLPropertyType getAssignedTLType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TLPropertyType setAssignedTLType(TLPropertyType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAssignedTypeLocalName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTLTypeName(String oldTLTypeName) {
		// TODO Auto-generated method stub

	}

}
