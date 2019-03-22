/**
 * 
 */
package org.opentravel.dex.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.repository.tasks.ListSubnamespacesTask;
import org.opentravel.objecteditor.DexIncludedControllerBase;
import org.opentravel.schemacompiler.repository.Repository;
import org.opentravel.schemacompiler.repository.RepositoryException;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Controller for posting a repository to display its namespaces in a tree view.
 * 
 * @author dmh
 *
 */
public class RepositoryNamespacesTreeController extends DexIncludedControllerBase<Repository>
		implements ResultHandlerI {
	private static Log log = LogFactory.getLog(RepositoryNamespacesTreeController.class);

	protected TreeView<NamespacesDAO> tree;
	protected TreeItem<NamespacesDAO> root;
	private HashMap<String, TreeItem<NamespacesDAO>> namespaceMap = new HashMap<>();

	@FXML
	protected TreeView<NamespacesDAO> repositoryNamespacesTree;

	public RepositoryNamespacesTreeController() {
		super();
	}

	@Override
	public void clear() {
		if (tree != null && tree.getRoot() != null)
			tree.getRoot().getChildren().clear();
	}

	@Override
	public ReadOnlyObjectProperty<TreeItem<NamespacesDAO>> getSelectable() {
		return tree.getSelectionModel().selectedItemProperty();
	}

	@Override
	public void initialize() {
		log.debug("Initializing repository tree controller.");

		if (repositoryNamespacesTree == null)
			throw new IllegalArgumentException("Repository tree view is null.");
		this.tree = repositoryNamespacesTree;

		root = initializeTree(tree);
	}

	private TreeItem<NamespacesDAO> initializeTree(TreeView<NamespacesDAO> tree) {
		// Set the hidden root item
		TreeItem<NamespacesDAO> root = new TreeItem<>();
		root.setExpanded(true); // Startout fully expanded
		// Set up the TreeTable
		tree.setRoot(root);
		tree.setShowRoot(false);
		tree.setEditable(true);

		// nsTreeController.getSelectable().addListener((v, old, newValue) -> treeSelectionListener(newValue));
		// tree.getSelectionModel().selectedItemProperty()
		// .addListener((v, old, newValue) -> treeSelectionListener(newValue));
		return root;
	}

	/**
	 * Post the contents from the repository into the tree.
	 * 
	 * @param repository
	 */
	@Override
	public void post(Repository repository) throws Exception {
		super.post(repository);

		// Get the root namespaces in real time
		try {
			for (String rootNS : repository.listRootNamespaces()) {
				TreeItem<NamespacesDAO> item = new TreeItem<>(new NamespacesDAO(rootNS, null, repository));
				item.setExpanded(false);
				root.getChildren().add(item);
				namespaceMap.put(rootNS, item);
				// // Get sub-namespaces in background thread
				new ListSubnamespacesTask(item.getValue(), null, null, this::handle).go();
			}
		} catch (RepositoryException e) {
			log.debug("Error: " + e.getLocalizedMessage());
		}
	}

	@Override
	public void handle(WorkerStateEvent event) {
		if (event.getTarget() instanceof ListSubnamespacesTask) {
			log.debug("Handling sub-namespace task results");
			String fullPath;
			String childNS;
			String parentNS;
			Map<String, NamespacesDAO> map = ((ListSubnamespacesTask) event.getTarget()).getMap();
			for (Entry<String, NamespacesDAO> nsEntry : map.entrySet()) {
				// un-marshal the entry
				fullPath = nsEntry.getValue().getFullPath();
				childNS = nsEntry.getValue().get();
				parentNS = nsEntry.getValue().getBasePath();
				if (parentNS == null || namespaceMap.get(parentNS) == null) {
					log.debug("Skipping.");
					continue;
				}

				TreeItem<NamespacesDAO> item = new TreeItem<>(nsEntry.getValue());
				item.setExpanded(false);
				namespaceMap.put(fullPath, item);

				// null parent is a root already in the tree
				TreeItem<NamespacesDAO> parent = namespaceMap.get(parentNS);
				if (parent != null) {
					parent.getChildren().add(item);
					// item.setGraphic(images.getView(element));
					log.debug("Added " + childNS + "  to  " + parentNS);
				}

			}
		}
	}

}
