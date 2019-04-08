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
import org.opentravel.dex.controllers.DexStatusController;
import org.opentravel.dex.controllers.MenuBarWithProjectController;
import org.opentravel.dex.controllers.dialogbox.DialogBoxContoller;
import org.opentravel.dex.repository.TaskResultHandlerI;
import org.opentravel.dex.repository.tasks.OpenProjectFileTask;
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.otmContainers.OtmLibrary;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;
import org.opentravel.objecteditor.RepositoryTabController.RepoTabNodes;
import org.opentravel.objecteditor.memberProperties.PropertiesDAO;
import org.opentravel.objecteditor.memberProperties.PropertiesTableController;
import org.opentravel.objecteditor.modelMembers.MemberDAO;
import org.opentravel.objecteditor.modelMembers.MemberFilterController;
import org.opentravel.objecteditor.modelMembers.MemberTreeController;
import org.opentravel.objecteditor.projectLibraries.LibrariesTreeController;
import org.opentravel.objecteditor.projectLibraries.LibraryDAO;
import org.opentravel.schemacompiler.repository.RepositoryManager;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

/**
 * Main controller for OtmObjecEditorLayout.fxml (1 FXML = 1Controller).
 * 
 * @author dmh
 *
 */
public class ObjectEditorController implements DexMainController, TaskResultHandlerI {
	private static Log log = LogFactory.getLog(ObjectEditorController.class);

	@FXML
	private MenuBarWithProjectController menuBarWithProjectController;
	@FXML
	private DexStatusController dexStatusController;
	@FXML
	private MemberFilterController memberFilterController;

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

	@FXML
	public TreeTableView<LibraryDAO> libraryTabTreeTableView;

	private DialogBoxContoller dialogBoxController;
	private Stage primaryStage = null;
	private OtmModelManager modelMgr;
	private ImageManager imageMgr;
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

	private void checkNodes() {
		if (!(menuBarWithProjectController instanceof MenuBarWithProjectController))
			throw new IllegalStateException("Menu bar not injected by FXML.");
		if (!(dexStatusController instanceof DexStatusController))
			throw new IllegalStateException("Status controller not injected by FXML.");
		if (!(memberFilterController instanceof MemberFilterController))
			throw new IllegalStateException("Member Filter Controller not injected by FXML.");
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

		// Set up menu bar and show the project combo
		menuBarWithProjectController.showCombo(true);
		menuBarWithProjectController.setStage(primaryStage);
		menuBarWithProjectController.setDialogBox(dialogBoxController); // needed for not implemented
		menuBarWithProjectController.setdoCloseHandler(this::handleCloseMenu);
		// menuBarWithProjectController.setFileOpenHandler(this::handleOpenMenu);

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

	public void initialize() {
		log.debug("Object Editor Controller - Initialize w/params is now loading!");
		checkNodes();

		// Load dialog box controller using a new dynamic loader
		FXMLLoader loader = new FXMLLoader(getClass().getResource(DialogBoxContoller.LAYOUT_FILE));
		dialogBoxController = DialogBoxContoller.init(loader);
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

	public void openFile(File selectedFile) {
		if (selectedFile == null)
			return;
		dialogBoxController.show("Loading Project", "Please wait");

		memberController.clear(); // prevent concurrent modification
		propertiesTableController.clear();
		modelMgr.clear();

		new OpenProjectFileTask(selectedFile, modelMgr, this::handleTaskComplete, dexStatusController).go();
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
			File selectedFile = fileHandler.fileChooser(primaryStage);
			openFile(selectedFile);
		}
	}

	public void handleCloseMenu(ActionEvent event) {
		log.debug("Handle file open action event.");
		if (event.getTarget() instanceof MenuItem) {
			memberController.clear(); // prevent concurrent modification
			propertiesTableController.clear();
			modelMgr.clear();
		}
	}

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

	@Override
	public void postProgress(double percent) {
		dexStatusController.postProgress(percent);
	}

	@Override
	public void postStatus(String status) {
		dexStatusController.postStatus(status);
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

	/**
	 * {@inheritDoc}
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

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
	}

	@Override
	public RepositoryManager getRepositoryManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DexStatusController getStatusController() {
		return dexStatusController;
	}

}
