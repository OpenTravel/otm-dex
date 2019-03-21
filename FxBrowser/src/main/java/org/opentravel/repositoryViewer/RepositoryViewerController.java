/**
 * 
 */
package org.opentravel.repositoryViewer;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.DexFileHandler;
import org.opentravel.common.ImageManager;
import org.opentravel.common.OpenProjectProgressMonitor;
import org.opentravel.dex.repository.NamespaceLibrariesTreeTableController;
import org.opentravel.dex.repository.NamespacesDAO;
import org.opentravel.dex.repository.RepoItemDAO;
import org.opentravel.dex.repository.RepositoryItemCommitHistoriesController;
import org.opentravel.dex.repository.RepositoryNamespacesTreeController;
import org.opentravel.dex.repository.RepositorySelectionController;
import org.opentravel.model.OtmModelManager;
import org.opentravel.objecteditor.DexController;
import org.opentravel.objecteditor.dialogbox.DialogBoxContoller;
import org.opentravel.schemacompiler.repository.RepositoryException;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;

/**
 * Manage the repository viewer. Includes controllers for the trees and tree-tables.
 * 
 * @author dmh
 *
 */
public class RepositoryViewerController implements DexController {
	private static Log log = LogFactory.getLog(RepositoryViewerController.class);

	protected ImageManager imageMgr;
	private OtmModelManager modelMgr;
	protected Stage stage;

	// Let FXML inject controllers
	@FXML
	private RepositoryNamespacesTreeController repositoryNamespacesTreeController;
	@FXML
	private NamespaceLibrariesTreeTableController namespaceLibrariesTreeTableController;
	@FXML
	private RepositoryItemCommitHistoriesController repositoryItemCommitHistoriesController;
	@FXML
	private RepositorySelectionController repositorySelectionController;

	// Will be initialized in startup
	@FXML
	private DialogBoxContoller dialogBoxController;

	private void checkNodes() {
		if (!(repositoryItemCommitHistoriesController instanceof RepositoryItemCommitHistoriesController))
			throw new IllegalStateException("Commit Histories controller not injected by FXML.");
		if (!(namespaceLibrariesTreeTableController instanceof NamespaceLibrariesTreeTableController))
			throw new IllegalStateException("Libraries tree table controller not injected by FXML.");
		if (!(repositoryNamespacesTreeController instanceof RepositoryNamespacesTreeController))
			throw new IllegalStateException("repository namespaces controller not injected by FXML.");
		if (!(repositorySelectionController instanceof RepositorySelectionController))
			throw new IllegalStateException("repository selection controller not injected by FXML.");

		log.debug("FXML Nodes checked OK.");
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
		checkNodes();

		// Set up the repository selection
		repositorySelectionController.setStage();
		repositorySelectionController.setParent(this);
		repositorySelectionController.getSelectable().addListener((v, old, newValue) -> repositorySelectionChanged());

		// Inject this controller into sub-controllers
		repositoryNamespacesTreeController.setParent(this);
		repositoryNamespacesTreeController.getSelectable()
				.addListener((v, old, newValue) -> namespaceSelectionListener(newValue));

		// Set up the libraries in a namespace table
		namespaceLibrariesTreeTableController.setParent(this);
		namespaceLibrariesTreeTableController.getSelectable()
				.addListener((v, old, newValue) -> librarySelectionListener(newValue));

		// initialize Dialog Box
		final String LAYOUT_FILE = "/DialogBox.fxml";
		// Create a new dynamic loader
		FXMLLoader loader = new FXMLLoader(getClass().getResource(LAYOUT_FILE));
		dialogBoxController = DialogBoxContoller.init(loader, this);

		configureProjectMenuButton(); // TODO - move

		log.debug("Stage set.");
	}

	public DialogBoxContoller getDialogBoxController() {
		return dialogBoxController;
	}

	@FXML
	public void doClose(ActionEvent e) {
		log.debug("Close menu item selected.");
	}

	private void librarySelectionListener(TreeItem<RepoItemDAO> item) {
		if (item == null)
			return;
		log.debug("Library selected: " + item.getValue());
		try {
			repositoryItemCommitHistoriesController.post(item.getValue());
		} catch (Exception e) {
			log.warn("Could not post commit history: " + e.getLocalizedMessage());
		}
	}

	// TODO
	// @Override
	public void refresh() {
		namespaceLibrariesTreeTableController.refresh();
		repositoryItemCommitHistoriesController.clear();
	}

	/**
	 * Handle namespace tree item selection by sending namespace to library table and clearing history.
	 * 
	 * @param item
	 */
	private void namespaceSelectionListener(TreeItem<NamespacesDAO> item) {
		if (item == null)
			return;
		log.debug("New namespace tree item selected: " + item.getValue());
		NamespacesDAO nsNode = item.getValue();
		if (nsNode.getRepository() != null) {
			try {
				namespaceLibrariesTreeTableController.post(nsNode);
				repositoryItemCommitHistoriesController.clear();
			} catch (Exception e) {
				log.debug("Error accessing namespace: " + e.getLocalizedMessage());
			}
		}
	}

	/**
	 * Called when the user modifies the selection of the 'repositoryChoice' control.
	 * 
	 * @throws RepositoryException
	 */
	private void repositorySelectionChanged() {
		log.debug("Selected new repository");
		try {
			repositoryNamespacesTreeController.post(repositorySelectionController.getSelectedRepository());
		} catch (Exception e) {
			log.warn("Error posting repository: " + e.getLocalizedMessage());
		}
	}

	/**
	 * Remove all items from the tables
	 */
	@Override
	public void clear() {
		repositoryNamespacesTreeController.clear();
		namespaceLibrariesTreeTableController.clear();
		repositoryItemCommitHistoriesController.clear();
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

	/**
	 * @return
	 */
	public RepositorySelectionController getRepositoryController() {
		return repositorySelectionController;
	}

}
