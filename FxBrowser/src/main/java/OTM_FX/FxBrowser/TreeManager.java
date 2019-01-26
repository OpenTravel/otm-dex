/**
 * 
 */
package OTM_FX.FxBrowser;

import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * @author dmh
 *
 */
@SuppressWarnings("restriction")
public class TreeManager {

	TreeItem<String> root, branch1, branch2, branch3;
	Stage stage;

	public TreeManager(Stage stage) {
		root = new TreeItem<>();
		root.setExpanded(true);
		this.stage = stage;

		branch1 = makeBranch("Item1", root);
		makeBranch("Leaf1", branch1);
		makeBranch("Leaf2", branch1);
		branch2 = makeBranch("Item2", root);
		makeBranch("Leaf1", branch2);
		branch3 = makeBranch("Item3", branch2);
		makeBranch("Leaf1", branch3);
		makeBranch("Leaf2", branch3);
		makeBranch("Leaf3", branch3);
	}

	public TreeItem<String> getRoot() {
		return root;
	}

	private TreeItem<String> makeBranch(String label, TreeItem<String> parent) {
		Image imageOk = new Image(getClass().getResourceAsStream("/icons/BusinessObject.png"));
		Image error = new Image(getClass().getResourceAsStream("/icons/error.gif"));
		ImageView iv = new ImageView(imageOk);
		ImageView ie = new ImageView(error);
		// iv.setBlendMode(BLENDMODE.);
		// GraphicDecoration decoration = new GraphicDecoration(ie, Pos.TOP_LEFT);

		TreeItem<String> item = new TreeItem<>(label);
		// Decorator.addDecoration( item, decoration);
		// Platform.runLater(new Runnable() {
		// @Override
		// public void run() {
		// try {
		// Decorator.addDecoration(item.graphicProperty().get(), decoration);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// });
		item.setGraphic(iv);
		item.setExpanded(true);
		parent.getChildren().add(item);
		return item;
	}
}
