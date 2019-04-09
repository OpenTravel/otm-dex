/**
 * 
 */
package org.opentravel.dex.events;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 * OTM DEX event for signaling when a filter controller setting has changed.
 * 
 * @author dmh
 *
 */
public class DexFilterChangeEvent extends DexEvent {
	private static Log log = LogFactory.getLog(DexFilterChangeEvent.class);
	private static final long serialVersionUID = 20190409L;

	public static final EventType<DexFilterChangeEvent> FILTER_CHANGED = new EventType<>(DEX_ALL, "FILTER_CHANGED");

	/**
	 * Filter change event with no subject.
	 */
	public DexFilterChangeEvent() {
		super(FILTER_CHANGED);
	}

	public DexFilterChangeEvent(Object source, EventTarget target) {
		super(source, target, FILTER_CHANGED);
		log.debug("DexEvent source/target constructor ran.");
		// If there is data, extract it from source or target here
	}

}
