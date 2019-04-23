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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.common.ImageManager.Icons;
import org.opentravel.model.OtmChildrenOwner;
import org.opentravel.model.OtmModelElement;
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.OtmTypeProvider;
import org.opentravel.model.otmContainers.OtmLibrary;
import org.opentravel.model.otmFacets.OtmContributedFacet;
import org.opentravel.schemacompiler.model.TLContextualFacet;
import org.opentravel.schemacompiler.model.TLModelElement;

/**
 * Abstract OTM Node for Custom Facets library members.
 * 
 * @author Dave Hollander
 * 
 */
public abstract class OtmContextualFacet extends OtmLibraryMemberBase<TLContextualFacet>
		implements OtmLibraryMember, OtmTypeProvider {
	private static Log log = LogFactory.getLog(OtmContextualFacet.class);

	// The contributed facet that is child of a library member.
	private OtmContributedFacet whereContributed = null;

	public OtmContextualFacet(TLContextualFacet tl, OtmModelManager manager) {
		super(tl, manager);
		// lazy evaluated modelChildren();
	}

	public OtmContributedFacet getWhereContributed() {
		if (whereContributed == null) {
			OtmModelElement<?> o = OtmModelElement.get((TLModelElement) getTL().getOwningEntity());
			if (o instanceof OtmContributedFacet)
				o = ((OtmContributedFacet) o).getContributor();
			if (o instanceof OtmChildrenOwner)
				for (OtmModelElement<?> c : ((OtmChildrenOwner) o).getChildren())
					if (c instanceof OtmContributedFacet && c.getName().equals(this.getName()))
						whereContributed = (OtmContributedFacet) c;
		}
		return whereContributed;
	}

	@Override
	public List<OtmModelElement<?>> getChildren() {
		children.clear();
		// if (children != null && children.isEmpty())
		modelChildren();
		// if (getWhereContributed() != null)
		// children.addAll(getWhereContributed().getChildren());
		// FIXME - what about children that are other contextual facets?
		//
		// Properties that are children will be on the contributed facet since that is a facet
		return children;
	}

	@Override
	public void modelChildren() {
		super.modelChildren();
		if (getWhereContributed() != null)
			children.addAll(getWhereContributed().getChildren());
	}

	@Override
	public TLContextualFacet getTL() {
		return (TLContextualFacet) tlObject;
	}

	@Override
	public OtmLibrary getLibrary() {
		return mgr.get(getTL().getOwningLibrary());
	}

	@Override
	public String getNamespace() {
		return getTL().getNamespace();
	}

	@Override
	public String getName() {
		return getTL().getLocalName();
		// return this.getClass().getSimpleName();
	}

	@Override
	public boolean isNameControlled() {
		return true;
	}

	@Override
	public String getLibraryName() {
		String libName = "";
		if (getTL().getOwningLibrary() != null)
			libName = getTL().getOwningLibrary().getName();
		return libName;
	}

	@Override
	public Icons getIconType() {
		return ImageManager.Icons.FACET_CONTEXTUAL;
	}

	@Override
	public boolean isEditable() {
		OtmLibrary ol = null;
		if (mgr != null || getTL() != null)
			ol = mgr.get(getTL().getOwningLibrary());
		return ol != null && ol.isEditable();
	}

	@Override
	public OtmContextualFacet getOwningMember() {
		return this;
	}

}
