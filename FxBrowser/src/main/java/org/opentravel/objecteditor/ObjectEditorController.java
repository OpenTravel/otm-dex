/**
 * 
 */
package org.opentravel.objecteditor;

import java.net.URL;
import java.util.EnumMap;
import java.util.ResourceBundle;

import org.opentravel.common.RepositoryController;
import org.opentravel.model.OtmModelManager;
import org.opentravel.objecteditor.NavigationTreeTableHandler.OtmTreeTableNode;
import org.opentravel.objecteditor.RepositoryTabController.RepoTabNodes;
import org.opentravel.schemacompiler.repository.RepositoryManager;
import org.opentravel.upversion.RepositoryItemWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import OTM_FX.FxBrowser.DemoNode;
import OTM_FX.FxBrowser.TableManager;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main controller for OtmObjecEditorLayout.fxml (1 FXML = 1Controller).
 * 
 * @author dmh
 *
 */
@SuppressWarnings("restriction")
public class ObjectEditorController implements Initializable {
	private static final Logger LOGGER = LoggerFactory.getLogger(ObjectEditorController.class);

	// Navigation Table Tree View
	//
	@FXML
	public TreeTableView<OtmTreeTableNode> navTreeTableView;
	NavigationTreeTableHandler treeTableMgr;

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
	OtmModelManager model;

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
		model = new OtmModelManager();
		model.createTestLibrary();
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

		treeTableMgr = new NavigationTreeTableHandler(stage, navTreeTableView, model);

	}

	@Deprecated
	private RepositoryManager repositoryManager;
	// private RepositoryAvailabilityChecker availabilityChecker;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("Object Editor Controller - Initialize w/params is now loading!");
		RepositoryController repoController = new RepositoryController();
		repositoryManager = repoController.getRepositoryManager();
		String[] projects = repoController.getProjects();

	}

	// Fires whenever a tab is selected. Fires on closed tab and opened tab.
	@FXML
	public void whereUsedTabSelection(Event e) {
		System.out.println("Where used tab selection event");
	}

	@FXML
	public void memberTabSelection(Event e) {
		System.out.println("memberTab selection event");
		// boolean enabled = memberTab.isDisabled();
		// if (tableMgr != null) {
		// if (memberTable != null)
		// memberTable.setItems(tableMgr.getNodes());
		// if (memberEditHbox != null && memberEditHbox.getChildren().isEmpty())
		// tableMgr.getEditPane(memberEditHbox);
		// // memberEditHbox.getChildren().add(tableMgr.getEditPane());
		// }
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

}
