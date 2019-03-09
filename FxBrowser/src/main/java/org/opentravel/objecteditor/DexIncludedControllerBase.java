/**
 * 
 */
package org.opentravel.objecteditor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.common.ImageManager;
import org.opentravel.model.OtmModelManager;

import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableColumn;

/**
 * Abstract base controller for included controllers.
 * 
 * @author dmh
 *
 */
public abstract class DexIncludedControllerBase<T> implements DexIncludedController<T> {
	private static Log log = LogFactory.getLog(DexIncludedControllerBase.class);

	protected ImageManager imageMgr;
	protected DexController parentController;
	protected T postedData;

	public DexIncludedControllerBase() {
		log.debug("Constructing controller.");
	}

	@Override
	public void initialize() {
		log.debug("Initializing controller.");
	}

	@Override
	public void setParent(DexController parent) {
		this.parentController = parent;
		imageMgr = parent.getImageManager();
		log.debug("Parent controller set.");
	}

	@Override
	public OtmModelManager getModelManager() {
		if (parentController != null)
			return parentController.getModelManager();
		return null;
	}

	@Override
	public void post(T businessData) throws Exception {
		// Clear the view
		clear();
		// Hold onto data
		postedData = businessData;
		// FUTURE - create navigation event
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

	/**
	 * Utility to set column properties.
	 */
	protected void setColumnProps(TreeTableColumn<?, ?> c, boolean visable, boolean editable, boolean sortable,
			int width) {
		c.setVisible(visable);
		c.setEditable(editable);
		c.setSortable(sortable);
		if (width > 0)
			c.setPrefWidth(width);
	}

	@Override
	public ImageManager getImageManager() {
		if (imageMgr == null)
			if (parentController != null)
				return parentController.getImageManager();
			else
				throw new IllegalStateException("Image manger is null.");
		return imageMgr;
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
