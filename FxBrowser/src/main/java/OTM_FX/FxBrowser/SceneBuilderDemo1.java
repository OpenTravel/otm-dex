/**
 * 
 */
package OTM_FX.FxBrowser;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * @author dmh
 *
 */
@SuppressWarnings("restriction")
public class SceneBuilderDemo1 extends Application {

	Stage window;
	static int sceneWidth = 800;
	static int sceneHeight = 600;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		System.out.println("Creating Primary Stage.");
		try {
			// Icons
			primaryStage.getIcons().add(new Image("/icons/alt_window_16.gif"));
			primaryStage.getIcons().add(new Image("/icons/BusinessObject.png"));
			primaryStage.setTitle("OpenTravel OTM-fxDE");

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/TestLayout1.fxml"));
			Parent root = loader.load();
			DemoController controller = loader.getController();
			controller.setStage(primaryStage);
			// Parent root = FXMLLoader.load(getClass().getResource("/TestLayout1.fxml"));

			Scene scene = new Scene(root, sceneWidth, sceneHeight);
			primaryStage.setScene(scene);
			scene.getStylesheets().add("DavesViper.css");
			primaryStage.show();
			window = primaryStage;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void closeProgram(WindowEvent e) {
		e.consume(); // take the event away from windows
		if (DialogBox.display("Close?", "Do you really want to close?"))
			window.close();
	}

}
