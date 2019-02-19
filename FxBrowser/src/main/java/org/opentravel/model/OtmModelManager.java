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
import org.opentravel.schemacompiler.model.TLModel;

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

		for (AbstractLibrary tlLib : tlModel.getAllLibraries()) {
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

}
