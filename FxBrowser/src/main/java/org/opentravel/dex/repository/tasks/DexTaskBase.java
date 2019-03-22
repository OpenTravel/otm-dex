/**
 * 
 */
package org.opentravel.dex.repository.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.repository.ResultHandlerI;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;

/**
 * OTM-DE-JavaFX task base class.
 * 
 * @author dmh
 *
 */
public abstract class DexTaskBase<T> extends Task<String> {
	private static Log log = LogFactory.getLog(DexTaskBase.class);

	protected T taskData;

	protected String startMsg = "Begin: ";
	private Double progress = -0.25;
	private Double progressMax = 1.0;
	private StringBuilder errorBuilder = null;
	protected StringBuilder msgBuilder = null;

	/**
	 * Create a task complete with result handler, double progress value and status
	 * 
	 * @param taskData
	 *            - ALL data needed to execute the task
	 * @param progressProperty
	 *            - progress bar or indicator progress property
	 * @param statusProperty
	 *            - a label or stringProperty for messages from the task
	 * @param handler
	 *            - handler to receive completion message. Must have controller for accessing the stage.
	 */
	public DexTaskBase(T taskData, DoubleProperty progressProperty, StringProperty statusProperty,
			ResultHandlerI handler) {
		this(taskData);

		// Bind the passed progress bar/indicator and status properties to this task's properties.
		if (progressProperty != null)
			progressProperty.bind(this.progressProperty());
		if (statusProperty != null)
			statusProperty.bind(this.messageProperty());

		// Set the result handler
		if (handler != null) {
			setOnSucceeded(handler::handle);
			setOnFailed(handler::handle);
		}
	}

	public DexTaskBase(T taskData) {
		this.taskData = taskData;

		msgBuilder = new StringBuilder(startMsg);

		updateMessage(msgBuilder.toString());
		updateProgress(progress, progressMax);
	}

	/**
	 * Execute this task in a background thread. Suitable for use in GUI thread. Creates thread, sets as daemon thread
	 * to all JVM to exit if thread hangs, then starts the thread.
	 * 
	 */
	public void go() {
		Thread lt = new Thread(this);
		lt.setDaemon(true);
		lt.start();
	}

	/**
	 * The actual task written as if it was going to run in the GUI thread.
	 */
	public abstract void doIT() throws Exception;

	@Override
	protected String call() throws Exception {
		log.debug("Starting Task.");
		String result = null; // Null result implies success
		if (taskData != null)
			try {
				doIT();
				updateMessage("Done.");
			} catch (Exception e) {
				errorBuilder = new StringBuilder("Error: ");
				errorBuilder.append(e.getLocalizedMessage());
				result = errorBuilder.toString(); // Signal business error via result
				failed();
				log.debug(errorBuilder.toString());
			}

		updateProgress(progressMax, progressMax);
		log.debug(" Task done. ");
		return result;
	}

	@Override
	protected void failed() {
		super.failed();
		updateMessage("Failed!");
	}

	public String getErrorMsg() {
		return errorBuilder != null ? errorBuilder.toString() : null;
	}

}
