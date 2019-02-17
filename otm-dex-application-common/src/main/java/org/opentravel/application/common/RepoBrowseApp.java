package org.opentravel.application.common;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.opentravel.schemacompiler.repository.RepositoryAvailabilityChecker;
import org.opentravel.schemacompiler.repository.RepositoryException;
import org.opentravel.schemacompiler.repository.RepositoryManager;

@SuppressWarnings("restriction")
public class RepoBrowseApp extends Application {

	private RepositoryManager repositoryManager;
	private RepositoryAvailabilityChecker availabilityChecker;

	static int sceneHeight = 500;
	static int sceneWidth = 750;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Title of Window"); // the primary stage
		// primaryStage.setOnCloseRequest(e -> closeProgram(e));

		VBox layout1 = new VBox();
		layout1.getChildren().addAll();
		Scene scene1 = new Scene(layout1, sceneWidth, sceneHeight);

		primaryStage.setScene(scene1);
		primaryStage.show();

		try {
			repositoryManager = RepositoryManager.getDefault();
			availabilityChecker = RepositoryAvailabilityChecker.getInstance(repositoryManager);
			availabilityChecker.pingAllRepositories(true);

		} catch (RepositoryException e) {
			e.printStackTrace(System.out);
		}

		BrowseRepositoryDialogController controller = BrowseRepositoryDialogController.createDialog(
				"Open Library or Release", null, primaryStage);

		controller.showAndWait();

	}

	// public void closeProgram(WindowEvent e) {
	// e.consume(); // take the event away from windows
	// if (DialogBox.display("Close?", "Do you really want to close?"))
	// primary.close();
	// }
	//
	// /**
	// * @param e
	// * @return
	// */
	// public void closeProgram(ActionEvent e) {
	// e.consume();
	// window.close();
	// }
}