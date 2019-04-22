/**
 * 
 */
package org.opentravel.dex.controllers.member;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.dex.controllers.DexController;
import org.opentravel.dex.controllers.DexIncludedControllerBase;
import org.opentravel.dex.controllers.DexMainController;
import org.opentravel.dex.events.DexFilterChangeEvent;
import org.opentravel.dex.events.DexMemberSelectionEvent;
import org.opentravel.model.OtmModelElement;
import org.opentravel.model.OtmModelManager;
import org.opentravel.model.OtmTypeProvider;
import org.opentravel.model.otmFacets.OtmContributedFacet;
import org.opentravel.model.otmLibraryMembers.OtmContextualFacet;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.SortType;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

/**
 * Manage the library member navigation tree.
 * 
 * @author dmh
 *
 */
public class MemberTreeTableController extends DexIncludedControllerBase<OtmModelManager> implements DexController {
	private static Log log = LogFactory.getLog(MemberTreeTableController.class);

	// Column labels
	// TODO - externalize strings
	public static final String PREFIXCOLUMNLABEL = "Prefix";
	private static final String NAMECOLUMNLABEL = "Member";
	private static final String VERSIONCOLUMNLABEL = "Version";
	private static final String LIBRARYLABEL = "Library";
	private static final String ERRORLABEL = "Errors";

	/*
	 * FXML injected
	 */
	@FXML
	TreeTableView<MemberDAO> memberTree;
	@FXML
	private VBox memberTreeController;

	//
	TreeItem<MemberDAO> root; // Root of the navigation tree. Is displayed.

	TreeTableColumn<MemberDAO, String> nameColumn; // an editable column

	OtmModelManager currentModelMgr; // this is postedData
	MemberFilterController filter = null;

	private boolean ignoreEvents = false;

	// By default, the tree is editable. Setting this to false will prevent edits.
	private boolean treeEditingEnabled = true;

	// All event types listened to by this controller's handlers
	private static final EventType[] subscribedEvents = { DexFilterChangeEvent.FILTER_CHANGED,
			DexMemberSelectionEvent.MEMBER_SELECTED };
	private static final EventType[] publishedEvents = { DexMemberSelectionEvent.MEMBER_SELECTED };

	public MemberTreeTableController() {
		super(subscribedEvents, publishedEvents);
	}

	@Override
	public void checkNodes() {
		if (memberTree == null)
			throw new IllegalStateException("Tree table view is null.");

	}

	@Override
	public void configure(DexMainController parent) {
		super.configure(parent);
		log.debug("Configuring Member Tree Table.");
		eventPublisherNode = memberTreeController;
		configure(parent.getModelManager(), parent.getImageManager(), treeEditingEnabled);
	}

	/**
	 * 
	 * @param modelMgr
	 * @param editable
	 *            sets tree editing enables
	 */
	public void configure(OtmModelManager modelMgr, ImageManager imageMgr, boolean editable) {
		if (modelMgr == null)
			throw new IllegalArgumentException("Model manager is null. Must configure member tree with model manager.");
		this.imageMgr = imageMgr;
		this.treeEditingEnabled = editable;

		// Set the hidden root item
		root = new TreeItem<>();
		root.setExpanded(true); // Startout fully expanded

		// Set up the TreeTable
		buildColumns();

		// create cells for members
		currentModelMgr = modelMgr;
		// for (OtmLibraryMember member : currentModelMgr.getMembers()) {
		// createTreeItem(member, root);
		// }
		refresh();

		memberTree.getSelectionModel().select(0);
		memberTree.setOnMouseClicked(this::mouseClick);
		// add a listener class with three parameters that invokes selection listener
		memberTree.getSelectionModel().selectedItemProperty()
				.addListener((v, old, newValue) -> memberSelectionListener(newValue));

	}

	public void setOnMouseClicked(EventHandler<? super MouseEvent> handler) {
		memberTree.setOnMouseClicked(handler);
	}

