/**
 * 
 */
package org.opentravel.dex.events;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.repository.NamespacesDAO;

import javafx.event.EventType;

/**
 * OTM DEX event for signaling when a library member has been selected.
 * 
 * @author dmh
 *
 */
public class DexRepositoryNamespaceSelectionEvent extends DexEvent {
	private static Log log = LogFactory.getLog(DexRepositoryNamespaceSelectionEvent.class);
	private static final long serialVersionUID = 20190409L;

	public static final EventType<DexRepositoryNamespaceSelectionEvent> REPOSITORY_NS_SELECTED = new EventType<>(
			DEX_ALL, "REPOSITORY_NS_SELECTED");

	private final NamespacesDAO namespace;

	public NamespacesDAO getValue() {
		return namespace;
	}

	public String getNamespace() {
		return namespace.get();
	}

	/**
	 * Filter change event with no subject.
	 */
	public DexRepositoryNamespaceSelectionEvent() {
		super(REPOSITORY_NS_SELECTED);
		namespace = null;
	}

	public DexRepositoryNamespaceSelectionEvent(Object source, NamespacesDAO item) {
		super(source, null, REPOSITORY_NS_SELECTED);
		log.debug("DexEvent source/target constructor ran.");

		this.namespace = item;
	}

}
