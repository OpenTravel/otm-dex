/**
 * 
 */
package org.opentravel.dex.controllers.member;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.controllers.DexIncludedControllerBase;
import org.opentravel.dex.controllers.DexMainController;
import org.opentravel.dex.controllers.popup.DexPopupController;
import org.opentravel.dex.events.DexFilterChangeEvent;
import org.opentravel.dex.events.DexLibrarySelectionEvent;
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.otmContainers.OtmLibrary;
import org.opentravel.model.otmLibraryMembers.OtmBusinessObject;
import org.opentravel.model.otmLibraryMembers.OtmChoiceObject;
import org.opentravel.model.otmLibraryMembers.OtmCoreObject;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;
import org.opentravel.model.otmLibraryMembers.OtmSimpleObject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * Controller for library member filter controller.
 * 
 * @author dmh
 *
 */
public class MemberFilterController extends DexIncludedControllerBase<Void> {
	private static Log log = LogFactory.getLog(MemberFilterController.class);

	public enum LibraryFilterNodes {
		Library, Name, Type, State;
	}

	/**
	 * FXML Java FX Nodes this controller is dependent upon
	 */
	@FXML
	private HBox memberFilter;
	@FXML
	private ChoiceBox<String> librarySelector;
	@FXML
	private TextField memberNameFilter;
	@FXML
	private ComboBox<String> memberTypeCombo;
	// @FXML
	// private MenuButton memberTypeMenu;
	@FXML
	private RadioButton latestButton;
	@FXML
	private RadioButton editableButton;
	@FXML
	private RadioButton errorsButton;

	// Class specific data
	//
	private static final String ALLLIBS = "All";

	private String textFilterValue = null;
	private OtmModelManager modelMgr;

	private HashMap<String, OtmLibrary> libraryMap = new HashMap<>();
	private String libraryFilter = null;

	private boolean ignoreClear = false;
	private boolean latestVersionOnly = false;
	private boolean editableOnly = false;
	private String classNameFilter = null;

	// Possible alternate target for change events
	private DexPopupController popupController = null;

	// All event types fired by this controller.
	private static final EventType[] publishedEvents = { DexFilterChangeEvent.FILTER_CHANGED,
			DexLibrarySelectionEvent.LIBRARY_SELECTED };

	// All event types listened to by this controller's handlers
	private static final EventType[] subscribedEvents = { DexLibrarySelectionEvent.LIBRARY_SELECTED };

	public MemberFilterController() {
		super(subscribedEvents, publishedEvents);
		log.debug("Member Filter Controller constructor.");
	}

	@Override
	public void checkNodes() {
		if (!(memberFilter instanceof HBox))
			throw new IllegalStateException("Member Filter not injected by FXML.");
		if (!(librarySelector instanceof ChoiceBox))
			throw new IllegalStateException("Library selector not injected by FXML.");
		if (!(memberNameFilter instanceof TextField))
			throw new IllegalStateException("memberNameFilter not injected by FXML.");
		if (!(memberTypeCombo instanceof ComboBox))
			throw new IllegalStateException("memberTypeCombo not injected by FXML.");
		if (!(latestButton instanceof RadioButton))
			throw new IllegalStateException("latestButton not injected by FXML.");
		if (!(editableButton instanceof RadioButton))
			throw new IllegalStateException("editableButton not injected by FXML.");
		if (!(errorsButton instanceof RadioButton))
			throw new IllegalStateException("errorsButton not injected by FXML.");
	}

	private static final String ALL = "All";
	private static final String BUSINESS = "Business";
	private static final String CHOICE = "Choice";
	private static final String CORE = "Core";
	private static final String SIMPLE = "Simple";

	@Override
	public void initialize() {
		log.debug("Member Filter Controller - Initialize");
		checkNodes();

		// Would work for combo
		ObservableList<String> data = FXCollections.observableArrayList(ALL, BUSINESS, CHOICE, CORE, SIMPLE);
		memberTypeCombo.setPromptText("Object Type");
		memberTypeCombo.setOnAction(this::setTypeFilter);
		memberTypeCombo.setItems(data);

		memberNameFilter.textProperty().addListener((v, o, n) -> applyTextFilter());
		// memberNameFilter.setOnKeyTyped(e -> applyTextFilter(e)); // Key event happens before the textField is updated
		// memberNameFilter.setOnAction(e -> applyTextFilter()); // Fires on CR only

		editableButton.setOnAction(e -> setEditableOnly());
		latestButton.setOnAction(e -> setLatestOnly());

		errorsButton.setVisible(false); // hide for now
	}

	@Override
	public void configure(DexMainController mainController) {
		super.configure(mainController);
		configure(mainController.getModelManager());
		eventPublisherNode = memberFilter;
	}

	public void configure(OtmModelManager modelManager, DexPopupController popupController) {
		configure(modelManager);
		this.popupController = popupController;
	}

	/**
	 * Configure filter.
	 * 
	 * @param modelManager
	 */
	public void configure(OtmModelManager modelManager) {
		modelMgr = modelManager;
		configureLibraryChoice();
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
		librarySelector.setOnAction(e -> setLibraryFilter());
	}

