/**
 * 
 */
package org.opentravel.model;

import java.util.ArrayList;
import java.util.List;

import org.opentravel.model.objectNodes.OtmLibraryMember;

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
