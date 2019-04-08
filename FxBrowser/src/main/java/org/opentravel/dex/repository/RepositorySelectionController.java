/**
 * 
 */
package org.opentravel.dex.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.controllers.dialogbox.RepositoryLoginDialogContoller;
import org.opentravel.dex.controllers.dialogbox.RepositoryLoginDialogContoller.Results;
import org.opentravel.objecteditor.DexIncludedControllerBase;
import org.opentravel.schemacompiler.repository.Repository;
import org.opentravel.schemacompiler.repository.RepositoryException;
import org.opentravel.schemacompiler.repository.RepositoryManager;
import org.opentravel.schemacompiler.repository.impl.RemoteRepositoryClient;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

/**
 * Manage the repository selection choice, user, password bar.
 * 
 * @author dmh
 *
 */
public class RepositorySelectionController extends DexIncludedControllerBase<RepositoryManager> {
	private static Log log = LogFactory.getLog(RepositorySelectionController.class);

	private static final String LOCAL_REPO = "Local";

	@FXML
	private ChoiceBox<String> repositoryChoice;
	@FXML
	private Label repositoryUser;
	@FXML
	private Button addRepository;
	@FXML
	private RepositoryLoginDialogContoller loginDialogController;

	private RepositoryManager repositoryManager;

	public RepositorySelectionController() {
		log.debug("Starting constructor.");
	}

	private void addRepository() {
		if (loginDialogController.showAndWait("", "") == Results.OK) {
			repositorySelectionChanged(); // update user field
			// repositoryChoice.getSelectionModel().select(loginDialogController.getLoginRepoID());
		}
	}

	private void checkNodes() {
		if (repositoryChoice == null)
			throw new IllegalStateException("Null repository choice node in repository controller.");
		if (repositoryUser == null)
			throw new IllegalArgumentException("repositoryUser is null.");
		// log.debug("FXML Nodes checked OK.");
	}

	/**
	 * Remove all items from the tables
	 */
	@Override
	public void clear() {
	}

	private void configureRepositoryChoice() {
		log.debug("Configuring repository choice box.");

		ObservableList<String> repositoryIds = FXCollections.observableArrayList();
		repositoryIds.add(LOCAL_REPO);
		repositoryManager.listRemoteRepositories().forEach(r -> repositoryIds.add(r.getId()));
		repositoryChoice.setItems(repositoryIds);
		repositoryChoice.getSelectionModel().select(0);

		// Configure listener for choice box
		repositoryChoice.valueProperty().addListener((observable, oldValue, newValue) -> repositorySelectionChanged());
		log.debug("Repository choice has " + repositoryIds.size() + " items.");
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

	public RepositoryManager getRepositoryManager() {
		return repositoryManager;
	}

	@Override
	public ReadOnlyObjectProperty<String> getSelectable() {
		return repositoryChoice.getSelectionModel().selectedItemProperty();
	}

	/**
	 * @throws RepositoryException
	 */
	public Repository getSelectedRepository() throws RepositoryException {
		Repository repository = RepositoryManager.getDefault();
		String rid = repositoryChoice.getSelectionModel().getSelectedItem();
		if (rid != null)
			if (rid.equals(LOCAL_REPO))
				repository = RepositoryManager.getDefault();
			else
				// Use selected repository
				repository = repositoryManager.getRepository(rid);
		return repository;
	}

	@Override
	@FXML
	public void initialize() {
		log.debug("Repository Selection Controller initialized.");
	}

	@Override
	public void post(RepositoryManager repositoryManager) throws Exception {
		this.repositoryManager = repositoryManager;
		// does not really do anything -- the local repository acts as default manager.
	}

	/**
	 * Get the user from the repository and post in the repository user field.
	 * 
	 * @param repository
	 */
	private void postUser(Repository repository) {
		String user = "--local--";
		if (repository instanceof RemoteRepositoryClient)
			user = ((RemoteRepositoryClient) repository).getUserId();
		repositoryUser.setText(user);
	}

	/**
	 * Called when the user modifies the selection of the 'repositoryChoice' control.
	 * 
	 * @throws RepositoryException
	 */
	private void repositorySelectionChanged() {
		log.debug("Selected new repository");
		try {
			postUser(getSelectedRepository());
		} catch (Exception e) {
			log.warn("Error posting repository: " + e.getLocalizedMessage());
		}
	}

	/**
	 */
	public void setStage() {
		checkNodes(); // Verify FXML loaded correctly

		repositoryManager = getRepoMgr();
		configureRepositoryChoice();

		// initialize login Dialog Box using a new dynamic loader
		FXMLLoader loader = new FXMLLoader(getClass().getResource(RepositoryLoginDialogContoller.LAYOUT_FILE));
		loginDialogController = RepositoryLoginDialogContoller.init(loader);
		addRepository.setOnAction(e -> addRepository());

		if (loginDialogController == null)
			throw new IllegalStateException("Could not load unlock dialog controller.");

		log.debug("Repository Selection Stage set.");
	}

}
