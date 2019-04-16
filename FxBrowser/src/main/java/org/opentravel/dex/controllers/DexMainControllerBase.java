/**
 * 
 */
package org.opentravel.dex.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.application.common.AbstractMainWindowController;
import org.opentravel.application.common.StatusType;
import org.opentravel.common.ImageManager;
import org.opentravel.dex.actions.DexActionManager;
import org.opentravel.dex.controllers.dialogbox.DialogBoxContoller;
import org.opentravel.dex.events.DexEvent;
import org.opentravel.model.OtmModelManager;
import org.opentravel.schemacompiler.repository.RepositoryManager;

import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * Abstract base controller for main controllers.
 * 
 * @author dmh
 *
 */
public abstract class DexMainControllerBase extends AbstractMainWindowController implements DexMainController {
	private static Log log = LogFactory.getLog(DexMainControllerBase.class);

	protected DexMainController mainController;
	protected ImageManager imageMgr;
	protected OtmModelManager modelMgr;
	protected DexActionManager actionMgr;

	private List<DexIncludedController<?>> includedControllers = new ArrayList<>();
	private Map<EventType<?>, List<DexIncludedController<?>>> eventPublishers = new HashMap<>();
	private Map<EventType<?>, List<DexIncludedController<?>>> eventSubscribers = new HashMap<>();

	protected DexStatusController statusController;
	protected DialogBoxContoller dialogBoxController;

	protected Stage stage;

	public DexMainControllerBase() {
		log.debug("Constructing controller.");
	}

	@Override
	public void addIncludedController(DexIncludedController<?> controller) {
		if (controller == null)
			throw new IllegalStateException("Tried to add null Included controller");

		controller.checkNodes();
		includedControllers.add(controller);
		controller.configure(this);

		// Register any published event types
		for (EventType et : controller.getPublishedEventTypes())
			if (eventPublishers.containsKey(et)) {
				eventPublishers.get(et).add(controller);
			} else {
				ArrayList<DexIncludedController<?>> list = new ArrayList<>();
				list.add(controller);
				eventPublishers.put(et, list);
			}
		// Register any subscribed event types
		for (EventType et : controller.getSubscribedEventTypes())
			if (eventSubscribers.containsKey(et)) {
				eventSubscribers.get(et).add(controller);
			} else {
				ArrayList<DexIncludedController<?>> list = new ArrayList<>();
				list.add(controller);
				eventSubscribers.put(et, list);
			}
	}

	@Override
	public void clear() {
		includedControllers.forEach(DexIncludedController::clear);
	}

	/**
	 * Use the subscribers and publishers maps to set handlers
	 */
	// TODO - do i need subscriber map? Can i just traverse all included controllers?
	protected void configureEventHandlersX() {
		if (!eventSubscribers.isEmpty())
			for (Entry<EventType<?>, List<DexIncludedController<?>>> entry : eventSubscribers.entrySet())
				// For each subscriber to this event type
				for (DexIncludedController<?> c : entry.getValue()) {
					// Get the handler from the subscriber
					EventType<? extends DexEvent> et = (EventType<? extends DexEvent>) entry.getKey();
					// EventHandler<DexEvent> handler = c::handler;
					if (eventPublishers.containsKey(entry.getValue()))
						for (DexIncludedController<?> publisher : eventPublishers.get(entry.getValue()))
							// Put handler in all publishers of this event
							publisher.setEventHandler(et, c::handleEvent);
				}
	}

	@SuppressWarnings("unchecked")
	protected void configureEventHandlers() {
		for (DexIncludedController<?> c : includedControllers) {
			List<EventType> subscriptions = c.getSubscribedEventTypes();
			if (subscriptions != null && !subscriptions.isEmpty())
				for (EventType et : subscriptions)
					if (eventPublishers.containsKey(et)) {
						List<DexIncludedController<?>> publishers = eventPublishers.get(et);
						EventType<? extends DexEvent> dexET = et;
						for (DexIncludedController<?> publisher : publishers)
							publisher.setEventHandler(dexET, c::handleEvent);
					}
		}
		// if (!eventSubscribers.isEmpty())
		// for (Entry<EventType<?>, List<DexIncludedController<?>>> entry : eventSubscribers.entrySet())
		// // For each subscriber to this event type
		// for (DexIncludedController<?> c : entry.getValue()) {
		// // Get the handler from the subscriber
		// EventType<? extends DexEvent> et = (EventType<? extends DexEvent>) entry.getKey();
		// // EventHandler<DexEvent> handler = c::handler;
		// if (eventPublishers.containsKey(entry.getValue()))
		// for (DexIncludedController<?> publisher : eventPublishers.get(entry.getValue()))
		// // Put handler in all publishers of this event
		// publisher.setEventHandler(et, c::eventHandler);
		// }
	}

