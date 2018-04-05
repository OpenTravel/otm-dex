/**
 * 
 */
package OTM_FX.FxBrowser;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;

/**
 * @author dmh
 *
 */
@SuppressWarnings("restriction")
public class MenuManager {

	Menu menu1;
	MenuBar menuBar;
	App app;

	public MenuBar get() {
		return menuBar;
	}

	public MenuManager(App app) {
		this.app = app;
		menuBar = new MenuBar();
		menuBar.getMenus().add(initFile());
		menuBar.getMenus().add(initDisplay());
	}

	private Menu initDisplay() {
		Menu menu = new Menu("Display");
		MenuItem link = new MenuItem("Link to Navigator");
		menu.getItems().add(link);
		menu.getItems().add(new SeparatorMenuItem());

		ToggleGroup tg = new ToggleGroup();
		RadioMenuItem outline = new RadioMenuItem("Name Only");
		RadioMenuItem summary = new RadioMenuItem("Summaries");
		RadioMenuItem details = new RadioMenuItem("All Details");
		outline.setSelected(true);
		tg.getToggles().addAll(outline, summary, details);
		menu.getItems().addAll(outline, summary, details);
		return menu;
	}

	private Menu initFile() {
		// File menu
		Menu fileMenu = new Menu("File");
		MenuItem newFile = new MenuItem("New...");
		newFile.setOnAction(e -> System.out.println("Create a new file..."));
		// TODO - newFile.setAccelerator(new KeyCombination("x"));

		fileMenu.getItems().add(newFile);
		fileMenu.getItems().add(new MenuItem("Open..."));

		MenuItem saveItem = new MenuItem("Save...");
		fileMenu.getItems().add(saveItem);
		saveItem.setDisable(true);

		fileMenu.getItems().add(new SeparatorMenuItem());
		CheckMenuItem alignmentItem = new CheckMenuItem("Automatic Alignment");
		alignmentItem.setSelected(true);
		alignmentItem.setOnAction(e -> {
			System.out.println(alignmentItem.isSelected());
		});
		fileMenu.getItems().add(alignmentItem);

		fileMenu.getItems().add(new SeparatorMenuItem());

		MenuItem exitItem = new MenuItem("Exit"); // underbar adds key shortcut
		fileMenu.getItems().add(exitItem);
		exitItem.setOnAction(e -> app.closeProgram(e));
		return fileMenu;
	}
}
