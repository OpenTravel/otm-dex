/**
 * 
 */
package org.opentravel.dex.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.controllers.DexIncludedControllerBase;
import org.opentravel.dex.controllers.DexMainController;
import org.opentravel.dex.controllers.popup.DexPopupControllerBase.Results;
import org.opentravel.dex.controllers.popup.RepositoryLoginDialogContoller;
import org.opentravel.dex.events.DexRepositorySelectionEvent;
import org.opentravel.schemacompiler.repository.Repository;
import org.opentravel.schemacompiler.repository.RepositoryException;
import org.opentravel.schemacompiler.repository.RepositoryManager;
import org.opentravel.schemacompiler.repository.impl.RemoteRepositoryClient;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXML;
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

	// All event types fired by this controller.
	private static final EventType[] publishedEvents = { DexRepositorySelectionEvent.REPOSITORY_SELECTED };

	// All event types listened to by this controller's handlers
	private static final EventType[] subscribedEvents = {};

	public RepositorySelectionController() {
		super(null, publishedEvents);
		log.debug("Starting constructor.");
	}

	private void addRepository() {
		if (loginDialogController.showAndWait("") == Results.OK) {
			repositorySelectionChanged(); // update user field
			// repositoryChoice.getSelectionModel().select(loginDialogController.getLoginRepoID());
		}
	}

	@Override
	public void checkNodes() {
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

	// @Override
	// public ReadOnlyObjectProperty<String> getSelectable() {
	// return repositoryChoice.getSelectionModel().selectedItemProperty();
	// }
	// FIXME - use Events

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
		if (repositoryManager != null)
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
			repositoryChoice.fireEvent(new DexRepositorySelectionEvent(getSelectedRepository()));
		} catch (Exception e) {
			log.error("Error posting repository: " + e.getLocalizedMessage());
		}
	}

	/**
	 */
	@Override
	public void configure(DexMainController parent) {
		super.configure(parent);
		eventPublisherNode = repositoryChoice;

		repositoryManager = getRepoMgr();
		configureRepositoryChoice();

		// initialize login Dialog Box using a new dynamic loader
		loginDialogController = RepositoryLoginDialogContoller.init();
		// FXMLLoader loader = new FXMLLoader(getClass().getResource(RepositoryLoginDialogContoller.LAYOUT_FILE));
		// loginDialogController = RepositoryLoginDialogContoller.init(loader);
		addRepository.setOnAction(e -> addRepository());

		log.debug("Repository Selection Stage set.");
	}

}
