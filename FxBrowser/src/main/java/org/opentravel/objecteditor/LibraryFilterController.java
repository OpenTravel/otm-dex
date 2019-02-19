/**
 * 
 */
package org.opentravel.objecteditor;

import java.util.EnumMap;

import org.opentravel.model.OtmModelElement;

import javafx.beans.property.ReadOnlyObjectProperty;
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

	private ChoiceBox<String> libraryChoice;
	private TextField nameFilter;
	private MenuButton typeMenu;
	private MenuButton stateMenu;

	private String textFilterValue = null;
	private DexController parent;

	/**
	 * Manage interaction with library selection panel.
	 * 
	 * @param nsLibraryTablePermissionField
	 */
	public LibraryFilterController(DexController parent, EnumMap<LibraryFilterNodes, Node> fxNodes) {
		System.out.println("Initializing library filter controller.");
		getFxNodes(fxNodes);
		this.parent = parent;

		nameFilter.setOnKeyPressed(this::applyTextFilter);
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

	public boolean isSelected(OtmModelElement<?> object) {
		// System.out.println(" Filter test of " + object.getName());
		if (textFilterValue != null)
			return object.getName().toLowerCase().startsWith(textFilterValue);
		// NO filters applied
		return true;
	}

	// Filter on any case of the value
	public void applyTextFilter(Event e) {
		textFilterValue = nameFilter.getText().toLowerCase();
		((LibraryMemberTreeController) parent).refresh();
		System.out.println("Apply text Filter: " + textFilterValue);
	}

	public void applyTypeFilter(Event e) {
		System.out.println("Apply Filter");
	}

	@Override
	public void clear() {
	}

	@Override
	public ReadOnlyObjectProperty<TreeItem<?>> getSelectable() {
		return null;
	}

	@Override
	public ImageManager getImageManager() {
		return null;
	}

}
