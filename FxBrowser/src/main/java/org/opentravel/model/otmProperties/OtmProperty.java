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
import org.opentravel.dex.controllers.member.properties.MemberPropertiesTreeTableController;
import org.opentravel.model.OtmModelElement;
import org.opentravel.model.OtmPropertyOwner;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;
import org.opentravel.schemacompiler.model.TLComplexTypeBase;
import org.opentravel.schemacompiler.model.TLModelElement;

/**
 * Abstract OTM Node for properties.
 * 
 * @author Dave Hollander
 * 
 */
public abstract class OtmProperty<TL extends TLModelElement> extends OtmModelElement<TLModelElement> {
	private static Log log = LogFactory.getLog(MemberPropertiesTreeTableController.class);

	private OtmPropertyOwner parent;

	/**
	 * @param tl
	 *            property owner
	 */
	public OtmProperty(TL tl, OtmPropertyOwner parent) {
		super(tl, parent.getActionManager());
		this.parent = parent;
	}

	// Needs to be abstract because getTL() is of type TLModelElement
	@Override
	public abstract TLModelElement getTL();

	// /**
	// * Property Factory
	// *
	// * @param tl
	// * @return OtmFacet<?> based on type or null.
	// */
	// public static OtmProperty<?> propertyFactory(TLModelElement tl) {
	// return null;
	// }

	@Override
	public OtmLibraryMember<?> getOwningMember() {
		return parent.getOwningMember();
	}

	@Override
	public String getNamespace() {
		return getOwningMember().getNamespace();
	}

	// @Override
	// public StringProperty nameProperty() {
	// StringProperty nameProperty;
	// if (isEditable()) {
	// nameProperty = new SimpleStringProperty(getName());
	// // TODO - move to action handler
	// // Add a change listener with lambda expression
	// nameProperty.addListener((ObservableValue<? extends String> ov, String old,
	// String newVal) -> new NameChangeAction(this).doIt(newVal));
	//
	// // nameProperty.addListener((ObservableValue<? extends String> ov, String oldVal, String newVal) -> {
	// // setName(newVal);
	// // });
	// } else {
	// nameProperty = new ReadOnlyStringWrapper("" + getName());
	// }
	// return nameProperty;
	// }

	@Override
	public abstract String getName();

	// public interface ActionHandler<T> {
	// public T doIt(T value);
	//
	// public T undo();
	// }
	//
	// public class NameChangeAction implements ActionHandler<String> {
	// private OtmModelElement<?> otm;
	// private boolean outcome = false;
	//
	// public NameChangeAction(OtmModelElement<?> otm) {
	// this.otm = otm;
	// }
	//
	// @Override
	// public String doIt(String name) {
	// // TODO - try using the TL model as test -- successful if changed.
	// // It allows change.
	// // IF so, consider using the test when creating fx property
	// // if (otm.isEditable() && isUserAssigned())
	// otm.setName(name);
	// // TODO
	// if (name.equals(otm.getName()))
	// outcome = true;
	// log.debug("Set name to " + name + " success: " + outcome);
	// return otm.getName();
	// }
	//
	// @Override
	// public String undo() {
	// // TODO
	// return getName();
	// }
	// }

	public boolean isUserAssigned() {
		if (getTL() instanceof TLComplexTypeBase)
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract String setName(String name);

	@Override
	public boolean isEditable() {
		return getOwningMember() != null && getOwningMember().isEditable();
	}

	@Override
	public String toString() {
		return getName();
	}

	public OtmPropertyOwner getParent() {
		return parent;
	}

	/**
	 * @return
	 */
	public abstract boolean isManditory();

	/**
	 * @param value
	 */
	public abstract void setManditory(boolean value);
}
