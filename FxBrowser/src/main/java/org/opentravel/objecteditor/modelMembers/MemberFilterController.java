/**
 * 
 */
package org.opentravel.objecteditor.modelMembers;

import java.util.EnumMap;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.model.OtmModelElement;
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.otmContainers.OtmLibrary;
import org.opentravel.objecteditor.DexController;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;

/**
 * Controller for a library history table. Creates table containing library history properties.
 * 
 * @author dmh
 *
 */
@SuppressWarnings("restriction")
public class MemberFilterController implements DexController {
	private static Log log = LogFactory.getLog(MemberFilterController.class);

	/**
	 * FXML Java FX Nodes this controller is dependent upon
	 * 
	 * @author dmh
	 *
	 */
	public enum LibraryFilterNodes {
		Library, Name, Type, State;
	}

	private static final String ALLLIBS = "All";

	private ChoiceBox<String> libraryChoice;
	private TextField nameFilter;
	private MenuButton typeMenu;
	private MenuButton stateMenu;

	private String textFilterValue = null;
	private DexController parentController;
	private OtmModelManager modelMgr;

	private HashMap<String, OtmLibrary> libraryMap = new HashMap<>();
	private String libraryFilter = null;

	private boolean ignoreClear = false;

	private boolean latestVersionOnly = false;
	private boolean editableOnly = false;

	/**
	 * Manage interaction with library selection panel.
	 * 
	 * @param nsLibraryTablePermissionField
	 */
	public MemberFilterController(DexController parent, EnumMap<LibraryFilterNodes, Node> fxNodes) {
		log.debug("Initializing library filter controller.");
		getFxNodes(fxNodes);
		this.parentController = parent;
		modelMgr = parent.getModelManager();

		configureLibraryChoice();

		nameFilter.setOnKeyTyped(this::applyTextFilter);
		nameFilter.setOnAction(this::applyTextFilter);
		typeMenu.setOnAction(this::setTypeFilter);
		stateMenu.setOnAction(this::setStateFilter);
		// May have to set the actions on each item
		for (MenuItem item : stateMenu.getItems())
			item.setOnAction(this::setStateFilter);
	}

	@SuppressWarnings("unchecked")
	private void getFxNodes(EnumMap<LibraryFilterNodes, Node> fxNodes) {
		libraryChoice = (ChoiceBox<String>) fxNodes.get(LibraryFilterNodes.Library);
		nameFilter = (TextField) fxNodes.get(LibraryFilterNodes.Name);
		typeMenu = (MenuButton) fxNodes.get(LibraryFilterNodes.Type);
		stateMenu = (MenuButton) fxNodes.get(LibraryFilterNodes.State);

		if (libraryChoice == null || nameFilter == null || typeMenu == null || stateMenu == null)
			throw new IllegalArgumentException("Null parameter.");
	}

	private void configureLibraryChoice() {
		libraryMap.clear();
		libraryMap.put(ALLLIBS, null);
		for (OtmLibrary lib : modelMgr.getLibraries())
			if (lib.getName() != null && !lib.getName().isEmpty())
				libraryMap.put(lib.getName(), lib);

		ObservableList<String> libList = FXCollections.observableArrayList(libraryMap.keySet());
		libList.sort(null);
		libraryChoice.setItems(libList);
		libraryChoice.setOnAction(this::setLibraryFilter);
	}

	/**
	 * 
	 * @param object
	 *            to test
	 * @return true if the object passes the selection filters (should be displayed)
	 */
	public boolean isSelected(OtmModelElement<?> object) {
		if (object.getOwningMember() == null || object.getOwningMember().getLibrary() == null)
			return true;
		// log.debug(" Filter test of " + object.getName());
		if (libraryFilter != null && !object.getLibrary().getName().startsWith(libraryFilter))
			return false;
		if (textFilterValue != null && !object.getName().toLowerCase().startsWith(textFilterValue))
			return false;
		if (latestVersionOnly && !object.getOwningMember().getLibrary().isLatestVersion())
			return false;
		if (editableOnly && !object.isEditable())
			return false;

		// NO filters applied OR passed all filters
		return true;
	}

	// Future - use fxControls or other package to get a multiple check box or even check tree to select versions.
	//
	private void setLibraryFilter(Event e) {
		String selection = ALLLIBS;
		if (libraryChoice.getSelectionModel().getSelectedItem() != null) {
			selection = libraryChoice.getSelectionModel().getSelectedItem();
			if (libraryChoice.getSelectionModel().getSelectedItem().equals(ALLLIBS)) {
				clear();
				((MemberTreeController) parentController).refresh();
			} else {
				setLibraryFilter(libraryMap.get(selection));
			}
		}
		libraryChoice.setValue(selection);
	}

	public void setLibraryFilter(OtmLibrary lib) {
		ignoreClear = true;
		libraryFilter = lib.getName();
		((MemberTreeController) parentController).refresh();
		log.debug("Set Library Filter to: " + libraryFilter);
		ignoreClear = false;
	}

	public void setTypeFilter(Event e) {
		log.debug("Set Type Filter: " + e.toString());
	}

	public void setStateFilter(Event e) {
		log.debug("Set Type Filter: " + e.toString());
		CheckMenuItem mi = null;
		if (e.getTarget() instanceof CheckMenuItem)
			mi = (CheckMenuItem) e.getTarget();
		if (mi != null) {
			if (((MenuItem) e.getTarget()).getText().startsWith("Latest")) {
				latestVersionOnly = mi.isSelected();
			}
			if (((MenuItem) e.getTarget()).getText().startsWith("Edit")) {
				editableOnly = mi.isSelected();
			}
			((MemberTreeController) parentController).refresh();
		}
	}

	// Filter on any case of the value
	public void applyTextFilter(Event e) {
		ignoreClear = true;
		textFilterValue = nameFilter.getText().toLowerCase();
		((MemberTreeController) parentController).refresh();
		log.debug("Apply text Filter: " + textFilterValue);
		ignoreClear = false;
	}

	// public void setTypeFilter(Event e) {
	// log.debug("Apply type Filter");
	// }

	@Override
	public void clear() {
		// When posting updated filter results, do not clear the filters.
		if (!ignoreClear) {
			modelMgr = parentController.getModelManager();
			configureLibraryChoice();
			libraryFilter = null;

			textFilterValue = null;
			nameFilter.setText("");
		}
	}

	@Override
	public ReadOnlyObjectProperty<TreeItem<?>> getSelectable() {
		return null;
	}

	@Override
	public ImageManager getImageManager() {
		return null;
	}

	@Override
	public OtmModelManager getModelManager() {
		return modelMgr;
	}

	@Override
	public void postStatus(String string) {
		parentController.postStatus(string);
	}

	@Override
	public void postProgress(double percentDone) {
		parentController.postProgress(percentDone);
	}

}
