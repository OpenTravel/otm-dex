/**
 * 
 */
package org.opentravel.dex.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.objecteditor.DexController;
import org.opentravel.objecteditor.dialogbox.DialogBoxContoller;
import org.opentravel.repositoryViewer.RepositoryViewerController;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;

/**
 * An handler for the results of repository tasks. When successful (null or empty message) the parent is refreshed. On
 * error, a dialog is displayed.
 * 
 * @author dmh
 *
 */
public class RepositoryResultHandler implements ResultHandlerI {
	private static Log log = LogFactory.getLog(RepositoryResultHandler.class);
	private static final String TITLE = "Repository Error";
	// private DexController parentController;
	private RepositoryViewerController parentController;

	public RepositoryResultHandler(DexController parentController) {
		this.parentController = (RepositoryViewerController) parentController;
	}

	@Override
	public void handle(WorkerStateEvent event) {
		DialogBoxContoller dbc = null;
		if (event != null && event.getTarget() instanceof Task) {
			Object data = ((Task<?>) event.getTarget()).getValue();
			if (data instanceof String && (!((String) data).isEmpty())) {
				dbc = parentController.getDialogBoxController();
				if (dbc != null)
					dbc.show(TITLE, (String) data);
			}
			parentController.refresh();
		} else {
			log.warn("Invalid event in result handler.");
		}
	}

	// private void handle(String message) {
	// log.debug(message);
	// if (message == null || message.isEmpty())
	// parentController.refresh();
	// else {
	// DialogBoxContoller dbc = parentController.getDialogBoxController();
	// dbc.show(TITLE, message);
	// }
	// }
}
