/**
 * 
 */
package org.opentravel.dex.controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.repository.NamespacesDAO;
import org.opentravel.dex.repository.tasks.DexTaskBase;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;

/**
 * Manage the status bar containing a label and progress indicator.
 * 
 * @author dmh
 *
 */
public class DexStatusController extends DexIncludedControllerBase<String> {
	// public class DexStatusController implements DexIncludedController<String> {
	private static Log log = LogFactory.getLog(DexStatusController.class);

	List<DexTaskBase<?>> runningTasks;
	SimpleDoubleProperty taskProgress = new SimpleDoubleProperty();

	// FXML inject
	@FXML
	private ProgressIndicator statusProgress;
	@FXML
	private Label statusLabel;
	@FXML
	private Label taskCount;

	@Override
	public void checkNodes() {
		if (!(statusProgress instanceof ProgressIndicator))
			throw new IllegalStateException("Progress indicator not injected by FXML.");
		if (!(statusLabel instanceof Label))
			throw new IllegalStateException("Status label not injected by FXML.");
		if (!(taskCount instanceof Label))
			throw new IllegalStateException("Task count not injected by FXML.");

		log.debug("FXML Nodes checked OK.");
	}

	public DexStatusController() {
		log.debug("Starting constructor.");
	}

	@Override
	@FXML
	public void initialize() {
		log.debug("Status Controller initialized.");

		if (runningTasks == null)
			runningTasks = new ArrayList<>();
	}

	/**
	 * @param primaryStage
	 */
	@SuppressWarnings("squid:S1172")
	public void setStage(Stage primaryStage) {
		checkNodes();
		// FIXME - should progress be set when there are no tasks?
		statusProgress.progressProperty().bind(taskProgress);
		taskProgress.set(1.0);
		log.debug("Stage set.");
	}

	public void postProgress(double percent) {
		if (statusProgress != null)
			updateProgress(percent);
		// if (Platform.isFxApplicationThread())
		// statusProgress.setProgress(percent);
		// else
		// Platform.runLater(() -> postProgress(percent));
	}

	public void postStatus(String status) {
		if (statusLabel != null)
			if (Platform.isFxApplicationThread())
				statusLabel.setText(status);
			else
				Platform.runLater(() -> postStatus(status));
	}

	public void postStatus(int count, String status) {
		if (statusLabel != null)
			if (Platform.isFxApplicationThread()) {
				statusLabel.setText(status);
				taskCount.setText(String.valueOf(count));
			} else
				Platform.runLater(() -> postStatus(count, status));
	}

	/**
	 * @param dexTaskBase
	 */
	public void start(DexTaskBase<?> task) {
		runningTasks.add(task);
		update();
		postStatus(runningTasks.size(), "Running: " + task.getMessage());
		// postStatus("Running " + runningTasks.size() + " tasks. Current task: " + task.getMessage());
	}

	/**
	 * Remove the task from list of running tasks.
	 * 
	 * @param dexTaskBase
	 */
	public void finish(DexTaskBase<?> task) {
		runningTasks.remove(task);
		update();
	}

	private void update() {
		postStatus("Running " + runningTasks.size() + " tasks.");
		if (runningTasks.isEmpty()) {
			updateProgress(1F);
			// taskProgress.set(1.0);
			postStatus(0, "Done.");
		} else {
			updateProgress(-1.0);
			postStatus(runningTasks.size(), "Running: " + runningTasks.get(runningTasks.size() - 1).getMessage());
		}
	}

	private void updateProgress(double value) {
		if (Platform.isFxApplicationThread())
			taskProgress.set(value);
		else
			Platform.runLater(() -> updateProgress(value));

	}

	@Override
	public ReadOnlyObjectProperty<TreeItem<NamespacesDAO>> getSelectable() {
		return null;
	}

}
