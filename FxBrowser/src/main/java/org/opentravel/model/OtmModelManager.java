/**
 * 
 */
package org.opentravel.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.DexFileHandler;
import org.opentravel.dex.actions.DexActionManager;
import org.opentravel.dex.controllers.DexStatusController;
import org.opentravel.dex.tasks.TaskResultHandlerI;
import org.opentravel.dex.tasks.model.ValidateModelManagerItemsTask;
import org.opentravel.model.otmContainers.OtmLibrary;
import org.opentravel.model.otmContainers.OtmProject;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMemberFactory;
import org.opentravel.schemacompiler.ic.ModelIntegrityChecker;
import org.opentravel.schemacompiler.model.AbstractLibrary;
import org.opentravel.schemacompiler.model.BuiltInLibrary;
import org.opentravel.schemacompiler.model.LibraryMember;
import org.opentravel.schemacompiler.model.TLLibrary;
import org.opentravel.schemacompiler.model.TLModel;
import org.opentravel.schemacompiler.model.TLModelElement;
import org.opentravel.schemacompiler.repository.Project;
import org.opentravel.schemacompiler.repository.ProjectItem;
import org.opentravel.schemacompiler.repository.ProjectManager;
import org.opentravel.schemacompiler.version.VersionChain;
import org.opentravel.schemacompiler.version.VersionChainFactory;

import javafx.concurrent.WorkerStateEvent;

/**
 * Manage access to all objects in scope.
 * 
 * @author dmh
 *
 */
public class OtmModelManager implements TaskResultHandlerI {
	private static Log log = LogFactory.getLog(OtmModelManager.class);

	// Open projects
	private Map<String, OtmProject> projects = new HashMap<>();
	// Map of base namespaces with all libraries in that namespace
	private Map<String, VersionChain<TLLibrary>> baseNSMap = new HashMap<>();
	// Open libraries - Abstract Libraries are built-in and user
	private Map<AbstractLibrary, OtmLibrary> libraries = new HashMap<>();

	// All members - Library Members are TLLibraryMembers and contextual facets
	private Map<LibraryMember, OtmLibraryMember> members = new HashMap<>();

	private DexFileHandler fileHandler = new DexFileHandler();
	private DexActionManager actionMgr;
	private DexStatusController statusController;

	private TLModel tlModel = null;

	/**
	 * Create a model manager.
	 * 
	 * @param controller
	 * @param actionManager
	 *            action manager to assign to all members
	 */
	public OtmModelManager(DexStatusController statusController, DexActionManager actionManager) {
		this.actionMgr = actionManager;
		actionManager.setModelManager(this);
		this.statusController = statusController;

		// Create a TL Model
		// FIXME - this is not the model being used when adding projects
		try {
			tlModel = new TLModel();
		} catch (Exception e) {
			log.debug("Exception creating new model: " + e.getLocalizedMessage());
		}
		// Tell model to track changes to maintain its type integrity
		tlModel.addListener(new ModelIntegrityChecker());
		log.debug("TL Model created and integrity checker added.");

		// Render the built-in libraries
		for (BuiltInLibrary tlLib : tlModel.getBuiltInLibraries())
			for (LibraryMember tlMember : tlLib.getNamedMembers()) {
				OtmLibraryMemberFactory.memberFactory(tlMember, this); // creates and adds
			}

	}

	public DexActionManager getActionManager() {
		return actionMgr;
	}

	/**
	 * @param TL
	 *            Library
	 * @return OtmLibrary associated with the TL Library
	 */
	public OtmLibrary get(TLLibrary tlLibrary) {
		return libraries.get(tlLibrary);
	}

	public OtmLibraryMember getMember(TLModelElement tlMember) {
		if (tlMember instanceof LibraryMember)
			return members.get((tlMember));
		return null;
	}

	/**
	 * @param TL
	 *            Abstract Library
	 * @return OtmLibrary associated with the abstract library
	 */
	public OtmLibrary get(AbstractLibrary absLibrary) {
		return libraries.get(absLibrary);
	}

	public Collection<OtmLibrary> getLibraries() {
		return Collections.unmodifiableCollection(libraries.values());
	}

	public Set<String> getBaseNamespaces() {
		return baseNSMap.keySet();
	}

