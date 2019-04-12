/**
 * 
 */
package org.opentravel.dex.events;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.schemacompiler.repository.Repository;

import javafx.event.EventType;

/**
 * OTM DEX event for signaling.
 * 
 * @author dmh
 *
 */
public class DexRepositorySelectionEvent extends DexEvent {
	private static Log log = LogFactory.getLog(DexRepositorySelectionEvent.class);
	private static final long serialVersionUID = 20190409L;

	public static final EventType<DexRepositorySelectionEvent> REPOSITORY_SELECTED = new EventType<>(DEX_ALL,
			"REPOSITORY_SELECTED");

	private final Repository repository;

	public Repository getRepository() {
		return repository;
	}

	/**
	 * Filter change event with no subject.
	 */
	public DexRepositorySelectionEvent() {
		super(REPOSITORY_SELECTED);
		repository = null;
	}

	/**
	 */
	public DexRepositorySelectionEvent(Repository repository) {
		super(REPOSITORY_SELECTED);
		log.debug("DexEvent source/target constructor ran.");
		this.repository = repository;
	}
}
