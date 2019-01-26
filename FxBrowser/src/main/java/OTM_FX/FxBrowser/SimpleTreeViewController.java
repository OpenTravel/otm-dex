/**
 * 
 */
package OTM_FX.FxBrowser;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;

/**
 * @author dmh
 *
 */

public class SimpleTreeViewController extends VBox {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TreeView<?> treeView;

	@FXML
	private Button button1;

	@FXML
	private Button button2;

	public SimpleTreeViewController() {
		System.out.println("Initializing controller.");

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/SimpleTreeView.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	@FXML
	void initialize() {
		assert treeView != null : "fx:id=\"treeView\" was not injected: check your FXML file 'SimpleTreeView.fxml'.";
		assert button1 != null : "fx:id=\"button1\" was not injected: check your FXML file 'SimpleTreeView.fxml'.";
		assert button2 != null : "fx:id=\"button2\" was not injected: check your FXML file 'SimpleTreeView.fxml'.";

	}

	@FXML
	public void doButton1(ActionEvent e) {
		System.out.println("Do button 1.");
	}
}
