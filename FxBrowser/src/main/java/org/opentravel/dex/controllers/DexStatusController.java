/**
 * 
 */
package org.opentravel.dex.controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.dex.repository.NamespacesDAO;
import org.opentravel.dex.repository.tasks.DexTaskBase;
import org.opentravel.model.OtmModelManager;
import org.opentravel.objecteditor.DexController;
import org.opentravel.objecteditor.DexIncludedController;

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
public class DexStatusController implements DexIncludedController<String> {
	private static Log log = LogFactory.getLog(DexStatusController.class);

	List<DexTaskBase<?>> runningTasks;
	SimpleDoubleProperty taskProgress = new SimpleDoubleProperty();

	// FXML inject
	@FXML
	private ProgressIndicator statusProgress;
	@FXML
	private Label statusLabel;

	private void checkNodes() {
		if (!(statusProgress instanceof ProgressIndicator))
			throw new IllegalStateException("Progress indicator not injected by FXML.");
		if (!(statusLabel instanceof Label))
			throw new IllegalStateException("Status label not injected by FXML.");

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
	public void setStage(Stage primaryStage) {
		checkNodes();
		statusProgress.progressProperty().bind(taskProgress);
		taskProgress.set(1.0);
		log.debug("Stage set.");
	}

	@Override
	public void postProgress(double percent) {
		if (statusProgress != null)
			if (Platform.isFxApplicationThread())
				statusProgress.setProgress(percent);
			else
				Platform.runLater(() -> postProgress(percent));
	}

	@Override
	public void postStatus(String status) {
		if (statusLabel != null)
			if (Platform.isFxApplicationThread())
				statusLabel.setText(status);
			else
				Platform.runLater(() -> postStatus(status));
	}

	/**
	 * @param dexTaskBase
	 */
	public void start(DexTaskBase<?> task) {
		runningTasks.add(task);
		update();
		postStatus("Running " + runningTasks.size() + " tasks. Current task: " + task.getMessage());
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
			updateProgress(1.0);
			// taskProgress.set(1.0);
			postStatus("Done.");
		} else
			updateProgress(-1.0);
		// taskProgress.set(-1.0);
		// if (runningTasks.size() > -1)
		// taskProgress.set(1.0 / (runningTasks.size() + 1));
		// statusProgress.setProgress(1.0 / (runningTasks.size() + 1));
	}

	private void updateProgress(double value) {
		if (Platform.isFxApplicationThread())
			taskProgress.set(value);
		else
			Platform.runLater(() -> updateProgress(value));

	}

	// @Override
	public void refresh() {
	}

	@Override
	public ImageManager getImageManager() {
		return null;
	}

	@Override
	public ReadOnlyObjectProperty<TreeItem<NamespacesDAO>> getSelectable() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return null
	 */
	@Override
	public OtmModelManager getModelManager() {
		return null;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setParent(DexController parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void post(String businessData) throws Exception {
		// TODO Auto-generated method stub

	}

}
