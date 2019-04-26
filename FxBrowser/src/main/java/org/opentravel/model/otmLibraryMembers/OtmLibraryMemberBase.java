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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.actions.DexActionManager;
import org.opentravel.model.OtmChildrenOwner;
import org.opentravel.model.OtmModelElement;
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.OtmTypeProvider;
import org.opentravel.model.otmContainers.OtmLibrary;
import org.opentravel.model.otmFacets.OtmFacet;
import org.opentravel.model.otmFacets.OtmFacetFactory;
import org.opentravel.schemacompiler.model.LibraryMember;
import org.opentravel.schemacompiler.model.TLFacet;
import org.opentravel.schemacompiler.model.TLFacetOwner;
import org.opentravel.schemacompiler.model.TLModelElement;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Abstract OTM Library Member base class.
 * 
 * @author Dave Hollander
 * 
 */
public abstract class OtmLibraryMemberBase<T extends TLModelElement> extends OtmModelElement<TLModelElement>
		implements OtmLibraryMember, OtmTypeProvider {
	private static Log log = LogFactory.getLog(OtmLibraryMemberBase.class);

	protected OtmModelManager mgr = null;
	LibraryMember lm;

	/**
	 */
	public OtmLibraryMemberBase(T tl, OtmModelManager mgr) {
		super(tl, mgr.getActionManager());
		this.mgr = mgr;

		// if (mgr == null)
		// throw new IllegalArgumentException();
		// assert mgr != null;
	}

	// Here for convince - part of OtmChildOwner interface
	public Collection<OtmModelElement<TLModelElement>> getChildrenHierarchy() {
		Collection<OtmModelElement<TLModelElement>> hierarchy = new ArrayList<>();
		children.forEach(c -> hierarchy.add((OtmModelElement<TLModelElement>) c));
		return hierarchy;
	}

	@Override
	public DexActionManager getActionManager() {
		return mgr.getActionManager();
	}

	/**
	 * @return immediate children who implement OtmTypeProvider or empty list.
	 */
	// Implemented here since most (not all) library members are children owners
	public Collection<OtmTypeProvider> getChildren_TypeProviders() {
		if (this instanceof OtmChildrenOwner) {
			List<OtmTypeProvider> providers = new ArrayList<>();
			for (OtmModelElement<?> child : getChildren())
				if (child instanceof OtmTypeProvider)
					providers.add((OtmTypeProvider) child);
			return providers;
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 */
	// Implemented here since most (not all) library members are children owners
	public List<OtmModelElement<?>> getChildren() {
		if (this instanceof OtmChildrenOwner) {
			if (children != null && children.isEmpty())
				modelChildren();
			return children;
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public boolean isEditable() {
		return getLibrary() != null && getLibrary().isEditable();
	}

	@Override
	public OtmLibrary getLibrary() {
		return mgr.get(getTlLM().getOwningLibrary());
	}

	@Override
	public String getLibraryName() {
		// String libName = "";
		return getTlLM().getOwningLibrary() != null ? getTlLM().getOwningLibrary().getName() : "";
		// return libName;
	}

	@Override
	public StringProperty libraryProperty() {
		if (isEditable())
			return new SimpleStringProperty(getLibraryName());
		else
			return new ReadOnlyStringWrapper(getLibraryName());
	}

	@Override
	public StringProperty prefixProperty() {
		return new ReadOnlyStringWrapper(getPrefix());
	}

	@Override
	public StringProperty versionProperty() {
		return getLibrary() != null ? new SimpleStringProperty(getLibrary().getVersion())
				: new ReadOnlyStringWrapper("");
	}

	@Override
	public String getNamespace() {
		return getTlLM().getNamespace();
	}

	@Override
	public String getPrefix() {
		return getTlLM().getOwningLibrary() != null ? getTlLM().getOwningLibrary().getPrefix() : "";
	}

	@Override
	public LibraryMember getTlLM() {
		return (LibraryMember) getTL();
	}

	// @Override
	// public String getNamespace() {
	// return getTL().getNamespace();
	// }
	//
	// @Override
	// public String getName() {
	// return tlObject.getLocalName();
	// // return this.getClass().getSimpleName();
	// }

	// @Override
	// public abstract String getLibraryName();
	// public String getLibraryName() {
	// String libName = "";
	// if (getTL().getOwningLibrary() != null)
	// libName = getTL().getOwningLibrary().getName();
	// return libName;
	// }

	// @Override
	// public boolean isEditable() {
	// OtmLibrary ol = null;
	// if (mgr != null || getTL() != null)
	// ol = mgr.get(getTL().getOwningLibrary());
	// return ol != null && ol.isEditable();
	// // return tlObject.getOwningLibrary() != null;
	// }

	// @Override
	// public String toString() {
	// return getName();
	// }

	// @Override
	// public String getPrefix() {
	// return getTL().getOwningLibrary() != null ? getTL().getOwningLibrary().getPrefix() : "";
	// }

	/**
	 * Creates facets to represent facets in the TL object.
	 */
	// Implemented here since most (not all) library members are children owners
	public void modelChildren() {
		if (getTL() instanceof TLFacetOwner)
			for (TLFacet tlFacet : ((TLFacetOwner) getTL()).getAllFacets()) {
				OtmFacet<?> facet = OtmFacetFactory.create(tlFacet, this);
				if (facet != null) {
					children.add(facet);
				}
			}
	}

	// /**
	// * {@inheritDoc}
	// */
	// @Override
	// public abstract String setName(String text);
	// TODO - update children

}
