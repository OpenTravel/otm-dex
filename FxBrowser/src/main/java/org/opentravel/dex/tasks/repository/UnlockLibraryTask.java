/**
 * 
 */
package org.opentravel.dex.tasks.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.controllers.DexStatusController;
import org.opentravel.dex.tasks.DexTaskBase;
import org.opentravel.dex.tasks.TaskResultHandlerI;
import org.opentravel.model.otmContainers.OtmLibrary;
import org.opentravel.model.otmContainers.OtmProject;
import org.opentravel.schemacompiler.repository.Project;
import org.opentravel.schemacompiler.repository.ProjectItem;
import org.opentravel.schemacompiler.repository.ProjectManager;
import org.opentravel.schemacompiler.repository.RepositoryException;

/**
 * A Dex/JavaFX task for unlocking libraries via their project item
 * 
 * @author dmh
 *
 */
public class UnlockLibraryTask extends DexTaskBase<OtmLibrary> {
	private static Log log = LogFactory.getLog(UnlockLibraryTask.class);

	boolean commitWIP = true;
	String remarks = "testing";

	public UnlockLibraryTask(OtmLibrary taskData, boolean commitWIP, String remarks, TaskResultHandlerI handler,
			DexStatusController status) {
		super(taskData, handler, status);
		this.commitWIP = commitWIP;
		this.remarks = remarks;

		// Replace start message from super-type.
		msgBuilder = new StringBuilder("Unlocking: ");
		msgBuilder.append(taskData.getName());
		updateMessage(msgBuilder.toString());
	}

	@Override
	public void doIT() throws RepositoryException {
		OtmProject managingProject = taskData.getManagingProject();
		if (managingProject != null) {
			Project managingTLProject = managingProject.getTL();
			ProjectItem pi = managingProject.getProjectItem(taskData.getTL());
			if (managingTLProject != null && pi != null) {
				ProjectManager projectManager = managingTLProject.getProjectManager();
				if (projectManager != null)
					projectManager.unlock(pi, commitWIP, remarks);
			}
		}
	}
}
