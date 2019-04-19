/**
 * 
 */
package org.opentravel.dex.tasks.repository;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.OpenProjectProgressMonitor;
import org.opentravel.dex.controllers.DexStatusController;
import org.opentravel.dex.tasks.DexTaskBase;
import org.opentravel.dex.tasks.TaskResultHandlerI;
import org.opentravel.model.OtmModelManager;

/**
 * A JavaFX opening a project file
 * 
 * @author dmh
 *
 */
public class OpenProjectFileTask extends DexTaskBase<File> {
	private static Log log = LogFactory.getLog(OpenProjectFileTask.class);

	private OtmModelManager modelMgr;
	private DexStatusController status;

	/**
	 * Create a open project file task.
	 * 
	 * @param taskData
	 *            - a file to open
	 * @param handler
	 *            - results handler
	 * @param status
	 *            - a status controller that can post message and progress indicator
	 */
	public OpenProjectFileTask(File taskData, OtmModelManager modelMgr, TaskResultHandlerI handler,
			DexStatusController status) {
		super(taskData, handler, status);
		this.modelMgr = modelMgr;
		this.status = status;

		// Replace start message from super-type.
		msgBuilder = new StringBuilder("Opening Project: ");
		msgBuilder.append(taskData.getName());
		updateMessage(msgBuilder.toString());
	}

	@Override
	public void doIT() {
		modelMgr.openProject(taskData, new OpenProjectProgressMonitor(status));
	}

}
