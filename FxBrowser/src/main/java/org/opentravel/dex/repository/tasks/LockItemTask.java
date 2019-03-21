/**
 * 
 */
package org.opentravel.dex.repository.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.repository.ResultHandlerI;
import org.opentravel.schemacompiler.repository.RepositoryException;
import org.opentravel.schemacompiler.repository.RepositoryItem;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;

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
	 * @param progressProperty
	 * @param statusProperty
	 * @param handler
	 */
	public LockItemTask(RepositoryItem taskData, DoubleProperty progressProperty, StringProperty statusProperty,
			ResultHandlerI handler) {
		super(taskData, progressProperty, statusProperty, handler);

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
