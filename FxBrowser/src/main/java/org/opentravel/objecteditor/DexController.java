/**
 * 
 */
package org.opentravel.objecteditor;

/**
 * Abstract interface for all Otm-DE FX view controllers.
 * 
 * @author dmh
 *
 */
public interface DexController {

	/**
	 * @return
	 */
	ImageManager getImageManager();

	/**
	 * Remove all items from the controlled view.
	 */
	void clear();

}
