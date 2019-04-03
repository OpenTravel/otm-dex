/**
 * 
 */
package org.opentravel.objecteditor;

import java.io.File;
import java.util.EnumMap;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.DexFileHandler;
import org.opentravel.common.DialogBox;
import org.opentravel.common.ImageManager;
import org.opentravel.common.OpenProjectProgressMonitor;
import org.opentravel.dex.controllers.DexStatusController;
import org.opentravel.dex.controllers.MenuBarWithProjectController;
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.otmContainers.OtmLibrary;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;
import org.opentravel.objecteditor.RepositoryTabController.RepoTabNodes;
import org.opentravel.objecteditor.dialogbox.DialogBoxContoller;
import org.opentravel.objecteditor.memberProperties.PropertiesDAO;
import org.opentravel.objecteditor.memberProperties.PropertiesTableController;
import org.opentravel.objecteditor.modelMembers.MemberDAO;
import org.opentravel.objecteditor.modelMembers.MemberFilterController;
import org.opentravel.objecteditor.modelMembers.MemberFilterController.LibraryFilterNodes;
import org.opentravel.objecteditor.modelMembers.MemberTreeController;
import org.opentravel.objecteditor.projectLibraries.LibrariesTreeController;
import org.opentravel.objecteditor.projectLibraries.LibraryDAO;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

/**
 * Main controller for OtmObjecEditorLayout.fxml (1 FXML = 1Controller).
 * 
 * @author dmh
 *
 */
@SuppressWarnings("restriction")
public class ObjectEditorController implements DexController {
	// public class ObjectEditorController implements Initializable, DexController {
	private static Log log = LogFactory.getLog(ObjectEditorController.class);

	@FXML
	private MenuBarWithProjectController menuBarWithProjectController;
	@FXML
	private DexStatusController dexStatusController;

	/** **** OLD FXML usage **/
	//
	// FIXME - use import/include to break up fxml files and controllers.
	// See: https://www.youtube.com/watch?v=osIRfgHTfyg
	//
	// Navigation Table Tree View
	//
	@FXML
	public TreeTableView<MemberDAO> navTreeTableView;

	// Facet Tab
	@FXML
	public Tab facetTab;
	@FXML
	public TreeTableView<PropertiesDAO> facetTabTreeTable;

	// Repository Tab
	@FXML
	public TreeView<?> repoTabRootNSs;
	@FXML
	private ChoiceBox<String> repoTabRepoChoice;
	@FXML
	private ChoiceBox<String> repoTabNSChoice;
	@FXML
	private TreeTableView<?> repoTabLibraryTreeTableView;
	@FXML
	private Label nsLibraryTablePermissionLabel;
	@FXML
	private TextField repoTabRepoUserField;
	@FXML
	public TableView<?> repoTabLibraryHistoryView;

	// Library Member Table Selection Filters
	@FXML
	private ChoiceBox<String> librarySelector;
	@FXML
	private TextField libraryNameFilter;
	@FXML
	private MenuButton libraryTypeMenu;
	@FXML
	private MenuButton libraryStateMenu;

	@FXML
	public TreeTableView<LibraryDAO> libraryTabTreeTableView;

	// Let FXML inject into the dialog box controller.
	@FXML
	private DialogBoxContoller dialogBoxController;

	Stage primaryStage = null;
	private OtmModelManager modelMgr;
	private ImageManager imageMgr;
	private DexFileHandler fileHandler = new DexFileHandler();

	// View Controllers
	private MemberFilterController memberFilters;
	private MemberTreeController memberController;
	private LibrariesTreeController libController;
	private PropertiesTableController propertiesTableController;

	// TODO - formalize handler for view controllers with iterator

	// DONE - hook up to the launcher
	// 1. Create *ApplicationProvider class
	// 1a. Create Images class for icon
	// 2. Create resources/META-INF file
	// 3. Add dependency to launcher pom.xml
	// 3a. artifactId and version from its POM
	// *** FIXME - this is wrong for repoViewer, project setup is wrong.
	// 4. Add application display name to /ota2-app-launcher/src/main/resources/ota2-app-launcher.properties
	//
	// DONE - Implement (extend) AbstractMainWindowController
	// Requires setStatusMessage(), xxx() methods

	// TODO - preferences (improve as i use it)
	// Uses java beans to read/write to file
	// 1. Abstract User Settings class (application common)
	// 1a. Add fields, getters, setters for app specific preferences
	// 2. Add load to main controller initialize

	// DONE - AbstractOtmApplication -
	// DONE - AbstractMainWindowController
	// DONE - background task
	//

	// TODO - create wizard/pop-up handlers
	// use TitledPane fx control

