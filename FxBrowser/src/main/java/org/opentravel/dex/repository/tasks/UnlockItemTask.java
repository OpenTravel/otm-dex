/**
 * 
 */
package org.opentravel.dex.repository.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.repository.TaskResultHandlerI;
import org.opentravel.schemacompiler.repository.RepositoryException;
import org.opentravel.schemacompiler.repository.RepositoryItem;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;

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

	public UnlockItemTask(RepositoryItem repoItem, boolean commitWIP, String remarks, DoubleProperty progressProperty,
			StringProperty statusProperty, TaskResultHandlerI handler) {
		super(repoItem, handler, progressProperty, statusProperty);
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
