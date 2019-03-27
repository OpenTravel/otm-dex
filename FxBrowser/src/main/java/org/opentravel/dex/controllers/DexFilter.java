/**
 * 
 */
package org.opentravel.dex.controllers;

/**
 * Interface for all Otm-DE FX view filters.
 * 
 * @author dmh
 *
 */
public interface DexFilter<T> {

	public boolean isSelected(T data);

}
