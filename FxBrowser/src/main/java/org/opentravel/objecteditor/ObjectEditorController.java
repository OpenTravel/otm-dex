/**
 * 
 */
package org.opentravel.objecteditor;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.DexFileHandler;
import org.opentravel.dex.controllers.DexMainControllerBase;
import org.opentravel.dex.controllers.DexStatusController;
import org.opentravel.dex.controllers.MenuBarWithProjectController;
import org.opentravel.dex.controllers.dialogbox.DialogBoxContoller;
import org.opentravel.dex.repository.RepositoryTabController;
import org.opentravel.dex.repository.TaskResultHandlerI;
import org.opentravel.dex.repository.tasks.OpenProjectFileTask;
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.otmContainers.OtmLibrary;
import org.opentravel.objecteditor.memberProperties.PropertiesDAO;
import org.opentravel.objecteditor.memberProperties.PropertiesTableController;
import org.opentravel.objecteditor.modelMembers.MemberDAO;
import org.opentravel.objecteditor.modelMembers.MemberFilterController;
import org.opentravel.objecteditor.modelMembers.MemberTreeController;
import org.opentravel.objecteditor.projectLibraries.LibrariesTreeController;
import org.opentravel.objecteditor.projectLibraries.LibraryDAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;

/**
 * Main controller for OtmObjecEditorLayout.fxml (1 FXML = 1Controller).
 * 
 * @author dmh
 *
 */
public class ObjectEditorController extends DexMainControllerBase implements TaskResultHandlerI {
	private static Log log = LogFactory.getLog(ObjectEditorController.class);

	@FXML
	private MenuBarWithProjectController menuBarWithProjectController;
	@FXML
	private DexStatusController dexStatusController;
	@FXML
	private MemberFilterController memberFilterController;
	@FXML
	private RepositoryTabController repositoryTabController;

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
	// @FXML
	// public TreeView<?> repoTabRootNSs;
	// @FXML
	// private ChoiceBox<String> repoTabRepoChoice;
	// @FXML
	// private ChoiceBox<String> repoTabNSChoice;
	// @FXML
	// private TreeTableView<?> repoTabLibraryTreeTableView;
	// @FXML
	// private Label nsLibraryTablePermissionLabel;
	// @FXML
	// private TextField repoTabRepoUserField;
	// @FXML
	// public TableView<?> repoTabLibraryHistoryView;

	@FXML
	public TreeTableView<LibraryDAO> libraryTabTreeTableView;

	// private DialogBoxContoller dialogBoxController;
	// private Stage stage = null;
	// private OtmModelManager modelMgr;
	// private ImageManager imageMgr;
	private DexFileHandler fileHandler = new DexFileHandler();

	// View Controllers
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

	@Override
	public void checkNodes() {
		if (!(menuBarWithProjectController instanceof MenuBarWithProjectController))
			throw new IllegalStateException("Menu bar not injected by FXML.");
		if (!(dexStatusController instanceof DexStatusController))
			throw new IllegalStateException("Status controller not injected by FXML.");
		if (!(memberFilterController instanceof MemberFilterController))
			throw new IllegalStateException("Member Filter Controller not injected by FXML.");
		if (!(repositoryTabController instanceof RepositoryTabController))
			throw new IllegalStateException("Repository Tab Controller not injected by FXML.");
	}

	/**
	 * Set up this FX controller
	 * 
	 * @param stage
	 */
	@Override
	public void setStage(Stage stage) {
		super.setStage(stage);
		log.debug("Controller - Initializing Object Editor Controller");
		// this.stage = stage;
		//
		// // Initialize managers
		// imageMgr = new ImageManager(stage);
		modelMgr = new OtmModelManager();

		// Set up menu bar and show the project combo
		menuBarWithProjectController.showCombo(true);
		menuBarWithProjectController.setStage(stage);
		menuBarWithProjectController.setdoCloseHandler(this::handleCloseMenu);
		// menuBarWithProjectController.setFileOpenHandler(this::handleOpenMenu);

		// Setup status controller
		dexStatusController.setStage(stage);
		dexStatusController.configure(this);
		statusController = dexStatusController; // Make available to base class

		// Setup Repository Tab controller
		repositoryTabController.setStage(stage, this);

		propertiesTableController = new PropertiesTableController(null, facetTabTreeTable, this);
		// TODO - what is right way to have facet listen to treeTable?
		propertiesTableController.registerListeners(navTreeTableView);

		configureProjectCombo();

		memberController = new MemberTreeController(this, navTreeTableView, modelMgr);
		memberFilterController.setParentController(this, memberController);
		memberController.setFilter(memberFilterController);

		libController = new LibrariesTreeController(this, libraryTabTreeTableView);
		libController.setSelectionListener((v, o, item) -> librarySelectionListener(item));
	}

	private void librarySelectionListener(TreeItem<LibraryDAO> item) {
		if (item == null || item.getValue() == null || item.getValue().getValue() == null)
			return;
		handleLibrarySelectionEvent(item.getValue().getValue());
	}

	public void handleLibrarySelectionEvent(OtmLibrary library) {
		memberFilterController.setLibraryFilter(library);
		memberController.refresh();
	}

	@Override
	public void initialize() {
		log.debug("Object Editor Controller - Initialize w/params is now loading!");
		checkNodes();
		dialogBoxController = DialogBoxContoller.init();
	}

	public void openFile(File selectedFile) {
		if (selectedFile == null)
			return;
		dialogBoxController.show("Loading Project", "Please wait");

		memberController.clear(); // prevent concurrent modification
		propertiesTableController.clear();
		modelMgr.clear();

		new OpenProjectFileTask(selectedFile, modelMgr, this::handleTaskComplete, statusController).go();
	}

	@Override
	public void handleTaskComplete(WorkerStateEvent event) {
		if (event.getTarget() instanceof OpenProjectFileTask) {
			dialogBoxController.close();
			memberController.post(modelMgr);
			libController.post(modelMgr);
		}
	}

	public void handleOpenMenu(ActionEvent event) {
		log.debug("Handle file open action event.");
		if (event.getTarget() instanceof MenuItem) {
			File selectedFile = fileHandler.fileChooser(stage);
			openFile(selectedFile);
		}
	}

	public void handleCloseMenu(ActionEvent event) {
		log.debug("Handle close action event.");
		if (event.getTarget() instanceof MenuItem) {
			memberController.clear(); // prevent concurrent modification
			propertiesTableController.clear();
			modelMgr.clear();
			clear();
		}
	}

	// public void postNotify(String label, String msg) {
	// DialogBox.notify(label, msg);
	// }
	//
	// public void clearNotify() {
	// DialogBox.close();
	// }

	// public void select(OtmLibraryMember<?> member) {
	// memberController.select(member);
	// }

	public void select(String name) {
		memberController.select(name);
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

	/**
	 * Configure the menu bar's combo box with projects
	 */
	private HashMap<String, File> projectMap = new HashMap<>();

	public void configureProjectCombo() {
		// FIXME - use UserSettings
		File initialDirectory = new File("C:\\Users\\dmh\\workspace\\OTM-DE_TestFiles");
		for (File file : fileHandler.getProjectList(initialDirectory)) {
			projectMap.put(file.getName(), file);
		}
		ObservableList<String> projectList = FXCollections.observableArrayList(projectMap.keySet());
		menuBarWithProjectController.configureComboBox(projectList, this::projectComboSelectionListener);
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

}
