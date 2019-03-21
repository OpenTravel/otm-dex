/**
 * 
 */
package org.opentravel.dex.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.schemacompiler.repository.Repository;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Data Access Object (DAO) for displaying repository namespaces in a tree view.
 * 
 * @author dmh
 *
 */
public class NamespacesDAO {
	private static Log log = LogFactory.getLog(NamespacesDAO.class);
	protected String ns;
	protected String basePath;
	private Repository repository;

	public NamespacesDAO(String ns) {
		this(ns, null, null);
	}

	public NamespacesDAO(String ns, String basePath, Repository repo) {
		this.ns = ns;
		this.basePath = basePath;
		this.setRepository(repo);
	}

	public StringProperty nsProperty() {
		return new SimpleStringProperty(ns);
	}

	//
	@Override
	public String toString() {
		return ns;
	}

	public String getValue() {
		return ns;
	}
	//
	// public ImageView getIcon() {
	// return images.getView(element.getIconType());
	// }
	//

	/**
	 * @return
	 */
	public String getFullPath() {
		return basePath != null ? basePath + "/" + ns : ns;
	}

	/**
	 * @return the repository
	 */
	public Repository getRepository() {
		return repository;
	}

	/**
	 * @param repository
	 *            the repository to set
	 */
	public void setRepository(Repository repository) {
		this.repository = repository;
	}
}
