/**
 * 
 */
package org.opentravel.objecteditor;

import java.util.EnumMap;

import org.opentravel.common.ImageManager;
import org.opentravel.common.RepositoryController;
import org.opentravel.model.OtmModelManager;
import org.opentravel.objecteditor.NamespaceLibrariesTableController.RepoItemNode;
import org.opentravel.objecteditor.RepositoryNamespacesTreeController.NamespaceNode;
import org.opentravel.schemacompiler.repository.Repository;
import org.opentravel.schemacompiler.repository.RepositoryException;
import org.opentravel.schemacompiler.repository.RepositoryManager;
import org.opentravel.schemacompiler.repository.impl.RemoteRepositoryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

//import javafx.scene.Node;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.control.TreeView;
//import javafx.scene.control.TreeItem;
//import javafx.util.converter.IntegerStringConverter;
//javafx.beans.property.SimpleBooleanProperty
// import javafx.beans.property.ReadOnlyStringWrapper;
//javafx.beans.property.ReadOnlyBooleanWrapper
//javafx.beans.property.SimpleintegerProperty
//javafx.beans.property.ReadOnlyintegerWrapper
//javafx.beans.property.SimpleDoubleProperty
//javafx.beans.property.ReadOnlyDoubleWrapper
//javafx.beans.property.ReadOnlyStringWrapper
//import javafx.beans.property.StringProperty;
//import javafx.beans.property.SimpleStringProperty;

/**
 * Manage the repository tab.
 * 
 * @author dmh
 *
 */
@SuppressWarnings("restriction")
public class RepositoryTabController implements DexController {
	private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryTabController.class);

	private static final String LOCAL_REPO = "Local";

	private RepositoryManager repositoryManager;
	protected RepositoryController repoController;
	// private RepositoryAvailabilityChecker availabilityChecker;

	protected ImageManager imageMgr;
	protected Stage stage;

	private RepositoryNamespacesTreeController nsTreeController;
	private NamespaceLibrariesTableController nsLibsController;
	private LibraryHistoryItemsController libHistoryController;

	/**
	 * FXML Java FX Nodes this controller is dependent upon
	 * 
	 * @author dmh
	 *
	 */
	public enum RepoTabNodes {
		TAB, RepositoryChoice, User, NamespaceTree, NamespaceLibraryTable, HistoryTable, NamespacePermission;
	}

	private Label nsPermission;
	private TreeView<NamespaceNode> tree;
	private ChoiceBox<String> repositoryChoice;
	private TreeTableView<RepoItemNode> libTable;
	private TextField userField;
	public TableView historyTable;

	@SuppressWarnings("unchecked")
	private void getRepoNodes(EnumMap<RepoTabNodes, Node> fxNodes) {

		repositoryChoice = (ChoiceBox<String>) fxNodes.get(RepoTabNodes.RepositoryChoice);
		libTable = (TreeTableView<RepoItemNode>) fxNodes.get(RepoTabNodes.NamespaceLibraryTable);
		nsPermission = (Label) fxNodes.get(RepoTabNodes.NamespacePermission);
		tree = (TreeView<NamespaceNode>) fxNodes.get(RepoTabNodes.NamespaceTree);
		historyTable = (TableView) fxNodes.get(RepoTabNodes.HistoryTable);
		userField = (TextField) fxNodes.get(RepoTabNodes.User);

		if (repositoryChoice == null)
			throw new IllegalStateException("Null control nodes passed to repsitory tab handler.");
		if (libTable == null)
			throw new IllegalArgumentException("Namespace Library tree table view is null.");
		if (nsPermission == null)
			throw new IllegalArgumentException("Namespace permission field is null.");
		if (tree == null)
			throw new IllegalArgumentException("Repository tree view is null.");
		if (userField == null)
			throw new IllegalArgumentException(" null.");
		if (historyTable == null)
			throw new IllegalArgumentException(" null.");
	}

	public RepositoryTabController(Stage stage, ObjectEditorController parent, EnumMap<RepoTabNodes, Node> fxNodes) {
		this.stage = stage;
		if (stage == null)
			throw new IllegalStateException("Stage is null.");
		imageMgr = new ImageManager(stage);

		getRepoNodes(fxNodes);
		nsTreeController = new RepositoryNamespacesTreeController(this, tree);
		nsTreeController.getSelectable().addListener((v, old, newValue) -> treeSelectionListener(newValue));

		nsLibsController = new NamespaceLibrariesTableController(this, libTable, nsPermission);
		nsLibsController.getSelectable().addListener((v, old, newValue) -> librarySelectionListener(newValue));

		libHistoryController = new LibraryHistoryItemsController(this, historyTable);

		// Set up repository Choice
		repoController = new RepositoryController();
		repositoryManager = repoController.getRepositoryManager(); // FIXME
		configureRepositoryChoice();

	}

	private void librarySelectionListener(TreeItem<RepoItemNode> item) {
		if (item == null)
			return;
		System.out.println("Library selected: " + item.getValue());
		libHistoryController.post(item.getValue());
	}

	private void treeSelectionListener(TreeItem<NamespaceNode> item) {
		if (item == null)
			return;
		System.out.println("New tree item selected: " + item.getValue());
		NamespaceNode nsNode = item.getValue();
		if (nsNode.repository != null) {
			try {
				nsLibsController.post(nsNode.repository, nsNode.getFullPath());
				libHistoryController.clear();
			} catch (RepositoryException e) {
				System.out.println("Error accessing namespace: " + e.getLocalizedMessage());
			}
		}
	}

	private void configureRepositoryChoice() {
		System.out.println("Configuring repository choice box.");
		stage.showingProperty().addListener((observable, oldValue, newValue) -> {
			ObservableList<String> repositoryIds = FXCollections.observableArrayList();
			repositoryIds.add(LOCAL_REPO);
			repositoryManager.listRemoteRepositories().forEach(r -> repositoryIds.add(r.getId()));
			repositoryChoice.setItems(repositoryIds);
			repositoryChoice.getSelectionModel().select(0);
		});

		// Configure listener for choice box
		repositoryChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
			repositorySelectionChanged();
		});
	}

	/**
	 * Called when the user modifies the selection of the 'repositoryChoice' control.
	 * 
	 * @throws RepositoryException
	 */
	private void repositorySelectionChanged() {
		System.out.println("Selected new repository");

		// Pass the repository to the nsTree
		Repository repository;
		try {
			repository = getSelectedRepository();
			postUser(repository);
			nsTreeController.post(repository);
		} catch (RepositoryException e) {
			System.out.println("Error: " + e.getLocalizedMessage());
		}
	}

	/**
	 * Add tree items to ROOT for each child and grandchild of the member.
	 * 
	 * @param member
	 * @throws RepositoryException
	 */
	private Repository getSelectedRepository() throws RepositoryException {
		Repository repository = repoController.getLocalRepository();
		String rid = repositoryChoice.getSelectionModel().getSelectedItem();
		if (rid != null)
			if (rid.equals(LOCAL_REPO))
				repository = repoController.getLocalRepository();
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
		nsLibsController.clear();
	}

	@Override
	public ImageManager getImageManager() {
		if (imageMgr == null)
			throw new IllegalStateException("Image manger is null.");
		return imageMgr;
	}

	@Override
	public ReadOnlyObjectProperty<TreeItem<NamespaceNode>> getSelectable() {
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

}
