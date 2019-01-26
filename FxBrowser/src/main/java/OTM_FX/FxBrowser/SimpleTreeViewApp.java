/**
 * 
 */
package OTM_FX.FxBrowser;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

/**
 * @author dmh
 *
 */

public class SimpleTreeViewApp extends Application {

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

	public SimpleTreeViewApp() {
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		System.out.println("Starting stage.");

		// Parent root = FXMLLoader.load(getClass().getResource("/SimpleTreeView.fxml"));
		// FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/SimpleTreeView.fxml"));
		// try {
		// fxmlLoader.setro
		// // fxmlLoader.setRoot(this);
		// fxmlLoader.load();
		// } catch (IOException exception) {
		// throw new RuntimeException(exception);
		// }

		// SimpleTreeViewController stv = fxmlLoader.getController();
		SimpleTreeViewController stv = new SimpleTreeViewController();

		stage.setScene(new Scene(stv));
		stage.setTitle("Custom SimpleTreeView");
		stage.setWidth(300);
		stage.setHeight(200);
		stage.show();

	}
}
