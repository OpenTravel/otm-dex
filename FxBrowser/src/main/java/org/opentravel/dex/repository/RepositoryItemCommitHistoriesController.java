/**
 * 
 */
package org.opentravel.dex.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.model.OtmModelManager;
import org.opentravel.objecteditor.DexIncludedControllerBase;
import org.opentravel.schemacompiler.repository.RepositoryItemCommit;
import org.opentravel.schemacompiler.repository.RepositoryItemHistory;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller for a library history table. Creates table containing library history properties.
 * 
 * @author dmh
 *
 */
public class RepositoryItemCommitHistoriesController extends DexIncludedControllerBase<RepoItemDAO> {
	private static Log log = LogFactory.getLog(RepositoryItemCommitHistoriesController.class);

	@FXML
	public TableView<CommitDAO> commitHistoriesTable;

	private TableView<CommitDAO> historyTable;
	private ObservableList<CommitDAO> commitList = FXCollections.observableArrayList();

	@Override
	public void initialize() {
		log.debug("Initializing repository library table view.");

		this.historyTable = commitHistoriesTable;
		if (historyTable == null)
			throw new IllegalStateException("Library History Table view is null.");

		// Initialize and build columns for library tree table
		buildColumns(historyTable);

		// Have table listen to observable list.
		historyTable.setItems(commitList);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return null
	 */
	@Override
	public OtmModelManager getModelManager() {
		return null;
	}

	@Override
	public void clear() {
		historyTable.getItems().clear();
	}

	@Override
	public void post(RepoItemDAO repoItem) throws Exception {
		super.post(repoItem);

		if (repoItem == null)
			throw new IllegalArgumentException("Missing repo item.");
		RepositoryItemHistory history = repoItem.getHistory();
		if (history == null) {
			log.debug("OOPS - need to wait for history to be retrieved.");
			return; // FIXME
		}
		for (RepositoryItemCommit cItem : history.getCommitHistory()) {
			commitList.add(new CommitDAO(cItem));
		}
	}

	/**
	 * Create Columns and set cell values
	 */
	private void buildColumns(TableView<CommitDAO> table) {
		TableColumn<CommitDAO, String> numCol = new TableColumn<>("Number");
		numCol.setCellValueFactory(new PropertyValueFactory<CommitDAO, String>("number"));
		setColumnProps(numCol, true, false, true, 0);

		TableColumn<CommitDAO, String> dateCol = new TableColumn<>("Date");
		dateCol.setCellValueFactory(new PropertyValueFactory<CommitDAO, String>("effective"));
		setColumnProps(dateCol, true, false, true, 250);

		TableColumn<CommitDAO, String> userCol = new TableColumn<>("User");
		userCol.setCellValueFactory(new PropertyValueFactory<CommitDAO, String>("user"));
		setColumnProps(userCol, true, false, true, 150);

		TableColumn<CommitDAO, String> remarksCol = new TableColumn<>("Remarks");
		remarksCol.setCellValueFactory(new PropertyValueFactory<CommitDAO, String>("remarks"));
		setColumnProps(remarksCol, true, false, true, 0);

		table.getColumns().setAll(numCol, dateCol, userCol, remarksCol);

		// // Give all left over space to the last column
		// double width = numCol.widthProperty().get();
		// width += dateCol.widthProperty().get();
		// width += userCol.widthProperty().get();
		// remarksCol.prefWidthProperty().bind(table.widthProperty().subtract(width));
		//
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return null
	 */
	@Override
	public ReadOnlyObjectProperty<TreeItem<CommitDAO>> getSelectable() {
		return null;
	}

}
