/**
 * 
 */
package org.opentravel.objecteditor;

import java.util.EnumMap;
import java.util.HashMap;

import org.opentravel.model.OtmModelElement;
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.otmContainers.OtmLibrary;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;

//import javafx.event.ActionEvent;
//import javafx.event.Event;
//import javafx.concurrent.Task;
//import javafx.scene.control.cell.TreeItemPropertyValueFactory;
//import javafx.scene.control.cell.TextFieldTreeTableCell;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.control.TreeView;
//import javafx.util.converter.IntegerStringConverter;
//javafx.beans.property.SimpleBooleanProperty
// import javafx.beans.property.ReadOnlyStringWrapper;
//javafx.beans.property.ReadOnlyBooleanWrapper
//javafx.beans.property.SimpleintegerProperty
//javafx.beans.property.ReadOnlyintegerWrapper
//javafx.beans.property.SimpleDoubleProperty
//javafx.beans.property.ReadOnlyDoubleWrapper
//javafx.beans.property.ReadOnlyStringWrapper
//import javafx.beans.property.StringProperty;
//import javafx.beans.property.SimpleStringProperty;

/**
 * Controller for a library history table. Creates table containing library history properties.
 * 
 * @author dmh
 *
 */
@SuppressWarnings("restriction")
public class LibraryFilterController implements DexController {
	// private static final Logger LOGGER = LoggerFactory.getLogger(LibraryFilterController.class);

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
	private DexController parent;
	private OtmModelManager modelMgr;

	/**
	 * Manage interaction with library selection panel.
	 * 
	 * @param nsLibraryTablePermissionField
	 */
	public LibraryFilterController(DexController parent, EnumMap<LibraryFilterNodes, Node> fxNodes) {
		System.out.println("Initializing library filter controller.");
		getFxNodes(fxNodes);
		this.parent = parent;
		modelMgr = parent.getModelManager();

		configureLibraryChoice();

		nameFilter.setOnKeyTyped(this::applyTextFilter);
		nameFilter.setOnAction(this::applyTextFilter);
		typeMenu.setOnAction(this::applyTextFilter);
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

	private HashMap<String, OtmLibrary> libraryMap = new HashMap<>();
	private String libraryFilter = null;

	private boolean ignoreClear = false;

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
		// System.out.println(" Filter test of " + object.getName());
		if (libraryFilter != null && !object.getLibrary().getName().startsWith(libraryFilter))
			return false;
		if (textFilterValue != null)
			return object.getName().toLowerCase().startsWith(textFilterValue);
		// NO filters applied
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
				((LibraryMemberTreeController) parent).refresh();
			} else {
				setLibraryFilter(libraryMap.get(selection));
			}
		}
		libraryChoice.setValue(selection);
	}

	public void setLibraryFilter(OtmLibrary lib) {
		ignoreClear = true;
		libraryFilter = lib.getName();
		((LibraryMemberTreeController) parent).refresh();
		System.out.println("Set Library Filter to: " + libraryFilter);
		ignoreClear = false;
	}

	// Filter on any case of the value
	public void applyTextFilter(Event e) {
		ignoreClear = true;
		textFilterValue = nameFilter.getText().toLowerCase();
		((LibraryMemberTreeController) parent).refresh();
		System.out.println("Apply text Filter: " + textFilterValue);
		ignoreClear = false;
	}

	public void applyTypeFilter(Event e) {
		System.out.println("Apply type Filter");
	}

	@Override
	public void clear() {
		// When posting updated filter results, do not clear the filters.
		if (!ignoreClear) {
			modelMgr = parent.getModelManager();
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
}
