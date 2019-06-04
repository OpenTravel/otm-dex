/**
 * 
 */
package org.opentravel.dex.tasks.model;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.controllers.DexStatusController;
import org.opentravel.dex.tasks.DexTaskBase;
import org.opentravel.dex.tasks.TaskResultHandlerI;
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMemberBase;
import org.opentravel.schemacompiler.repository.RepositoryException;

/**
 * A task for computing whereUsed for all members.
 * 
 * @author dmh
 *
 */
// FIXME - will get concurrent modification error if the model is closed before validation is finsihed
//
public class TypeResolverTask extends DexTaskBase<OtmModelManager> {
	private static Log log = LogFactory.getLog(TypeResolverTask.class);

	/**
	 * 
	 * A task for computing whereUsed for all members.
	 * 
	 * @param taskData
	 *            - a model manager with members to resolve types
	 * @param handler
	 *            - results handler
	 * @param status
	 *            - a status controller that can post message and progress indicator
	 */
	public TypeResolverTask(OtmModelManager taskData, TaskResultHandlerI handler, DexStatusController status) {
		super(taskData, handler, status);

		// Replace start message from super-type.
		msgBuilder = new StringBuilder("Resolving assigned types in the model.");
		// msgBuilder.append(taskData.getName());
		// updateMessage(msgBuilder.toString());
	}

	@Override
	public void doIT() throws RepositoryException {
		// Create local copy because other tasks may update
		Collection<OtmLibraryMember> members = new ArrayList<>(taskData.getMembers());
		// For each member in the model, force a computation of where used.
		members.forEach(m -> ((OtmLibraryMemberBase<?>) m).getWhereUsed(true));
	}

}
