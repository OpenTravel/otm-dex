/**
 * 
 */
package org.opentravel.dex.tasks.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.controllers.DexStatusController;
import org.opentravel.dex.tasks.DexTaskBase;
import org.opentravel.dex.tasks.TaskResultHandlerI;
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

	/**
	 * Create a lock repository item task.
	 * 
	 * @param taskData
	 *            - an repository item to lock
	 * @param handler
	 *            - results handler
	 * @param status
	 *            - a status controller that can post message and progress indicator
	 */
	public LockItemTask(RepositoryItem taskData, TaskResultHandlerI handler, DexStatusController status) {
		super(taskData, handler, status);

		// Replace start message from super-type.
		msgBuilder = new StringBuilder("Locking: ");
		msgBuilder.append(taskData.getLibraryName());
		updateMessage(msgBuilder.toString());
	}

	@Override
	public void doIT() throws RepositoryException {
		taskData.getRepository().lock(taskData);
	}

}
