/**
 * 
 */
package org.opentravel.objecteditor;

import org.opentravel.common.ImageManager;

import javafx.scene.image.ImageView;

/**
 * Interface for all Otm-DE FX view Data Access Objects. DAOs are simple POJOs that expose data using JavaFX properties.
 * Listeners are registered for editable properties.
 * <p>
 * No business logic (actions, validation, filters, etc.) should be in the DAO.
 * 
 * @author dmh
 *
 */
public interface DexDAO<T> {

	/**
	 * 
	 * @param imageMgr
	 *            manages access to image views for icons
	 * @return JavaFX imageView for the icon representing the data item.
	 */
	@SuppressWarnings("restriction")
	public ImageView getIcon(ImageManager imageMgr);

	/**
	 * @return the data item
	 */
	public T getValue();

	/**
	 * @return a string representing this data item
	 */
	@Override
	public String toString();
}
