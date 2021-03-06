/**
 * 
 */
package org.opentravel.dex.controllers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.dex.events.DexEvent;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableColumn;

/**
 * Abstract base controller for included controllers.
 * <p>
 * This controller exposes the collection members of the posted business object. The generic type is the Collection
 * containing business data object used when "posting" to this controller.
 * 
 * @author dmh
 *
 */
public abstract class DexIncludedControllerBase<C> implements DexIncludedController<C> {
	private static Log log = LogFactory.getLog(DexIncludedControllerBase.class);

	protected ImageManager imageMgr;
	protected DexMainController mainController;
	protected C postedData;

	protected List<EventType> publishedEventTypes = null;
	protected List<EventType> subscribedEventTypes = null;
	// source FX Node for events fired from this controller. None fired if null.
	protected Node eventPublisherNode = null;

	public DexIncludedControllerBase() {
		log.debug("Constructing included controller.");
	}

	@Override
	public DexMainController getMainController() {
		return mainController;
	}

	public DexIncludedControllerBase(EventType[] subscribed) {
		log.debug("Constructing included controller with subscribed event types.");
		if (subscribed != null && subscribed.length > 0)
			subscribedEventTypes = Collections.unmodifiableList(Arrays.asList(subscribed));
	}

	/**
	 * Constructor for controller included by FXML into another controller.
	 * 
	 * @param subscribed
	 *            event types handled by this controller. Used by main controller to register handlers.
	 * @param published
	 *            event types fired by this controller. Used by main controller to register handlers.
	 */
	public DexIncludedControllerBase(EventType[] subscribed, EventType[] published) {
		this(subscribed);
		log.debug("Constructing included controller with subscribed and published event types.");
		if (published != null && published.length > 0)
			publishedEventTypes = Collections.unmodifiableList(Arrays.asList(published));
	}

	@Override
	// @SuppressWarnings("rawtypes")
	public List<EventType> getPublishedEventTypes() {
		return publishedEventTypes != null ? publishedEventTypes : Collections.emptyList();
	}

	@Override
	// @SuppressWarnings("rawtypes")
	public List<EventType> getSubscribedEventTypes() {
		return subscribedEventTypes != null ? subscribedEventTypes : Collections.emptyList();
	}

	@Override
	public void handleEvent(Event e) {
		// override
	}

	@Override
	public void setEventHandler(EventType<? extends DexEvent> type, EventHandler<DexEvent> handler) {
		if (eventPublisherNode != null && publishedEventTypes.contains(type)) {
			eventPublisherNode.addEventHandler(type, handler);
			log.debug("Event handler set: " + type.getName() + " " + handler.getClass().getName());
		} else
			log.warn("Publisher node not set or unhandled event type attempted to have handler set.");
	}

	@Override
	public void clear() {
	}

	@Override
	public void configure(DexMainController parent) {
		checkNodes();
		this.mainController = parent;
		imageMgr = parent.getImageManager();
		log.debug("Main controller set.");
	}

	@Override
	public void fireEvent(DexEvent event) {
		if (publishedEventTypes != null && publishedEventTypes.contains(event.getEventType()))
			eventPublisherNode.fireEvent(event);
		else
			log.warn(event.getEventType() + "event not fired.");
	}

	@FXML
	@Override
	public void initialize() {
		log.debug("Initializing controller.");
	}

	@Override
	public void post(C businessData) throws Exception {
		// Clear the view
		clear(); // no-op unless overridden
		// Hold onto data
		postedData = businessData;
		// FUTURE - create navigation event
	}

	@Override
	public void publishEvent(DexEvent event) {
		eventPublisherNode.fireEvent(event);
	}

	@Override
	public void refresh() {
		try {
			post(postedData);
		} catch (Exception e) {
			log.error("Unhandled error refreshing repository item commit history: " + e.getLocalizedMessage());
		}
	}

	@Override
	public void select(Object selector) {
		// Override if supported.
	}

	/**
	 * Utility to set table column properties.
	 */
	protected void setColumnProps(TableColumn<?, ?> c, boolean visable, boolean editable, boolean sortable, int width) {
		c.setVisible(visable);
		c.setEditable(editable);
		c.setSortable(sortable);
		if (width > 0)
			c.setPrefWidth(width);
	}

	/**
	 * Utility to set tree table column properties.
	 */
	protected void setColumnProps(TreeTableColumn<?, ?> c, boolean visable, boolean editable, boolean sortable,
			int width) {
		c.setVisible(visable);
		c.setEditable(editable);
		c.setSortable(sortable);
		if (width > 0)
			c.setPrefWidth(width);
	}

	/**
	 * TODO
	 */
	protected void setWidths(TableView table) {
		// Give all left over space to the last column
		// double width = fileCol.widthProperty().get();
		// width += versionCol.widthProperty().get();
		// width += statusCol.widthProperty().get();
		// width += lockedCol.widthProperty().get();
		// remarkCol.prefWidthProperty().bind(table.widthProperty().subtract(width));
	}

}
