/**
 * 
 */
package org.opentravel.objecteditor;

import java.io.File;
import java.net.URL;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.opentravel.common.DexFileHandler;
import org.opentravel.model.OtmModelManager;
import org.opentravel.objecteditor.LibraryFilterController.LibraryFilterNodes;
import org.opentravel.objecteditor.RepositoryTabController.RepoTabNodes;
import org.opentravel.upversion.RepositoryItemWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import OTM_FX.FxBrowser.DemoNode;
import OTM_FX.FxBrowser.TableManager;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.application.Platform;
//import javafx.scene.control.ProgressIndicator;
//import javafx.scene.control.MenuButton;

/**
 * Main controller for OtmObjecEditorLayout.fxml (1 FXML = 1Controller).
 * 
 * @author dmh
 *
 */
@SuppressWarnings("restriction")
public class ObjectEditorController implements Initializable, DexController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ObjectEditorController.class);

	// Navigation Table Tree View
	//
	@FXML
	public TreeTableView<LibraryMemberTreeDAO> navTreeTableView;
	LibraryMemberTreeController memberController;

	// Facet Tab
	@FXML
	public Tab facetTab;
	@FXML
	public TreeTableView facetTabTreeTable;
	private FacetTabTreeTableHandler facetTableMgr;

	// Repository Tab
	@FXML
	public TreeView repoTabRootNSs;
	@FXML
	private ChoiceBox<String> repoTabRepoChoice;
	@FXML
	private ChoiceBox<String> repoTabNSChoice;
	@FXML
	private TreeTableView repoTabLibraryTreeTableView;
	@FXML
	private Label nsLibraryTablePermissionLabel;
	@FXML
	private TextField repoTabRepoUserField;
	@FXML
	public TableView repoTabLibraryHistoryView;

	// Library Member Table Selection Filters
	@FXML
	private ChoiceBox<String> librarySelector;
	@FXML
	private TextField libraryNameFilter;
	@FXML
	private MenuButton libraryTypeMenu;
	@FXML
	private MenuButton libraryStateMenu;

	//
	// OLD - to be removed
	//
	@FXML
	public Accordion facetTwisties;
	@FXML
	public VBox facetTabVbox;
	@FXML
	public TreeView facetTabFacetTree;

	@FXML
	public TableView<DemoNode> memberTable;
	TableManager tableMgr;

	@FXML
	public HBox memberEditHbox;
	@FXML
	public Tab memberTab;

	@FXML
	private ChoiceBox<String> repositoryChoice;
	@FXML
	private ChoiceBox<String> namespaceChoice;
	@FXML
	private TableView<RepositoryItemWrapper> selectedLibrariesTable;
	private TableView<RepositoryItemWrapper> namespaceTable;

	Stage primaryStage = null;
	private OtmModelManager modelMgr;
	private ImageManager imageMgr;
	private DexFileHandler fileHandler = new DexFileHandler();
	private LibraryFilterController libraryFilters;

	// TODO - create wizard/pop-up handlers
	// use TitledPane fx control
	/**
	 * Set up this controller
	 * 
	 * @param stage
	 */
	public void setStage(Stage stage) {
		LOGGER.debug("Controller - Initializing Object Editor Controller");

		primaryStage = stage;

		// Initialize managers
		imageMgr = new ImageManager(primaryStage);
		modelMgr = new OtmModelManager();
		modelMgr.createTestLibrary();
		tableMgr = new TableManager();

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

		facetTableMgr = new FacetTabTreeTableHandler(null, facetTabTreeTable, stage);
		// TODO - what is right way to have facet listen to treeTable?
		facetTableMgr.registerListeners(navTreeTableView);

		configureProjectMenuButton();

		memberController = new LibraryMemberTreeController(this, navTreeTableView, modelMgr);
		// Set up library selector/filter controller
		EnumMap<LibraryFilterNodes, Node> filterNodes = new EnumMap<>(LibraryFilterNodes.class);
		filterNodes.put(LibraryFilterNodes.Library, librarySelector);
		filterNodes.put(LibraryFilterNodes.Name, libraryNameFilter);
		filterNodes.put(LibraryFilterNodes.Type, libraryTypeMenu);
		filterNodes.put(LibraryFilterNodes.State, libraryStateMenu);
		libraryFilters = new LibraryFilterController(memberController, filterNodes);
		memberController.setFilter(libraryFilters);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("Object Editor Controller - Initialize w/params is now loading!");
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
		System.out.println("File Open selected.");
		File selectedFile = fileHandler.fileChooser(primaryStage);
		openFile(selectedFile);
	}

	public void openFile(File selectedFile) {
		if (selectedFile == null)
			return;
		memberController.clear(); // prevent concurrent modification
		modelMgr.clear();
		postStatus("Opening " + selectedFile.getName());
		postProgress(0.2F);

		Runnable task = new Runnable() {
			@Override
			public void run() {
				openFile(fileHandler, selectedFile);
			}
		};
		// Run the task in a background thread and Terminate the running thread if the application exits
		Thread backgroundThread = new Thread(task);
		backgroundThread.setDaemon(true);
		backgroundThread.start();
	}

	/**
	 * Open the file using the handler. Expected to be run in the background.
	 * 
	 * @param fileHandler
	 * @param selectedFile
	 */
	public void openFile(DexFileHandler fileHandler, File selectedFile) {
		fileHandler.openFile(selectedFile);
		Platform.runLater(() -> postProgress(0.75F));
		modelMgr.add(fileHandler.getNewModel());
		facetTableMgr.clear();

		// When done, update display in the UI thread
		Platform.runLater(() -> {
			memberController.post(modelMgr);
			postStatus("Done");
			postProgress(1F);
		});
	}

	@FXML
	ProgressIndicator statusProgress;

	public void postProgress(double percent) {
		if (statusProgress != null)
			statusProgress.setProgress(percent);
	}

	@FXML
	Label statusLabel;

	public void postStatus(String status) {
		if (statusLabel != null)
			statusLabel.setText(status);
	}

	// Fires whenever a tab is selected. Fires on closed tab and opened tab.
	@FXML
	public void whereUsedTabSelection(Event e) {
		System.out.println("Where used tab selection event");
	}

	@FXML
	public void memberTabSelection(Event e) {
		System.out.println("memberTab selection event");
		// // boolean enabled = memberTab.isDisabled();
		// // if (tableMgr != null) {
		// // if (memberTable != null)
		// // memberTable.setItems(tableMgr.getNodes());
		// // if (memberEditHbox != null && memberEditHbox.getChildren().isEmpty())
		// // tableMgr.getEditPane(memberEditHbox);
		// // // memberEditHbox.getChildren().add(tableMgr.getEditPane());
		// // }
	}

	@FXML
	public ComboBox<String> projectCombo;
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
		// FIXME - use modelMrg's project list
	}

	@FXML
	public void projectComboSelectionListener(Event e) {
		System.out.println("project selection event");
		if (e.getTarget() instanceof ComboBox) {
			String p = (String) ((ComboBox) e.getTarget()).getValue();
			openFile(projectMap.get(p));
			System.out.println("P = " + p);
		}
		// if (e.getTarget() instanceof MenuItem) {
		// File projectFile = (File) ((MenuItem) e.getTarget()).getUserData();
		// openFile(projectFile);
		// }
	}

	public void postCurrentProject() {
	}

	@FXML
	public void deleteProperty(ActionEvent e) {
		System.out.println("Delete Button");
	}

	@FXML
	public void setName(ActionEvent e) {
		System.out.println("set Name");
	}

	@FXML
	public void radioButton1(ActionEvent e) {
		System.out.println("Button1");
	}

	@FXML
	public void radioButton2(ActionEvent e) {
		System.out.println("Button2");
	}

	@FXML
	public void simpleButton(ActionEvent e) {
		System.out.println("simpleButton");
	}

	@FXML
	public void open(ActionEvent e) {
		System.out.println("open");
	}

	@FXML
	public void appExit(ActionEvent e) {
		System.out.println("exit");
		primaryStage.close();
	}

	/**
	 * Called when the user clicks the menu to display the about-application dialog.
	 * 
	 * @param event
	 *            the action event that triggered this method call
	 */
	@FXML
	public void aboutApplication(ActionEvent event) {
		// AboutDialogController.createAboutDialog( getPrimaryStage() ).showAndWait();
	}

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