	public DialogBoxContoller getDialogBoxController() {
		if (dialogBoxController == null)
			dialogBoxController = DialogBoxContoller.init();
		return dialogBoxController;
	}

	@Override
	public ImageManager getImageManager() {
		if (imageMgr != null)
			return imageMgr;
		return mainController != null ? mainController.getImageManager() : null;
	}

	@Override
	public OtmModelManager getModelManager() {
		if (modelMgr != null)
			return modelMgr;
		return mainController != null ? mainController.getModelManager() : null;
	}

	@Override
	public RepositoryManager getRepositoryManager() {
		return null;
	}

	@Override
	public Stage getStage() {
		return stage;
	}

	// @Override
	// public ReadOnlyObjectProperty<?> getSelectable() {
	// return null;
	// }

	@Override
	public DexStatusController getStatusController() {
		if (statusController != null)
			return statusController;
		return mainController != null ? mainController.getStatusController() : null;
	}

	@FXML
	@Override
	public void initialize() {
		log.debug("Initializing controller: " + this.getClass().getSimpleName());
	}

	@Override
	public void postError(Exception e, String title) {
		if (getDialogBoxController() != null)
			if (e == null)
				getDialogBoxController().show("", title);
			else {
				log.debug(title + e.getLocalizedMessage());
				if (e.getCause() == null)
					getDialogBoxController().show(title, e.getLocalizedMessage());
				else
					getDialogBoxController().show(title,
							e.getLocalizedMessage() + " \n\n(" + e.getCause().toString() + ")");
			}
		else
			log.debug("Missing dialog box to show: " + title);
	}

	@Override
	public void postProgress(double percentDone) {
		if (getStatusController() != null)
			getStatusController().postProgress(percentDone);
	}

	@Override
	public void postStatus(String string) {
		if (getStatusController() != null)
			getStatusController().postStatus(string);
	}

	@Override
	public void refresh() {
		includedControllers.forEach(DexIncludedController::refresh);
	}

	/**
	 * Set the stage for a top level main controller. Called by the application on startup.
	 * 
	 * @param primaryStage
	 */
	public void setStage(Stage primaryStage) {
		// These may be needed by sub-controllers
		this.stage = primaryStage;
		this.mainController = null;

		// Initialize managers
		actionMgr = new DexActionManager(this);
		modelMgr = new OtmModelManager(actionMgr);
		imageMgr = new ImageManager(primaryStage);

		checkNodes();
	}

	/**
	 * Create a main controller that has a main controller parent.
	 * 
	 * @param parent
	 */
	public void setParent(DexMainController parent) {
		this.stage = parent.getStage();
		this.mainController = parent;
		if (mainController.getImageManager() == null)
			imageMgr = new ImageManager(stage);
	}

	// Required by AbstractApp...
	@Override
	protected void setStatusMessage(String message, StatusType statusType, boolean disableControls) {
		if (getStatusController() != null)
			getStatusController().postStatus(message);
	}

	@Override
	protected void updateControlStates() {
		// Platform.runLater(() -> {
		// // boolean exDisplayDisabled = (originalDocument == null);
		// // boolean exControlsDisabled = (model == null) || (originalDocument == null);
		// //
		// // libraryText.setText( (modelFile == null) ? "" : modelFile.getName() );
		// // libraryTooltip.setText( (modelFile == null) ? "" : modelFile.getAbsolutePath() );
		// // exampleText.setText( (exampleFile == null) ? "" : exampleFile.getName() );
		// // exampleTooltip.setText( (exampleFile == null) ? "" : exampleFile.getAbsolutePath() );
	}

}