	private OtmLibrary getSelectedLibrary() {
		String key = librarySelector.getSelectionModel().getSelectedItem();
		return libraryMap.get(key);
	}

	/**
	 * 
	 * @param member
	 *            to test
	 * @return true if the object passes the selection filters (should be displayed)
	 */
	public boolean isSelected(OtmLibraryMember member) {
		// log.debug("Is " + object.getName() + " selected?");
		if (member == null || member.getLibrary() == null)
			return true;
		if (libraryFilter != null && !member.getLibrary().getName().startsWith(libraryFilter))
			return false;
		if (textFilterValue != null && !member.getName().toLowerCase().startsWith(textFilterValue))
			return false;
		if (latestVersionOnly && !member.getLibrary().isLatestVersion())
			return false;
		if (editableOnly && !member.isEditable())
			return false;
		if (classNameFilter != null && !member.getClass().getSimpleName().startsWith(classNameFilter))
			return false;

		// NO filters applied OR passed all filters
		return true;
	}

	// Future - use fxControls or other package to get a multiple check box or even check tree to select versions.
	//
	/**
	 * Run when a GUI control changes the selected library.
	 */
	private void setLibraryFilter() {
		String selection = ALLLIBS;
		if (librarySelector.getSelectionModel().getSelectedItem() != null) {
			selection = librarySelector.getSelectionModel().getSelectedItem();
			if (librarySelector.getSelectionModel().getSelectedItem().equals(ALLLIBS)) {
				clear();
				fireFilterChangeEvent();
			} else {
				setLibraryFilter(libraryMap.get(selection));
			}
		}
		librarySelector.setValue(selection);
		memberFilter.fireEvent(new DexLibrarySelectionEvent(libraryMap.get(selection)));
	}

	private void setLibraryFilter(OtmLibrary lib) {
		ignoreClear = true;
		if (lib != null) {
			libraryFilter = lib.getName();
			librarySelector.getSelectionModel().select(lib.getName());
			fireFilterChangeEvent();
		}
		// log.debug("Set Library Filter to: " + libraryFilter);
		ignoreClear = false;
	}

	@Override
	public void handleEvent(Event event) {
		if (event instanceof DexLibrarySelectionEvent)
			librarySelectionHandler((DexLibrarySelectionEvent) event);
	}

	public void librarySelectionHandler(DexLibrarySelectionEvent event) {
		if (event != null && event.getLibrary() != null) {
			libraryFilter = event.getLibrary().getName();
			ignoreClear = true;
			librarySelector.getSelectionModel().select(event.getLibrary().getName());
			fireFilterChangeEvent();
			log.debug("Set Library Filter to: " + libraryFilter);
			ignoreClear = false;
		}
	}

	private void setEditableOnly() {
		log.debug("Editable set to: " + editableButton.isSelected());
		editableOnly = editableButton.isSelected();
		fireFilterChangeEvent();
	}

	/**
	 * Make and fire a filter event. Set ignore clear in case event handler tries to clear() this controller.
	 */
	private void fireFilterChangeEvent() {
		if (eventPublisherNode != null) {
			ignoreClear = true; // Set just in case event handler does a clear
			log.debug("Ready to fire controller level Filter Change event.");
			eventPublisherNode.fireEvent(new DexFilterChangeEvent(this, memberFilter));
			ignoreClear = false;
		} else if (popupController != null) {
			popupController.refresh();
		}
	}

	private void setLatestOnly() {
		log.debug("Latest only set to: " + latestButton.isSelected());
		latestVersionOnly = latestButton.isSelected();
		fireFilterChangeEvent();
	}

	private void setTypeFilter(ActionEvent e) {
		if (memberTypeCombo.getValue() != null) {
			String value = memberTypeCombo.getValue();
			if (value.isEmpty() || value.equals(ALL))
				classNameFilter = null;
			else if (value.startsWith(BUSINESS))
				classNameFilter = OtmBusinessObject.class.getSimpleName();
			else if (value.startsWith(CHOICE))
				classNameFilter = OtmChoiceObject.class.getSimpleName();
			else if (value.startsWith(CORE))
				classNameFilter = OtmCoreObject.class.getSimpleName();
			else if (value.startsWith(SIMPLE))
				classNameFilter = OtmSimpleObject.class.getSimpleName();

		}
		log.debug("Set Type Filter: " + classNameFilter);
		fireFilterChangeEvent();
	}

	/**
	 * Filter on any case of the text in the memberNameFilter
	 */
	private void applyTextFilter() {
		textFilterValue = memberNameFilter.getText().toLowerCase();
		fireFilterChangeEvent();
	}

	@Override
	public void clear() {
		// When posting updated filter results, do not clear the filters.
		if (!ignoreClear) {
			if (mainController != null)
				modelMgr = mainController.getModelManager();
			configureLibraryChoice();
			libraryFilter = null;

			textFilterValue = null;
			memberNameFilter.setText("");
		}
	}

	@Override
	public void refresh() {
		OtmLibrary prevLib = getSelectedLibrary();
		configureLibraryChoice();
		setLibraryFilter(prevLib);
	}

}
