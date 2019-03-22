/**
 * 
 */
package org.opentravel.dex.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.model.OtmModelManager;
import org.opentravel.objecteditor.DexIncludedControllerBase;
import org.opentravel.schemacompiler.repository.RepositoryException;
import org.opentravel.schemacompiler.repository.RepositoryManager;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

/**
 * Manage the repository search panel
 * 
 * @author dmh
 *
 */
public class RepositorySearchController extends DexIncludedControllerBase<RepositoryManager> {
	private static Log log = LogFactory.getLog(RepositorySearchController.class);

	private static final String LOCAL_REPO = "Local";

	private RepositoryManager repositoryManager;

	@FXML
	private ChoiceBox<String> statusChoice;
	@FXML
	private TextField searchTerm;
	@FXML
	private Button doSearch;
	@FXML
	private RadioButton lastestOnlyRadio;
	@FXML
	private RadioButton lockedRadio;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private Label progressStatus;

	private void checkNodes() {
		// if (repositoryChoice == null)
		// throw new IllegalStateException("Null repository choice node in repository controller.");
		// if (repositoryUser == null)
		// throw new IllegalArgumentException("repositoryUser is null.");
		// if (repositoryPassword == null)
		// throw new IllegalArgumentException("repositorPassword is null.");
		// if (repositoryProgressBar == null)
		// throw new IllegalArgumentException("repositoryProgressBar is null.");
		// if (repositoryStatusField == null)
		// throw new IllegalArgumentException("repositoryStatusField is null.");
		log.debug("FXML Nodes checked OK.");
	}

	public RepositorySearchController() {
		log.debug("Starting constructor.");
	}

	@Override
	@FXML
	public void initialize() {
		log.debug("Repository Selection Controller initialized.");
	}

	/**
	 */
	public void setStage() {
		checkNodes(); // Verify FXML loaded correctly

		// repositoryManager = getRepoMgr();
		// configureRepositoryChoice();
		//
		// // initialize unlock Dialog Box using a new dynamic loader
		// FXMLLoader loader = new FXMLLoader(getClass().getResource(UnlockLibraryDialogContoller.LAYOUT_FILE));
		// unlockDialogController = UnlockLibraryDialogContoller.init(loader, this);
		//
		// if (unlockDialogController == null)
		// throw new IllegalStateException("Could not load unlock dialog controller.");

		log.debug("Repository Selection Stage set.");
	}

	private RepositoryManager getRepoMgr() {
		// // Set up repository access
		RepositoryManager rm = null;
		try {
			rm = RepositoryManager.getDefault();
			// availabilityChecker = RepositoryAvailabilityChecker.getInstance(repositoryManager);
			// repoStatus = availabilityChecker.pingAllRepositories(true);
		} catch (RepositoryException e) {
			log.error("Repository manager unavailable: " + e);
		}
		return rm;
	}

	@Override
	public void post(RepositoryManager repositoryManager) throws Exception {
		this.repositoryManager = repositoryManager;
		// does not really do anything -- the local repository acts as default manager.
	}

	// private void configureRepositoryChoice() {
	// log.debug("Configuring repository choice box.");
	//
	// ObservableList<String> repositoryIds = FXCollections.observableArrayList();
	// repositoryIds.add(LOCAL_REPO);
	// repositoryManager.listRemoteRepositories().forEach(r -> repositoryIds.add(r.getId()));
	// repositoryChoice.setItems(repositoryIds);
	// repositoryChoice.getSelectionModel().select(0);
	//
	// // Configure listener for choice box
	// repositoryChoice.valueProperty().addListener((observable, oldValue, newValue) -> repositorySelectionChanged());
	// log.debug("Repository choice has " + repositoryIds.size() + " items.");
	// }

	// /**
	// * Called when the user modifies the selection of the 'repositoryChoice' control.
	// *
	// * @throws RepositoryException
	// */
	// private void repositorySelectionChanged() {
	// log.debug("Selected new repository");
	// try {
	// postUser(getSelectedRepository());
	// // post password
	// } catch (Exception e) {
	// log.warn("Error posting repository: " + e.getLocalizedMessage());
	// }
	//
	// // // Pass the repository to the nsTree
	// // Repository repository;
	// // try {
	// // repository = getSelectedRepository();
	// // postUser(repository);
	// // // FIXME
	// //
	// // // repositoryNamespacesTreeController.post(repository);
	// // } catch (Exception e) {
	// // log.warn("Error posting repository: " + e.getLocalizedMessage());
	// // }
	// // // } catch (RepositoryException e) {
	// // // log.debug("Error: " + e.getLocalizedMessage());
	// // // }
	// }

	// /**
	// * @throws RepositoryException
	// */
	// public Repository getSelectedRepository() throws RepositoryException {
	// Repository repository = RepositoryManager.getDefault();
	// // repoController.getLocalRepository();
	// String rid = repositoryChoice.getSelectionModel().getSelectedItem();
	// if (rid != null)
	// if (rid.equals(LOCAL_REPO))
	// repository = RepositoryManager.getDefault();
	// // repository = repoController.getLocalRepository();
	// else
	// // Use selected repository
	// repository = repositoryManager.getRepository(rid);
	// return repository;
	// }

	// private void postUser(Repository repository) {
	// String user = "--local--";
	// if (repository instanceof RemoteRepositoryClient)
	// user = ((RemoteRepositoryClient) repository).getUserId();
	// repositoryUser.setText(user);
	// }

	/**
	 * Remove all items from the tables
	 */
	@Override
	public void clear() {
		// repositoryNamespacesTreeController.clear();
		// namespaceLibrariesTreeTableController.clear();
		// repositoryItemCommitHistoriesController.clear();
	}

	@Override
	public ImageManager getImageManager() {
		if (imageMgr == null)
			throw new IllegalStateException("Image manger is null.");
		return imageMgr;
	}

	@Override
	public ReadOnlyObjectProperty<String> getSelectable() {
		return null;
		// return repositoryChoice.getSelectionModel().selectedItemProperty();
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

	/******************************************
	 * @param e
	 */
	@Override
	public void postProgress(double percent) {
		// if (repositoryProgressBar != null)
		// if (Platform.isFxApplicationThread())
		// repositoryProgressBar.setProgress(percent);
		// else
		// Platform.runLater(() -> postProgress(percent));
	}

	@Override
	public void postStatus(String status) {
		// if (repositoryStatusField != null)
		// if (Platform.isFxApplicationThread())
		// repositoryStatusField.setText(status);
		// else
		// Platform.runLater(() -> postStatus(status));
	}

}
