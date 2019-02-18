/**
 * 
 */
package org.opentravel.objecteditor;

import java.util.EnumMap;

import org.opentravel.common.RepositoryController;
import org.opentravel.objecteditor.NamespaceLibrariesTableController.RepoItemNode;
import org.opentravel.objecteditor.NamespaceTreeController.NamespaceNode;
import org.opentravel.schemacompiler.repository.Repository;
import org.opentravel.schemacompiler.repository.RepositoryException;
import org.opentravel.schemacompiler.repository.RepositoryManager;
import org.opentravel.schemacompiler.repository.impl.RemoteRepositoryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

//import javafx.scene.Node;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.control.TreeView;
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

	private NamespaceTreeController nsTreeController;
	private NamespaceLibrariesTableController nsLibsController;

	/**
	 * FXML Java FX Nodes this controller is dependent upon
	 * 
	 * @author dmh
	 *
	 */
	public enum RepoTabNodes {
		TAB, RepositoryChoice, NamespaceTree, NamespaceLibraryTable, HistoryTable, NamespacePermission;
	}

	private TextField nsPermissionField;
	private TreeView<NamespaceNode> tree;
	private ChoiceBox<String> repositoryChoice;
	private TreeTableView<RepoItemNode> libTable;

	@SuppressWarnings("unchecked")
	private void getRepoNodes(EnumMap<RepoTabNodes, Node> fxNodes) {

		repositoryChoice = (ChoiceBox<String>) fxNodes.get(RepoTabNodes.RepositoryChoice);
		libTable = (TreeTableView<RepoItemNode>) fxNodes.get(RepoTabNodes.NamespaceLibraryTable);
		nsPermissionField = (TextField) fxNodes.get(RepoTabNodes.NamespacePermission);
		tree = (TreeView<NamespaceNode>) fxNodes.get(RepoTabNodes.NamespaceTree);

		if (repositoryChoice == null)
			throw new IllegalStateException("Null control nodes passed to repsitory tab handler.");
		if (libTable == null)
			throw new IllegalArgumentException("Namespace Library tree table view is null.");
		if (nsPermissionField == null)
			throw new IllegalArgumentException("Namespace permission field is null.");
		if (tree == null)
			throw new IllegalArgumentException("Repository tree view is null.");
	}

	public RepositoryTabController(Stage stage, ObjectEditorController parent, EnumMap<RepoTabNodes, Node> fxNodes) {
		this.stage = stage;
		if (stage == null)
			throw new IllegalStateException("Stage is null.");
		imageMgr = new ImageManager(stage);

		getRepoNodes(fxNodes);
		nsLibsController = new NamespaceLibrariesTableController(this, libTable, nsPermissionField);
		nsTreeController = new NamespaceTreeController(stage, tree, libTable, nsPermissionField, repositoryChoice,
				null);

		// TODO - move management of library table view to here.

		// Set up repository Choice
		repoController = new RepositoryController();
		repositoryManager = repoController.getRepositoryManager(); // FIXME
		configureRepositoryChoice();

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
		// FIXME - post the user
		String user = "";
		if (repository instanceof RemoteRepositoryClient)
			user = ((RemoteRepositoryClient) repository).getUserId();
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
		return imageMgr;
	}

}
