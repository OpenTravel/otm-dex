/**
 * 
 */
package org.opentravel.dex.events;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.controllers.library.LibraryDAO;
import org.opentravel.model.otmContainers.OtmLibrary;

import javafx.event.EventType;
import javafx.scene.control.TreeItem;

/**
 * OTM DEX event for signaling when a library member has been selected.
 * 
 * @author dmh
 *
 */
public class DexLibrarySelectionEvent extends DexEvent {
	private static Log log = LogFactory.getLog(DexLibrarySelectionEvent.class);
	private static final long serialVersionUID = 20190409L;

	public static final EventType<DexLibrarySelectionEvent> LIBRARY_SELECTED = new EventType<>(DEX_ALL,
			"LIBRARY_SELECTED");

	private final transient OtmLibrary library;

	public OtmLibrary getLibrary() {
		return library;
	}

	/**
	 * Filter change event with no subject.
	 */
	public DexLibrarySelectionEvent() {
		super(LIBRARY_SELECTED);
		library = null;
	}

	/**
	 * A library member selection event.
	 * 
	 * @param source
	 *            is the controller that created the event
	 * @param target
	 *            the tree item that was selected
	 */
	public DexLibrarySelectionEvent(Object source, TreeItem<LibraryDAO> target) {
		super(source, target, LIBRARY_SELECTED);
		// log.debug("DexEvent source/target constructor ran.");

		// If there is data, extract it from target
		if (target != null && target.getValue() != null && target.getValue().getValue() != null
				&& target.getValue().getValue() instanceof OtmLibrary)
			library = target.getValue().getValue();
		else
			library = null;
	}

	/**
	 * @param otmLibrary
	 */
	public DexLibrarySelectionEvent(OtmLibrary target) {
		super(LIBRARY_SELECTED);
		// log.debug("DexEvent target constructor ran.");
		library = target;
	}

}