	private void checkNodes() {
		if (!(menuBarWithProjectController instanceof MenuBarWithProjectController))
			throw new IllegalStateException("Menu bar not injected by FXML.");
		if (!(dexStatusController instanceof DexStatusController))
			throw new IllegalStateException("Status controller not injected by FXML.");
	}

	/**
	 * Set up this FX controller
	 * 
	 * @param stage
	 */
	public void setStage(Stage stage) {
		log.debug("Controller - Initializing Object Editor Controller");

		primaryStage = stage;

		// Initialize managers
		imageMgr = new ImageManager(primaryStage);
		modelMgr = new OtmModelManager();
		modelMgr.createTestLibrary();

		// Set up menu bar and show the project combo
		menuBarWithProjectController.showProjectCombo(true);
		menuBarWithProjectController.setStage(primaryStage);
		// Setup status controller
		dexStatusController.setStage(primaryStage);
		dexStatusController.setParent(this);

		// Set up Repository Tab
		EnumMap<RepoTabNodes, Node> repoNodes = new EnumMap<>(RepoTabNodes.class);
		repoNodes.put(RepoTabNodes.TAB, repoTabRootNSs);
		repoNodes.put(RepoTabNodes.RepositoryChoice, repoTabRepoChoice);
		repoNodes.put(RepoTabNodes.NamespaceTree, repoTabRootNSs);
		repoNodes.put(RepoTabNodes.NamespaceLibraryTable, repoTabLibraryTreeTableView);
		repoNodes.put(RepoTabNodes.NamespacePermission, nsLibraryTablePermissionLabel);
		repoNodes.put(RepoTabNodes.HistoryTable, repoTabLibraryHistoryView);
		repoNodes.put(RepoTabNodes.User, repoTabRepoUserField);
		new RepositoryTabController(primaryStage, this, repoNodes);

		propertiesTableController = new PropertiesTableController(null, facetTabTreeTable, this);
		// TODO - what is right way to have facet listen to treeTable?
		propertiesTableController.registerListeners(navTreeTableView);

		configureProjectCombo();

		memberController = new MemberTreeController(this, navTreeTableView, modelMgr);
		// Set up library selector/filter controller
		EnumMap<LibraryFilterNodes, Node> filterNodes = new EnumMap<>(LibraryFilterNodes.class);
		filterNodes.put(LibraryFilterNodes.Library, librarySelector);
		filterNodes.put(LibraryFilterNodes.Name, libraryNameFilter);
		filterNodes.put(LibraryFilterNodes.Type, libraryTypeMenu);
		filterNodes.put(LibraryFilterNodes.State, libraryStateMenu);
		memberFilters = new MemberFilterController(memberController, filterNodes);
		memberController.setFilter(memberFilters);

		libController = new LibrariesTreeController(this, libraryTabTreeTableView);
	}

	public void handleLibrarySelectionEvent(OtmLibrary library) {
		memberFilters.setLibraryFilter(library);
		memberController.refresh();
	}

	// @Override
	// public void initialize(URL location, ResourceBundle resources) {
	public void initialize() {
		log.debug("Object Editor Controller - Initialize w/params is now loading!");
		checkNodes();
		initializeDialogBox();
	}

