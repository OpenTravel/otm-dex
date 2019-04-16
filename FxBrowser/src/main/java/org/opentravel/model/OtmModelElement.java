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
package org.opentravel.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.dex.actions.DexActionManager;
import org.opentravel.dex.actions.DexActionManager.DexActions;
import org.opentravel.model.otmContainers.OtmLibrary;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;
import org.opentravel.schemacompiler.event.ModelElementListener;
import org.opentravel.schemacompiler.model.NamedEntity;
import org.opentravel.schemacompiler.model.TLDocumentation;
import org.opentravel.schemacompiler.model.TLDocumentationOwner;
import org.opentravel.schemacompiler.model.TLExample;
import org.opentravel.schemacompiler.model.TLExampleOwner;
import org.opentravel.schemacompiler.model.TLModelElement;
import org.opentravel.schemacompiler.validate.ValidationFindings;
import org.opentravel.schemacompiler.validate.compile.TLModelCompileValidator;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

/**
 * Abstract base for OTM Facade objects which wrap all OTM libraries, objects, facets and properties.
 * 
 * @author Dave Hollander
 * 
 */
public abstract class OtmModelElement<T extends TLModelElement> {
	private static Log log = LogFactory.getLog(OtmModelElement.class);

	private static final String NONAMESPACE = "no-namespace-for-for-this-object";

	private static final String NONAME = "no-name-for-for-this-object";

	/**
	 * Utility to <i>get</i> the OTM facade object that wraps the TL Model object. Uses the listener added to all TL
	 * objects in the facade's constructor.
	 * 
	 * @param tlObject
	 *            the wrapped TLModelElement
	 * @return otm facade wrapper or null if no listener found.
	 */
	public static OtmModelElement<TLModelElement> get(TLModelElement tlObject) {
		if (tlObject != null)
			for (ModelElementListener l : tlObject.getListeners())
				if (l instanceof OtmModelElementListener)
					return ((OtmModelElementListener) l).get();
		return null;
	}

	protected T tlObject;

	// leave empty if object can have children but does not or has not been modeled yet.
	// leave null if the element can not have children.
	protected List<OtmModelElement<?>> children = new ArrayList<>();
	private ValidationFindings findings = null;

	private SimpleStringProperty nameProperty;
	private SimpleStringProperty descriptionProperty;

	private DexActionManager actionMgr = null;

	// /**
	// * @param
	// */
	// @SuppressWarnings("unchecked")
	// public OtmModelElement(T tl) {
	// tlObject = tl;
	// tl.addListener(new OtmModelElementListener((OtmModelElement<TLModelElement>) this));
	// checkListener();
	// }

	public OtmModelElement(T tl, DexActionManager actionManager) {
		tlObject = tl;
		tl.addListener(new OtmModelElementListener((OtmModelElement<TLModelElement>) this));
		checkListener();
		this.actionMgr = actionManager;
	}

	public DexActionManager getActionManager() {
		return actionMgr;
	}

	private void checkListener() {
		for (ModelElementListener l : tlObject.getListeners())
			if (l instanceof OtmModelElementListener)
				assert this == ((OtmModelElementListener) l).get();
	}

	public String getDeprecation() {
		if (getTL() instanceof TLDocumentationOwner) {
			TLDocumentation doc = ((TLDocumentationOwner) getTL()).getDocumentation();
			if (doc != null && doc.getDeprecations() != null && !doc.getDeprecations().isEmpty())
				return doc.getDeprecations().get(0).getText();
		}
		return "";
	}

	public StringProperty descriptionProperty() {
		if (descriptionProperty == null) {
			if (isEditable()) {
				descriptionProperty = new SimpleStringProperty(getDescription());
				if (actionMgr != null)
					actionMgr.addAction(DexActions.DESCRIPTIONCHANGE, descriptionProperty(), this);
			} else {
				descriptionProperty = new ReadOnlyStringWrapper("" + getName());
			}
		}
		return descriptionProperty;
	}

