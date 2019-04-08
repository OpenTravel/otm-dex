/**
 * 
 */
package org.opentravel.dex.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.dex.controllers.DexStatusController;
import org.opentravel.dex.controllers.dialogbox.DialogBoxContoller;
import org.opentravel.dex.controllers.dialogbox.UnlockLibraryDialogContoller;
import org.opentravel.model.OtmModelManager;
import org.opentravel.objecteditor.DexMainController;
import org.opentravel.objecteditor.NamespaceLibrariesTableController.RepoItemNode;
import org.opentravel.schemacompiler.repository.RepositoryException;
import org.opentravel.schemacompiler.repository.RepositoryManager;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

/**
 * Manage the repository tab.
 * 
 * @author dmh
 *
 */
public class RepositoryTabController implements DexMainController {
	private static Log log = LogFactory.getLog(RepositoryTabController.class);

	private static final String LOCAL_REPO = "Local";

	@FXML
	private RepositoryNamespacesTreeController repositoryNamespacesTreeController;
	@FXML
	private NamespaceLibrariesTreeTableController namespaceLibrariesTreeTableController;
	@FXML
	private RepositoryItemCommitHistoriesController repositoryItemCommitHistoriesController;
	@FXML
	private RepositorySelectionController repositorySelectionController;

	protected ImageManager imageMgr;
	protected Stage stage;
	private DexMainController parentController;
	private RepositoryManager repositoryManager;
	// protected RepositoryController repoController;
	// private RepositoryAvailabilityChecker availabilityChecker;
	private DexStatusController dexStatusController;
	private DialogBoxContoller dialogBoxController;
	private UnlockLibraryDialogContoller unlockDialogController;

	// private RepositoryNamespacesController nsTreeController;
	// private NamespaceLibrariesTableController nsLibsController;
	// private LibraryHistoryItemsController libHistoryController;

	/**
	 * FXML Java FX Nodes this controller is dependent upon
	 * 
	 * @author dmh
	 *
	 */
	// public enum RepoTabNodes {
	// TAB, RepositoryChoice, User, NamespaceTree, NamespaceLibraryTable, HistoryTable, NamespacePermission;
	// }

	private Label nsPermission;
	private TreeView<NamespacesDAO> tree;
	private ChoiceBox<String> repositoryChoice;
	private TreeTableView<RepoItemNode> libTable;
	private TextField userField;
	public TableView historyTable;

	// @SuppressWarnings("unchecked")
	// private void getRepoNodes(EnumMap<RepoTabNodes, Node> fxNodes) {
	//
	// repositoryChoice = (ChoiceBox<String>) fxNodes.get(RepoTabNodes.RepositoryChoice);
	// libTable = (TreeTableView<RepoItemNode>) fxNodes.get(RepoTabNodes.NamespaceLibraryTable);
	// nsPermission = (Label) fxNodes.get(RepoTabNodes.NamespacePermission);
	// tree = (TreeView<NamespacesDAO>) fxNodes.get(RepoTabNodes.NamespaceTree);
	// historyTable = (TableView) fxNodes.get(RepoTabNodes.HistoryTable);
	// userField = (TextField) fxNodes.get(RepoTabNodes.User);
	//
	// if (repositoryChoice == null)
	// throw new IllegalStateException("Null control nodes passed to repsitory tab handler.");
	// if (libTable == null)
	// throw new IllegalArgumentException("Namespace Library tree table view is null.");
	// if (nsPermission == null)
	// throw new IllegalArgumentException("Namespace permission field is null.");
	// if (tree == null)
	// throw new IllegalArgumentException("Repository tree view is null.");
	// if (userField == null)
	// throw new IllegalArgumentException(" null.");
	// if (historyTable == null)
	// throw new IllegalArgumentException(" null.");
	// }

	public RepositoryTabController() {
		// this.stage = stage;
		// if (stage == null)
		// throw new IllegalStateException("Stage is null.");
		// imageMgr = new ImageManager(stage);
		//
		// parentController = parent;
		//
		// getRepoNodes(fxNodes);
		// nsTreeController = new RepositoryNamespacesController(this, tree);
		// nsTreeController.getSelectable().addListener((v, old, newValue) -> treeSelectionListener(newValue));
		//
		// nsLibsController = new NamespaceLibrariesTableController(this, libTable, nsPermission);
		// nsLibsController.getSelectable().addListener((v, old, newValue) -> librarySelectionListener(newValue));
		//
		// libHistoryController = new LibraryHistoryItemsController(this, historyTable);

		// Set up repository Choice
		// repoController = new RepositoryController();
		// repositoryManager = repoController.getRepositoryManager(); // FIXME
		// repositoryManager = getRepoMgr();
		// configureRepositoryChoice();

		log.debug("Repository Tab Controller constructed.");
	}

	@FXML
	public void initialize() {
		log.debug("Repository Tab Controller initialized.");
		// Get user settings / preferences
		// UserSettings settings = UserSettings.load();
		// settings.getWindowSize();
	}

