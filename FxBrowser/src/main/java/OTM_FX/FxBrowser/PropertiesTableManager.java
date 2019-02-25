/**
 * 
 */
package OTM_FX.FxBrowser;

import org.opentravel.common.ImageManager;
import org.opentravel.model.OtmModelElement;
import org.opentravel.model.otmFacets.OtmFacet;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;
import org.opentravel.model.otmProperties.OtmProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * Manage a properties table.
 * 
 * @author dmh
 *
 */
@Deprecated
@SuppressWarnings("restriction")
public class PropertiesTableManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesTableManager.class);

	// Obscure generic from table view
	public class PropertyNode {
		protected OtmProperty<?> property;

		public PropertyNode(OtmProperty<?> property) {
			this.property = property;
		}

		public String getName() {
			return property.getName();
		}

		public boolean isEditable() {
			return property.isEditable();
		}

		@Override
		public String toString() {
			return property.toString();
		}
	}

	protected ImageManager images;

	public PropertiesTableManager(OtmFacet<?> facet, TableView<PropertyNode> table, Stage stage) {
		System.out.println("Initializing property table for " + facet + "facet.");

		if (table == null)
			throw new IllegalStateException("Tree view is null.");

		images = new ImageManager(stage);

		buildRows(facet, table);
		buildColumns(table);
	}

	public PropertiesTableManager(OtmLibraryMember<?> member, TableView<PropertyNode> table, Stage stage) {
		System.out.println("Initializing property table for " + member + "member.");

		if (table == null)
			throw new IllegalStateException("Tree view is null.");

		images = new ImageManager(stage);

		for (OtmModelElement<?> facet : member.getChildren())
			if (facet instanceof OtmFacet)
				buildRows((OtmFacet<?>) facet, table);

		buildColumns(table);
	}

	private void buildColumns(TableView<PropertyNode> table) {
		TableColumn nameCol = new TableColumn("Name");
		TableColumn roleCol = new TableColumn("Role");
		TableColumn typeCol = new TableColumn("Assigned Type");
		TableColumn descCol = new TableColumn("Description");

		TableColumn constraintCol = new TableColumn("Repeat");
		TableColumn minCol = new TableColumn("min");
		TableColumn maxCol = new TableColumn("max");
		constraintCol.getColumns().addAll(minCol, maxCol);

		nameCol.setCellValueFactory(new PropertyValueFactory<PropertyNode, String>("name"));

		table.getColumns().addAll(nameCol, roleCol, typeCol, constraintCol, descCol);
	}

	/**
	 * Create observable list containing all the properties
	 */
	private ObservableList<PropertyNode> buildRows(OtmFacet<?> facet, TableView<PropertyNode> table) {
		ObservableList<PropertyNode> properties = table.getItems();
		for (OtmModelElement<?> p : facet.getChildren())
			if (p instanceof OtmProperty)
				properties.add(new PropertyNode((OtmProperty<?>) p));

		System.out.println("built data for " + properties.size() + " rows.");

		// Set the number of rows in the table view
		// https://stackoverflow.com/questions/27945817/javafx-adapt-tableview-height-to-number-of-rows
		//
		// IntStream.range(0, 20).mapToObj(Integer::toString).forEach(table.getItems()::add);
		// table.setPrefHeight(150);
		// table.setFixedCellSize(25);
		// table.prefHeightProperty().bind(table.fixedCellSizeProperty().multiply(Bindings.size(table.getItems()).add(1.01)));
		// table.minHeightProperty().bind(table.prefHeightProperty());
		// table.maxHeightProperty().bind(table.prefHeightProperty());

		return properties;
	}
}
