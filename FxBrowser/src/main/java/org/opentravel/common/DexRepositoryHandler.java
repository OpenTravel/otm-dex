/**
 * 
 */
package org.opentravel.common;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.schemacompiler.repository.RemoteRepository;
import org.opentravel.schemacompiler.repository.Repository;
import org.opentravel.schemacompiler.repository.RepositoryAvailabilityChecker;
import org.opentravel.schemacompiler.repository.RepositoryException;
import org.opentravel.schemacompiler.repository.RepositoryItem;
import org.opentravel.schemacompiler.repository.RepositoryManager;

/**
 * Handle requests for repository services.
 * 
 * @author dmh
 *
 */
public class DexRepositoryHandler {
	private static Log log = LogFactory.getLog(DexRepositoryHandler.class);

	private RepositoryManager repositoryManager;
	private RepositoryAvailabilityChecker availabilityChecker;
	private boolean repoStatus;

	public DexRepositoryHandler() {
		System.out.println("Repository Controller initialized.");
		// // Set up repository access
		try {
			repositoryManager = RepositoryManager.getDefault();
			availabilityChecker = RepositoryAvailabilityChecker.getInstance(repositoryManager);
			repoStatus = availabilityChecker.pingAllRepositories(true);
		} catch (RepositoryException e) {
			log.error("Error initializing repository: " + e.getLocalizedMessage());
		}
	}

	public List<RemoteRepository> getRepositories() {
		return repositoryManager.listRemoteRepositories();
	}

	/**
	 * FUTURE
	 * 
	 * @return
	 */
	public String[] getProjects() {
		String[] empty = {};
		File projectDir = repositoryManager.getProjectsFolder();
		if (projectDir.list() == null)
			return empty;
		return projectDir.list();
	}

	/**
	 * @return
	 * @throws RepositoryException
	 */
	public Repository getLocalRepository() throws RepositoryException {
		return repositoryManager.getDefault();
	}

	public void lock(RepositoryItem ri) {

	}

	public void unlock(RepositoryItem ri) {

	}
}
