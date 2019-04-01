/**
 * 
 */
package org.opentravel.dex.repository.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.controllers.DexStatusController;
import org.opentravel.dex.repository.TaskResultHandlerI;
import org.opentravel.schemacompiler.repository.RepositoryException;
import org.opentravel.schemacompiler.repository.RepositoryItem;
import org.opentravel.schemacompiler.repository.RepositoryItemHistory;

/**
 * A DEX JavaFX task retrieving a repository item's history.
 * 
 * @author dmh
 *
 */
public class GetRepositoryItemHistoryTask extends DexTaskBase<RepositoryItem> {
	private static Log log = LogFactory.getLog(GetRepositoryItemHistoryTask.class);

	RepositoryItemHistory history = null;

	public RepositoryItemHistory getHistory() {
		return history;
	}

	@Override
	public void doIT() throws RepositoryException {
		history = taskData.getRepository().getHistory(taskData);
	}

	public GetRepositoryItemHistoryTask(RepositoryItem taskData, TaskResultHandlerI handler,
			DexStatusController statusController) {
		super(taskData, handler, statusController);

		// Replace start message from super-type.
		msgBuilder = new StringBuilder("Getting history for: ");
		msgBuilder.append(taskData.getLibraryName());
		updateMessage(msgBuilder.toString());
	}

}
