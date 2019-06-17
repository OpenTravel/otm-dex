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
package org.opentravel.model.otmContainers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.common.ImageManager.Icons;
import org.opentravel.model.OtmModelManager;
import org.opentravel.ns.ota2.repositoryinfo_v01_00.RepositoryPermission;
import org.opentravel.schemacompiler.model.AbstractLibrary;
import org.opentravel.schemacompiler.model.TLInclude;
import org.opentravel.schemacompiler.model.TLLibrary;
import org.opentravel.schemacompiler.model.TLLibraryStatus;
import org.opentravel.schemacompiler.repository.Project;
import org.opentravel.schemacompiler.repository.ProjectItem;
import org.opentravel.schemacompiler.repository.RepositoryItemState;
import org.opentravel.schemacompiler.validate.ValidationFindings;
import org.opentravel.schemacompiler.validate.compile.TLModelCompileValidator;

/**
 * OTM Object for libraries.
 * 
 * @author Dave Hollander
 * 
 */
public class OtmLibrary {
	private static Log log = LogFactory.getLog(OtmLibrary.class);

	protected OtmModelManager mgr;
	protected List<ProjectItem> projectItems = new ArrayList<>();
	protected AbstractLibrary tlLib;

	protected ValidationFindings findings;

	public OtmLibrary(ProjectItem pi, OtmModelManager mgr) {
		this.mgr = mgr;
		projectItems.add(pi);
		tlLib = pi.getContent();
	}

	// @Deprecated
	// public OtmLibrary(TLLibrary lib, OtmModelManager mgr) {
	// tlLib = lib;
	// this.mgr = mgr;
	// }

	protected OtmLibrary(OtmModelManager mgr) {
		this.mgr = mgr;
	}

	/**
	 * Add the project item to the list maintained by the library. Libraries can be members of multiple, open projects.
	 * 
	 * @param pi
	 */
	public void add(ProjectItem pi) {
		if (pi.getContent() == null
				|| (!(pi.getNamespace().equals(getTL().getNamespace()) && pi.getContent().getName().equals(getName()))))

			// if (pi.getContent() != tlLib)
			throw new IllegalArgumentException("Can not add project item with wrong library.");
		projectItems.add(pi);
		log.debug("Added project item to " + this.getName() + ". Now has " + projectItems.size() + " items.");
	}

	public boolean contains(AbstractLibrary aLib) {
		if (tlLib == aLib)
			return true;
		for (ProjectItem pi : projectItems)
			if (pi.getContent() == aLib)
				return true;
		return false;
	}

	public AbstractLibrary getTL() {
		return tlLib;
	}

	public String getFullName() {
		return getTL() != null ? getTL().getNamespace() + "/" + getTL().getName() : null;
	}

	public OtmModelManager getModelManager() {
		return mgr;
	}

	public OtmProject getManagingProject() {
		return mgr.getManagingProject(this);
	}

	public String getName() {
		return getTL() != null ? getTL().getName() : "";
	}

	public String getPrefix() {
		return getTL().getPrefix();
	}

	public Icons getIconType() {
		return ImageManager.Icons.LIBRARY;
	}

	/**
	 * A library is editable if any associated project item state is Managed_WIP -OR- unmanaged.
	 * 
	 * @return
	 */
	public boolean isEditable() {
		return getState() == RepositoryItemState.MANAGED_WIP || getState() == RepositoryItemState.UNMANAGED;
	}

	/**
	 * @return actual status of TL Libraries otherwise DRAFT
	 */
	public TLLibraryStatus getStatus() {
		if (tlLib instanceof TLLibrary)
			return ((TLLibrary) tlLib).getStatus();
		else
			return TLLibraryStatus.FINAL;
	}

	public List<OtmLibrary> getIncludes() {
		List<OtmLibrary> libs = new ArrayList<>();
		for (TLInclude include : tlLib.getIncludes()) {
			if (include.getOwningLibrary() != null)
				libs.add(mgr.get(include.getOwningLibrary()));
		}
		return libs;
	}

