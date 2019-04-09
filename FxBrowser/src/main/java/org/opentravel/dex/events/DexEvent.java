/**
 * 
 */
package org.opentravel.dex.events;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.objecteditor.modelMembers.MemberFilterController;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 * Abstract OTM DEX event.
 * <p>
 * DexEvents leverage and extend JavaFX event architecture to provide the ability to loosely couple the interaction
 * between two or more controllers.
 * <p>
 * Implementation steps:
 * <ol>
 * <li>Create sub-type of DexEvent (if needed)
 * <li>Add setHandler() method in controller. Use underlying pane as fx:node to broadcast events.
 * <li>Add node.fireEvent() in event provider controllers where needed.
 * <li>Add logic to register handlers in event consumers.
 * <li>Add handler business logic.
 * </ul>
 * <p>
 * 
 * @see MemberFilterController MemberFilterController for example of a provider.
 *      <p>
 * @see https://stackoverflow.com/questions/27416758/how-to-emit-and-handle-custom-events
 * @author dmh
 *
 */
public abstract class DexEvent extends Event {
	private static Log log = LogFactory.getLog(DexEvent.class);
	private static final long serialVersionUID = 20190406L;
	public static final EventType<DexEvent> DEX_ALL = new EventType<>("DEX_ALL");

	@SuppressWarnings("unchecked")
	@Override
	public EventType<? extends DexEvent> getEventType() {
		return (EventType<? extends DexEvent>) super.getEventType();
	}

	/**
	 * Filter change event with no subject.
	 */
	public DexEvent() {
		super(DEX_ALL);
	}

	/**
	 * @param eventType
	 */
	public DexEvent(EventType<? extends DexEvent> eventType) {
		super(eventType);
	}

	public DexEvent(Object source, EventTarget target, EventType<? extends DexEvent> eventType) {
		super(source, target, eventType);
		log.debug("DexEvent source/target/type constructor ran.");
		// If there is data, extract it from source or target here
	}

	public DexEvent(Object source, EventTarget target) {
		super(source, target, DEX_ALL);
		log.debug("DexEvent source/target constructor ran.");
		// If there is data, extract it from source or target here
	}
}