	/**
	 * @param primaryStage
	 */
	public void setStage(Stage primaryStage, DexMainController parent) {
		// These may be needed by sub-controllers
		this.stage = primaryStage;
		this.parentController = parent;
		imageMgr = new ImageManager(primaryStage);
		checkNodes();

		// repositorySearchController.setParent(this);
		// repositorySearchController.setStage();
		// repositorySearchController.setRepository(null);

		// Set up the repository selection
		repositorySelectionController.setStage();
		repositorySelectionController.setParent(this);
		repositorySelectionController.getSelectable().addListener((v, old, newV) -> repositorySelectionChanged(newV));

		// Set up repository namespaces tree
		repositoryNamespacesTreeController.setParent(this);
		repositoryNamespacesTreeController.getSelectable()
				.addListener((v, old, newValue) -> namespaceSelectionListener(newValue));
		// repositoryNamespacesTreeController.setFilter(repositorySearchController);

		// Set up the libraries in a namespace table
		namespaceLibrariesTreeTableController.setParent(this);
		namespaceLibrariesTreeTableController.getSelectable()
				.addListener((v, old, newValue) -> repoItemSelectionListener(newValue));

		dexStatusController = parent.getStatusController();

		// initialize Dialog Box with a new dynamic loader
		FXMLLoader loader = new FXMLLoader(getClass().getResource(DialogBoxContoller.LAYOUT_FILE));
		dialogBoxController = DialogBoxContoller.init(loader);
		if (dialogBoxController == null)
			throw new IllegalStateException("Dialog box controller is null.");

		// initialize unlock Dialog Box using a new dynamic loader
		loader = new FXMLLoader(getClass().getResource(UnlockLibraryDialogContoller.LAYOUT_FILE));
		unlockDialogController = UnlockLibraryDialogContoller.init(loader);
		if (unlockDialogController == null)
			throw new IllegalStateException("Could not load unlock dialog controller.");

		log.debug("Repository Tab Stage set.");
	}

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

	private void repoItemSelectionListener(TreeItem<RepoItemDAO> item) {
		if (item == null)
			return;
		log.debug("Repository Item selected: " + item.getValue());
		// libHistoryController.post(item.getValue());
	}

	private void namespaceSelectionListener(TreeItem<NamespacesDAO> item) {
		if (item == null)
			return;
		log.debug("Namespace  selected: " + item.getValue());
		// NamespacesDAO nsNode = item.getValue();
		// if (nsNode.getRepository() != null) {
		// try {
		// nsLibsController.post(nsNode.getRepository(), nsNode.getFullPath());
		// libHistoryController.clear();
		// } catch (RepositoryException e) {
		// log.debug("Error accessing namespace: " + e.getLocalizedMessage());
		// }
		// }
	}

	// private void configureRepositoryChoice() {
	// log.debug("Configuring repository choice box.");
	// stage.showingProperty().addListener((observable, oldValue, newValue) -> {
	// ObservableList<String> repositoryIds = FXCollections.observableArrayList();
	// repositoryIds.add(LOCAL_REPO);
	// repositoryManager.listRemoteRepositories().forEach(r -> repositoryIds.add(r.getId()));
	// repositoryChoice.setItems(repositoryIds);
	// repositoryChoice.getSelectionModel().select(0);
	// });
	//
	// // Configure listener for choice box
	// repositoryChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
	// repositorySelectionChanged();
	// });
	// }

	/**
	 * Called when the user modifies the selection of the 'repositoryChoice' control.
	 * 
	 * @throws RepositoryException
	 */
	private void repositorySelectionChanged(String newValue) {
		log.debug("Selected new repository: " + newValue);

		// Pass the repository to the nsTree
		// Repository repository;
		// try {
		// repository = getSelectedRepository();
		// postUser(repository);
		// nsTreeController.post(repository);
		// } catch (RepositoryException e) {
		// log.debug("Error: " + e.getLocalizedMessage());
		// }
	}

	// /**
	// * Add tree items to ROOT for each child and grandchild of the member.
	// *
	// * @param member
	// * @throws RepositoryException
	// */
	// private Repository getSelectedRepository() throws RepositoryException {
	// Repository repository = RepositoryManager.getDefault();
	// // repoController.getLocalRepository();
	// String rid = repositoryChoice.getSelectionModel().getSelectedItem();
	// if (rid != null)
	// if (rid.equals(LOCAL_REPO))
	// repository = RepositoryManager.getDefault();
	// // repository = repoController.getLocalRepository();
	// else
	// // Use selected repository
	// repository = repositoryManager.getRepository(rid);
	// return repository;
	// }

	// private void postUser(Repository repository) {
	// String user = "--local--";
	// if (repository instanceof RemoteRepositoryClient)
	// user = ((RemoteRepositoryClient) repository).getUserId();
	// userField.setText(user);
	// }

	/**
	 * Remove all items from the table
	 */
	@Override
	public void clear() {
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

	@Override
	public void postStatus(String string) {
		parentController.postStatus(string);
	}

	@Override
	public void postProgress(double percentDone) {
		parentController.postProgress(percentDone);
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public RepositoryManager getRepositoryManager() {
		return repositoryManager;
	}

	@Override
	public DexStatusController getStatusController() {
		// TODO Auto-generated method stub
		return dexStatusController;
	}

}
