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
 * A Dex/JavaFX task for unlocking repository items
 * 
 * @author dmh
 *
 */
public class UnlockItemTask extends DexTaskBase<RepositoryItem> {
	private static Log log = LogFactory.getLog(UnlockItemTask.class);

	boolean commitWIP = true;
	String remarks = "testing";

	public UnlockItemTask(RepositoryItem repoItem, boolean commitWIP, String remarks, TaskResultHandlerI handler,
			DexStatusController status) {
		super(repoItem, handler, status);
		this.commitWIP = commitWIP;
		this.remarks = remarks;

		// Replace start message from super-type.
		msgBuilder = new StringBuilder("Unlocking: ");
		msgBuilder.append(repoItem.getLibraryName());
		updateMessage(msgBuilder.toString());
	}

	@Override
	public void doIT() throws RepositoryException {
		taskData.getRepository().unlock(taskData, commitWIP, remarks);
	}
}
