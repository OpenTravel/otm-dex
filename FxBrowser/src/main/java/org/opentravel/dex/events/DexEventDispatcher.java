/**
 * 
 */
package org.opentravel.dex.events;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;

/**
 *  OTM DEX event dispatcher.
 * @author dmh
 *
 */
/**
 * Useful for debugging, this is a Dispatcher that simply logs when an DexEvent is received. All events are then passed
 * to the original dispatcher.
 * 
 * @author dmh
 *
 */
public class DexEventDispatcher implements EventDispatcher {
	private static Log log = LogFactory.getLog(DexEventDispatcher.class);
	private final EventDispatcher originalDispatcher;

	public DexEventDispatcher(EventDispatcher originalDispatcher) {
		this.originalDispatcher = originalDispatcher;
	}

	@Override
	public Event dispatchEvent(Event event, EventDispatchChain tail) {
		if (event instanceof DexEvent) {
			log.debug("Using my dispatcher on my event");
			// Add code here if the event is to be handled outside of the dispatch chain
			// event.consume();
			// some event filter and business logic ...
			// return event;
		}
		return originalDispatcher.dispatchEvent(event, tail);
	}
}
