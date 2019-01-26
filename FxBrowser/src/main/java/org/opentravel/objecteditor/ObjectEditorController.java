/**
 * 
 */
package org.opentravel.objecteditor;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.opentravel.common.RepositoryController;
import org.opentravel.model.OtmModelManager;
import org.opentravel.objecteditor.NavigationTreeManager.TreeNode;
import org.opentravel.objecteditor.NavigationTreeTableManager.OtmTreeTableNode;
import org.opentravel.schemacompiler.repository.Repository;
import org.opentravel.schemacompiler.repository.RepositoryException;
import org.opentravel.schemacompiler.repository.RepositoryItem;
import org.opentravel.schemacompiler.repository.RepositoryManager;
import org.opentravel.upversion.RepositoryItemWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import OTM_FX.FxBrowser.DemoNode;
import OTM_FX.FxBrowser.TableManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * @author dmh
 *
 */
@SuppressWarnings("restriction")
public class ObjectEditorController implements Initializable {
	private static final Logger LOGGER = LoggerFactory.getLogger(ObjectEditorController.class);

	// public class repoTree extends TreeView {
	// }

	// @FXML
	// // public TreeView<OtmLibraryMember<?>> treeView;
	// public TreeView<NavigationTreeManager.TreeNode> treeView;
	@FXML
	public TreeView<TreeNode> navigationTreeView;
	NavigationTreeManager treeMgr;

	@FXML
	public TreeTableView<OtmTreeTableNode> navTreeTableView;
	NavigationTreeTableManager treeTableMgr;

	// @FXML
	// public TreeTableView<DemoNode> treeTableView;
	// TreeTableManager ttMgr;

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
		configureRepositoryChoice();

		if (navigationTreeView == null)
			throw new IllegalStateException("Tree view is null.");

		// Load and display tree view in left pane
		treeMgr = new NavigationTreeManager(stage, navigationTreeView, model);
		// treeView.setRoot(treeMgr.getRoot());
		// treeView.setShowRoot(false);
		// treeView.getSelectionModel().selectedItemProperty().addListener((v, old, newValue) ->
		// handleTreeItem(newValue));

		treeTableMgr = new NavigationTreeTableManager(stage, navTreeTableView, model);

