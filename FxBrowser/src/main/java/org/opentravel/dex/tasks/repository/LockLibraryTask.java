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
 * A JavaFX task for locking Otm Libraries
 * 
 * @author dmh
 *
 */
public class LockLibraryTask extends DexTaskBase<OtmLibrary> {
	private static Log log = LogFactory.getLog(LockLibraryTask.class);

	// private DexStatusController statusController;
	private DexIncludedController<?> eventController;
	private OtmProject proj = null;
	private OtmLibrary library = null;
	private OtmModelManager modelManager;

	/**
	 * Create a lock library task.
	 * 
	 * @param taskData
	 *            - an repository item to lock.
	 * @param handler
	 *            - results handler
	 * @param statusController
	 *            - status controller that can post message and progress indicator
	 * @param eventController
	 *            - controller to publish repository item replaced event
	 * @param modelManager
	 *            - model manager that holds projects that could contain the library in this repository item
	 */
	public LockLibraryTask(OtmLibrary taskData, TaskResultHandlerI handler, DexStatusController statusController,
			DexIncludedController<?> eventController, OtmModelManager modelManager) {
		super(taskData, handler, statusController);
		if (taskData == null)
			return;

		this.library = taskData;
		// this.statusController = statusController;
		this.eventController = eventController;
		// this.modelManager = modelManager;

		// Try to find the actual modeled library. A modeled library will be created by opening a project.
		// library = modelManager.get(taskData.getNamespace() + "/" + taskData.getLibraryName());
		// See if there is an open project to manage this item and use it
		// if (library != null) {
		proj = modelManager.getManagingProject(library);
		// }

		// Replace start message from super-type.
		msgBuilder = new StringBuilder("Locking: ");
		msgBuilder.append(library.getName());
		updateMessage(msgBuilder.toString());
	}

	@Override
	public void doIT() throws RepositoryException {
		log.debug("Lock library task: " + library.hashCode());
		// if (mgr == null)
		// return;

		if (proj != null) {
			log.debug("Locking with project item: " + proj.getProjectItem(library.getTL()).hashCode());

			proj.getTL().getProjectManager().lock(proj.getProjectItem(library.getTL()));

			// RepositoryItem newRI = taskData.getRepository().getRepositoryItem(taskData.getBaseNamespace(),
			// taskData.getFilename(), taskData.getVersion());
			// if (newRI != null && repoItem != newRI) {
			// // repoItem is now stale--and held in a list by the NamespacesDAO
			// // throw event so ns-library view is rebuilt on task complete.
			// log.debug("Ready to replace" + repoItem.hashCode() + " with " + newRI.hashCode());
			// throwRepoItemReplacedEvent(repoItem, newRI);
			// log.debug(newRI.getLibraryName() + " locked by " + newRI.getLockedByUser());
			// }
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
