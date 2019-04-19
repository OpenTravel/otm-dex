/**
 * 
 */
package org.opentravel.model;

/**
 * @author dmh
 *
 */
public interface OtmTypeProvider {

	public String getName();

	/**
	 * Providers that are name controlled require that all element names assigned to this provider be set based on the
	 * name of the provider.
	 * 
	 * @return
	 */
	boolean isNameControlled();
}
