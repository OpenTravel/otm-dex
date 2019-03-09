/**
 * 
 */
package org.opentravel.objecteditor.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.objecteditor.DexDAO;
import org.opentravel.schemacompiler.repository.RepositoryException;
import org.opentravel.schemacompiler.repository.RepositoryItem;
import org.opentravel.schemacompiler.repository.RepositoryItemHistory;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.ImageView;

/**
 * Controller for a libraries in a namespace tree table view. Creates table containing repository item properties.
 * <p>
 * This class is designed to be injected into a parent controller by FXML loader. It has a VBOX containing the label
 * header and a tree table view.
 * 
 * @author dmh
 *
 */
public class RepoItemDAO implements DexDAO<RepositoryItem> {
	private static Log log = LogFactory.getLog(RepoItemDAO.class);

	protected RepositoryItem repoItem;
	SimpleStringProperty lastHistory = new SimpleStringProperty(":> working...");
	Double historyProgess = 1.0;

	RepositoryItemHistory history = null;

	public RepoItemDAO(RepositoryItem item) {
		this.repoItem = item;

		// Task<Void> task = new Task<Void>() {
		// @Override
		// protected Void call() throws Exception {
		// historyProgess = 0.1;
		// getHistory();
		// updateMessage("Retrieving history.");
		// updateProgress(historyProgess, 1);
		// return null;
		// }
		// };

		// Don't wait for history to be loaded.
		// Load histories from repository in the background.
		Runnable task = new Runnable() {
			@Override
			public void run() {
				getHistory();
			}
		};
		// Run the task in a background thread
		Thread backgroundThread = new Thread(task);
		// Terminate the running thread if the application exits
		backgroundThread.setDaemon(true);
		// Start the thread
		backgroundThread.start();
	}

	public StringProperty libraryNameProperty() {
		return new SimpleStringProperty(repoItem.getLibraryName());
	}

	public StringProperty versionProperty() {
		return new SimpleStringProperty(repoItem.getVersion());
	}

	public StringProperty statusProperty() {
		return new SimpleStringProperty(repoItem.getStatus().toString());
	}

	public StringProperty lockedProperty() {
		return new SimpleStringProperty(repoItem.getLockedByUser());
	}

	public StringProperty historyProperty() {
		return lastHistory;
	}

	public void setHistory() {
		if (history == null)
			return;
		historyProgess = 1.0;
		StringBuilder remark = new StringBuilder(history.getCommitHistory().get(0).getUser());
		remark.append(" - ");
		remark.append(history.getCommitHistory().get(0).getRemarks());
		lastHistory.set(remark.toString());
		log.debug("History set: " + remark.toString());
	}

	// public DoubleProperty historyTask() {
	// return new SimpleDoubleProperty(0.5);
	// // return new SimpleDoubleProperty(historyProgess);
	// }

	/**
	 * Background thread ready getter for the history of this repository item.
	 * 
	 * @param repoItem
	 * @param value
	 * @return the history item if already retrieved or starts a background task to retrieve it.
	 */
	public RepositoryItemHistory getHistory() {
		if (history != null)
			return history;
		log.debug("Finding history item for " + repoItem.getFilename());
		try {
			history = repoItem.getRepository().getHistory(repoItem);
			setHistory();
		} catch (RepositoryException e) {
		}
		return null;
	}

	@Override
	public ImageView getIcon(ImageManager imageMgr) {
		return null;
	}

	@Override
	public RepositoryItem getValue() {
		return repoItem;
	}
}
