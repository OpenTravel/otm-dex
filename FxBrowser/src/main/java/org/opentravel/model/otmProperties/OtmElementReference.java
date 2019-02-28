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
package org.opentravel.model.otmProperties;

import org.opentravel.common.ImageManager;
import org.opentravel.common.ImageManager.Icons;
import org.opentravel.model.OtmTypeUser;
import org.opentravel.model.OtmPropertyOwner;
import org.opentravel.schemacompiler.model.TLProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract OTM Node for attribute properties.
 * 
 * @author Dave Hollander
 * 
 */
public class OtmElementReference<TL extends TLProperty> extends OtmElement<TLProperty> implements OtmTypeUser {
	private static final Logger LOGGER = LoggerFactory.getLogger(OtmElementReference.class);

	/**
	 */
	protected OtmElementReference(TL tl, OtmPropertyOwner parent) {
		super(tl, parent);

		if (!(tl instanceof TLProperty))
			throw new IllegalArgumentException("OtmElement constructor not passed a tl property.");
		if (!tl.isReference())
			throw new IllegalArgumentException("OtmElementRef constructor a reference element.");
	}

	@Override
	public Icons getIconType() {
		return ImageManager.Icons.ELEMENTREF;
	}

}
