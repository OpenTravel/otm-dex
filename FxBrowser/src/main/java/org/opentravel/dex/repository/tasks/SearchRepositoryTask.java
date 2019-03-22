/**
 * 
 */
package org.opentravel.dex.repository.tasks;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.repository.ResultHandlerI;
import org.opentravel.schemacompiler.model.TLLibraryStatus;
import org.opentravel.schemacompiler.repository.Repository;
import org.opentravel.schemacompiler.repository.RepositoryException;
import org.opentravel.schemacompiler.repository.RepositoryItem;
import org.opentravel.schemacompiler.repository.RepositoryItemType;
import org.opentravel.schemacompiler.repository.RepositorySearchResult;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;

/**
 * A JavaFX task for searching for repository items
 * 
 * @author dmh
 *
 */
public class SearchRepositoryTask extends DexTaskBase<Repository> {
	private static Log log = LogFactory.getLog(SearchRepositoryTask.class);

	/**
	 * Create a lock repository item task.
	 * 
	 * @param taskData
	 * @param progressProperty
	 * @param statusProperty
	 * @param handler
	 */
	public SearchRepositoryTask(Repository taskData, DoubleProperty progressProperty, StringProperty statusProperty,
			ResultHandlerI handler) {
		super(taskData, progressProperty, statusProperty, handler);

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
		String freeTextQuery = null;
		TLLibraryStatus includeStatus = null; // Draft, Review, Final, Obsolete
		boolean latestVersionsOnly = false;
		RepositoryItemType itemType = null; // .otm or .otr
		List<RepositorySearchResult> found = taskData.search(freeTextQuery, includeStatus, latestVersionsOnly,
				itemType);

		List<RepositoryItem> locked = taskData.getLockedItems();
	}

}