	public String getStateName() {
		return projectItems.isEmpty() ? "" : getState().toString();
	}

	/**
	 * Examine all project items and return the state that grants the user the most rights.
	 * 
	 * @return
	 */
	public RepositoryItemState getState() {
		RepositoryItemState state = RepositoryItemState.MANAGED_UNLOCKED; // the weakest state
		if (projectItems != null)
			for (ProjectItem pi : projectItems) {
				// log.debug("state = " + pi.getState());
				switch (pi.getState()) {
				case MANAGED_UNLOCKED:
					break;
				case BUILT_IN:
				case UNMANAGED:
					// These are true regardless of user or user actions
					return pi.getState();

				case MANAGED_LOCKED:
					if (state != RepositoryItemState.MANAGED_WIP)
						state = pi.getState();
					break;

				case MANAGED_WIP:
					// This gives user most rights and is therefore always used as state
					return pi.getState();
				}
			}
		return state;
	}

	public String getNameWithBasenamespace() {
		return getBaseNamespace() + "/" + getName();
	}

	public String getLockedBy() {
		for (ProjectItem pi : projectItems)
			if (pi.getLockedByUser() != null)
				return pi.getLockedByUser();
		return "";
	}

	public String getBaseNamespace() {
		return projectItems.isEmpty() ? "" : projectItems.get(0).getBaseNamespace();
	}

	public boolean isLatestVersion() {
		return mgr.isLatest(this);
	}

	/**
	 * Get the name(s) of the project(s) that contain this library.
	 * 
	 * @return new array of string containing the project names
	 */
	public List<String> getProjectNames() {
		// if (projectItems.size() > 1)
		// log.debug("Library has multiple Project Items.");
		//
		List<String> names = new ArrayList<>();
		if (projectItems != null)
			for (ProjectItem pi : projectItems) {
				for (Project p : pi.memberOfProjects())
					if (!names.contains(p.getName()))
						names.add(p.getName());
			}
		names.sort(null);
		return names;
	}

	public void validate() {
		findings = TLModelCompileValidator.validateModelElement(getTL(), true);
	}
	// extends FacetOwners
	// implements ExtensionOwner, AliasOwner, Sortable, ContextualFacetOwnerInterface, VersionedObjectInterface {

	/**
	 * @return
	 */
	public String getVersion() {
		return getTL().getVersion();
	}

	/**
	 * @return
	 */
	public boolean canBeLocked() {
		if (getStatus() == TLLibraryStatus.DRAFT && getState() == RepositoryItemState.MANAGED_UNLOCKED
				&& getManagingProject() != null && getManagingProject().getPermission() != null)
			return getManagingProject().getPermission().equals(RepositoryPermission.WRITE);
		return false;
	}

	public boolean canBeUnlocked() {
		// TODO - check to see if this is the user that locked it
		return getState() == RepositoryItemState.MANAGED_LOCKED || getState() == RepositoryItemState.MANAGED_WIP;
	}

