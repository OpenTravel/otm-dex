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
import org.opentravel.model.OtmChildrenOwner;
import org.opentravel.model.OtmModelElement;
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.OtmObject;
import org.opentravel.model.OtmTypeProvider;
import org.opentravel.schemacompiler.model.TLComplexTypeBase;
import org.opentravel.schemacompiler.model.TLLibraryMember;
import org.opentravel.schemacompiler.model.TLModelElement;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.StringProperty;

/**
 * Abstract OTM Object for Complex Library Members.
 * 
 * @author Dave Hollander
 * 
 */
public abstract class OtmComplexObjects<T extends TLComplexTypeBase> extends OtmLibraryMemberBase<TLLibraryMember>
		implements OtmLibraryMember, OtmTypeProvider, OtmChildrenOwner {
	private static Log log = LogFactory.getLog(OtmComplexObjects.class);

	/**
	 */
	public OtmComplexObjects(T tl, OtmModelManager mgr) {
		super(tl, mgr);
	}

	@Override
	public T getTL() {
		return (T) tlObject;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Use the extension class on the complex object to find the extends entity.
	 */
	@Override
	public OtmObject getBaseType() {
		if (getTL().getExtension() != null)
			return OtmModelElement.get((TLModelElement) getTL().getExtension().getExtendsEntity());
		return null;
	}

	@Override
	public String getName() {
		return getTL().getLocalName();
	}

	@Override
	public StringProperty baseTypeProperty() {
		if (getTL().getExtension() != null) {
			// log.debug("Extension found on " + this);
			return new ReadOnlyStringWrapper(getTL().getExtension().getExtendsEntityName());
		}
		return super.baseTypeProperty();
	}

	/**
	 * @return this
	 */
	@Override
	public OtmComplexObjects<T> getOwningMember() {
		return this;
	}

	@Override
	public boolean isNameControlled() {
		return true;
	}

	@Override
	public boolean isExpanded() {
		return true;
	}

}
