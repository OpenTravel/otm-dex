/**
 * 
 */
package org.opentravel.dex.tasks.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.controllers.DexIncludedController;
import org.opentravel.dex.controllers.DexStatusController;
import org.opentravel.dex.events.DexRepositoryItemReplacedEvent;
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

	private DexStatusController statusController;

	private RepositoryItem repoItem;

	private OtmModelManager modelManager;

	private DexIncludedController<?> eventController;

	/**
	 * Create a lock repository item task.
	 * 
	 * @param taskData
	 *            - an repository item to lock.
	 * @param handler
	 *            - results handler
	 * @param controller
	 *            - status controller that can post message and progress indicator
	 * @param eventController
	 *            - controller to publish repository item replaced event
	 * @param modelManager
	 *            - model manager that holds projects that could contain the library in this repository item
	 */
	public LockItemTask(RepositoryItem taskData, TaskResultHandlerI handler, DexStatusController controller,
			DexIncludedController<?> eventController, OtmModelManager modelManager) {
		super(taskData, handler, controller);
		if (taskData == null)
			return;

		this.statusController = controller;
		this.repoItem = taskData;
		this.modelManager = modelManager;
		this.eventController = eventController;

		// Replace start message from super-type.
		msgBuilder = new StringBuilder("Locking: ");
		msgBuilder.append(taskData.getLibraryName());
		updateMessage(msgBuilder.toString());
	}

	@Override
	public void doIT() throws RepositoryException {
		OtmModelManager mgr = modelManager;
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
			// repoItem is a copy made by java/fx concurrency model (I think). It has a different hashcode than
			log.debug("Locking with project manger: " + repoItem.hashCode());
			proj.getTL().getProjectManager().lock(proj.getProjectItem(library.getTL()));
			RepositoryItem newRI = taskData.getRepository().getRepositoryItem(taskData.getBaseNamespace(),
					taskData.getFilename(), taskData.getVersion());
			if (newRI != null && repoItem != newRI) {
				// repoItem is now stale--and held in a list by the NamespacesDAO
				// throw event so ns-library view is rebuilt on task complete.
				log.debug("Ready to replace" + repoItem.hashCode() + " with " + newRI.hashCode());
				throwRepoItemReplacedEvent(repoItem, newRI);
				log.debug(newRI.getLibraryName() + " locked by " + newRI.getLockedByUser());
			}
		} else {
			taskData.getRepository().lock(taskData);
		}
	}

	/**
	 * Inform application that a repository item has changed. May be needed when locking an item since the items are
	 * held in other controller's DAOs.
	 * 
	 * @param oldItem
	 * @param newItem
	 */
	private void throwRepoItemReplacedEvent(RepositoryItem oldItem, RepositoryItem newItem) {
		eventController.publishEvent(new DexRepositoryItemReplacedEvent(this, oldItem, newItem));
	}

}
