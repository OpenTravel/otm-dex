/**
 * 
 */
package org.opentravel.dex.events;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.model.OtmModelManager;

import javafx.event.EventType;

/**
 * OTM DEX event for signaling when a model managed has a significant change to the model. This event signals that the
 * old model is invalid and users should reload from the manager.
 * 
 * @author dmh
 *
 */
public class DexModelChangeEvent extends DexEvent {
	private static Log log = LogFactory.getLog(DexModelChangeEvent.class);
	private static final long serialVersionUID = 20190409L;

	public static final EventType<DexModelChangeEvent> MODEL_CHANGED = new EventType<>(DEX_ALL, "MODEL_CHANGED");

	private final transient OtmModelManager modelManager;

	public OtmModelManager getModelManager() {
		return modelManager;
	}

	/**
	 * Filter change event with no subject.
	 */
	public DexModelChangeEvent() {
		super(MODEL_CHANGED);
		modelManager = null;
	}

	/**
	 * @param otmLibrary
	 */
	public DexModelChangeEvent(OtmModelManager manager) {
		super(MODEL_CHANGED);
		// log.debug("DexEvent model manager constructor ran.");
		modelManager = manager;
	}

}