	/**
	 * Look into the chain and return true if this is the latest version (next version = null)
	 * 
	 * @param lib
	 * @return
	 */
	public boolean isLatest(OtmLibrary lib) {
		if (lib == null || lib.getTL() == null)
			return false;
		VersionChain<TLLibrary> chain = baseNSMap.get(lib.getNameWithBasenamespace());
		if (chain != null && lib.getTL() instanceof TLLibrary)
			return (chain.getNextVersion((TLLibrary) lib.getTL())) == null;
		return true;
	}

	/**
	 * Get all the libraries in a given base namespace (namespace root)
	 * 
	 * @param baseNamespace
	 * @return
	 */
	public Set<OtmLibrary> getLibraryChain(String baseNamespace) {
		Set<OtmLibrary> libs = new LinkedHashSet<>();
		VersionChain<TLLibrary> chain = baseNSMap.get(baseNamespace);
		for (TLLibrary tlLib : chain.getVersions())
			if (libraries.get(tlLib) != null)
				libs.add(libraries.get(tlLib));
			else
				log.debug("OOPS - library in chain is null.");
		return libs;
	}

	// public void createTestLibrary() {
	// OtmLibrary lib = new OtmLibrary(this);
	// libraries.put(lib.getTL(), lib);
	// lib.createTestChildren(this);
	// }

	public void add(ProjectManager pm) {
		log.debug("Oh la la -- a new project to consume!");
		log.debug("            new project has " + pm.getModel().getAllLibraries().size() + " libraries");

		// Add projects to project map
		for (Project project : pm.getAllProjects())
			projects.put(project.getName(), new OtmProject(project));

		if (pm.getModel() != tlModel)
			log.debug("Models are different");
		// Tell model to track changes to maintain its type integrity
		// TODO - where should this be set?
		pm.getModel().addListener(new ModelIntegrityChecker());

		// Get Libraries
		//
		// base namespaces can have multiple libraries. Map will de-dup the entries based on baseNS and name.
		// Libraries can belong to multiple projects.
		for (ProjectItem pi : pm.getAllProjectItems()) {
			addLibrary(pi);
		}

		// Get Members
		TLModel tlModel = pm.getModel();
		for (AbstractLibrary tlLib : tlModel.getAllLibraries()) {
			for (LibraryMember tlMember : tlLib.getNamedMembers()) {
				OtmLibraryMemberFactory.memberFactory(tlMember, this); // creates and adds
			}
		}

		new ValidateModelManagerItemsTask(this, this, statusController).go();

		log.debug("Model has " + members.size() + " members.");
	}

	private void addLibrary(ProjectItem pi) {
		if (pi == null)
			return;
		VersionChainFactory versionChainFactory = new VersionChainFactory(pi.getProjectManager().getModel());
		AbstractLibrary absLibrary = pi.getContent();
		if (absLibrary == null)
			return;

		if (libraries.containsKey(absLibrary)) {
			libraries.get(absLibrary).add(pi); // let the library track additional project
		} else {
			OtmLibrary lib = new OtmLibrary(pi, this);
			libraries.put(absLibrary, lib);
			if (absLibrary instanceof TLLibrary)
				baseNSMap.put(lib.getNameWithBasenamespace(),
						versionChainFactory.getVersionChain((TLLibrary) absLibrary));
		}
	}

	@Override
	public void handleTaskComplete(WorkerStateEvent event) {
		// NO-OP
	}

	/**
	 * @return
	 */
	public Collection<OtmLibraryMember> getMembers() {
		return Collections.unmodifiableCollection(members.values());
	}

	/**
	 * @param member
	 */
	public void add(OtmLibraryMember member) {
		if (member != null && member.getTL() instanceof LibraryMember)
			members.put(member.getTlLM(), member);
	}

	/**
	 * 
	 */
	public void clear() {
		projects.clear();
		libraries.clear();
		members.clear();
		baseNSMap.clear();
	}

	// /**
	// * Open the selected project and monitor the progress.
	// *
	// * @param projectFile
	// * @param monitor
	// */
	// // TODO - where does this belong? Most of it is adding project to mgr.
	// public void openProject(File projectFile, OpenProjectProgressMonitor monitor) {
	// // Open the project
	// log.debug("Opening Project");
	// ProjectManager pm = fileHandler.openProject(projectFile, monitor);
	//
	// // Record the results
	// for (Project project : pm.getAllProjects())
	// projects.put(project.getName(), new OtmProject(project));
	//
	// // TODO - p.getTL().addProjectChangeListener(listener);
	//
	// add(pm);
	// }
}
