/**
 * 
 */
package org.opentravel.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.opentravel.schemacompiler.version.VersionChain;
import org.opentravel.schemacompiler.version.VersionChainFactory;

/**
 * Manage access to all objects in scope.
 * 
 * @author dmh
 *
 */
public class OtmModelManager {

	List<OtmProject> projects;

	// Abstract Libraries are built-in and user
	static Map<AbstractLibrary, OtmLibrary> libraries = new HashMap<>();

	// Library Members are TLLibraryMembers and contextual facets
	static Map<LibraryMember, OtmLibraryMember<?>> members = new HashMap<>();

	VersionChainFactory versionChainFactory;

	public OtmModelManager() {
		projects = new ArrayList<>();
	}

	public OtmLibraryMember<?> get(TLLibraryMember tlMember) {
		return members.get(tlMember);
	}

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

	public void createTestLibrary() {
		OtmLibrary lib = new OtmLibrary(this);
		libraries.put(lib.getTL(), lib);
		lib.createTestChildren(this);
	}

	public void add(TLModel tlModel) {
		System.out.println("Oh la la -- a new model to consume!.");
		System.out.println("            new model has " + tlModel.getAllLibraries().size() + " libraries");
		versionChainFactory = new VersionChainFactory(tlModel);
		// List<String> baseNamespaces = versionChainFactory.getBaseNamespaces();

		VersionChain<TLLibrary> libChain;
		for (AbstractLibrary tlLib : tlModel.getAllLibraries()) {
			if (tlLib instanceof TLLibrary) {
				OtmLibrary lib = new OtmLibrary((TLLibrary) tlLib);
				libraries.put(tlLib, lib);
			}
			// if (tlLib instanceof TLLibrary) {
			// libChain = versionChainFactory.getVersionChain((TLLibrary) tlLib);
			// for (TLLibrary v : libChain.getVersions())
			// System.out.println("Versioned lib found: " + v.getBaseNamespace() + " " + v.getVersion());
			// }
			for (LibraryMember tlMember : tlLib.getNamedMembers()) {
				OtmLibraryMember<?> m = memberFactory(tlMember);
				if (m != null && tlMember instanceof TLLibraryMember)
					members.put(tlMember, m);
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
	}

}
