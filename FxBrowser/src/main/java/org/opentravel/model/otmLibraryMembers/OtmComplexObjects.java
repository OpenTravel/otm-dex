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
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.OtmTypeProvider;
import org.opentravel.schemacompiler.model.TLComplexTypeBase;
import org.opentravel.schemacompiler.model.TLLibraryMember;

/**
 * Abstract OTM Object Node for Library Members.
 * 
 * @author Dave Hollander
 * 
 */
public abstract class OtmComplexObjects<T extends TLComplexTypeBase> extends OtmLibraryMemberBase<TLLibraryMember>
		implements OtmLibraryMember, OtmTypeProvider, OtmChildrenOwner {
	private static Log log = LogFactory.getLog(OtmComplexObjects.class);

	// private OtmModelManager mgr = null;
	// LibraryMember lm;

	/**
	 */
	public OtmComplexObjects(T tl, OtmModelManager mgr) {
		super(tl, mgr);
	}

	// /**
	// * @return immediate children who implement OtmTypeProvider or empty list.
	// */
	// @Override
	// public Collection<OtmTypeProvider> getChildren_TypeProviders() {
	// List<OtmTypeProvider> providers = new ArrayList<>();
	// for (OtmModelElement<?> child : getChildren())
	// if (child instanceof OtmTypeProvider)
	// providers.add((OtmTypeProvider) child);
	// return providers;
	// }

	// /**
	// * {@inheritDoc}
	// */
	// @Override
	// public List<OtmModelElement<?>> getChildren() {
	// if (children != null && children.isEmpty())
	// modelChildren();
	// return children;
	// }

	// @Override
	// public TLLibraryMember getTL() {
	// return (TLLibraryMember) tlObject;
	// }
	@Override
	public T getTL() {
		return (T) tlObject;
	}

	// @Override
	// public OtmLibrary getLibrary() {
	// return mgr.get(getTL().getOwningLibrary());
	// }

	// @Override
	// public String getNamespace() {
	// return getTL().getNamespace();
	// }

	@Override
	public String getName() {
		return getTL().getLocalName();
		// return this.getClass().getSimpleName();
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
	};

	// /**
	// * {@inheritDoc}
	// */
	// @Override
	// public abstract String setName(String text);
	// TODO - update children

	// /**
	// * {@inheritDoc}
	// * <p>
	// * Add properties to the facets
	// *
	// * @return this object
	// */
	// @Override
	// @Deprecated
	// public OtmLibraryMember<?> createTestChildren() {
	// for (OtmModelElement<?> child : getChildren())
	// if (child instanceof OtmFacet)
	// ((OtmFacet<?>) child).createTestChildren();
	// return this;
	// }

}
