/**
 * 
 */
package org.opentravel.common;

import java.io.File;
import java.util.List;

import org.opentravel.schemacompiler.repository.RemoteRepository;
import org.opentravel.schemacompiler.repository.RepositoryAvailabilityChecker;
import org.opentravel.schemacompiler.repository.RepositoryException;
import org.opentravel.schemacompiler.repository.RepositoryManager;

/**
 * Manage access to OTM Repositories.
 * 
 * @author dmh
 *
 */
public class RepositoryController {

	private RepositoryManager repositoryManager;

	@Deprecated
	public RepositoryManager getRepositoryManager() {
		return repositoryManager;
	}

	private RepositoryAvailabilityChecker availabilityChecker;
	private boolean repoStatus;

	public boolean getRepoStatus() {
		return repoStatus;
	}

	public RepositoryController() {
		System.out.println("Repository Controller initialized.");
		// // Set up repository access
		try {
			repositoryManager = RepositoryManager.getDefault();
			availabilityChecker = RepositoryAvailabilityChecker.getInstance(repositoryManager);
			repoStatus = availabilityChecker.pingAllRepositories(true);
		} catch (RepositoryException e) {
			e.printStackTrace(System.out);
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
		File projectDir = repositoryManager.getProjectsFolder();
		return projectDir.list();
	}
}
