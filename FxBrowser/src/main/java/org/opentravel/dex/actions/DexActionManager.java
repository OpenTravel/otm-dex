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
import org.opentravel.model.OtmModelElement;
import org.opentravel.schemacompiler.validate.ValidationFindings;

import javafx.beans.value.ObservableValue;

/**
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
		NAMECHANGE, DESCRIPTIONCHANGE
	};

	public boolean addAction(DexActions action, ObservableValue<? extends String> op, OtmModelElement<?> subject) {
		// Make sure the action can register itself and access main controller
		if (subject.getActionManager() == null)
			throw new IllegalStateException("Subject of an action must provide access to action manger.");

		switch (action) {
		case NAMECHANGE:
			// op.addListener((ObservableValue<? extends String> o, String old,
			// String newVal) -> new NameChangeAction(subject).doIt(o, old, newVal));
			op.addListener((ObservableValue<? extends String> o, String old,
					String newVal) -> doString(new NameChangeAction(subject), o, old, newVal));
			break;
		case DESCRIPTIONCHANGE:
			op.addListener((ObservableValue<? extends String> o, String old,
					String newVal) -> new DescriptionChangeAction(subject).doIt(o, old, newVal));
			break;
		}
		return false;
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

	public void push(DexAction<?> action) {
		// TODO - make sure not a duplicate
		if (!action.getVetoFindings().isEmpty()) {
			// TODO - create validation findings controller and post it
			// TODO - allow undo option in dialog.
			ValidationFindings findings = action.getVetoFindings();
			String msg = "Can not make change.\n" + ValidationUtils.getMessagesAsString(findings);
			mainController.postError(null, msg);
			ignore = true;
			action.undo();
			ignore = false;
		} else {
			queue.push(action);
			mainController.updateActionQueueSize(getQueueSize());
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
		log.debug("TODO undo action");
		if (!queue.isEmpty()) {
			DexAction<?> action = queue.pop();
			action.undo();
			mainController.updateActionQueueSize(getQueueSize());
		}
		ignore = false;
	}
}
