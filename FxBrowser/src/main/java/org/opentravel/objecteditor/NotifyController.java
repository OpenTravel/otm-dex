/**
 * 
 */
package org.opentravel.objecteditor;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author dmh
 *
 */
// @SuppressWarnings("restriction")
public class NotifyController {
	private static Log log = LogFactory.getLog(NotifyController.class);

	private static int sceneWidth = 600;
	private static int sceneHeight = 400;
	private static final String LAYOUT_FILE = "/NotifyLayout.fxml";

	Stage notifyWindow;

	@FXML
	private void initialize(URL location, ResourceBundle resources) {
		log.debug("Notify Controller - Initialize w/params is now loading!");
	}

	public void show(String title, String message) {
		notifyWindow = new Stage();
		notifyWindow.initModality(Modality.NONE);
		notifyWindow.setTitle(title);
		notifyWindow.setMinWidth(sceneWidth);
		notifyWindow.setMinHeight(sceneHeight);

		FXMLLoader loader = new FXMLLoader(getClass().getResource(LAYOUT_FILE));
		Parent root = null;
		try {
			root = loader.load();
			NotifyController controller = loader.getController();
			// controller.setStage(notifyWindow);
			Scene scene = new Scene(root, sceneWidth, sceneHeight);
			// Scene scene = new Scene(layout);
			notifyWindow.setScene(scene);
			notifyWindow.show();
		} catch (IOException e) {
			log.error("Error loading layout: " + e.getLocalizedMessage());
		} // will initialize controller

		// Label label = new Label(message);
		//
		// VBox layout = new VBox(10);
		// layout.getChildren().addAll(label);
		// layout.setAlignment(Pos.CENTER);
		// Display window and wait for it to be closed before returning
		// notifyWindow.showAndWait();
	}

	public void close() {
		notifyWindow.close();
	}
}