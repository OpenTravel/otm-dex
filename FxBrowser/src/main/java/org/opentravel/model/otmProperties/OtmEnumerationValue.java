/**
 * 
 */
package org.opentravel.model.otmProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.common.ImageManager.Icons;
import org.opentravel.model.OtmModelElement;
import org.opentravel.model.otmLibraryMembers.OtmEnumeration;
import org.opentravel.schemacompiler.model.TLAbstractEnumeration;
import org.opentravel.schemacompiler.model.TLEnumValue;

/**
 * @author dmh
 *
 */
public class OtmEnumerationValue extends OtmModelElement<TLEnumValue> {
	private static Log log = LogFactory.getLog(OtmEnumerationValue.class);

	private OtmEnumeration<TLAbstractEnumeration> parent;

	public OtmEnumerationValue(TLEnumValue value, OtmEnumeration<TLAbstractEnumeration> parent) {
		super(value, parent.getActionManager());
		this.parent = parent;
	}

	@Override
	public Icons getIconType() {
		return ImageManager.Icons.ENUMERATION_VALUE;
	}

	@Override
	public String setName(String name) {
		getTL().setLiteral(name);
		nameProperty().set(getName()); // may not fire otm name change listener
		isValid(true);
		log.debug("Set name to: " + getName());
		return getName();
	}

	@Override
	public OtmEnumeration<TLAbstractEnumeration> getOwningMember() {
		return parent;
	}

	@Override
	public String getName() {
		return getTL().getLiteral();
	}

	@Override
	public TLEnumValue getTL() {
		return tlObject;
	}
}
