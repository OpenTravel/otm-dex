/**
 * 
 */
package org.opentravel.objecteditor;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.DexFileHandler;
import org.opentravel.common.ImageManager;
import org.opentravel.common.OpenProjectProgressMonitor;
import org.opentravel.model.OtmModelManager;
import org.opentravel.objecteditor.LibraryHistoryItemsController.CommitNode;
import org.opentravel.objecteditor.NamespaceLibrariesTableController.RepoItemNode;
import org.opentravel.objecteditor.dialogbox.DialogBoxContoller;
import org.opentravel.objecteditor.repository.NamespaceLibrariesTreeTableController;
import org.opentravel.objecteditor.repository.NamespacesDAO;
import org.opentravel.objecteditor.repository.RepoItemDAO;
import org.opentravel.objecteditor.repository.RepositoryNamespacesTreeController;
import org.opentravel.schemacompiler.repository.Repository;
import org.opentravel.schemacompiler.repository.RepositoryException;
import org.opentravel.schemacompiler.repository.RepositoryManager;
import org.opentravel.schemacompiler.repository.impl.RemoteRepositoryClient;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;

/**
 * Manage the repository viewer. Includes controllers for the trees and tree-tables.
 * 
 * @author dmh
 *
 */
public class RepositoryViewerController implements DexController {
	private static Log log = LogFactory.getLog(RepositoryViewerController.class);

	private static final String LOCAL_REPO = "Local";

	private RepositoryManager repositoryManager;

	protected ImageManager imageMgr;
	private OtmModelManager modelMgr;
	protected Stage stage;

	/**
	 * FXML Java FX Nodes this controller is dependent upon
	 * 
	 * @author dmh
	 *
	 */
	public enum RepoTabNodes {
		TAB, RepositoryChoice, User, NamespaceTree, NamespaceLibraryTable, HistoryTable, NamespacePermission;
	}

	@FXML
	private ChoiceBox<String> repoTabRepoChoice;
	@FXML
	private ChoiceBox<String> repoTabNSChoice;
	@FXML
	private TreeTableView<RepoItemNode> repoTabLibraryTreeTableView;
	@FXML
	private TextField repoTabRepoUserField;
	@FXML
	public TableView<CommitNode> repoTabLibraryHistoryView;

	// Let FXML inject
	@FXML
	private RepositoryNamespacesTreeController repositoryNamespacesTreeController;
	private RepositoryNamespacesTreeController nsTreeController;

	@FXML
	private NamespaceLibrariesTreeTableController namespaceLibrariesTreeTableController;
	@FXML
	public org.opentravel.objecteditor.repository.NamespaceLibrariesTreeTableController foo;

	// Will be initialized in startup
	@FXML
	private DialogBoxContoller dialogBoxController;

	private ChoiceBox<String> repositoryChoice = repoTabRepoChoice;
	private TextField userField = repoTabRepoUserField;
	private TableView<CommitNode> historyTable = repoTabLibraryHistoryView;

	private LibraryHistoryItemsController libHistoryController;

	private void getRepoNodes() {

		repositoryChoice = repoTabRepoChoice;
		userField = repoTabRepoUserField;
		historyTable = repoTabLibraryHistoryView;

		checkNodes();
	}

	private void checkNodes() {
		if (repositoryChoice == null)
			throw new IllegalStateException("Null repository choice node in repository controller.");
		if (userField == null)
			throw new IllegalArgumentException(" null.");
		if (historyTable == null)
			throw new IllegalArgumentException(" null.");

		if (!(namespaceLibrariesTreeTableController instanceof NamespaceLibrariesTreeTableController))
			throw new IllegalStateException("Libraries tree table controller not injected by FXML.");

		// Repository Namespaces
		if (!(repositoryNamespacesTreeController instanceof RepositoryNamespacesTreeController))
			throw new IllegalStateException("repository namespaces controller not injected by FXML.");
		nsTreeController = repositoryNamespacesTreeController;
		if (!(nsTreeController instanceof RepositoryNamespacesTreeController))
			throw new IllegalStateException("Controller not injected by FXML.");

		log.debug("FXML Nodes are not null.");
	}

	public RepositoryViewerController() {
		log.debug("Starting constructor.");
	}

	@FXML
	public void initialize() {
		log.debug("Repository Viewer Controller initialized.");
	}

