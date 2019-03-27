/**
 * 
 */
package org.opentravel.dex.repository.tasks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.repository.RepositorySearchCriteria;
import org.opentravel.dex.repository.TaskResultHandlerI;
import org.opentravel.schemacompiler.repository.LibrarySearchResult;
import org.opentravel.schemacompiler.repository.Repository;
import org.opentravel.schemacompiler.repository.RepositoryException;
import org.opentravel.schemacompiler.repository.RepositoryItem;
import org.opentravel.schemacompiler.repository.RepositorySearchResult;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;

/**
 * A JavaFX task for searching for repository items
 * 
 * @author dmh
 *
 */
public class SearchRepositoryTask extends DexTaskBase<RepositorySearchCriteria> {
	private static Log log = LogFactory.getLog(SearchRepositoryTask.class);

	// private List<RepositorySearchResult> found;
	private Map<String, RepositoryItem> filterMap;

	/**
	 * Create a lock repository item task.
	 * 
	 * @param taskData
	 * @param progressProperty
	 * @param statusProperty
	 * @param handler
	 */
	public SearchRepositoryTask(RepositorySearchCriteria taskData, TaskResultHandlerI handler,
			DoubleProperty progressProperty, StringProperty statusProperty) {
		super(taskData, handler, progressProperty, statusProperty);

		// Replace start message from super-type.
		// msgBuilder = new StringBuilder("Locking: ");
		// msgBuilder.append(taskData.getLibraryName());
		// updateMessage(msgBuilder.toString());
	}

	/**
	 * Searches the contents of the repository using the free-text keywords.
	 * <p>
	 * When latestVersionsOnly selected and when multiple versions of a library match the query, only the latest version
	 * will be returned.
	 * <p>
	 * If status is selected, only versions with the specified status or later will be considered during the search.
	 */
	@Override
	public void doIT() throws RepositoryException {
		Repository repo = taskData.getRepository();

		// // TLLibraryStatus includeStatus = null; // Draft, Review, Final, Obsolete
		// TLLibraryStatus includeStatus = TLLibraryStatus.DRAFT; // Draft, Review, Final, Obsolete
		// // RepositoryItemType itemType = null; // .otm or .otr
		// RepositoryItemType itemType = RepositoryItemType.LIBRARY; // .otm or .otr

		// Run search
		List<RepositorySearchResult> found = repo.search(taskData.getQuery(), taskData.getIncludeStatus(),
				taskData.isLatestVersionsOnly(), taskData.getItemType());

		// Without itemType set, list contains EntitySearchResult(s) and LibrarySearchResult(s)
		// Library results contain a repositoryItem
		// Entity contains: object (bo, core, choice...), object type, repositoryItem

		//
		// Package up a map of namespaces (repoItem.baseNamespace() : repoItem) as filter selector
		// Use keys in namespace tree
		// use entryset for repo items in ns-libraries tree
		// Throw away entity entries
		filterMap = new HashMap<>();
		for (RepositorySearchResult result : found) {
			if (result instanceof LibrarySearchResult) {
				RepositoryItem ri = ((LibrarySearchResult) result).getRepositoryItem();
				if (ri != null)
					filterMap.put(ri.getBaseNamespace(), ri);
			}
		}

		// TODO -
		// Merge into repositorySelectionController???
		// Add filter for object names

		// TODO
		// List<RepositoryItem> locked = taskData.getLockedItems();
		// Clear search
	}

	/**
	 * Get the map of namespaces and repository items that should be included in displayed trees.
	 * 
	 * @return
	 */
	public Map<String, RepositoryItem> getFilterMap() {
		return filterMap;
	}
}