	/**
	 * Create a working stage from an FXML file and its own controller complete with its own FXML injected controls and
	 * nodes.
	 * <p>
	 * Use this pattern for FXML files that are not included in another FXML file.
	 */
	private void initializeDialogBox() {
		final String LAYOUT_FILE = "/DialogBox.fxml";
		// Create a new dynamic loader
		FXMLLoader loader = new FXMLLoader(getClass().getResource(LAYOUT_FILE));
		dialogBoxController = DialogBoxContoller.init(loader, this);
		// dialogBoxController.injectMainController(this);

		// try {
		// // Load the fxml file initialize controller it declares.
		// Pane pane = loader.load();
		// // Create scene and stage
		// Stage dialogStage = new Stage();
		// dialogStage.setScene(new Scene(pane));
		// dialogStage.initModality(Modality.APPLICATION_MODAL);
		//
		// // get the controller from it.
		// dialogBoxController = loader.getController();
		// if (dialogBoxController == null)
		// log.error("Missing dialog box controller.");
		// else {
		// dialogBoxController.injectMainController(this);
		// dialogBoxController.injectStage(dialogStage);
		// }
		// } catch (IOException e1) {
		// log.error("Error loading dialog box.");
		// }
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return
	 */
	@Override
	public OtmModelManager getModelManager() {
		return modelMgr;
	}

	@FXML
	public void fileOpen(Event e) {
		// FIXME - this is in MenuBarWithProjectController - hook it up
		log.debug("File Open selected.");
		File selectedFile = fileHandler.fileChooser(primaryStage);
		openFile(selectedFile);
	}

	public void openFile(File selectedFile) {
		if (selectedFile == null)
			return;
		dialogBoxController.show("Loading Project", "Please wait");
		// postNotify("Loading Project", "Wait please.");
		// dialog.display("LOADING", "Well now, just wait and watch...");

		memberController.clear(); // prevent concurrent modification
		propertiesTableController.clear();
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
			memberController.post(modelMgr);
			libController.post(modelMgr);
			postStatus("");
			postProgress(1F);
		});
		// TODO
		// update ProjectLibrariesTable
		// update RepositoryTab with selected repository from project
	}

	// DialogBox dialog = new DialogBox();

	public void postNotify(String label, String msg) {
		DialogBox.notify(label, msg);
	}

	public void clearNotify() {
		DialogBox.close();
	}

	public void select(OtmLibraryMember<?> member) {
		memberController.select(member);
	}

	public void select(String name) {
		memberController.select(name);
	}

	// @FXML
	// ProgressIndicator statusProgress;

	@Override
	public void postProgress(double percent) {
		dexStatusController.postProgress(percent);
		// if (statusProgress != null)
		// if (Platform.isFxApplicationThread())
		// statusProgress.setProgress(percent);
		// else
		// Platform.runLater(() -> postProgress(percent));
	}

	// @FXML
	// Label statusLabel;

	@Override
	public void postStatus(String status) {
		dexStatusController.postStatus(status);
		// if (statusLabel != null)
		// if (Platform.isFxApplicationThread())
		// statusLabel.setText(status);
		// else
		// Platform.runLater(() -> postStatus(status));
	}

	// Fires whenever a tab is selected. Fires on closed tab and opened tab.
	@FXML
	public void whereUsedTabSelection(Event e) {
		log.debug("Where used tab selection event");
	}

	@FXML
	public void memberTabSelection(Event e) {
		log.debug("memberTab selection event");
	}

	private HashMap<String, File> projectMap = new HashMap<>();

	public void configureProjectCombo() {
		// FIXME - use UserSettings
		// TODO - should getting projects be migrated into menu bar? Or should menu bar allow changing combo label?
		// I don't think it should be...we will end up with multiple projects so the combo will not be enough
		File initialDirectory = new File("C:\\Users\\dmh\\workspace\\OTM-DE_TestFiles");
		for (File file : fileHandler.getProjectList(initialDirectory)) {
			projectMap.put(file.getName(), file);
		}
		ObservableList<String> projectList = FXCollections.observableArrayList(projectMap.keySet());
		menuBarWithProjectController.configureProjectMenuButton(projectList, this::projectComboSelectionListener);
	}

	public void projectComboSelectionListener(Event e) {
		log.debug("project selection event");
		if (e.getTarget() instanceof ComboBox)
			openFile(projectMap.get(((ComboBox<?>) e.getTarget()).getValue()));
	}

	@FXML
	public void deleteProperty(ActionEvent e) {
		log.debug("Delete Button");
	}

	@FXML
	public void setName(ActionEvent e) {
		log.debug("set Name");
	}

	@FXML
	public void open(ActionEvent e) {
		log.debug("open");
	}

	// @FXML
	// public void doClose(ActionEvent e) {
	// log.debug("Close menu item selected.");
	// StringBuilder libs = new StringBuilder();
	// for (OtmLibrary lib : getModelManager().getLibraries())
	// libs.append(lib.getBaseNamespace() + "\n");
	// dialogBoxController.show("Do you want to close the project?", libs.toString());
	// // dialogBoxController.add(libs.toString());
	// }

	// @FXML
	// public void appExit(ActionEvent e) {
	// log.debug("exit");
	// primaryStage.close();
	// }

	// /**
	// * Called when the user clicks the menu to display the about-application dialog.
	// *
	// * @param event
	// * the action event that triggered this method call
	// */
	// @FXML
	// public void aboutApplication(ActionEvent event) {
	// // AboutDialogController.createAboutDialog( getPrimaryStage() ).showAndWait();
	// }

	/**
	 * {@inheritDoc}
	 * <p>
	 * 
	 * @return null
	 */
	@Override
	public ReadOnlyObjectProperty<?> getSelectable() {
		return null;
	}

	@Override
	public ImageManager getImageManager() {
		if (imageMgr == null)
			throw new IllegalStateException("Image manger is null.");
		return imageMgr;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Does nothing.
	 */
	@Override
	public void clear() {
		// TODO - should this do anything?
	}

}
