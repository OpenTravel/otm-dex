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
import org.opentravel.model.OtmObject;
import org.opentravel.model.OtmTypeProvider;
import org.opentravel.model.OtmTypeUser;
import org.opentravel.model.otmContainers.OtmLibrary;
import org.opentravel.model.otmFacets.OtmAlias;
import org.opentravel.model.otmFacets.OtmFacet;
import org.opentravel.model.otmFacets.OtmFacetFactory;
import org.opentravel.schemacompiler.model.LibraryMember;
import org.opentravel.schemacompiler.model.TLAlias;
import org.opentravel.schemacompiler.model.TLAliasOwner;
import org.opentravel.schemacompiler.model.TLFacet;
import org.opentravel.schemacompiler.model.TLFacetOwner;
import org.opentravel.schemacompiler.model.TLModelElement;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Abstract OTM Library Member base class.
 * <p>
 * Note: implements children owner even though not all library members are children owners, but most are.
 * 
 * @author Dave Hollander
 * 
 */
public abstract class OtmLibraryMemberBase<T extends TLModelElement> extends OtmModelElement<TLModelElement>
		implements OtmLibraryMember, OtmTypeProvider, OtmChildrenOwner {
	private static Log log = LogFactory.getLog(OtmLibraryMemberBase.class);

	protected OtmModelManager mgr = null;
	// LibraryMember lm;
	protected List<OtmTypeProvider> providers = null;
	protected List<OtmTypeUser> users = new ArrayList<>();

	/**
	 */
	public OtmLibraryMemberBase(T tl, OtmModelManager mgr) {
		super(tl, mgr.getActionManager());
		this.mgr = mgr;

		// if (mgr == null)
		// throw new IllegalArgumentException();
	}

	@Override
	public void addAlias(TLAlias tla) {
		if (tla.getOwningEntity() instanceof TLFacet) {
			String baseName = tla.getLocalName().substring(0, tla.getName().lastIndexOf('_'));
			// log.debug("Adding alias " + tla.getName() + " to " + baseName);

			children.forEach(c -> {
				if (c instanceof OtmAlias && c.getName().equals(baseName))
					((OtmAlias) c).add(tla);
			});
		}
	}

	@Override
	public Collection<OtmObject> getChildrenHierarchy() {
		Collection<OtmObject> hierarchy = new ArrayList<>();
		children.forEach(c -> hierarchy.add(c));
		return hierarchy;
	}

	@Override
	public DexActionManager getActionManager() {
		return mgr.getActionManager();
	}

	/**
	 */
	@Override
	public Collection<OtmTypeProvider> getChildrenTypeProviders() {
		if (getChildren() != null) {
			List<OtmTypeProvider> pChildren = new ArrayList<>();
			for (OtmObject child : getChildren())
				if (child instanceof OtmTypeProvider)
					pChildren.add((OtmTypeProvider) child);
			return pChildren;
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 */
	@Override
	public Collection<OtmTypeProvider> getDescendantsTypeProviders() {
		if (getChildrenTypeProviders() != null) {
			providers = new ArrayList<>();
			for (OtmTypeProvider p : getChildrenTypeProviders()) {
				providers.add(p);
				// Recurse
				if (p instanceof OtmChildrenOwner)
					providers.addAll(((OtmChildrenOwner) p).getDescendantsTypeProviders());
			}
		}
		return providers;
	}

	@Override
	public Collection<OtmChildrenOwner> getDescendantsChildrenOwners() {
		List<OtmChildrenOwner> owners = new ArrayList<>();
		for (OtmObject child : getChildren()) {
			if (child instanceof OtmChildrenOwner) {
				owners.add((OtmChildrenOwner) child);
				// Recurse
				owners.addAll(((OtmChildrenOwner) child).getDescendantsChildrenOwners());
			}
		}
		return owners;
	}

	@Override
	public Collection<OtmTypeUser> getDescendantsTypeUsers() {
		for (OtmObject child : getChildren()) {
			if (child instanceof OtmTypeUser)
				users.add((OtmTypeUser) child);
		}
		// Recurse
		getDescendantsChildrenOwners().forEach(d -> users.addAll(d.getDescendantsTypeUsers()));
		return users;
	}

	/**
	 */
	@Override
	public List<OtmObject> getChildren() {
		if (children != null && children.isEmpty())
			modelChildren();
		return children;
	}

	@Override
	public boolean isEditable() {
		return getLibrary() != null && getLibrary().isEditable();
	}

	@Override
	public boolean isValid(boolean force) {
		if (force)
			getDescendantsChildrenOwners().forEach(c -> c.isValid(force));
		return super.isValid(force);
	}

	@Override
	public OtmLibrary getLibrary() {
		return mgr.get(getTlLM().getOwningLibrary());
	}

	@Override
	public String getLibraryName() {
		return getTlLM().getOwningLibrary() != null ? getTlLM().getOwningLibrary().getName() : "";
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
	public String getObjectTypeName() {
		return OtmLibraryMemberFactory.getObjectName(this);
	}

	@Override
	public String getPrefix() {
		return getTlLM().getOwningLibrary() != null ? getTlLM().getOwningLibrary().getPrefix() : "";
	}

	// TODO - do i need a clearProviders() ???
	@Override
	public List<OtmTypeProvider> getUsedTypes() {
		if (providers == null) {
			getDescendantsTypeUsers().forEach(d -> addProvider(d));
		}
		return providers;
	}

	private void addProvider(OtmTypeUser user) {
		if (providers == null)
			providers = new ArrayList<>();
		OtmTypeProvider p = user.getAssignedType();
		// if (user.getAssignedType()) == null) {
		// // TODO - if provider is null, check the TL
		// }
		if (p != null && !providers.contains(p))
			providers.add(p);
		// TODO - set listener
	}

	@Override
	public LibraryMember getTlLM() {
		return (LibraryMember) getTL();
	}

	/**
	 * Creates facets to represent facets in the TL object.
	 */
	@Override
	public void modelChildren() {
		// Must do aliases first so facet aliases will have a parent
		// Aliases from contextual facets come from the member where injected (contributed)
		if (!(this instanceof OtmContextualFacet) && getTL() instanceof TLAliasOwner)
			((TLAliasOwner) getTL()).getAliases().forEach(t -> children.add(new OtmAlias(t, this)));

		if (getTL() instanceof TLFacetOwner)
			for (TLFacet tlFacet : ((TLFacetOwner) getTL()).getAllFacets()) {
				OtmFacet<?> facet = OtmFacetFactory.create(tlFacet, this);
				if (facet != null) {
					children.add(facet);
				}
			}
	}

}