	public String getDescription() {
		if (getTL() instanceof TLDocumentationOwner) {
			TLDocumentation doc = ((TLDocumentationOwner) getTL()).getDocumentation();
			if (doc != null)
				return doc.getDescription();
		}
		return "";
	}

	public void setDescription(String description) {
		if (getTL() instanceof TLDocumentationOwner) {
			TLDocumentation doc = ((TLDocumentationOwner) getTL()).getDocumentation();
			if (doc == null) {
				doc = new TLDocumentation();
				((TLDocumentationOwner) getTL()).setDocumentation(doc);
			}
			doc.setDescription(description);
		}
	}

	public String getExample() {
		if (getTL() instanceof TLExampleOwner) {
			List<TLExample> exs = ((TLExampleOwner) getTL()).getExamples();
			if (exs != null && !exs.isEmpty())
				return exs.get(0).getValue();
		}
		return "";
	}

	public Image getIcon() {
		return new ImageManager().get(this.getIconType());
	}

	public abstract ImageManager.Icons getIconType();

	/**
	 * @return this library, owning library or null
	 */
	public OtmLibrary getLibrary() {
		// if (this instanceof OtmLibraryMember<?>) return getLibrary();
		if (getOwningMember() != null)
			return getOwningMember().getLibrary();
		return null;
	}

	public String getName() {
		if (tlObject instanceof NamedEntity)
			return ((NamedEntity) tlObject).getLocalName();
		return NONAME;
	}

	public StringProperty nameProperty() {
		if (nameProperty == null) {
			if (isEditable()) {
				nameProperty = new SimpleStringProperty(getName());
				if (actionMgr != null)
					actionMgr.addAction(DexActions.NAMECHANGE, nameProperty, this);
			} else {
				nameProperty = new ReadOnlyStringWrapper("" + getName());
			}
		}
		return nameProperty;
	}

	public String getNamespace() {
		if (tlObject instanceof NamedEntity)
			return ((NamedEntity) tlObject).getNamespace();
		return NONAMESPACE;
	}

	public abstract OtmLibraryMember<?> getOwningMember();

	/**
	 * 
	 */
	public String getPrefix() {
		return getOwningMember() != null && getOwningMember().getLibrary() != null
				? getOwningMember().getLibrary().getPrefix() : "---";
	}

	public boolean isValid() {
		return isValid(false);
	}

	public boolean isValid(boolean refresh) {
		if (findings == null || refresh) {
			boolean deep = false;
			try {
				findings = TLModelCompileValidator.validateModelElement(getTL(), deep);
			} catch (Exception e) {
				log.debug("Validation threw error: " + e.getLocalizedMessage());
			}
			log.debug(findings != null ? findings.count() + " findings found" : " null" + " findings found.");
		}
		return findings == null || findings.isEmpty();
	}

	public ValidationFindings getFindings() {
		return findings;
	}
	// /**
	// * Get Children. To allow lazy evaluation, children will be modeled if list is empty.
	// *
	// * @return the live list of children for this library member.
	// */
	// public List<OtmModelElement<?>> getChildren() {
	// // Create OtmNodes for all the children of this member
	// if (children != null && children.isEmpty())
	// modelChildren();
	//
	// return children;
	// }

	// /**
	// * Model the children of this object from its' tlObject.
	// */
	// public void modelChildren() {
	// // Override if the element has children
	// }

	/**
	 * @return
	 */
	public String getRole() {
		return getClass().getSimpleName();
	}

	public abstract T getTL();

	public boolean isEditable() {
		return getOwningMember() != null ? getOwningMember().isEditable() : false;
	}

	/**
	 * Set the name if possible.
	 * 
	 * @param name
	 * @return the actual name after assignment attempted
	 */
	public String setName(String name) {
		// NO-OP unless overridden
		return getName();
	}

	@Override
	public String toString() {
		return getName();
	}
}
