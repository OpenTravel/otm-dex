/**
 * 
 */
package org.opentravel.dex.tasks.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.controllers.DexMainController;
import org.opentravel.dex.repository.RepoItemDAO;
import org.opentravel.dex.tasks.DexTaskBase;
import org.opentravel.dex.tasks.TaskResultHandlerI;
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.otmContainers.OtmLibrary;
import org.opentravel.model.otmContainers.OtmProject;
import org.opentravel.schemacompiler.repository.RepositoryException;
import org.opentravel.schemacompiler.repository.RepositoryItem;

/**
 * A JavaFX task for locking repository items
 * 
 * @author dmh
 *
 */
public class LockItemTask extends DexTaskBase<RepositoryItem> {
	private static Log log = LogFactory.getLog(LockItemTask.class);

	private DexMainController mainController;

	private RepoItemDAO repoItemDAO;

	/**
	 * Create a lock repository item task.
	 * 
	 * @param taskData
	 *            - an repository item DAO to lock. If project is used, the RepoItem is updated. Must <b>not</b> be
	 *            null.
	 * @param handler
	 *            - results handler
	 * @param controller
	 *            - a main controller that has model manager and status controller that can post message and progress
	 *            indicator
	 */
	// public LockItemTask(RepositoryItem taskData, TaskResultHandlerI handler, DexMainController controller) {
	// super(taskData, handler, controller.getStatusController());
	//
	// // Replace start message from super-type.
	// mainController = controller;
	// msgBuilder = new StringBuilder("Locking: ");
	// msgBuilder.append(taskData.getLibraryName());
	// updateMessage(msgBuilder.toString());
	// }

	public LockItemTask(RepoItemDAO taskData, TaskResultHandlerI handler, DexMainController controller) {
		super(taskData.getValue(), handler, controller.getStatusController());

		// Replace start message from super-type.
		mainController = controller;
		msgBuilder = new StringBuilder("Locking: ");
		msgBuilder.append(taskData.getValue().getLibraryName());
		updateMessage(msgBuilder.toString());
		this.repoItemDAO = taskData;
	}

	@Override
	public void doIT() throws RepositoryException {
		OtmModelManager mgr = mainController.getModelManager();
		OtmProject proj = null;
		if (mgr == null)
			return;

		// Try to find the actual modeled library. A modeled library will be created by opening a project.
		OtmLibrary library = mgr.get(taskData.getNamespace() + "/" + taskData.getLibraryName());
		if (library != null) {
			// See if there is an open project to manage this item and use it
			proj = mgr.getManagingProject(library);
		}
		if (proj != null) {
			proj.getTL().getProjectManager().lock(proj.getProjectItem(library.getTL()));
			// repoItem is now stale--update.
			// RepoItemDAO can be updated, but the repoItem is held in a list by the NamespacesDAO
			RepositoryItem newRI = taskData.getRepository().getRepositoryItem(taskData.getBaseNamespace(),
					taskData.getFilename(), taskData.getVersion());
			if (newRI != null && repoItemDAO != null) {
				// Should not be needed, the ns-library view is rebuilt on task complete.
				// repoItemDAO.setValue(newRI);
				log.debug(newRI.getLibraryName() + " locked by " + newRI.getLockedByUser());
			}
		} else {
			taskData.getRepository().lock(taskData);
		}
	}

}
