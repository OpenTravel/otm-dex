/**
 * 
 */
package org.opentravel.dex.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.DialogBox;
import org.opentravel.dex.controllers.dialogbox.DialogBoxContoller;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

/**
 * Manage the menu bar.
 * 
 * @author dmh
 *
 */
public class MenuBarWithProjectController extends DexIncludedControllerBase<String> {
	private static Log log = LogFactory.getLog(MenuBarWithProjectController.class);

	// FXML injected objects
	@FXML
	private ComboBox<String> projectCombo;
	@FXML
	private Label projectLabel;

	// private DialogBoxContoller dialogBoxController = null;
	private Stage stage;

	@FXML
	public void appExit(Event e) {
		log.debug("exit");
		e.consume(); // take the event away from windows in case they answer no.
		if (DialogBox.display("Exit", "Do you really want to exit?"))
			stage.close();
	}

	@FXML
	public MenuItem doCloseItem;

	public void setdoCloseHandler(EventHandler<ActionEvent> handler) {
		doCloseItem.setOnAction(handler);
	}

	@FXML
	public void doClose(ActionEvent e) {
		// This is only run if the handler is not set.
		log.debug("Close menu item selected.");
		DialogBoxContoller.init().show("Close", "Not Implemented");
	}

	@FXML
	public MenuItem fileOpenItem;

	public void setFileOpenHandler(EventHandler<ActionEvent> handler) {
		fileOpenItem.setOnAction(handler);
	}

	@FXML
	public void fileOpen(ActionEvent e) {
		// This is only run if the handler is not set.
		log.debug("File Open selected.");
		DialogBoxContoller.init().show("Open", "Not implemented");
		stage.fireEvent(new MyEvent(BEFORE_STORE, "Test"));
		// Event.fireEvent(new EventDispatcher(), null);
	}

	// https://stackoverflow.com/questions/27416758/how-to-emit-and-handle-custom-events
	//
	// On fx.nodes that hand events:
	// public final void setOnAction(EventHandler<ActionEvent> value) { onActionProperty().set(value); }
	public static final EventType<MyEvent> OPTIONS_ALL = new EventType<>("OPTIONS_ALL");
	// make sub-event-type
	public static final EventType<MyEvent> BEFORE_STORE = new EventType<>(OPTIONS_ALL, "BEFORE_STORE");

	public class MyEvent extends Event {
		private static final long serialVersionUID = 20190406L;
		private String subject;

		/**
		 * @param eventType
		 */
		public MyEvent(EventType<? extends Event> eventType) {
			super(eventType);
		}

		public MyEvent(EventType<? extends Event> eventType, String data) {
			super(eventType);
			subject = data;
			log.debug("MyEvent constructor ran. data = " + data);
		}

		public void handle() {
			log.debug("Handling event: " + getEventType() + " data = " + subject);
		}
	}

	@FXML
	public void aboutApplication(ActionEvent event) {
		AboutDialogController.createAboutDialog(stage).showAndWait();
	}

	/** *********************************************************** **/

	/**
	 * Show or hide the combo box and its label.
	 * 
	 * @param value
	 *            true to show, false to hide
	 */
	public void showCombo(boolean value) {
		projectCombo.setVisible(value);
		projectLabel.setVisible(value);
	}

	public void setComboLabel(String text) {
		projectLabel.setText(text);
	}

	/**
	 * Configure the combo box with a list and listener.
	 * <p>
	 * Usage: menuBarWithProjectController.configureProjectMenuButton(projectList, this::projectComboSelectionListener);
	 * 
	 * @param projectList
	 * @param listener
	 */
	public void configureComboBox(ObservableList<String> projectList, EventHandler<ActionEvent> listener) {
		// log.debug("Setting combo.");
		projectList.sort(null);
		projectCombo.setItems(projectList);
		projectCombo.setOnAction(listener);
	}

	@Deprecated
	@Override
	public ReadOnlyObjectProperty<String> getSelectable() {
		return projectCombo.getSelectionModel().selectedItemProperty();
		// return null;
	}

	@Override
	public void checkNodes() {
		log.debug("FXML Nodes checked OK.");
	}

	public MenuBarWithProjectController() {
		log.debug("Starting constructor.");
	}

	@Override
	@FXML
	public void initialize() {
		log.debug("Status Controller initialized.");
	}

	/**
	 * @param primaryStage
	 */
	public void setStage(Stage primaryStage) {
		checkNodes();
		log.debug("Stage set.");
		stage = primaryStage;
		// handle window and other close request events
		stage.setOnCloseRequest(this::appExit);

		stage.setEventDispatcher(new MyDispatcher(stage.getEventDispatcher()));
	}

	private class MyDispatcher implements EventDispatcher {
		private final EventDispatcher originalDispatcher;

		private MyDispatcher(EventDispatcher originalDispatcher) {
			this.originalDispatcher = originalDispatcher;
		}

		@Override
		public Event dispatchEvent(Event event, EventDispatchChain tail) {
			if (event instanceof MyEvent) {
				log.debug("Using my dispatcher on my event");
				// event.consume();
				((MyEvent) event).handle();
				return event;
			}

			return originalDispatcher.dispatchEvent(event, tail);
		}
	}

	@Override
	public void clear() {
	}

}