	/**
	 * @param primaryStage
	 */
	public void setStage(Stage primaryStage) {
		// These may be needed by sub-controllers
		this.stage = primaryStage;
		imageMgr = new ImageManager(primaryStage);
		modelMgr = new OtmModelManager();
		getRepoNodes();

		// Inject this controller into sub-controllers
		repositoryNamespacesTreeController.setParent(this);
		repositoryNamespacesTreeController.getSelectable()
				.addListener((v, old, newValue) -> nsTreeSelectionListener(newValue));

		// Set up the libraries in a namespace table
		namespaceLibrariesTreeTableController.setParent(this);
		namespaceLibrariesTreeTableController.getSelectable()
				.addListener((v, old, newValue) -> librarySelectionListener(newValue));

		// initialize Dialog Box
		final String LAYOUT_FILE = "/DialogBox.fxml";
		// Create a new dynamic loader
		FXMLLoader loader = new FXMLLoader(getClass().getResource(LAYOUT_FILE));
		dialogBoxController = DialogBoxContoller.init(loader, this);

		repositoryManager = getRepoMgr();
		configureRepositoryChoice();

		configureProjectMenuButton(); // TODO - move

		log.debug("Stage set.");
	}

	// public RepositoryViewerController(Stage stage, ObjectEditorController parent, EnumMap<RepoTabNodes, Node>
	// fxNodes) {
	// log.debug("Starting constructor with params.");
	// this.stage = stage;
	// if (stage == null)
	// throw new IllegalStateException("Stage is null.");
	// imageMgr = new ImageManager(stage);
	//
	// // getRepoNodes(fxNodes);
	// nsTreeController = new RepositoryNamespacesTreeController(this, tree);
	// nsTreeController.getSelectable().addListener((v, old, newValue) -> treeSelectionListener(newValue));
	//
	// nsLibsController = new NamespaceLibrariesTableController(this, libTable, nsPermission);
	// nsLibsController.getSelectable().addListener((v, old, newValue) -> librarySelectionListener(newValue));
	//
	// libHistoryController = new LibraryHistoryItemsController(this, historyTable);
	//
	// // Set up repository Choice
	// // repoController = new RepositoryController();
	// // repositoryManager = repoController.getRepositoryManager(); // FIXME
	// repositoryManager = getRepoMgr();
	// configureRepositoryChoice();
	//
	// log.debug("Repository Controller initialized.");
	// }

