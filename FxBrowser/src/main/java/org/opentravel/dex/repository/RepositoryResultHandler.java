/**
 * 
 */
package org.opentravel.dex.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.controllers.DexMainController;
import org.opentravel.dex.controllers.popup.DialogBoxContoller;
import org.opentravel.dex.tasks.TaskResultHandlerI;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;

/**
 * An handler for the results of repository tasks. When successful (null or empty message) the parent is refreshed. On
 * error, a dialog is displayed.
 * 
 * @author dmh
 *
 */
public class RepositoryResultHandler implements TaskResultHandlerI {
	private static Log log = LogFactory.getLog(RepositoryResultHandler.class);
	private static final String TITLE = "Repository Error";
	private DexMainController parentController;

	public RepositoryResultHandler(DexMainController parentController) {
		this.parentController = parentController;
	}

	@Override
	public void handleTaskComplete(WorkerStateEvent event) {
		DialogBoxContoller dbc = DialogBoxContoller.init();
		if (event != null && event.getTarget() instanceof Task) {
			Object data = ((Task<?>) event.getTarget()).getValue();
			if (data instanceof String && (!((String) data).isEmpty())) {
				if (dbc != null)
					dbc.show(TITLE, (String) data);
			}
			parentController.refresh();
		} else {
			log.warn("Invalid event in result handler.");
		}
	}
}
