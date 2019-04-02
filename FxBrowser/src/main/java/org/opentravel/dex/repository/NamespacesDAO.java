/**
 * 
 */
package org.opentravel.dex.repository;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.dex.repository.tasks.GetRepositoryItemsTask;
import org.opentravel.objecteditor.DexDAO;
import org.opentravel.schemacompiler.model.TLLibraryStatus;
import org.opentravel.schemacompiler.repository.Repository;
import org.opentravel.schemacompiler.repository.RepositoryItem;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.image.ImageView;

/**
 * Data Access Object (DAO) for displaying repository namespaces in a tree view.
 * 
 * @author dmh
 *
 */
public class NamespacesDAO implements DexDAO<String>, TaskResultHandlerI {
	private static Log log = LogFactory.getLog(NamespacesDAO.class);

	// Namespaces - for root namespaces it will be like: http://www.opentravel.org/OTM
	// for sub-namespaces it will be just the sub-ns: e.g. hospitality
	protected String ns;

	// BasePath is the path of parent or null if root namespace
	// e.g. http://www.opentravel.org/OTM/product
	protected String basePath;

	private Repository repository;
	//
	private String decoration = "";
	private String permission = "Unknown";

	private List<RepositoryItem> allItems = null;
	private List<RepositoryItem> latestItems = null;

	public NamespacesDAO(String ns, String basePath, Repository repo) {
		this.ns = ns;
		this.basePath = basePath;
		this.setRepository(repo);

		// task to retrieve items to allow filter by item type (Draft, etc) or Locked
		new GetRepositoryItemsTask(this, this::handleTaskComplete, null).go();
	}

	public List<RepositoryItem> getAllItems(TLLibraryStatus includeStatus, boolean lockedOnly) {
		List<RepositoryItem> selected = new ArrayList<>();
		for (RepositoryItem item : allItems) {
			if (item.getStatus().compareTo(includeStatus) < 0)
				continue;
			if (item.getLibraryName() == null && lockedOnly)
				continue;
			selected.add(item);
		}
		return selected;
	}

	@Override
	public void handleTaskComplete(WorkerStateEvent event) {
		if (event.getTarget() instanceof GetRepositoryItemsTask) {
			permission = ((GetRepositoryItemsTask) event.getTarget()).getPermission();
			allItems = ((GetRepositoryItemsTask) event.getTarget()).getAllItems();
			latestItems = ((GetRepositoryItemsTask) event.getTarget()).getLatestItems();
			int locked = 0;
			for (RepositoryItem item : allItems)
				if (item.getLockedByUser() != null)
					locked++;
			decoration = "   ( " + allItems.size() + "/" + locked + " )";
		}
	}

	public List<RepositoryItem> getAllItems() {
		return allItems;
	}

	public List<RepositoryItem> getLatestItems() {
		return latestItems;
	}

	public StringProperty permissionProperty() {
		return new ReadOnlyStringWrapper(permission);
		// return new ReadOnlyStringWrapper(permission);
	}
	// public String getPermission() {
	// return permission;
	// }

	public StringProperty nsProperty() {
		return new SimpleStringProperty(ns);
	}

	/**
	 * Used by tree item for displayed value.
	 */
	@Override
	public String toString() {
		return ns + decoration;
	}

	@Override
	public String getValue() {
		return ns;
	}
	//
	// public ImageView getIcon() {
	// return images.getView(element.getIconType());
	// }
	//

	/**
	 * Get the unique name (key) for this namespace. If not a root namespace, the parent's path will be added.
	 * 
	 * @return
	 */
	public String getFullPath() {
		return basePath != null ? basePath + "/" + ns : ns;
	}

	public StringProperty fullPathProperty() {
		return new ReadOnlyStringWrapper(basePath != null ? basePath + "/" + ns : ns);
	}

	public String getBasePath() {
		return basePath;
	}

	/**
	 * @return the repository
	 */
	public Repository getRepository() {
		return repository;
	}

	/**
	 * @param repository
	 *            the repository to set
	 */
	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	/**
	 * @return the namespace without base path
	 */
	public String get() {
		return ns;
	}

	@Override
	public ImageView getIcon(ImageManager imageMgr) {
		return null;
	}
}
