/**
 * 
 */
package org.opentravel.dex.repository;

import org.opentravel.schemacompiler.model.TLLibraryStatus;
import org.opentravel.schemacompiler.repository.Repository;
import org.opentravel.schemacompiler.repository.RepositoryItemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Object for repository search terms and criteria.
 * 
 * @author dmh
 *
 */
public class RepositorySearchCriteria {
	private static final Logger LOGGER = LoggerFactory.getLogger(RepositorySearchCriteria.class);

	private String query;
	private Repository repository;

	private boolean latestVersionsOnly = false;
	private boolean lockedOnly = false;

	// TLLibraryStatus includeStatus = null; // Draft, Review, Final, Obsolete
	private TLLibraryStatus includeStatus = TLLibraryStatus.DRAFT; // Draft, Review, Final, Obsolete

	// RepositoryItemType itemType = null; // .otm or .otr
	private RepositoryItemType itemType = RepositoryItemType.LIBRARY; // .otm or .otr

	public RepositorySearchCriteria(Repository repository, String query) {
		this.repository = repository;
		this.query = query;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public boolean isLatestVersionsOnly() {
		return latestVersionsOnly;
	}

	public void setLatestVersionsOnly(boolean latestVersionsOnly) {
		this.latestVersionsOnly = latestVersionsOnly;
	}

	public boolean isLockedOnly() {
		return lockedOnly;
	}

	public void setLockedOnly(boolean lockedOnly) {
		this.lockedOnly = lockedOnly;
	}

	public TLLibraryStatus getIncludeStatus() {
		return includeStatus;
	}

	public void setIncludeStatus(TLLibraryStatus includeStatus) {
		this.includeStatus = includeStatus;
	}

	public RepositoryItemType getItemType() {
		return itemType;
	}

	public void setItemType(RepositoryItemType itemType) {
		this.itemType = itemType;
	}

	public Repository getRepository() {
		return repository;
	}

}
