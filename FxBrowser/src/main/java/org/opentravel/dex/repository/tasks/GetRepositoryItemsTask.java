/**
 * 
 */
package org.opentravel.dex.repository.tasks;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.controllers.DexStatusController;
import org.opentravel.dex.repository.NamespacesDAO;
import org.opentravel.dex.repository.TaskResultHandlerI;
import org.opentravel.schemacompiler.model.TLLibraryStatus;
import org.opentravel.schemacompiler.repository.RepositoryException;
import org.opentravel.schemacompiler.repository.RepositoryItem;

/**
 * A DEX JavaFX task retrieving repository items for a namespace.
 * 
 * @author dmh
 *
 */
public class GetRepositoryItemsTask extends DexTaskBase<NamespacesDAO> {
	private static Log log = LogFactory.getLog(GetRepositoryItemsTask.class);

	private List<RepositoryItem> allItems = null;
	private List<RepositoryItem> latestItems = null;
	private String permission = "unknown";

	public List<RepositoryItem> getAllItems() {
		return allItems;
	}

	public List<RepositoryItem> getLatestItems() {
		return latestItems;
	}

	public String getPermission() {
		return permission;
	}

	@Override
	public void doIT() throws RepositoryException {
		// indicates the latest library status to include in the results (null = all statuses)
		TLLibraryStatus includeStatus = null;
		allItems = taskData.getRepository().listItems(taskData.getFullPath(), includeStatus, false);
		latestItems = taskData.getRepository().listItems(taskData.getFullPath(), includeStatus, true);
		//
		permission = taskData.getRepository().getUserAuthorization(taskData.getFullPath()).toString();

	}

	public GetRepositoryItemsTask(NamespacesDAO taskData, TaskResultHandlerI handler,
			DexStatusController statusController) {
		super(taskData, handler, statusController);

		// Replace start message from super-type.
		msgBuilder = new StringBuilder("Getting items for: ");
		msgBuilder.append(taskData.getFullPath());
		updateMessage(msgBuilder.toString());
	}

}
