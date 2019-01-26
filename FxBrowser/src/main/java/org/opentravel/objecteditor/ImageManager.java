/**
 * 
 */
package org.opentravel.objecteditor;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Manage access to icons and images.
 * 
 * @author dmh
 *
 */
public class ImageManager {
	public enum Icons {
		APPLICATION("/icons/alt_window_16.gif"),
		BUSINESS("/icons/BusinessObject.png"),
		Error("/icons/error.gif"),
		LIBRARY("/icons/library.png"),
		CORE("/icons/CoreObject.gif"),
		CHOICE("/icons/Choice.gif");

		private String label; // User displayed value

		Icons(String label) {
			this.label = label;
		}
	}

	boolean initalized = false;

	/**
	 * Use primary stage icons. Will throw npe if not initialized.
	 */
	public ImageManager() {
	}

	public ImageManager(Stage primaryStage) {
		if (!initalized) {
			// All icons must be loaded into the stage
			for (Icons icon : Icons.values())
				primaryStage.getIcons().add(new Image(icon.label));
		}
		initalized = true;
	}

	public Image get(Icons icon) {
		return new Image(getClass().getResourceAsStream(icon.label));
	}

	public ImageView getView(Icons icon) {
		// Image i = get(icon);
		// ImageView iv = new ImageView(i);
		return new ImageView(get(icon));
	}

	// Image imageOk = new Image(getClass().getResourceAsStream("/icons/BusinessObject.png"));
	// Image error = new Image(getClass().getResourceAsStream("/icons/error.gif"));

}
