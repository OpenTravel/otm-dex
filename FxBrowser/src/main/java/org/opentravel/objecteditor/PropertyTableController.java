/**
 * 
 */
package org.opentravel.objecteditor;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

/**
 * 2/10/2019 - this does NOT work. When instanciated, the controller load throws an error.
 * 
 * java.lang.RuntimeException: javafx.fxml.LoadException: Controller value already specified.
 * /C:/Users/dmh/Git/otm-dex/FxBrowser/target/classes/PropertyTable.fxml:10
 * 
 * at org.opentravel.objecteditor.PropertyTableController.<init>(PropertyTableController.java:37) at
 * org.opentravel.objecteditor.ObjectEditorController.doAccordian(ObjectEditorController.java:140) at
 * org.opentravel.objecteditor.ObjectEditorController.setStage(ObjectEditorController.java:125) at
 * org.opentravel.objecteditor.ObjectEditorApp.start(ObjectEditorApp.java:47)
 * 
 * 
 * <p>
 * TODO - fix or delete. If delete, delete PropertyTable.fxml also.
 * 
 * @author dmh
 *
 */
@SuppressWarnings("restriction")
public class PropertyTableController extends GridPane {
	private static final Logger LOGGER = LoggerFactory.getLogger(PropertyTableController.class);
	private static final String LAYOUT_FILE = "/PropertyTable.fxml";

	// @FXML
	// public TreeView<TreeNode> navigationTreeView;
	// NavigationTreeManager treeMgr;

	public PropertyTableController() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(LAYOUT_FILE));

		// FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PropertyTable.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}

		LOGGER.debug("Created property table.");
	}

}
