/**
 * 
 */
package org.opentravel.model;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.opentravel.common.DexFileHandler;
import org.opentravel.common.OpenProjectProgressMonitor;
import org.opentravel.model.otmContainers.OtmLibrary;
import org.opentravel.model.otmContainers.OtmProject;
import org.opentravel.model.otmLibraryMembers.OtmBusinessObject;
import org.opentravel.model.otmLibraryMembers.OtmChoiceObject;
import org.opentravel.model.otmLibraryMembers.OtmCoreObject;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;
import org.opentravel.schemacompiler.model.AbstractLibrary;
import org.opentravel.schemacompiler.model.LibraryMember;
import org.opentravel.schemacompiler.model.TLBusinessObject;
import org.opentravel.schemacompiler.model.TLChoiceObject;
import org.opentravel.schemacompiler.model.TLCoreObject;
import org.opentravel.schemacompiler.model.TLLibrary;
import org.opentravel.schemacompiler.model.TLLibraryMember;
import org.opentravel.schemacompiler.model.TLModel;
import org.opentravel.schemacompiler.repository.Project;
import org.opentravel.schemacompiler.repository.ProjectItem;
import org.opentravel.schemacompiler.repository.ProjectManager;
import org.opentravel.schemacompiler.version.VersionChain;
import org.opentravel.schemacompiler.version.VersionChainFactory;

/**
 * Manage access to all objects in scope.
 * 
 * @author dmh
 *
 */
public class OtmModelManager {

	// Open projects
	private Map<String, OtmProject> projects = new HashMap<>();
	// Map of base namespaces with all libraries in that namespace
	private Map<String, VersionChain<TLLibrary>> baseNSMap = new HashMap<>();
	// Open libraries - Abstract Libraries are built-in and user
	private Map<AbstractLibrary, OtmLibrary> libraries = new HashMap<>();
	// All members - Library Members are TLLibraryMembers and contextual facets
	private Map<LibraryMember, OtmLibraryMember<?>> members = new HashMap<>();

	private DexFileHandler fileHandler = new DexFileHandler();

	public OtmModelManager() {
		// NO-OP
	}

	// /**
	// * Test if the library namespace is in any of the open projects.
	// *
	// * @param lib
	// * @return
	// */
	// public boolean isInProject(OtmLibrary lib) {
	//// for (OtmProject p : projects.values())
	// return false;
	// }

	/**
	 * @param TL
	 *            Library
	 * @return OtmLibrary associated with the TL Library
	 */
	public OtmLibrary get(TLLibrary tlLibrary) {
		return libraries.get(tlLibrary);
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
		VersionChain<TLLibrary> chain = baseNSMap.get(lib.getNameWithBasenamespace());
		if (lib.getTL() instanceof TLLibrary)
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
				System.out.println("OOPS - library in chain is null.");
		return libs;
	}

	public void createTestLibrary() {
		OtmLibrary lib = new OtmLibrary(this);
		libraries.put(lib.getTL(), lib);
		lib.createTestChildren(this);
	}

	public void add(ProjectManager pm) {
		System.out.println("Oh la la -- a new project to consume!");
		System.out.println("            new project has " + pm.getModel().getAllLibraries().size() + " libraries");

		VersionChainFactory versionChainFactory = new VersionChainFactory(pm.getModel());

		// Get Libraries
		//
		// base namespaces can have multiple libraries. Map will de-dup the entries based on baseNS and name.
		// Libraries can belong to multiple projects.
		for (ProjectItem pi : pm.getAllProjectItems()) {
			AbstractLibrary al = pi.getContent();
			if (al == null)
				continue;
			if (libraries.containsKey(al)) {
				libraries.get(al).add(pi); // let the library track additional project
				// TODO - all done - already modeled
			} else {
				OtmLibrary lib = new OtmLibrary(pi, this);
				libraries.put(al, lib);
				if (al instanceof TLLibrary)
					baseNSMap.put(lib.getNameWithBasenamespace(), versionChainFactory.getVersionChain((TLLibrary) al));
			}
		}

		// Get Members
		TLModel tlModel = pm.getModel();
		for (AbstractLibrary tlLib : tlModel.getAllLibraries()) {
			for (LibraryMember tlMember : tlLib.getNamedMembers()) {
				OtmLibraryMember<?> otmMember = memberFactory(tlMember);
				if (otmMember != null && tlMember instanceof TLLibraryMember)
					members.put(tlMember, otmMember);
			}
		}
		System.out.println("Members has " + members.size() + " members.");
	}

	// TODO - have new objects register themselves
	public OtmLibraryMember<?> memberFactory(LibraryMember tlMember) {
		if (tlMember instanceof TLBusinessObject)
			return new OtmBusinessObject((TLBusinessObject) tlMember, this);
		if (tlMember instanceof TLChoiceObject)
			return new OtmChoiceObject((TLChoiceObject) tlMember, this);
		if (tlMember instanceof TLCoreObject)
			return new OtmCoreObject((TLCoreObject) tlMember, this);
		return null;
	}

	/**
	 * @return
	 */
	public Collection<OtmLibraryMember<?>> getMembers() {
		return Collections.unmodifiableCollection(members.values());
	}

	/**
	 * @param member
	 */
	public void add(OtmLibraryMember<?> member) {
		members.put(member.getTL(), member);
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

	/**
	 * @param selectedFile
	 * @param monitor
	 */
	public void openProject(File selectedFile, OpenProjectProgressMonitor monitor) {
		// Open the project
		ProjectManager pm = fileHandler.openProject(selectedFile, monitor);

		// Record the results
		for (Project project : pm.getAllProjects())
			projects.put(project.getName(), new OtmProject(project));

		// TODO - p.getTL().addProjectChangeListener(listener);

		add(pm);
	}

}
