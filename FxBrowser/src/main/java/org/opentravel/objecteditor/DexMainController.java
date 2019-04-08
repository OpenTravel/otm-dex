/**
 * 
 */
package org.opentravel.objecteditor;

import org.opentravel.common.ImageManager;
import org.opentravel.dex.controllers.DexStatusController;
import org.opentravel.model.OtmModelManager;
import org.opentravel.schemacompiler.repository.RepositoryManager;

/**
 * Abstract interface for main Otm-DE FX controllers. Main controllers provide access to menu bars, status controllers
 * and system resources such as model managers and image managers.
 * 
 * @author dmh
 *
 */
public interface DexMainController extends DexController {

	/**
	 * @return the image manager used by this controller
	 */
	public ImageManager getImageManager();

	/**
	 * @return the model manager used by this controller
	 */
	public OtmModelManager getModelManager();

	/**
	 * @return
	 */
	public RepositoryManager getRepositoryManager();

	/**
	 * @return
	 */
	DexStatusController getStatusController();

	/**
	 * Update the progress indicator displayed value.
	 * 
	 * @param percentDone
	 */
	public void postProgress(double percentDone);

	/**
	 * Put the status string into the status label.
	 * 
	 * @param string
	 */
	public void postStatus(String string);

}
