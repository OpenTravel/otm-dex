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
package org.opentravel.model.otmFacets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.common.ImageManager.Icons;
import org.opentravel.dex.actions.DexActionManager;
import org.opentravel.model.OtmChildrenOwner;
import org.opentravel.model.OtmModelElement;
import org.opentravel.model.OtmObject;
import org.opentravel.model.OtmPropertyOwner;
import org.opentravel.model.OtmTypeProvider;
import org.opentravel.model.OtmTypeUser;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;
import org.opentravel.model.otmProperties.OtmProperty;
import org.opentravel.model.otmProperties.OtmPropertyFactory;
import org.opentravel.schemacompiler.model.TLAlias;
import org.opentravel.schemacompiler.model.TLAttribute;
import org.opentravel.schemacompiler.model.TLFacet;
import org.opentravel.schemacompiler.model.TLIndicator;
import org.opentravel.schemacompiler.model.TLModelElement;
import org.opentravel.schemacompiler.model.TLProperty;

/**
 * Abstract OTM Node for Facets.
 * 
 * @author Dave Hollander
 * 
 */
public abstract class OtmFacet<T extends TLFacet> extends OtmModelElement<TLFacet>
		implements OtmPropertyOwner, OtmTypeProvider {
	private static Log log = LogFactory.getLog(OtmFacet.class);

	private OtmLibraryMember parent;

	public OtmFacet(T tl, OtmLibraryMember parent) {
		super(tl, parent.getActionManager());
		this.parent = parent;
	}

	/**
	 * Create a facet for OtmOperations which are not library members
	 * 
	 * @param tl
	 * @param actionMgr
	 */
	public OtmFacet(T tl, DexActionManager actionMgr) {
		super(tl, actionMgr);
	}

	@Override
	public Collection<OtmObject> getChildrenHierarchy() {
		Collection<OtmObject> hierarchy = new ArrayList<>();
		children.forEach(c -> hierarchy.add(c));
		return hierarchy;
	}

	public DexActionManager getActionManger() {
		return parent.getActionManager();
	}

	public OtmLibraryMember getParent() {
		return parent;
	}

	@Override
	public boolean isNameControlled() {
		return true;
	}

	@Override
	public Collection<OtmTypeProvider> getChildrenTypeProviders() {
		return Collections.emptyList();
	}

	@Override
	public Collection<OtmTypeProvider> getDescendantsTypeProviders() {
		return Collections.emptyList();
	}

	@Override
	public Collection<OtmChildrenOwner> getDescendantsChildrenOwners() {
		return Collections.emptyList();
	}

	@Override
	public Collection<OtmTypeUser> getDescendantsTypeUsers() {
		Collection<OtmTypeUser> users = new ArrayList<>();
		if (getChildren() != null)
			getChildren().forEach(c -> {
				if (c instanceof OtmTypeUser)
					users.add((OtmTypeUser) c);
			});
		return users;
	}

	@Override
	public String getNamespace() {
		return tlObject.getNamespace();
	}

	@Override
	public String getName() {
		return tlObject.getLocalName();
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	public String getRole() {
		return tlObject.getFacetType().getIdentityName();
	}

	@Override
	public boolean isExpanded() {
		return true;
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<OtmObject> getChildren() {
		if (children != null && children.isEmpty())
			modelChildren();
		return children;
	}

	@Override
	public Icons getIconType() {
		return ImageManager.Icons.FACET;
	}

	@Override
	public OtmLibraryMember getOwningMember() {
		return getParent();
	}

	@Override
	public OtmProperty<?> add(TLModelElement tl) {
		if (tl instanceof TLIndicator)
			tlObject.addIndicator((TLIndicator) tl);
		else if (tl instanceof TLProperty)
			tlObject.addElement((TLProperty) tl);
		else if (tl instanceof TLAttribute)
			tlObject.addAttribute((TLAttribute) tl);
		else
			log.debug("unknown/not-implemented property type.");

		return OtmPropertyFactory.create(tl, this);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Creates properties to represent facet children.
	 */
	@Override
	public void modelChildren() {
		for (TLIndicator c : tlObject.getIndicators())
			addChild(OtmPropertyFactory.create(c, this));
		for (TLAttribute c : tlObject.getAttributes())
			addChild(OtmPropertyFactory.create(c, this));
		for (TLProperty c : tlObject.getElements())
			addChild(OtmPropertyFactory.create(c, this));
		for (TLAlias c : tlObject.getAliases())
			getOwningMember().addAlias(c);
	}

	private void addChild(OtmProperty<?> child) {
		if (child != null)
			children.add(child);
	}
}
