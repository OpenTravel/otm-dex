/**
 * 
 */
package org.opentravel.dex.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableColumn;

/**
 * Abstract base controller for included controllers.
 * <p>
 * The generic type is the type of business data object used when "posting" to this controller.
 * 
 * @author dmh
 *
 */
public abstract class DexIncludedControllerBase<T> implements DexIncludedController<T> {
	private static Log log = LogFactory.getLog(DexIncludedControllerBase.class);

	protected ImageManager imageMgr;
	protected DexMainController parentController;
	protected T postedData;

	public DexIncludedControllerBase() {
		log.debug("Constructing controller.");
	}

	@Override
	public void clear() {
	}

	@Override
	public void configure(DexMainController parent) {
		checkNodes();
		this.parentController = parent;
		imageMgr = parent.getImageManager();
		log.debug("Parent controller set.");
	}

	@Override
	public DexMainController getParentController() {
		return parentController;
	}

	@FXML
	@Override
	public void initialize() {
		log.debug("Initializing controller.");
	}

	@Override
	public void post(T businessData) throws Exception {
		// Clear the view
		clear();
		// Hold onto data
		postedData = businessData;
		// FUTURE - create navigation event
	}

	@Override
	public void refresh() {
		try {
			post(postedData);
		} catch (Exception e) {
			log.error("Unhandled error refreshing repository item commit history: " + e.getLocalizedMessage());
		}
	}

	/**
	 * Utility to set table column properties.
	 */
	protected void setColumnProps(TableColumn<?, ?> c, boolean visable, boolean editable, boolean sortable, int width) {
		c.setVisible(visable);
		c.setEditable(editable);
		c.setSortable(sortable);
		if (width > 0)
			c.setPrefWidth(width);
	}

	/**
	 * Utility to set tree table column properties.
	 */
	protected void setColumnProps(TreeTableColumn<?, ?> c, boolean visable, boolean editable, boolean sortable,
			int width) {
		c.setVisible(visable);
		c.setEditable(editable);
		c.setSortable(sortable);
		if (width > 0)
			c.setPrefWidth(width);
	}

	/**
	 * TODO
	 */
	protected void setWidths(TableView table) {
		// Give all left over space to the last column
		// double width = fileCol.widthProperty().get();
		// width += versionCol.widthProperty().get();
		// width += statusCol.widthProperty().get();
		// width += lockedCol.widthProperty().get();
		// remarkCol.prefWidthProperty().bind(table.widthProperty().subtract(width));
	}

}
