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
import org.opentravel.model.otmFacets.OtmContributedFacet;
import org.opentravel.model.otmFacets.OtmFacet;
import org.opentravel.model.otmFacets.OtmFacetFactory;
import org.opentravel.schemacompiler.codegen.util.FacetCodegenUtils;
import org.opentravel.schemacompiler.model.AbstractLibrary;
import org.opentravel.schemacompiler.model.LibraryMember;
import org.opentravel.schemacompiler.model.TLAlias;
import org.opentravel.schemacompiler.model.TLAliasOwner;
import org.opentravel.schemacompiler.model.TLContextualFacet;
import org.opentravel.schemacompiler.model.TLFacet;
import org.opentravel.schemacompiler.model.TLFacetOwner;
import org.opentravel.schemacompiler.model.TLFacetType;
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

	// A list of all descendants that are type providers. Created by getDescendantsTypeProviders
	protected List<OtmTypeProvider> membersProviders = null;

	// A list of all descendants that are type users. Created by getDescendantsTypeUsers.
	protected List<OtmTypeUser> memberTypeUsers = new ArrayList<>();

	// A list of all members that have a descendant type user that assigned to this member and its descendants.
	protected List<OtmLibraryMember> whereUsed = null;

	/**
	 */
	public OtmLibraryMemberBase(T tl, OtmModelManager mgr) {
		super(tl, mgr.getActionManager());
		this.mgr = mgr;
	}

	@Override
	public void addAlias(TLAlias tla) {
		if (tla.getOwningEntity() instanceof TLFacet) {
			String baseName = tla.getLocalName().substring(0, tla.getName().lastIndexOf('_'));

			children.forEach(c -> {
				if (c instanceof OtmAlias && c.getName().equals(baseName))
					((OtmAlias) c).add(tla);
			});
		}
	}

	@Override
	public Collection<OtmObject> getChildrenHierarchy() {
		Collection<OtmObject> hierarchy = new ArrayList<>();
		getInheritedChildren().forEach(hierarchy::add);
		getChildren().forEach(hierarchy::add);
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
		if (membersProviders == null)
			if (getChildrenTypeProviders() != null) {
				membersProviders = new ArrayList<>();
				for (OtmTypeProvider p : getChildrenTypeProviders()) {
					membersProviders.add(p);
					// Recurse
					if (p instanceof OtmChildrenOwner)
						membersProviders.addAll(((OtmChildrenOwner) p).getDescendantsTypeProviders());
				}
			}
		return membersProviders;
	}

	@Override
	public Collection<OtmChildrenOwner> getDescendantsChildrenOwners() {
		List<OtmObject> children = new ArrayList<>(getChildren());
		List<OtmChildrenOwner> owners = new ArrayList<>();
		for (OtmObject child : children) {
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
		memberTypeUsers.clear();
		for (OtmObject child : getChildren()) {
			if (child instanceof OtmTypeUser)
				memberTypeUsers.add((OtmTypeUser) child);
		}
		// Recurse
		for (OtmChildrenOwner co : getDescendantsChildrenOwners()) {
			Collection<OtmTypeUser> u = co.getDescendantsTypeUsers();
			memberTypeUsers.addAll(u);
		}
		// getDescendantsChildrenOwners().forEach(d -> users.addAll(d.getDescendantsTypeUsers()));
		// log.debug("Users now has " + memberTypeUsers.size() + " items");
		return memberTypeUsers;
	}

	@Override
	public OtmObject getBaseType() {
		return null;
	}

	@Override
	public boolean contains(OtmObject o) {
		return children.contains(o);
	}

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
		AbstractLibrary absLib = getTlLM().getOwningLibrary();
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
	public StringProperty baseTypeProperty() {
		return new ReadOnlyStringWrapper("");
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
		List<OtmTypeProvider> typesUsed = new ArrayList<>();
		// Prevent concurrent modification
		Collection<OtmTypeUser> descendants = new ArrayList<>(getDescendantsTypeUsers());
		descendants.forEach(d -> addProvider(d, typesUsed));
		// log.debug(this + " typesUsed size = " + typesUsed.size());
		typesUsed.sort(
				(OtmObject o1, OtmObject o2) -> o1.getNameWithPrefix().compareToIgnoreCase(o2.getNameWithPrefix()));
		return typesUsed;
	}

	@Override
	public List<OtmLibraryMember> getWhereUsed() {
		return getWhereUsed(false);
	}

	public List<OtmLibraryMember> getWhereUsed(boolean force) {
		if (force)
			whereUsed = null;
		if (whereUsed == null) {
			whereUsed = new ArrayList<>();
			whereUsed.addAll(mgr.findUsersOf(this));
			getDescendantsTypeProviders().forEach(p -> {
				whereUsed.addAll(mgr.findUsersOf(p));
			});
			// log.debug("Creating Where Used List " + whereUsed.size() + " for : " + this.getNameWithPrefix());
		}
		// FIXME - clear list when changing assigned type
		return whereUsed;
	}

	private void addProvider(OtmTypeUser user, List<OtmTypeProvider> list) {
		if (user == null)
			return;
		OtmTypeProvider p = user.getAssignedType();
		if (p != null && !list.contains(p))
			list.add(p);
	}

	@Override
	public LibraryMember getTlLM() {
		return (LibraryMember) getTL();
	}

	/**
	 * {@inheritDoc} Creates facets to represent facets in the TL object.
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

	@Override
	public List<OtmObject> getInheritedChildren() {
		modelInheritedChildren();
		return inheritedChildren;
	}

	@Override
	public void modelInheritedChildren() {
		if (inheritedChildren == null)
			inheritedChildren = new ArrayList<>();
		else
			inheritedChildren.clear(); // force re-compute

		OtmObject baseType = getBaseType();
		if (getTL() instanceof TLFacetOwner && baseType != null) {

			TLFacetOwner extendedOwner = (TLFacetOwner) getTL();
			List<TLContextualFacet> ghosts = FacetCodegenUtils.findGhostFacets(extendedOwner, TLFacetType.CUSTOM);
			ghosts.addAll(FacetCodegenUtils.findGhostFacets(extendedOwner, TLFacetType.QUERY));
			ghosts.addAll(FacetCodegenUtils.findGhostFacets(extendedOwner, TLFacetType.CHOICE));
			ghosts.addAll(FacetCodegenUtils.findGhostFacets(extendedOwner, TLFacetType.UPDATE));
			// Ghosts do NOT have any children! See OtmFacet.modelInheritedChildren()

			// Create a contributed facet for each ghost
			ghosts.forEach(g -> inheritedChildren.add(OtmFacetFactory.create(g, this)));

			// Replace contributor in each contributed facet with one from the base
			inheritedChildren.forEach(i -> setContributor(i, baseType));

			// if (ghosts.size() > 0)
			// log.debug("Found and modeled " + ghosts.size() + " ghost facets on " + this.getName());
		}
	}

	private void setContributor(OtmObject i, OtmObject baseType) {
		OtmContributedFacet contributed = null;
		OtmLibraryMember base = null;
		if (i instanceof OtmContributedFacet)
			contributed = (OtmContributedFacet) i;
		if (baseType instanceof OtmLibraryMember)
			base = (OtmLibraryMember) baseType;

		// Find a contextual facet with the same name and use it as the contributor
		if (contributed != null && base != null)
			for (OtmObject child : base.getChildren())
				// TL names do not include owner
				if (child instanceof OtmContributedFacet
						&& ((TLContextualFacet) child.getTL()).getName().equals(contributed.getTL().getName())) {
					contributed.setContributor(((OtmContributedFacet) child).getContributor());
					assert contributed.isInherited();
					break;
				}
	}
}
