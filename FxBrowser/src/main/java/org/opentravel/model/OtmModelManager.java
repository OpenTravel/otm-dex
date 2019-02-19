/**
 * 
 */
package org.opentravel.model;

import java.util.ArrayList;
import java.util.List;

import org.opentravel.model.objectNodes.OtmBusinessObject;
import org.opentravel.model.objectNodes.OtmChoiceObject;
import org.opentravel.model.objectNodes.OtmCoreObject;
import org.opentravel.model.objectNodes.OtmLibraryMember;
import org.opentravel.schemacompiler.model.AbstractLibrary;
import org.opentravel.schemacompiler.model.LibraryMember;
import org.opentravel.schemacompiler.model.TLBusinessObject;
import org.opentravel.schemacompiler.model.TLChoiceObject;
import org.opentravel.schemacompiler.model.TLCoreObject;
import org.opentravel.schemacompiler.model.TLLibrary;
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
	List<OtmLibrary> libraries;
	List<OtmLibraryMember<?>> members;
	VersionChainFactory versionChainFactory;

	public OtmModelManager() {
		projects = new ArrayList<>();
		libraries = new ArrayList<>();
		members = new ArrayList<>();
	}

	public void createTestLibrary() {
		OtmLibrary lib = new OtmLibrary();
		libraries.add(lib);
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
				libraries.add(lib);
			}
			// if (tlLib instanceof TLLibrary) {
			// libChain = versionChainFactory.getVersionChain((TLLibrary) tlLib);
			// for (TLLibrary v : libChain.getVersions())
			// System.out.println("Versioned lib found: " + v.getBaseNamespace() + " " + v.getVersion());
			// }
			for (LibraryMember tlMember : tlLib.getNamedMembers()) {
				OtmLibraryMember<?> m = memberFactory(tlMember);
				if (m != null)
					members.add(m);
			}
		}
		System.out.println("Members has " + members.size() + " members.");
	}

	public OtmLibraryMember<?> memberFactory(LibraryMember tlMember) {
		if (tlMember instanceof TLBusinessObject)
			return new OtmBusinessObject((TLBusinessObject) tlMember);
		if (tlMember instanceof TLChoiceObject)
			return new OtmChoiceObject((TLChoiceObject) tlMember);
		if (tlMember instanceof TLCoreObject)
			return new OtmCoreObject((TLCoreObject) tlMember);
		return null;
	}

	/**
	 * @return
	 */
	public List<OtmLibraryMember<?>> getMembers() {
		return members;
	}

	/**
	 * @param member
	 */
	public void add(OtmLibraryMember<?> member) {
		members.add(member);
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
