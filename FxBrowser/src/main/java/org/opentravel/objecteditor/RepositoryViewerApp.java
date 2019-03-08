/**
 * 
 */
package org.opentravel.objecteditor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.application.common.AbstractOTMApplication;
import org.opentravel.application.common.AbstractUserSettings;
import org.opentravel.common.DialogBox;
import org.opentravel.common.ImageManager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * @author dmh
 *
 */
public class RepositoryViewerApp extends AbstractOTMApplication {
	private static Log log = LogFactory.getLog(RepositoryViewerApp.class);

	Stage window;
	private static int sceneWidth = 1200;
	private static int sceneHeight = 800;
	private static final String LAYOUT_FILE = "/RepositoryViewer.fxml";
	private static final String APPLICATION_TITLE = "OpenTravel Repository Viewer";

	public static void main(String[] args) {
		launch(args); // start this application in its own window
	}

	@Override
	public void start(Stage primaryStage) {
		log.debug("Creating Primary Stage.");
		try {
			// Icons
			ImageManager imageManager = new ImageManager(primaryStage);
			// primaryStage.getIcons().add(new Image("/icons/alt_window_16.gif"));
			// primaryStage.getIcons().add(new Image("/icons/BusinessObject.png"));
			primaryStage.setTitle(APPLICATION_TITLE);

			// Set up main controller
			log.debug("Loading: " + LAYOUT_FILE);
			FXMLLoader loader = new FXMLLoader(getClass().getResource(LAYOUT_FILE));
			Parent root = loader.load(); // will initialize controller
			RepositoryViewerController controller = loader.getController();
			controller.setStage(primaryStage);

			// Set the scene into the primary stage
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opentravel.application.common.AbstractOTMApplication#getMainWindowFxmlLocation()
	 */
	@Override
	protected String getMainWindowFxmlLocation() {
		return LAYOUT_FILE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opentravel.application.common.AbstractOTMApplication#getUserSettings()
	 */
	@Override
	protected AbstractUserSettings getUserSettings() {
		// TODO return UserSettings.load();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opentravel.application.common.AbstractOTMApplication#getMainWindowTitle()
	 */
	@Override
	protected String getMainWindowTitle() {
		return APPLICATION_TITLE;
	}

}
