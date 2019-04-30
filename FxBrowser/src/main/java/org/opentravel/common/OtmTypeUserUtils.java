/**
 * 
 */
package org.opentravel.common;

import org.opentravel.model.OtmModelElement;
import org.opentravel.model.OtmObject;
import org.opentravel.model.OtmTypeProvider;
import org.opentravel.model.OtmTypeUser;
import org.opentravel.schemacompiler.model.AbstractLibrary;
import org.opentravel.schemacompiler.model.NamedEntity;
import org.opentravel.schemacompiler.model.TLModelElement;

/**
 * OtmTypeUser is an interface not backed by an abstract class. These utilities help simplify and unify typeUser related
 * methods.
 * 
 * @author dmh
 *
 */
public class OtmTypeUserUtils {
	// private static Log log = LogFactory.getLog(OtmTypeUserUtils.class);

	private OtmTypeUserUtils() {
		// NO-OP - static methods only. Do not instantiate this class.
	}

	public static String formatAssignedType(OtmTypeUser user) {
		assert user != null;
		assert user.getLibrary() != null;

		String name = "";

		NamedEntity tlType = user.getAssignedTLType();
		if (tlType == null)
			// If the type is not found, use the TypeName from the tl object.
			name = user.getTlAssignedTypeName();
		else {
			// If the libraries are different add the prefix of the provider library.
			AbstractLibrary userLib = user.getLibrary().getTL();
			AbstractLibrary typeLib = tlType.getOwningLibrary();
			name = assignedTypeWithPrefix(tlType.getLocalName(), userLib, typeLib);
		}
		return name;
	}

	public static OtmTypeProvider getAssignedType(OtmTypeUser user) {
		OtmObject type = OtmModelElement.get((TLModelElement) user.getAssignedTLType());
		return type instanceof OtmTypeProvider ? (OtmTypeProvider) type : null;
	}

	/**
	 * Add the library prefix if libraries are different.
	 */
	public static String assignedTypeWithPrefix(String localName, AbstractLibrary userLib,
			AbstractLibrary providerLib) {
		if (localName.contains(":"))
			return localName;
		if (userLib == null)
			return localName;
		String prefix = userLib != providerLib ? providerLib.getPrefix() + " : " : "";
		return prefix + localName;
	}

}
