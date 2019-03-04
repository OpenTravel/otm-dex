/**
 * 
 */
package org.opentravel.common;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author dmh
 *
 */
// @SuppressWarnings("restriction")
public class DialogBox {
	static boolean answer;
	private static String cancelText = "Cancel";
	private static String closeText = "Close";

	static Stage notifyWindow;

	public static void notify(String title, String message) {
		notifyWindow = new Stage();
		notifyWindow.initModality(Modality.NONE);
		notifyWindow.setTitle(title);
		notifyWindow.setMinWidth(350);

		Label label = new Label(message);

		VBox layout = new VBox(10);
		layout.getChildren().addAll(label);
		layout.setAlignment(Pos.CENTER);
		// Display window and wait for it to be closed before returning
		Scene scene = new Scene(layout);
		notifyWindow.setScene(scene);
		notifyWindow.show();
		// notifyWindow.showAndWait();
	}

	public static void close() {
		notifyWindow.close();
	}

	// TODO - constructor with text passed in

	public static boolean display(String title, String message) {
		Stage window = new Stage();

		// Block events to other windows
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.setMinWidth(350);

		Label label = new Label(message);

		Button cancelButton = new Button(cancelText);
		cancelButton.setAlignment(Pos.BOTTOM_LEFT); // does not work
		cancelButton.setOnAction(e -> {
			answer = false;
			window.close();
		});

		Button closeButton = new Button(closeText);
		closeButton.setAlignment(Pos.BOTTOM_RIGHT); // does not work
		closeButton.setOnAction(e -> {
			answer = true;
			window.close();
		});

		VBox layout = new VBox(10);
		layout.getChildren().addAll(label, closeButton, cancelButton);
		layout.setAlignment(Pos.CENTER);

		// Display window and wait for it to be closed before returning
		Scene scene = new Scene(layout);
		window.setScene(scene);
		window.showAndWait();
		return answer;
	}
}