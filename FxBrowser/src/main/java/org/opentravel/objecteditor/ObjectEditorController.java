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
import org.opentravel.dex.controllers.library.LibrariesTabController;
import org.opentravel.dex.controllers.library.LibrariesTreeTableController;
import org.opentravel.dex.controllers.library.LibraryDAO;
import org.opentravel.dex.controllers.member.MemberFilterController;
import org.opentravel.dex.controllers.member.MemberTreeTableController;
import org.opentravel.dex.controllers.member.properties.MemberPropertiesTabController;
import org.opentravel.dex.controllers.member.properties.MemberPropertiesTreeTableController;
import org.opentravel.dex.controllers.member.properties.PropertiesDAO;
import org.opentravel.dex.events.DexLibrarySelectionEvent;
import org.opentravel.dex.repository.RepositoryTabController;
import org.opentravel.dex.repository.TaskResultHandlerI;
import org.opentravel.dex.repository.tasks.OpenProjectFileTask;
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.otmContainers.OtmLibrary;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
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
	@FXML
	private MemberTreeTableController memberTreeTableController;
	@FXML
	private MemberPropertiesTabController memberPropertiesTabController;
	@FXML
	private LibrariesTabController librariesTabController;

	/** **** OLD FXML usage **/
	//
	// FIXME - use import/include to break up fxml files and controllers.
	// See: https://www.youtube.com/watch?v=osIRfgHTfyg
	//
	// Navigation Table Tree View
	//
	// @FXML
	// public TreeTableView<MemberDAO> navTreeTableView;

	// Facet Tab
	@FXML
	public Tab facetTab;
	@FXML
	public TreeTableView<PropertiesDAO> facetTabTreeTable;

	@FXML
	public TreeTableView<LibraryDAO> libraryTabTreeTableView;

	private DexFileHandler fileHandler = new DexFileHandler();

	// View Controllers
	// private MemberTreeTableController memberController;
	private LibrariesTreeTableController libController;
	// private MemberPropertiesTreeTableController propertiesTableController;

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
		if (!(repositoryTabController instanceof RepositoryTabController))
			throw new IllegalStateException("Repository tab not injected by FXML.");
		if (!(memberPropertiesTabController instanceof MemberPropertiesTabController))
			throw new IllegalStateException("Member properties tab not injected by FXML.");
		if (!(librariesTabController instanceof LibrariesTabController))
			throw new IllegalStateException("Libraries tab not injected by FXML.");

		// Included controllers do not need to be checked...they will be checked when added
		// if (!(menuBarWithProjectController instanceof MenuBarWithProjectController))
		// throw new IllegalComponentStateException("Menu bar not injected by FXML.");
		// if (!(dexStatusController instanceof DexStatusController))
		// throw new IllegalComponentStateException("Status controller not injected by FXML.");
		// if (!(memberFilterController instanceof MemberFilterController))
		// throw new IllegalComponentStateException("Member Filter Controller not injected by FXML.");
		// if (!(repositoryTabController instanceof RepositoryTabController))
		// throw new IllegalComponentStateException("Repository Tab Controller not injected by FXML.");
		// if (!(memberTreeTableController instanceof MemberTreeTableController))
		// throw new IllegalComponentStateException("Member tree table Controller not injected by FXML.");
		// if (!(memberPropertiesTreeTableController instanceof MemberPropertiesTreeTableController))
		// throw new IllegalComponentStateException("Member properties tree table Controller not injected by FXML.");
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
		addIncludedController(menuBarWithProjectController);
		menuBarWithProjectController.showCombo(true);
		menuBarWithProjectController.setdoCloseHandler(this::handleCloseMenu);
		// menuBarWithProjectController.setFileOpenHandler(this::handleOpenMenu);

		// Setup status controller
		addIncludedController(dexStatusController);
		statusController = dexStatusController; // Make available to base class

		// Setup Tab controllers
		repositoryTabController.setStage(stage, this); // TODO - this is slow!
		memberPropertiesTabController.setStage(stage, this);
		librariesTabController.setStage(stage, this);
		librariesTabController.setLibrarySelectionEventHandler(this::librarySelectionEventHandler);
		configureProjectCombo();

		// Member tree and its filter
		addIncludedController(memberFilterController);
		addIncludedController(memberTreeTableController);
		memberTreeTableController.setFilter(memberFilterController);

		// TODO - there has to be a better way to access the properties table controller
		MemberPropertiesTreeTableController memberPropertiesTreeTableController = memberPropertiesTabController
				.getPropertiesTableController();
		memberTreeTableController.setChangeEventHandler(memberPropertiesTreeTableController::memberSelectionListener);

		// libController = new LibrariesTreeTableController(this, libraryTabTreeTableView);
		// FIXME - set selection handler/event/etc.
		// libController.setSelectionListener((v, o, item) -> librarySelectionListener(item));
	}

	// private void librarySelectionListener(TreeItem<LibraryDAO> item) {
	// if (item == null || item.getValue() == null || item.getValue().getValue() == null)
	// return;
	// handleLibrarySelectionEvent(item.getValue().getValue());
	// }

	private void librarySelectionEventHandler(DexLibrarySelectionEvent event) {
		memberFilterController.setLibraryFilter(event.getLibrary());
		memberTreeTableController.refresh();
	}

	@Deprecated
	public void handleLibrarySelectionEvent(OtmLibrary library) {
		memberFilterController.setLibraryFilter(library);
		memberTreeTableController.refresh();
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

		memberTreeTableController.clear(); // prevent concurrent modification
		memberPropertiesTabController.clear();
		modelMgr.clear();

		new OpenProjectFileTask(selectedFile, modelMgr, this::handleTaskComplete, statusController).go();
	}

	@Override
	public void handleTaskComplete(WorkerStateEvent event) {
		if (event.getTarget() instanceof OpenProjectFileTask) {
			dialogBoxController.close();
			// TODO - pass to tab not table controller
			memberTreeTableController.post(modelMgr);
			librariesTabController.post(modelMgr);
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
			memberTreeTableController.clear(); // prevent concurrent modification
			memberPropertiesTabController.clear();
			modelMgr.clear();
			clear();
		}
	}

	public void select(String name) {
		memberTreeTableController.select(name);
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
