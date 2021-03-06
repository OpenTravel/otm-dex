/**
 * 
 */
package org.opentravel.dex.events;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.repository.RepoItemDAO;

import javafx.event.EventType;

/**
 * OTM DEX event for signaling when a library member has been selected.
 * 
 * @author dmh
 *
 */
public class DexRepositoryItemSelectionEvent extends DexEvent {
	private static Log log = LogFactory.getLog(DexRepositoryItemSelectionEvent.class);
	private static final long serialVersionUID = 20190409L;

	public static final EventType<DexRepositoryItemSelectionEvent> REPOSITORY_ITEM_SELECTED = new EventType<>(DEX_ALL,
			"REPOSITORY_ITEM_SELECTED");

	private final RepoItemDAO repoItem;

	public RepoItemDAO getValue() {
		return repoItem;
	}

	/**
	 * Filter change event with no subject.
	 */
	public DexRepositoryItemSelectionEvent() {
		super(REPOSITORY_ITEM_SELECTED);
		repoItem = null;
	}

	// /**
	// * A library member selection event.
	// *
	// * @param source
	// * is the controller that created the event
	// * @param target
	// * the tree item that was selected
	// */
	public DexRepositoryItemSelectionEvent(Object source, RepoItemDAO item) {
		super(source, null, REPOSITORY_ITEM_SELECTED);
		log.debug("DexEvent source/target constructor ran.");

		this.repoItem = item;
	}

	// /**
	// */
	// public DexRepositoryNamespaceSelectionEvent(Repository repository) {
	// super(REPOSITORY_ITEM_SELECTED);
	// log.debug("DexEvent source/target constructor ran.");
	// this.repository = repository;
	// }

}
