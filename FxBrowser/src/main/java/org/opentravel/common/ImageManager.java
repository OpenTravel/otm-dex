/**
 * 
 */
package org.opentravel.common;

import java.util.EnumMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.model.OtmObject;

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
	private static Log log = LogFactory.getLog(ImageManager.class);

	public enum Icons {
		APPLICATION("/icons/alt_window_16.gif"),
		ALIAS("/icons/alias.gif"),
		BUSINESS("/icons/BusinessObject.png"),
		ENUMERATION_OPEN("/icons/EnumerationOpen.gif"),
		ENUMERATION_CLOSED("/icons/EnumerationClosed.gif"),
		ENUMERATION_VALUE("/icons/EnumerationValue.jpg"),
		ELEMENT("/icons/Element.gif"),
		ELEMENTREF("/icons/ElementRef.gif"),
		Error("/icons/error.gif"),
		LIBRARY("/icons/library.png"),
		CORE("/icons/CoreObject.gif"),
		CHOICE("/icons/Choice.gif"),
		FACET("/icons/Facet.gif"),
		FACET_CONTEXTUAL("/icons/Facet-contextual.gif"),
		FACET_CONTRIBUTED("/icons/Facet-contributed.gif"),
		ATTRIBUTE("/icons/Attribute.jpg"),
		INDICATOR("/icons/Indicator.gif"),
		INDICATORELEMENT("/icons/IndicatorElement.gif"),
		OPERATION("/icons/Operation.jpg"),
		RESOURCE("/icons/ResourceObject.gif"),
		RUN("/icons/run.gif"),
		SERVICE("/icons/Service.gif"),
		SIMPLE("/icons/SimpleObject.gif"),
		V_OK("/icons/checkmark.png"),
		V_ERROR("/icons/error_st_obj.gif"),
		V_WARN("/icons/warning_st_obj.gif"),
		VWA("/icons/VWA.gif"),
		XSD_SIMPLE("/icons/XSDSimpleType.gif");
		private String label; // User displayed value

		Icons(String label) {
			this.label = label;
		}
	}

	boolean initalized = false;
	private static Map<Icons, Image> iconMap = new EnumMap<>(Icons.class);

	/**
	 * Use primary stage icons. Will throw npe if not initialized.
	 */
	public ImageManager() {
		// Only used as to run get(OtmObject)
	}

	public ImageManager(Stage primaryStage) {
		if (!initalized) {
			// All icons must be loaded into the stage and retained for reuse
			Image image;
			for (Icons icon : Icons.values()) {
				try {
					image = new Image(icon.label);
					// Control height, width, ratio, smooth resize, in background
					// image = new Image(icon.label, 64, 64, true, true, true);
					if (primaryStage != null)
						primaryStage.getIcons().add(image);
					iconMap.put(icon, image);
				} catch (Exception e) {
					log.error("Could not create image: " + e.getLocalizedMessage());
				}
			}
		}
		initalized = true;
	}

	// TEST - make a map of icon type and Image - use that map in getView()
	// TODO - make this a child of modelManager and choose ONE api method
	@Deprecated
	public Image get_OLD(Icons icon) {
		return icon != null ? iconMap.get(icon) : null;
		// return icon != null ? new Image(getClass().getResourceAsStream(icon.label)) : null;
	}

	/**
	 * Preferred method for getting an image view to represent an OTM object.
	 * 
	 * @param element
	 *            OtmObject to select which type of icon
	 * @return new imageView containing the image associated with the icon or null if no icon image is found
	 */
	@Deprecated
	public ImageView get_OLD(OtmObject element) {
		return get_OLD(element.getIconType()) != null ? new ImageView(get_OLD(element.getIconType())) : null;
	}

	/**
	 * Preferred method for getting an image view to represent an OTM object.
	 * 
	 * @param otm
	 *            OtmObject to select which type of icon
	 * @return new imageView containing the image associated with the icon or null if no icon image is found
	 */
	public static ImageView get(OtmObject otm) {
		return get(otm.getIconType()) != null ? new ImageView(get(otm.getIconType())) : null;
	}

	/**
	 * Get an image view for a non-OTM object.
	 * 
	 * @see #get(OtmObject)
	 * 
	 * @param icon
	 *            is one of the icon types listed in the enumeration
	 * @return a JavaFX node for the icon
	 */
	public static Image get(Icons icon) {
		return icon != null ? iconMap.get(icon) : null;
		// return icon != null ? new Image(getClass().getResourceAsStream(icon.label)) : null;
	}

	/**
	 * Get an image view for a non-OTM object.
	 * 
	 * @see #get_OLD(OtmObject)
	 * 
	 * @param icon
	 *            is one of the icon types listed in the enumeration
	 * @return a JavaFX node for the icon
	 */
	@Deprecated
	public ImageView getView(Icons icon) {
		// Image i = get(icon);
		// ImageView iv = new ImageView(i);
		return new ImageView(get_OLD(icon));
	}

	// /**
	// * @param Image
	// * from OtmModelElement.getIcon()
	// *
	// * @return a javafx node for the icon
	// */
	// @Deprecated
	// public ImageView getView(Image icon) {
	// return new ImageView(icon);
	// }

	/**
	 * get an image view to represent an OTM object.
	 * 
	 * @param OtmModelElement
	 * 
	 * @return a JavaFX node for the icon
	 */
	@Deprecated
	public ImageView getView(OtmObject element) {
		return new ImageView(element.getIcon());
	}

	// Image imageOk = new Image(getClass().getResourceAsStream("/icons/BusinessObject.png"));
	// Image error = new Image(getClass().getResourceAsStream("/icons/error.gif"));

}
