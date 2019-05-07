/**
 * 
 */
package org.opentravel.dex.tasks.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.controllers.DexStatusController;
import org.opentravel.dex.tasks.DexTaskBase;
import org.opentravel.dex.tasks.TaskResultHandlerI;
import org.opentravel.model.OtmModelManager;
import org.opentravel.schemacompiler.repository.RepositoryException;

/**
 * A JavaFX task for locking repository items
 * 
 * @author dmh
 *
 */
// FIXME - will get concurrent modification error if the model is closed before validation is finsihed
//
public class ValidateModelManagerItemsTask extends DexTaskBase<OtmModelManager> {
	private static Log log = LogFactory.getLog(ValidateModelManagerItemsTask.class);

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
	public ValidateModelManagerItemsTask(OtmModelManager taskData, TaskResultHandlerI handler,
			DexStatusController status) {
		super(taskData, handler, status);

		// Replace start message from super-type.
		msgBuilder = new StringBuilder("Validating model.");
		// msgBuilder.append(taskData.getName());
		// updateMessage(msgBuilder.toString());
	}

	@Override
	public void doIT() throws RepositoryException {
		taskData.getMembers().forEach(m -> m.isValid(true));
	}

}
