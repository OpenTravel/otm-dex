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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.common.ImageManager.Icons;
import org.opentravel.model.OtmChildrenOwner;
import org.opentravel.model.OtmModelElement;
import org.opentravel.model.OtmObject;
import org.opentravel.model.OtmTypeProvider;
import org.opentravel.model.OtmTypeUser;
import org.opentravel.model.otmContainers.OtmLibrary;
import org.opentravel.schemacompiler.model.TLRole;
import org.opentravel.schemacompiler.model.TLRoleEnumeration;

/**
 * OTM Object core object's role enumeration.
 * 
 * @author Dave Hollander
 * 
 */
public class OtmRoleEnumeration extends OtmModelElement<TLRoleEnumeration>
		implements OtmTypeProvider, OtmChildrenOwner {
	private static Log log = LogFactory.getLog(OtmRoleEnumeration.class);

	private OtmCoreObject parent;

	public OtmRoleEnumeration(TLRoleEnumeration tlo, OtmCoreObject parent) {
		super(tlo, parent.getActionManager());
		this.parent = parent;
	}

	@Override
	public TLRoleEnumeration getTL() {
		return tlObject;
	}

	@Override
	public String setName(String name) {
		isValid(true);
		return getName();
	}

	@Override
	public OtmLibrary getLibrary() {
		return getParent().getLibrary();
	}

	public OtmCoreObject getParent() {
		return parent;
	}

	@Override
	public OtmCoreObject getOwningMember() {
		return getParent();
	}

	/**
	 * @param child
	 */
	private void add(TLRole child) {
		// if (child != null)
		// children.add(child);
	}

	@Override
	public void modelChildren() {
		for (TLRole role : getTL().getRoles())
			add(role); // TODO - model child ?? do we need to?
	}

	@Override
	public List<OtmObject> getInheritedChildren() {
		return Collections.emptyList();
	}

	@Override
	public void modelInheritedChildren() {
	}

	@Override
	public List<OtmObject> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<OtmObject> getChildrenHierarchy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<OtmTypeProvider> getChildrenTypeProviders() {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

	@Override
	public Collection<OtmChildrenOwner> getDescendantsChildrenOwners() {
		return Collections.emptyList();
	}

	@Override
	public Collection<OtmTypeProvider> getDescendantsTypeProviders() {
		return Collections.emptyList();
	}

	@Override
	public Collection<OtmTypeUser> getDescendantsTypeUsers() {
		return Collections.emptyList();
	}

	@Override
	public boolean isExpanded() {
		return false;
	}

	@Override
	public Icons getIconType() {
		// TODO Auto-generated method stub
		return ImageManager.Icons.FACET;
	}

	@Override
	public boolean isNameControlled() {
		return true;
	}

}