		// Load and add listener for table
		// same thing as next line
		// memberTab.setOnSelectionChanged(e -> memberTabSelection(e));
		memberTab.setOnSelectionChanged(this::memberTabSelection);
		tableMgr.build(memberTable);

	}

	@Deprecated
	private RepositoryManager repositoryManager;
	// private RepositoryAvailabilityChecker availabilityChecker;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("Controller - View is now loading!");
		// // Set up repository access
		// try {
		// repositoryManager = RepositoryManager.getDefault();
		// availabilityChecker = RepositoryAvailabilityChecker.getInstance(repositoryManager);
		// boolean repoStatus = availabilityChecker.pingAllRepositories(true);
		// } catch (RepositoryException e) {
		// e.printStackTrace(System.out);
		// }
		//
		RepositoryController repoController = new RepositoryController();
		repositoryManager = repoController.getRepositoryManager();
		String[] projects = repoController.getProjects();
	}

	// private void handleTreeItem(TreeItem<String> item) {
	// System.out.println("Tree Item: " + item.getValue() + " from " + item.getParent().getValue());
	// }
	// private void handleTreeItem(TreeItem<OtmLibraryMember<?>> item) {
	// System.out.println("Tree Item: " + item.getValue() + " from " + item.getParent().getValue());
	// }

	@FXML
	public void memberTabSelection(Event e) {
		System.out.println("memberTab selection event");
		// boolean enabled = memberTab.isDisabled();
		if (tableMgr != null) {
			if (memberTable != null)
				memberTable.setItems(tableMgr.getNodes());
			if (memberEditHbox != null && memberEditHbox.getChildren().isEmpty())
				tableMgr.getEditPane(memberEditHbox);
			// memberEditHbox.getChildren().add(tableMgr.getEditPane());
		}

		// if (cPane != null) {
		// ObservableList<Node> list = FXCollections.observableArrayList();
		// cPane.getChildren().addAll(list);
		// }

		DemoNode nodes = new DemoNode();
		// if (treeTableView != null) {
		// ttMgr = new TreeTableManager(treeTableView);
		// System.out.println("Populate Tree Table View.");
		// ttMgr.build(nodes.getNodes());
		// } else
		// System.out.println("Can't populate Tree Table View.");
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

	private void configureRepositoryChoice() {
		System.out.println("Configuring repository choice box.");
		primaryStage.showingProperty().addListener((observable, oldValue, newValue) -> {
			ObservableList<String> repositoryIds = FXCollections.observableArrayList();

			repositoryManager.listRemoteRepositories().forEach(r -> repositoryIds.add(r.getId()));
			repositoryChoice.setItems(repositoryIds);
			repositoryChoice.getSelectionModel().select(0);
		});

		// Configure listeners for choice boxes
		repositoryChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
			repositorySelectionChanged();
		});
		namespaceChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
			namespaceSelectionChanged();
		});
	}

	/**
	 * Called when the user modifies the selection of the 'repositoryChoice' control.
	 * 
	 * @throws RepositoryException
	 */
	private void repositorySelectionChanged() {
		// Runnable r = new BackgroundTask("Updating namespaces from remote repository...", StatusType.INFO) {
		// public void execute() throws Throwable {
		String rid = repositoryChoice.getSelectionModel().getSelectedItem();
		Repository repository = repositoryManager.getRepository(rid);
		try {
			List<String> baseNamespaces = repository.listBaseNamespaces();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		System.out.println("Selected new repository");

		List<String> baseNamespaces;
		try {
			baseNamespaces = repository.listBaseNamespaces();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			baseNamespaces = new ArrayList<>();
		}
		baseNamespaces.add(0, null);
		// Platform.runLater(() -> {
		namespaceChoice.setItems(FXCollections.observableList(baseNamespaces));
		namespaceChoice.getSelectionModel().select(0);
		// });
		System.out.println("Added namespaces to choice.");
	}

	// };
	//
	// new Thread(r).start();
	// }
	/**
	 * Returns the list of candidate namespaces that are either equal to or sub-namespaces of the currently selected
	 * namespace.
	 * 
	 * @return List<String>
	 */
	private List<String> getCandidateNamespaces() {
		List<String> candidateNamespaces = new ArrayList<>();
		String selectedNS = namespaceChoice.getSelectionModel().getSelectedItem();
		String nsPrefix = selectedNS + "/";

		candidateNamespaces.add(selectedNS);

		for (String ns : namespaceChoice.getItems()) {
			if ((ns != null) && ns.startsWith(nsPrefix)) {
				candidateNamespaces.add(ns);
			}
		}
		return candidateNamespaces;
	}

	/**
	 * Called when the user modifies the selection of the 'namespaceChoice' control.
	 */
	private void namespaceSelectionChanged() {
		String selectedNS = namespaceChoice.getSelectionModel().getSelectedItem();

		if (selectedNS != null) {
			// Runnable r = new BackgroundTask( "Updating candidate libraries from remote repository...",
			// StatusType.INFO ) {
			// public void execute() throws Throwable {
			String rid = repositoryChoice.getSelectionModel().getSelectedItem();
			Repository repository = repositoryManager.getRepository(rid);
			List<RepositoryItemWrapper> selectedItems = null;
			if (selectedLibrariesTable != null)
				selectedItems = selectedLibrariesTable.getItems();
			List<String> candidateNamespaces = getCandidateNamespaces();
			List<RepositoryItemWrapper> candidateItems = new ArrayList<>();

			for (String candidateNS : candidateNamespaces) {
				List<RepositoryItem> items = null;
				try {
					items = repository.listItems(candidateNS, null, true);
				} catch (RepositoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				for (RepositoryItem item : items) {
					if (selectedItems != null && !selectedItems.contains(item)) {
						candidateItems.add(new RepositoryItemWrapper(item));
					}
				}
			}
			Collections.sort(candidateItems);

			// Platform.runLater( () -> {
			selectedLibrariesTable.setItems(FXCollections.observableList(candidateItems));
			// candidateLibrariesTable.setItems( FXCollections.observableList( candidateItems ) );
			// });
		}

		// };
		//
		// new Thread( r ).start();
		//
		// } else {
		// Platform.runLater( () -> {
		// candidateLibrariesTable.setItems( FXCollections.emptyObservableList() );
		// updateControlStates();
		// });
		// }
	}

}
