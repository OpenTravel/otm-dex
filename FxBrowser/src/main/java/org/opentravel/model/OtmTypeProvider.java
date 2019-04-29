/**
 * 
 */
package org.opentravel.model;

import org.opentravel.schemacompiler.model.TLModelElement;

/**
 * @author dmh
 *
 */
public interface OtmTypeProvider {

	public String getName();

	public TLModelElement getTL();

	/**
	 * Providers that are name controlled require that all element names assigned to this provider be set based on the
	 * name of the provider.
	 * 
	 * @return
	 */
	boolean isNameControlled();

}
