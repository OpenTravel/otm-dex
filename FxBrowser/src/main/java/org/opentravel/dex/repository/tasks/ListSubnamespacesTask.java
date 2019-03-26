/**
 * 
 */
package org.opentravel.dex.repository.tasks;

import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.controllers.DexStatusController;
import org.opentravel.dex.repository.NamespacesDAO;
import org.opentravel.dex.repository.TaskResultHandlerI;
import org.opentravel.schemacompiler.repository.RepositoryException;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;

/**
 * A JavaFX task for listing all sub-namespaces of a namespace root in a repository
 * 
 * @author dmh
 *
 */
public class ListSubnamespacesTask extends DexTaskBase<NamespacesDAO> {
	private static Log log = LogFactory.getLog(ListSubnamespacesTask.class);

	// Map indexed by the full path of each namespace found
	// Must be sorted to assure parent can be found when processed.
	private SortedMap<String, NamespacesDAO> namespaceMap = new TreeMap<>();

	/**
	 * Create a lock repository item task.
	 * 
	 * @param taskData
	 * @param progressProperty
	 * @param statusProperty
	 * @param handler
	 */
	public ListSubnamespacesTask(NamespacesDAO taskData, TaskResultHandlerI handler, DoubleProperty progressProperty,
			StringProperty statusProperty, DexStatusController statusController) {
		super(taskData, handler, progressProperty, statusProperty, statusController);

		// Replace start message from super-type.
		msgBuilder = new StringBuilder("Getting: ");
		msgBuilder.append(taskData.getBasePath());
		updateMessage(msgBuilder.toString());
	}

	/**
	 * Get the sorted list of namespaces (full path) and the associated NamespacesDAO
	 * 
	 * @return
	 */
	public SortedMap<String, NamespacesDAO> getMap() {
		return namespaceMap;
	}

	/**
	 * Creates map of all descendant namespaces of the parent namespace. Does add the parent to the map.
	 */
	@Override
	public void doIT() throws RepositoryException {
		namespaceMap.put(taskData.getFullPath(), taskData);
		get(taskData);
	}

	private void get(NamespacesDAO parentDAO) throws RepositoryException {
		NamespacesDAO nsData = null;
		for (String childNS : parentDAO.getRepository().listNamespaceChildren(parentDAO.getFullPath())) {
			nsData = new NamespacesDAO(childNS, parentDAO.getFullPath(), parentDAO.getRepository());
			namespaceMap.put(nsData.getFullPath(), nsData);
			get(nsData); // recurse
		}
	}
}
