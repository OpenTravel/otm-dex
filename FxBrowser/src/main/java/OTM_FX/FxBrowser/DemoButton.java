/**
 * 
 */
package OTM_FX.FxBrowser;

import javafx.scene.control.Button;

/**
 * @author dmh
 *
 */
public class DemoButton extends Button {

	public DemoButton() {
		this("");
	}

	public DemoButton(String text) {
		super(text);
		getStyleClass().add("demo-button");
	}

	@Override
	public String getUserAgentStylesheet() {
		return DemoButton.class.getResource("/DemoButton.css").toExternalForm();
	}

}
