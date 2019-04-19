/**
 * 
 */
package org.opentravel.dex.actions;

import java.util.ArrayDeque;
import java.util.Deque;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ValidationUtils;
import org.opentravel.dex.controllers.DexMainController;
import org.opentravel.dex.controllers.member.properties.PropertiesDAO;
import org.opentravel.model.OtmModelElement;
import org.opentravel.schemacompiler.validate.ValidationFindings;

import javafx.beans.value.ObservableValue;

/**
 * Default action manager used by OTM elements to determine what actions are available and how to execute them.
 * <p>
 * Controls and manages actions. Maintains queue of past actions and creates new actions. Notifies user of performed
 * action status.
 * 
 * @author dmh
 *
 */
public class DexActionManager {
	private static Log log = LogFactory.getLog(DexActionManager.class);

	// Controller for accessing GUI controls
	DexMainController mainController = null;
	Deque<DexAction<?>> queue;

	private boolean ignore;

	public enum DexActions {
		NAMECHANGE, DESCRIPTIONCHANGE, TYPECHANGE
	}

	/**
	 * Actions available for OTM Properties wrapped by PropertiesDAO.
	 * 
	 * @param action
	 * @param subject
	 */
	public void addAction(DexActions action, PropertiesDAO subject) {
		switch (action) {
		case TYPECHANGE:
			ignore = true; // may fire a name change
			new AssignedTypeChangeAction(subject).doIt();
			ignore = false;
			break;
		default:
		}
	}

	/**
	 * Triggering of actions on observable properties is delegated to the observable via its listener.
	 * 
	 * @param action
	 * @param op
	 * @param subject
	 * @return
	 */
	public boolean addAction(DexActions action, ObservableValue<? extends String> op, OtmModelElement<?> subject) {
		// Make sure the action can register itself and access main controller
		if (subject.getActionManager() == null)
			throw new IllegalStateException("Subject of an action must provide access to action manger.");

		switch (action) {
		case NAMECHANGE:
			op.addListener((ObservableValue<? extends String> o, String old,
					String newVal) -> doString(new NameChangeAction(subject), o, old, newVal));
			break;
		case DESCRIPTIONCHANGE:
			op.addListener((ObservableValue<? extends String> o, String old,
					String newVal) -> doString(new DescriptionChangeAction(subject), o, old, newVal));
			break;
		default:
			return false;
		}
		return true;
	}

	public void doString(DexStringAction action, ObservableValue<? extends String> o, String oldName, String name) {
		if (!ignore) {
			ignore = true;
			action.doIt(o, oldName, name);
			ignore = false;
		}
	}

	public DexStringAction stringActionFactory(DexActions action, OtmModelElement<?> subject) {
		// Make sure the action can register itself and access main controller
		if (subject.getActionManager() == null)
			throw new IllegalStateException("Subject of an action must provide access to action manger.");

		DexStringAction a = null;
		switch (action) {
		case NAMECHANGE:
			a = new NameChangeAction(subject);
			break;
		case DESCRIPTIONCHANGE:
			a = new DescriptionChangeAction(subject);
			break;
		default:
			log.debug("Unknown action: " + action.toString());
		}
		return a;
	}

	public DexActionManager(DexMainController mainController) {
		this.mainController = mainController;
		queue = new ArrayDeque<>();
	}

	public DexMainController getMainController() {
		return mainController;
	}

	/**
	 * Record action to allow undo. Will validate results and warn user on errors. Veto'ed actions will not be pushed
	 * onto the queue.
	 * 
	 * @param action
	 */
	public void push(DexAction<?> action) {
		if (queue.contains(action)) {
			// TEST - make sure not a duplicate
			log.debug("Duplicate Action found!");
			return;
		}
		if (action.getVetoFindings() != null && !action.getVetoFindings().isEmpty()) {
			// Warn the user of the errors and back out the changes
			ValidationFindings findings = action.getVetoFindings();
			String msg = "Can not make change.\n" + ValidationUtils.getMessagesAsString(findings);
			mainController.postError(null, msg);
			ignore = true;
			action.undo();
			ignore = false;
			// TODO - if warnings, post them and allow undo option in dialog.
		} else {
			queue.push(action);
			mainController.updateActionQueueSize(getQueueSize());
			mainController.postStatus("Performed action: " + action.toString());
			log.debug("Put action on queue: " + action.getClass().getSimpleName());
		}
	}

	public void postWarning(String warning) {
		mainController.postError(null, warning);

	}

	public int getQueueSize() {
		return queue.size();
	}

	public String getLastActionName() {
		return queue.peek() != null ? queue.peek().getClass().getSimpleName() : "";
	}

	/**
	 * Pop an action from the queue and then undo it.
	 */
	public void undo() {
		ignore = true;
		if (!queue.isEmpty()) {
			DexAction<?> action = queue.pop();
			log.debug("Undo action: " + action.getClass().getSimpleName());
			action.undo();
			mainController.updateActionQueueSize(getQueueSize());
			mainController.postStatus("Undid action: " + action.toString());
		}
		ignore = false;
	}
}