	// private static final Logger LOGGER = LoggerFactory.getLogger(BusinessObjectNode.class);
	// private ExtensionHandler extensionHandler = null;
	// private LibraryNode owningLibrary = null;
	//
	// public BusinessObjectNode(TLBusinessObject mbr) {
	// super(mbr);
	//
	// childrenHandler = new BusinessObjectChildrenHandler(this);
	// extensionHandler = new ExtensionHandler(this);
	// }
	//
	// /**
	// * Create a new business object using the core as a template and add to the same library as the core object.
	// *
	// * @param core
	// */
	// public BusinessObjectNode(CoreObjectNode core) {
	// this(new TLBusinessObject());
	// if (core == null)
	// return;
	// cloneAliases(core.getAliases());
	//
	// setName(core.getName());
	// core.getLibrary().addMember(this); // version managed library safe add
	// setDocumentation(core.getDocumentation());
	//
	// if (core.isDeleted())
	// return;
	// getFacet_Summary().copy(core.getFacet_Summary());
	// getFacet_Detail().copy(core.getFacet_Detail());
	//
	// // Assure business object has one and only one ID and it is in the ID facet.
	// fixIDs();
	// }
	//
	// public BusinessObjectNode(VWA_Node vwa) {
	// this(new TLBusinessObject());
	// if (vwa == null)
	// return;
	//
	// setName(vwa.getName());
	// vwa.getLibrary().addMember(this);
	// setDocumentation(vwa.getDocumentation());
	// if (vwa.isDeleted())
	// return;
	//
	// getFacet_Summary().copy(vwa.getFacet_Attributes());
	//
	// // Assure business object has one and only one ID and it is in the ID facet.
	// fixIDs();
	//
	// }
	//
	// @Override
	// public LibraryNode getLibrary() {
	// return owningLibrary;
	// }
	//
	// @Override
	// public void setLibrary(LibraryNode library) {
	// owningLibrary = library;
	// }
	//
	// @Override
	// public String getName() {
	// return emptyIfNull(getTLModelObject().getName());
	// }
	//
	// @Override
	// public TLBusinessObject getTLModelObject() {
	// return (TLBusinessObject) tlObj;
	// }
	//
	// @Override
	// public boolean isExtensibleObject() {
	// return true;
	// }
	//
	// @Override
	// public Node setExtensible(boolean extensible) {
	// if (isEditable_newToChain())
	// if (getTLModelObject() instanceof TLComplexTypeBase)
	// ((TLComplexTypeBase) getTLModelObject()).setNotExtendable(!extensible);
	// return this;
	// }
	//
	// @Override
	// public boolean hasChildren_TypeProviders() {
	// return true;
	// }
	//
	// @Override
	// public boolean isAssignedByReference() {
	// return true;
	// }
	//
	// @Override
	// public ComponentNodeType getComponentNodeType() {
	// return ComponentNodeType.BUSINESS;
	// }
	//
	// @Override
	// public ContributedFacetNode getContributedFacet(TLContextualFacet tlcf) {
	// ContributedFacetNode cfn = null;
	// for (TLModelElement tlo : getChildrenHandler().getChildren_TL())
	// if (tlo == tlcf)
	// if (Node.GetNode(tlo) instanceof ContextualFacetNode) {
	// ContextualFacetNode cxn = (ContextualFacetNode) Node.GetNode(tlo);
	// if (cxn != null) {
	// cfn = cxn.getWhereContributed();
	// break;
	// }
	// }
	// return cfn;
	// }
	//
	// @Override
	// public FacetProviderNode getFacet_Default() {
	// return getFacet_Summary();
	// }
	//
	// @Override
	// public Image getImage() {
	// return Images.getImageRegistry().get(Images.BusinessObject);
	// }
	//
	// @Override
	// public BaseNodeListener getNewListener() {
	// return new TypeProviderListener(this);
	// }
	//
	// @Override
	// public void remove(AliasNode alias) {
	// getTLModelObject().removeAlias(alias.getTLModelObject());
	// clearAllAliasHolders();
	// }
	//
	// @Override
	// public void addAlias(AliasNode alias) {
	// if (!getTLModelObject().getAliases().contains(alias.getTLModelObject()))
	// getTLModelObject().addAlias(alias.getTLModelObject());
	// clearAllAliasHolders();
	// }
	//
	// @Override
	// public AliasNode addAlias(String name) {
	// AliasNode alias = null;
	// if (this.isEditable_newToChain())
	// alias = new AliasNode(this, NodeNameUtils.fixBusinessObjectName(name));
	// return alias;
	// }
	//
	// @Override
	// public void cloneAliases(List<AliasNode> aliases) {
	// for (AliasNode a : aliases)
	// addAlias(a.getName());
	// }
	//
	// private void clearAllAliasHolders() {
	// for (Node child : getChildren())
	// if (child.getChildrenHandler() != null)
	// child.getChildrenHandler().clear();
	// getChildrenHandler().clear();
	// }
	//
	// /**
	// *
	// * New facets can only be added in unmanaged or head versions.
	// *
	// * @param name
	// * @param type
	// * @return the new contextual facet (not contributed)
	// */
	// // TODO - consider allowing them in minor and use createMinorVersionOfComponent()
	// @Override
	// public AbstractContextualFacet addFacet(String name, TLFacetType type) {
	// if (!isEditable_newToChain()) {
	// isEditable_newToChain();
	// throw new IllegalArgumentException("Not editable - Can not add facet to " + this);
	// }
	// TLContextualFacet tlCf = ContextualFacetNode.createTL(name, type);
	// AbstractContextualFacet cf = NodeFactory.createContextualFacet(tlCf);
	// cf.setOwner(this);
	// if (cf instanceof LibraryMemberInterface)
	// getLibrary().addMember((LibraryMemberInterface) cf);
	// cf.setName(NodeNameUtils.fixContextualFacetName(cf, name));
	//
	// if (OTM16Upgrade.otm16Enabled) {
	// assert cf.getParent() instanceof NavNode;
	// assert getChildren().contains(((ContextualFacetNode) cf).getWhereContributed());
	// } else {
	// assert cf.getParent() == this;
	// assert getChildren().contains(cf);
	// }
	// return cf;
	// }
	//
	// @Override
	// public boolean canOwn(AbstractContextualFacet targetCF) {
	// return canOwn(targetCF.getTLModelObject().getFacetType());
	// }
	//
	// @Override
	// public boolean canOwn(TLFacetType type) {
	// switch (type) {
	// case ID:
	// case SUMMARY:
	// case DETAIL:
	// case CUSTOM:
	// case QUERY:
	// case UPDATE:
	// return true;
	// default:
	// return false;
	// }
	// }
	//
	// @Override
	// public ComponentNode createMinorVersionComponent() {
	// TLBusinessObject tlMinor = (TLBusinessObject) createMinorTLVersion(this);
	// if (tlMinor != null)
	// return super.createMinorVersionComponent(new BusinessObjectNode(tlMinor));
	// return null;
	// }
	//
	// /**
	// * @return Custom Facets without inherited
	// */
	// public List<AbstractContextualFacet> getCustomFacets() {
	// ArrayList<AbstractContextualFacet> ret = new ArrayList<>();
	// for (INode f : getContextualFacets(false))
	// if (f instanceof CustomFacetNode)
	// ret.add((CustomFacetNode) f);
	// else if (f instanceof CustomFacet15Node)
	// ret.add((CustomFacet15Node) f);
	//
	// return ret;
	// }
	//
	// @Override
	// public NavNode getParent() {
	// return (NavNode) parent;
	// }
	//
	// // FIXME - make return abstractContextualFacet
	// public List<ComponentNode> getQueryFacets() {
	// ArrayList<ComponentNode> ret = new ArrayList<>();
	// for (AbstractContextualFacet f : getContextualFacets(false)) {
	// if (f instanceof QueryFacetNode)
	// ret.add(f);
	// if (f instanceof QueryFacet15Node)
	// ret.add(f);
	// }
	// return ret;
	// }
	//
	// @Override
	// public void delete() {
	// // Must delete the contextual facets separately because they are separate library members.
	// for (Node n : getChildren_New())
	// if (n instanceof ContextualFacetNode)
	// n.delete();
	// super.delete();
	// }
	//
	// /**
	// * Assure business object has one and only one ID and it is in the ID facet. Change extra IDs to attributes.
	// Create
	// * new ID if needed.
	// *
	// * @return
	// */
	// private IdNode fixIDs() {
	// IdNode finalID = null;
	// // Use from ID facet if found. if more than one found, change extras to attribute
	// for (Node n : getFacet_ID().getChildren())
	// if (n instanceof IdNode)
	// if (finalID == null) {
	// ((IdNode) n).moveProperty(getFacet_ID());
	// finalID = (IdNode) n;
	// } else
	// ((PropertyNode) n).changePropertyRole(PropertyNodeType.ATTRIBUTE);
	//
	// // Search for any ID types. Move 1st one to ID facet and make rest into attributes.
	// List<Node> properties = new ArrayList<>(getFacet_Summary().getChildren());
	// properties.addAll(getFacet_Detail().getChildren());
	// for (Node n : properties)
	// if (n instanceof IdNode)
	// if (finalID == null) {
	// ((IdNode) n).moveProperty(getFacet_ID());
	// finalID = (IdNode) n;
	// } else
	// ((PropertyNode) n).changePropertyRole(PropertyNodeType.ATTRIBUTE);
	//
	// // If none were found, make one
	// if (finalID == null)
	// finalID = new IdNode(getFacet_ID(), "newID"); // BO must have at least one ID facet property
	// return finalID;
	// }
	//
	// @Override
	// public INode.CommandType getAddCommand() {
	// return INode.CommandType.PROPERTY;
	// }
	//
	// @Override
	// public List<AliasNode> getAliases() {
	// List<AliasNode> aliases = new ArrayList<>();
	// for (Node c : getChildren())
	// if (c instanceof AliasNode)
	// aliases.add((AliasNode) c);
	// return aliases;
	// }
	//
	// @Override
	// public void setName(String name) {
	// getTLModelObject().setName(NodeNameUtils.fixBusinessObjectName(name));
	// updateNames(NodeNameUtils.fixBusinessObjectName(name));
	// }
	//
	// @Override
	// public void sort() {
	// getFacet_Summary().sort();
	// getFacet_Detail().sort();
	// for (ComponentNode f : getCustomFacets())
	// ((FacetOMNode) f).sort();
	// for (ComponentNode f : getQueryFacets())
	// ((FacetOMNode) f).sort();
	// }
	//
	// @Override
	// public void merge(Node source) {
	// if (!(source instanceof BusinessObjectNode)) {
	// throw new IllegalStateException("Can only merge objects with the same type");
	// }
	// BusinessObjectNode business = (BusinessObjectNode) source;
	// getFacet_ID().add(business.getFacet_ID().getProperties(), true);
	// getFacet_Summary().add(business.getFacet_Summary().getProperties(), true);
	// getFacet_Detail().add(business.getFacet_Detail().getProperties(), true);
	//
	// copyFacet(business.getContextualFacets(false));
	// getChildrenHandler().clear();
	// }
	//
	// private void copyFacet(List<AbstractContextualFacet> facets) {
	// // assert false;
	// // // FIXME
	// // }
	// //
	// // private void copyFacet(List<ComponentNode> facets) {
	// // FIXME
	// for (ComponentNode f : facets) {
	// FacetInterface facet = (FacetInterface) f;
	// if (!NodeUtils.checker((Node) facet).isInheritedFacet().get()) {
	// TLFacet tlFacet = (TLFacet) facet.getTLModelObject();
	// String name = "";
	// if (tlFacet instanceof TLContextualFacet)
	// name = ((TLContextualFacet) tlFacet).getName();
	// ComponentNode newFacet = addFacet(name, tlFacet.getFacetType());
	// ((FacetInterface) newFacet).add(facet.getProperties(), true);
	// }
	// }
	// }
	//
	// @Override
	// public boolean isMergeSupported() {
	// return true;
	// }
	//
	// // /////////////////////////////////////////////////////////////////
	// //
	// // Extension Owner implementations
	// //
	// @Override
	// public Node getExtensionBase() {
	// return extensionHandler != null ? extensionHandler.get() : null;
	// }
	//
	// @Override
	// public String getExtendsTypeNS() {
	// return getExtensionBase() != null ? getExtensionBase().getNamespace() : "";
	// }
	//
	// @Override
	// public void setExtension(final Node base) {
	// if (extensionHandler == null)
	// extensionHandler = new ExtensionHandler(this);
	// extensionHandler.set(base);
	// }
	//
	// @Override
	// public ExtensionHandler getExtensionHandler() {
	// return extensionHandler;
	// }
	//
}
