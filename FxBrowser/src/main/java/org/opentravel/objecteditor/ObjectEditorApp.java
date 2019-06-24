/**
 * 
 */
package org.opentravel.objecteditor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.application.common.AbstractOTMApplication;
import org.opentravel.application.common.AbstractUserSettings;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * DEx - OpenTravel development environment object editor in JavaFX. This is the main application that launches the
 * window.
 * 
 * @author dmh
 *
 */
public class ObjectEditorApp extends AbstractOTMApplication {
	private static Log log = LogFactory.getLog(ObjectEditorApp.class);

	private static final String LAYOUT_FILE = "/OtmObjectEditorLayout.fxml";
	private static final String APPLICATION_TITLE = "DEx - OpenTravel Development Environment Object Editor";

	/**
	 * Default constructor.
	 */
	// public ObjectEditorApp() {
	// }

	// /**
	// * Constructor that provides the manager that should be used when accessing remote OTM repositories.
	// *
	// * @param repositoryManager
	// * the repository manager instance
	// */
	// public ObjectEditorApp(RepositoryManager repositoryManager) {
	// super(repositoryManager);
	// }

	public static void main(String[] args) {
		launch(args); // start this application in its own window
	}

	@Override
	public void start(Stage primaryStage) {
		// Super.start() can be used when getController() can be resolved.
		// super.start(primaryStage);

		log.debug("Creating Primary Stage.");
		try {
			primaryStage.setTitle(getMainWindowTitle());

			// Set up main controller
			FXMLLoader loader = new FXMLLoader(getClass().getResource(getMainWindowFxmlLocation()));
			Parent root = loader.load(); // will initialize controller

			// ***NEEDED*** controller needed to set stage into secondary controllers.
			ObjectEditorController oeController = loader.getController();
			oeController.setStage(primaryStage);
			// getController().setStage(primaryStage);

			//
			// Set the scene into the primary stage
			AbstractUserSettings userSettings = getUserSettings();
			Scene scene = new Scene(root, userSettings.getWindowSize().getWidth(),
					userSettings.getWindowSize().getHeight());
			primaryStage.setScene(scene);
			//
			// ADDED - not sure it is needed. it seems to get stylesheet from fxml files
			// scene.getStylesheets().add("DavesViper.css");

			primaryStage.show();

		} catch (Exception e) {
			if (e.getCause() != null)
				log.error("Error starting application, cause: " + e.getCause().toString());
			log.error("Error starting application: " + e.getLocalizedMessage());
		}
	}

	// Close handled in menuBarController

	// **** Required methods by the abstract base class ***************************
	//
	@Override
	protected String getMainWindowFxmlLocation() {
		return LAYOUT_FILE;
	}

	@Override
	protected String getMainWindowTitle() {
		return APPLICATION_TITLE;
	}

	@Override
	protected AbstractUserSettings getUserSettings() {
		return UserSettings.load();
	}

}
