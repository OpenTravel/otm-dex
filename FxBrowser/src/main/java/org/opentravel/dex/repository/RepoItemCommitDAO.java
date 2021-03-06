/**
 * 
 */
package org.opentravel.dex.repository;

import org.opentravel.common.ImageManager;
import org.opentravel.dex.controllers.DexDAO;
import org.opentravel.schemacompiler.repository.RepositoryItemCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.StringProperty;
import javafx.scene.image.ImageView;

/**
 * Data Access Object for repository item commit data used in library history table.
 * 
 * @author dmh
 *
 */
public class RepoItemCommitDAO implements DexDAO<RepositoryItemCommit> {
	private static final Logger LOGGER = LoggerFactory.getLogger(RepoItemCommitDAO.class);

	private RepositoryItemCommit item;

	public RepoItemCommitDAO(RepositoryItemCommit item) {
		this.item = item;
	}

	public StringProperty numberProperty() {
		return new ReadOnlyStringWrapper(Integer.toString(item.getCommitNumber()));
	}

	public StringProperty effectiveProperty() {
		return new ReadOnlyStringWrapper(item.getEffectiveOn().toString());
	}

	public StringProperty userProperty() {
		return new ReadOnlyStringWrapper(item.getUser());
	}

	public StringProperty remarksProperty() {
		return new ReadOnlyStringWrapper(item.getRemarks());
	}

	@Override
	public ImageView getIcon(ImageManager imageMgr) {
		return null;
	}

	@Override
	public RepositoryItemCommit getValue() {
		return item;
	}
}
