/**
 * 
 */
package org.opentravel.dex.controllers.library;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.dex.controllers.DexDAO;
import org.opentravel.model.otmContainers.OtmLibrary;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.ImageView;

/**
 * The type of the TreeItem instances used in this TreeTableView. Simple Data Access Object that contains and provides
 * gui access to OTM model library members.
 *
 * @author dmh
 *
 * 
 * @author dmh
 *
 */
public class LibraryDAO implements DexDAO<OtmLibrary> {
	private static Log log = LogFactory.getLog(LibraryDAO.class);

	protected OtmLibrary library;
	String editable = "False";

	public LibraryDAO(OtmLibrary library) {
		this.library = library;
		if (library == null)
			throw new IllegalArgumentException("No library provided to Project-Library DAO");
	}

	public StringProperty editProperty() {
		editable = "False";
		if (library.isEditable())
			editable = "True";
		return new SimpleStringProperty(editable);
	}

	@Override
	public ImageView getIcon(ImageManager imageMgr) {
		return imageMgr.getView(library.getIconType());
	}

	@Override
	public OtmLibrary getValue() {
		return library;
	}

	public StringProperty nameProperty() {
		SimpleStringProperty ssp = new SimpleStringProperty(library.getName());
		// ssp.addListener((ov, old, newValue) -> setName(newValue)); // Track changes
		return ssp;
	}

	public StringProperty namespaceProperty() {
		return new SimpleStringProperty(library.getTL().getNamespace());
	}

	public StringProperty prefixProperty() {
		return new SimpleStringProperty(library.getPrefix());
	}

	public StringProperty stateProperty() {
		return new SimpleStringProperty(library.getStateName());
	}

	public IntegerProperty referenceProperty() {
		return new SimpleIntegerProperty(library.getTL().getReferenceCount());
	}

	public StringProperty statusProperty() {
		return new SimpleStringProperty(library.getStatus().name());
	}

	public StringProperty lockedProperty() {
		return new SimpleStringProperty(library.getLockedBy());
	}

	public StringProperty projectsProperty() {
		String projects = "";
		for (String name : library.getProjectNames())
			if (projects.isEmpty())
				projects = name;
			else
				projects = projects + ", " + name;
		return new SimpleStringProperty(projects);
	}

	public BooleanProperty readonlyProperty() {
		return new SimpleBooleanProperty(library.getTL().isReadOnly());
	}

	@Override
	public String toString() {
		return library.toString();
	}

	public StringProperty versionProperty() {
		return new SimpleStringProperty(library.getTL().getVersion());
	}

}