	public void mouseClick(MouseEvent event) {
		// this fires after the member selection listener
		if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2)
			log.debug("Double click selection: "
					+ memberTree.getSelectionModel().getSelectedItem().getValue().nameProperty().toString());
	}

	public MemberDAO getSelected() {
		return memberTree.getSelectionModel().getSelectedItem() != null
				? memberTree.getSelectionModel().getSelectedItem().getValue() : null;
	}

	@Override
	public void handleEvent(Event event) {
		log.debug(event.getEventType() + " event received.");
		if (!ignoreEvents) {
			if (event instanceof DexMemberSelectionEvent)
				handleEvent((DexMemberSelectionEvent) event);
			if (event instanceof DexFilterChangeEvent)
				handleEvent((DexFilterChangeEvent) event);
			else
				refresh();
		}
	}

	private void handleEvent(DexFilterChangeEvent event) {
		if (!ignoreEvents)
			refresh();
	}

	private void handleEvent(DexMemberSelectionEvent event) {
		if (!ignoreEvents)
			select(event.getMember());
	}

	private void buildColumns() {
		memberTree.setRoot(getRoot());
		memberTree.setShowRoot(false);
		memberTree.setEditable(true);
		memberTree.getSelectionModel().setCellSelectionEnabled(true); // allow individual cells to be edited
		memberTree.setTableMenuButtonVisible(true); // allow users to select columns

		// Enable context menus at the row level and add change listener for for applying style
		memberTree.setRowFactory((TreeTableView<MemberDAO> p) -> new MemberRowFactory(this));

		//
		// Create columns
		//
		TreeTableColumn<MemberDAO, String> prefixColumn = new TreeTableColumn<>(PREFIXCOLUMNLABEL);
		prefixColumn.setPrefWidth(100);
		prefixColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
		prefixColumn.setVisible(false); // Works - is true by default
		prefixColumn.setCellValueFactory(new TreeItemPropertyValueFactory<MemberDAO, String>("prefix"));

		// TreeTableColumn<MemberDAO, ImageView> iconColumn = new TreeTableColumn<>("");
		// iconColumn.setPrefWidth(50);
		// iconColumn.setSortable(false);
		// iconColumn.setCellValueFactory((CellDataFeatures<MemberDAO, ImageView> p) -> {
		// if (p.getValue() != null)
		// p.getValue().setGraphic(p.getValue().getValue().getIcon(imageMgr));
		// return null;
		// });

		// TODO - use formatter in base type
		nameColumn = new TreeTableColumn<>(NAMECOLUMNLABEL);
		nameColumn.setPrefWidth(150);
		nameColumn.setEditable(true);
		nameColumn.setSortable(true);
		// nameColumn.setSortType(TreeTableColumn.SortType.DESCENDING);
		nameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<MemberDAO, String>("name"));
		// not needed - nameColumn.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		// Start out sorted on names
		// nameColumn.setSortType(SortType.DESCENDING);
		nameColumn.setSortType(SortType.ASCENDING);

		TreeTableColumn<MemberDAO, String> versionColumn = new TreeTableColumn<>(VERSIONCOLUMNLABEL);
		versionColumn.setCellValueFactory(new TreeItemPropertyValueFactory<MemberDAO, String>("version"));

		TreeTableColumn<MemberDAO, String> libColumn = new TreeTableColumn<>(LIBRARYLABEL);
		libColumn.setCellValueFactory(new TreeItemPropertyValueFactory<MemberDAO, String>("library"));

		TreeTableColumn<MemberDAO, String> errTextColumn = new TreeTableColumn<>(ERRORLABEL);
		errTextColumn.setCellValueFactory(new TreeItemPropertyValueFactory<MemberDAO, String>("error"));

		TreeTableColumn<MemberDAO, ImageView> valColumn = new TreeTableColumn<>("");
		// errColumn.setCellValueFactory(new TreeItemPropertyValueFactory<MemberDAO, ImageView>("errorImage"));
		valColumn.setPrefWidth(25);
		valColumn.setEditable(false);
		valColumn.setSortable(false);

		valColumn.setCellFactory(c -> {
			return new TreeTableCell<MemberDAO, ImageView>() {
				@Override
				protected void updateItem(ImageView item, boolean empty) {
					super.updateItem(item, empty);
					// Provide imageView directly - does not update automatically as the observable property would
					// Provide tooltip showing validation results
					String name = "";
					if (!empty && getTreeTableRow() != null && getTreeTableRow().getItem() != null) {
						setGraphic(getTreeTableRow().getItem().getValue().validationImage());
						name = getTreeTableRow().getItem().getValue().getValidationFindingsAsString();
						if (!name.isEmpty())
							setTooltip(new Tooltip(name));
					} else {
						setGraphic(null);
						setTooltip(null);
					}
				}
			};
		});
		// Does NOT work - provides image but not tool tip. Ignored when cell factory is set.
		// errColumn.setCellValueFactory(
		// new Callback<CellDataFeatures<MemberDAO, ImageView>, ObservableValue<ImageView>>() {
		// @Override
		// public ObservableValue<ImageView> call(CellDataFeatures<MemberDAO, ImageView> p) {
		// ObjectProperty<ImageView> iv = p.getValue().getValue().getValue().validationImageProperty();
		// return iv;
		// }
		// });

		// Add columns to table
		memberTree.getColumns().addAll(nameColumn, valColumn, libColumn, versionColumn, prefixColumn, errTextColumn);

		memberTree.getSortOrder().add(nameColumn);
	}

	/**
	 * {@inheritDoc} Remove all items from the member tree.
	 */
	@Override
	public void clear() {
		memberTree.getRoot().getChildren().clear();
	}

	/**
	 * TreeItem class does not extend the Node class.
	 * 
	 * Therefore, you cannot apply any visual effects or add menus to the tree items. Use the cell factory mechanism to
	 * overcome this obstacle and define as much custom behavior for the tree items as your application requires.
	 * 
	 * @param item
	 * @return
	 */
	TreeItem<MemberDAO> createTreeItem(OtmLibraryMember member, TreeItem<MemberDAO> parent) {
		// Apply Filter
		if (filter != null && !filter.isSelected(member))
			return null;
		TreeItem<MemberDAO> item = new TreeItem<>(new MemberDAO(member));
		item.setExpanded(false);
		item.setGraphic(imageMgr.getView((OtmModelElement<?>) member));

		// Skip over contextual facets that have been injected into an object
		if ((!(member instanceof OtmContextualFacet) || ((OtmContextualFacet) member).getWhereContributed() == null)) {
			// Create item for the library member
			parent.getChildren().add(item);
		}
		// Create items for the type provider children of this member
		for (OtmTypeProvider ele : member.getChildren_TypeProviders()) {
			TreeItem<MemberDAO> innerItem = createTreeItem(ele, item);
			if (ele instanceof OtmContributedFacet && ((OtmContributedFacet) ele).getContributor() != null) {
				for (OtmTypeProvider child : ((OtmContributedFacet) ele).getContributor().getChildren_TypeProviders())
					createTreeItem(child, innerItem);
			}
		}

		return item;
	}

	private TreeItem<MemberDAO> createTreeItem(OtmTypeProvider ele, TreeItem<MemberDAO> parent) {
		TreeItem<MemberDAO> item = new TreeItem<>(new MemberDAO(ele));
		item.setExpanded(false);
		parent.getChildren().add(item);
		item.setGraphic(imageMgr.getView((OtmModelElement<?>) ele));
		return item;
	}

	public MemberFilterController getFilter() {
		return filter;
	}

	public TreeItem<MemberDAO> getRoot() {
		return root;
	}

	/**
	 * Listener for selected library members in the tree table.
	 * 
	 * @param item
	 */
	private void memberSelectionListener(TreeItem<MemberDAO> item) {
		if (item == null)
			return;
		log.debug("Selection Listener: " + item.getValue());
		assert item != null;
		boolean editable = false;
		if (treeEditingEnabled && item.getValue() != null)
			editable = item.getValue().isEditable();
		nameColumn.setEditable(editable);
		ignoreEvents = true;
		if (eventPublisherNode != null)
			eventPublisherNode.fireEvent(new DexMemberSelectionEvent(this, item));
		ignoreEvents = false;
	}

	/**
	 * Get the library members from the model manager and put them into a cleared tree.
	 * 
	 * @param modelMgr
	 */
	@Override
	public void post(OtmModelManager modelMgr) {
		if (modelMgr != null)
			currentModelMgr = modelMgr;
		if (getFilter() != null)
			getFilter().clear();
		refresh();
	}

	public void select(OtmLibraryMember otm) {
		if (otm != null) {
			for (TreeItem<MemberDAO> item : memberTree.getRoot().getChildren()) {
				if (item.getValue().getValue() == otm) {
					int row = memberTree.getRow(item);
					// This may not highlight the row if the event comes from or goes to a different controller.
					Platform.runLater(() -> {
						ignoreEvents = true;
						memberTree.requestFocus();
						memberTree.getSelectionModel().clearAndSelect(row);
						memberTree.scrollTo(row);
						memberTree.getFocusModel().focus(row);
						ignoreEvents = false;
					});
					log.debug("Selected " + otm.getName() + " in member tree.");
					return;
				}
			}
			log.debug(otm.getName() + " not found in member tree.");
		}
	}

	@Override
	public void refresh() {
		if (memberTree != null) {
			// create cells for members
			memberTree.getRoot().getChildren().clear();
			for (OtmLibraryMember member : currentModelMgr.getMembers()) {
				createTreeItem(member, root);
			}
			try {
				memberTree.sort();
			} catch (Exception e) {
				// FIXME - why does first sort always throw exception?
				log.debug("Exception sorting: " + e.getLocalizedMessage());
			}
		}
	}

	public void setFilter(MemberFilterController filter) {
		this.filter = filter;
	}
}
