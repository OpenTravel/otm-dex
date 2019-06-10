/**
 * 
 */
package org.opentravel.dex.events;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.schemacompiler.repository.RepositoryItem;

import javafx.event.EventType;

/**
 * OTM DEX event for signaling when a repository item has been replaced by a different repository item. This can happen
 * when libraries are locked.
 * 
 * @author dmh
 *
 */
public class DexRepositoryItemReplacedEvent extends DexEvent {
	private static Log log = LogFactory.getLog(DexRepositoryItemReplacedEvent.class);
	private static final long serialVersionUID = 20190606L;

	public static final EventType<DexRepositoryItemReplacedEvent> REPOSITORY_ITEM_REPLACED = new EventType<>(DEX_ALL,
			"REPOSITORY_ITEM_REPLACED");

	private final RepositoryItem oldItem;
	private final RepositoryItem newItem;

	public RepositoryItem getValue() {
		return newItem;
	}

	public RepositoryItem getNewItem() {
		return newItem;
	}

	public RepositoryItem getOldItem() {
		return oldItem;
	}

	// /**
	// * Filter change event with no subject.
	// */
	// public DexRepositoryItemReplacedEvent() {
	// super(REPOSITORY_ITEM_REPLACED);
	// repoItem = null;
	// }

	public DexRepositoryItemReplacedEvent(Object source, RepositoryItem oldItem, RepositoryItem newItem) {
		super(source, null, REPOSITORY_ITEM_REPLACED);
		log.debug("DexEvent source/target constructor ran.");

		this.oldItem = oldItem;
		this.newItem = newItem;
	}
}
