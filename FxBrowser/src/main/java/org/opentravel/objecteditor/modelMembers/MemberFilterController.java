/**
 * 
 */
package org.opentravel.objecteditor.modelMembers;

import java.awt.IllegalComponentStateException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.controllers.DexIncludedControllerBase;
import org.opentravel.dex.controllers.DexMainController;
import org.opentravel.model.OtmModelElement;
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.otmContainers.OtmLibrary;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;

/**
 * Controller for a library history table. Creates table containing library history properties.
 * 
 * @author dmh
 *
 */
public class MemberFilterController extends DexIncludedControllerBase<Void> {
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

	// Library Member Table Selection Filters
	@FXML
	private ChoiceBox<String> librarySelector;
	@FXML
	private TextField memberNameFilter;
	@FXML
	private MenuButton memberTypeMenu;
	@FXML
	private RadioButton latestButton;
	@FXML
	private RadioButton editableButton;
	@FXML
	private RadioButton errorsButton;

	// FIX these
	private static final String ALLLIBS = "All";

	private String textFilterValue = null;
	private OtmModelManager modelMgr;

	private HashMap<String, OtmLibrary> libraryMap = new HashMap<>();
	private String libraryFilter = null;

	private boolean ignoreClear = false;

	private boolean latestVersionOnly = false;
	private boolean editableOnly = false;
	private MemberTreeController memberController;

	public void setParentController(DexMainController parent, MemberTreeController controller) {
		super.configure(parent);
		memberController = controller;
		modelMgr = parent.getModelManager();
		configureLibraryChoice();
	}

	// public void setMemberController(MemberTreeController controller) {
	// memberController = controller;
	// }

	public MemberFilterController() {
		log.debug("Member Filter Controller constructor.");

	}

	@Override
	public void checkNodes() {
		if (!(librarySelector instanceof ChoiceBox))
			throw new IllegalComponentStateException("Library selector not injected by FXML.");
		if (!(memberNameFilter instanceof TextField))
			throw new IllegalComponentStateException("memberNameFilter not injected by FXML.");
		if (!(memberTypeMenu instanceof MenuButton))
			throw new IllegalComponentStateException("memberTypeMenu not injected by FXML.");

		if (!(latestButton instanceof RadioButton))
			throw new IllegalComponentStateException("latestButton not injected by FXML.");
		if (!(editableButton instanceof RadioButton))
			throw new IllegalComponentStateException("editableButton not injected by FXML.");
		if (!(errorsButton instanceof RadioButton))
			throw new IllegalComponentStateException("errorsButton not injected by FXML.");
	}

	@Override
	public void initialize() {
		log.debug("Member Filter Controller - Initialize");
		checkNodes();

		memberNameFilter.setOnKeyTyped(this::applyTextFilter);
		memberNameFilter.setOnAction(this::applyTextFilter);
		memberTypeMenu.setOnAction(this::setTypeFilter);
		editableButton.setOnAction(this::setEditableOnly);
		latestButton.setOnAction(this::setLatestOnly);

		errorsButton.setVisible(false); // hide for now
	}

	private void configureLibraryChoice() {
		if (modelMgr == null) {
			log.error("Needed Model Manager is null.");
			return;
		}
		libraryMap.clear();
		libraryMap.put(ALLLIBS, null);
		for (OtmLibrary lib : modelMgr.getLibraries())
			if (lib.getName() != null && !lib.getName().isEmpty())
				libraryMap.put(lib.getName(), lib);

		ObservableList<String> libList = FXCollections.observableArrayList(libraryMap.keySet());
		libList.sort(null);
		librarySelector.setItems(libList);
		librarySelector.setOnAction(this::setLibraryFilter);
	}

	/**
	 * 
	 * @param object
	 *            to test
	 * @return true if the object passes the selection filters (should be displayed)
	 */
	public boolean isSelected(OtmModelElement<?> object) {
		// log.debug("Is " + object.getName() + " selected?");
		if (object.getOwningMember() == null || object.getOwningMember().getLibrary() == null)
			return true;
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
		if (librarySelector.getSelectionModel().getSelectedItem() != null) {
			selection = librarySelector.getSelectionModel().getSelectedItem();
			if (librarySelector.getSelectionModel().getSelectedItem().equals(ALLLIBS)) {
				clear();
				refreshMembers();
			} else {
				setLibraryFilter(libraryMap.get(selection));
			}
		}
		librarySelector.setValue(selection);
	}

	public void setLibraryFilter(OtmLibrary lib) {
		ignoreClear = true;
		libraryFilter = lib.getName();
		librarySelector.getSelectionModel().select(lib.getName());
		refreshMembers();
		// log.debug("Set Library Filter to: " + libraryFilter);
		ignoreClear = false;
	}

	public void setTypeFilter(Event e) {
		log.debug("Set Type Filter: " + e.toString());
	}

	public void setEditableOnly(ActionEvent event) {
		log.debug("Editable set to: " + editableButton.isSelected());
		editableOnly = editableButton.isSelected();
		refreshMembers();
	}

	private void refreshMembers() {
		if (memberController == null)
			throw new IllegalStateException("Could not refresh view because member controller was null.");
		memberController.refresh();
	}

	public void setLatestOnly(ActionEvent event) {
		log.debug("Latest only set to: " + latestButton.isSelected());
		latestVersionOnly = latestButton.isSelected();
		refreshMembers();
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
			refreshMembers();
		}
	}

	// Filter on any case of the value
	public void applyTextFilter(Event e) {
		ignoreClear = true;
		textFilterValue = memberNameFilter.getText().toLowerCase();
		refreshMembers();
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
			memberNameFilter.setText("");
		}
	}

	@Override
	public ReadOnlyObjectProperty<TreeItem<?>> getSelectable() {
		return null;
	}

	// public ImageManager getImageManager() {
	// return null;
	// }

	// public OtmModelManager getModelManager() {
	// return modelMgr;
	// }

	// public void postStatus(String string) {
	// parentController.postStatus(string);
	// }

	// public void postProgress(double percentDone) {
	// parentController.postProgress(percentDone);
	// }

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

}