	@FXML
	public void doClose(ActionEvent e) {
		log.debug("Close menu item selected.");
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

	private void librarySelectionListener(TreeItem<RepoItemDAO> item) {
		if (item == null)
			return;
		log.debug("Library selected: " + item.getValue());
		// libHistoryController.post(item.getValue());
	}

	/**
	 * Handle namespace tree item selection by sending namespace to library table and clearing history.
	 * 
	 * @param item
	 */
	private void nsTreeSelectionListener(TreeItem<NamespacesDAO> item) {
		if (item == null)
			return;
		log.debug("New namespace tree item selected: " + item.getValue());
		NamespacesDAO nsNode = item.getValue();
		if (nsNode.getRepository() != null) {
			try {
				namespaceLibrariesTreeTableController.post(nsNode);
				// namespaceLibrariesTreeTableController.post(nsNode.getRepository(), nsNode.getFullPath());
				libHistoryController.clear();
				// } catch (RepositoryException e) {
				// log.debug("Error accessing namespace: " + e.getLocalizedMessage());
			} catch (Exception e) {
				log.debug("Error accessing namespace: " + e.getLocalizedMessage());
			}
		}
	}

	private void configureRepositoryChoice() {
		log.debug("Configuring repository choice box.");
		stage.showingProperty().addListener((observable, oldValue, newValue) -> {
			ObservableList<String> repositoryIds = FXCollections.observableArrayList();
			repositoryIds.add(LOCAL_REPO);
			repositoryManager.listRemoteRepositories().forEach(r -> repositoryIds.add(r.getId()));
			repositoryChoice.setItems(repositoryIds);
			repositoryChoice.getSelectionModel().select(0);
		});

		// Configure listener for choice box
		repositoryChoice.valueProperty().addListener((observable, oldValue, newValue) -> repositorySelectionChanged());
	}

	/**
	 * Called when the user modifies the selection of the 'repositoryChoice' control.
	 * 
	 * @throws RepositoryException
	 */
	private void repositorySelectionChanged() {
		log.debug("Selected new repository");

		// Pass the repository to the nsTree
		Repository repository;
		try {
			repository = getSelectedRepository();
			postUser(repository);
			nsTreeController.post(repository);
		} catch (Exception e) {
			log.warn("Error posting repository: " + e.getLocalizedMessage());
		}
		// } catch (RepositoryException e) {
		// log.debug("Error: " + e.getLocalizedMessage());
		// }
	}

	/**
	 * Add tree items to ROOT for each child and grandchild of the member.
	 * 
	 * @param member
	 * @throws RepositoryException
	 */
	private Repository getSelectedRepository() throws RepositoryException {
		Repository repository = RepositoryManager.getDefault();
		// repoController.getLocalRepository();
		String rid = repositoryChoice.getSelectionModel().getSelectedItem();
		if (rid != null)
			if (rid.equals(LOCAL_REPO))
				repository = RepositoryManager.getDefault();
			// repository = repoController.getLocalRepository();
			else
				// Use selected repository
				repository = repositoryManager.getRepository(rid);
		return repository;
	}

	private void postUser(Repository repository) {
		String user = "--local--";
		if (repository instanceof RemoteRepositoryClient)
			user = ((RemoteRepositoryClient) repository).getUserId();
		userField.setText(user);
	}

	/**
	 * Remove all items from the table
	 */
	@Override
	public void clear() {
		nsTreeController.clear();
		namespaceLibrariesTreeTableController.clear();
	}

	@Override
	public ImageManager getImageManager() {
		if (imageMgr == null)
			throw new IllegalStateException("Image manger is null.");
		return imageMgr;
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

	/******************************************
	 * These need to be in a menu controller
	 * 
	 * @param e
	 */
	@FXML
	private ProgressIndicator statusProgress;

	@Override
	public void postProgress(double percent) {
		if (statusProgress != null)
			if (Platform.isFxApplicationThread())
				statusProgress.setProgress(percent);
			else
				Platform.runLater(() -> postProgress(percent));
	}

	@FXML
	private Label statusLabel;

	@Override
	public void postStatus(String status) {
		if (statusLabel != null)
			if (Platform.isFxApplicationThread())
				statusLabel.setText(status);
			else
				Platform.runLater(() -> postStatus(status));
	}

	@FXML
	public ComboBox<String> projectCombo;

	@FXML
	public void projectComboSelectionListener(Event e) {
		log.debug("project selection event");
		if (e.getTarget() instanceof ComboBox)
			openFile(projectMap.get(((ComboBox<?>) e.getTarget()).getValue()));
	}

	private DexFileHandler fileHandler = new DexFileHandler();
	private HashMap<String, File> projectMap = new HashMap<>();

	public void configureProjectMenuButton() {
		if (projectCombo != null) {
			File initialDirectory = new File("C:\\Users\\dmh\\workspace\\OTM-DE_TestFiles");
			for (File file : fileHandler.getProjectList(initialDirectory)) {
				projectMap.put(file.getName(), file);
			}
			ObservableList<String> projectList = FXCollections.observableArrayList(projectMap.keySet());
			projectList.sort(null);
			projectCombo.setItems(projectList);
			projectCombo.setOnAction(e -> projectComboSelectionListener(e));
		}
	}

	public void openFile(File selectedFile) {
		if (selectedFile == null)
			return;
		dialogBoxController.show("Loading Project", "Please wait");
		// postNotify("Loading Project", "Wait please.");
		// dialog.display("LOADING", "Well now, just wait and watch...");

		// memberController.clear(); // prevent concurrent modification
		// propertiesTableController.clear();
		modelMgr.clear();
		postStatus("Opening " + selectedFile.getName());
		postProgress(0.1F);

		// Run the task in a background thread and Terminate the running thread if the application exits
		Runnable task = () -> openFileTask(selectedFile);
		Thread backgroundThread = new Thread(task);
		backgroundThread.setDaemon(true);
		backgroundThread.start();

		// See openFileTask for post completion actions
	}

	/**
	 * Open the file using the handler. Expected to be run in the background.
	 * 
	 * @param fileHandler
	 * @param selectedFile
	 */
	public void openFileTask(File selectedFile) {
		modelMgr.openProject(selectedFile, new OpenProjectProgressMonitor(this));
		// When done, update display in the UI thread
		Platform.runLater(() -> {
			dialogBoxController.close();
			// clearNotify();
			// memberController.post(modelMgr);
			// libController.post(modelMgr);
			postStatus("");
			postProgress(1F);
		});
		// TODO
		// update ProjectLibrariesTable
		// update RepositoryTab with selected repository from project
	}

	@FXML
	public void open(ActionEvent e) {
		log.debug("open");
	}

	@FXML
	public void appExit(ActionEvent e) {
		log.debug("exit");
		// TODO
		// primaryStage.close();
	}

	@FXML
	public void aboutApplication(ActionEvent event) {
		// AboutDialogController.createAboutDialog( getPrimaryStage() ).showAndWait();
	}

	@FXML
	public void fileOpen(Event e) {
		log.debug("File Open selected.");
	}

}
