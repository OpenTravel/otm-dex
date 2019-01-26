package OTM_FX.FxBrowser;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

@SuppressWarnings("restriction")
public class App extends Application {

	Button button1, button2, button3;
	Scene scene1, scene2, scene3;
	Stage window;
	CheckBox box1, box2;
	ListView<String> listView;
	TreeView<String> treeView;

	static int sceneHeight = 500;
	static int sceneWidth = 750;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		this.window = primaryStage;
		window.setTitle("JavaFX Demo Application"); // the primary stage
		window.setOnCloseRequest(e -> closeProgram(e));

		MenuManager menuMgr = new MenuManager(this);

		// Button with action
		button1 = new Button();
		button1.setText("Go To scene 2");
		button1.setOnAction(e -> sceneHandler(e, window));

		// DemoButton demoButton = new DemoButton("Demo Button");

		RadioButton caspian = new RadioButton("Caspian"); // my favorite
		RadioButton modena = new RadioButton("Modena");
		caspian.setOnAction(e -> setUserAgentStylesheet(STYLESHEET_CASPIAN));
		modena.setOnAction(e -> setUserAgentStylesheet(STYLESHEET_MODENA));
		ToggleGroup sg = new ToggleGroup();
		sg.getToggles().addAll(caspian, modena);

		TableManager tableMgr = new TableManager();
		TableView<DemoNode> table = tableMgr.build();
		table.setItems(tableMgr.getNodes());

		// Check boxes
		box1 = new CheckBox("Box 1");
		box2 = new CheckBox("Box 2");
		box2.setSelected(true);

		// Combo box
		ComboBox<String> combo = new ComboBox<>();
		combo.getItems().addAll("A", "B", "C");
		combo.setOnAction(e -> System.out.println(combo.getValue()));
		combo.setPromptText("Letter");
		combo.setEditable(true); // Overrides prompt text

		// Choice box - drop down list
		ChoiceBox<String> choiceBox = new ChoiceBox<>();
		choiceBox.getItems().addAll("Apples", "Pears", "Cherry");
		choiceBox.setValue("Apples");
		choiceBox.setOnAction(e -> choiceHandler(choiceBox));
		// Alternate way to get an event from an Observable List
		choiceBox.getSelectionModel().selectedItemProperty()
				.addListener((v, old, newValue) -> System.out.println("property: " + newValue));

		listView = new ListView<>();
		listView.getItems().addAll("Dog", "Cat", "Hamster", "Fish", "Turtle");
		listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		// Todo - how to create selection events

		// Layout scene 1
		// StackPane layout1 = new StackPane();
		VBox layout1 = new VBox();
		// layout1.getChildren().addAll(button1, demoButton, caspian, modena, table, tableMgr.getEditPane(), box1, box2,
		// choiceBox, combo, listView);
		layout1.getChildren().addAll(button1, caspian, modena, table, tableMgr.getEditPane(), box1, box2, choiceBox,
				combo, listView);
		scene1 = new Scene(layout1, sceneWidth, sceneHeight);
		// scene1.getStylesheets().add("DemoButton.css");
		// scene1.getStylesheets().add(DemoButton.class.getResource("/DemoButton.css").toExternalForm());
		// scene1.getStylesheets().add(demoButton.getUserAgentStylesheet());
		//
		// second scene
		BorderPane layout2 = new BorderPane();

		layout2.setTop(menuMgr.get());

		// Center of layout
		Label label2 = new Label("Welcome to the Second scene!");
		button2 = new Button("Go to scene 1");
		button2.setOnAction(e -> sceneHandler(e, window));
		TextField txtField = new TextField();
		txtField.setPromptText("hint - enter a number");
		txtField.setOnAction(e -> myTextHandler(txtField));
		Button prettyButton = new Button("Does Nothing");
		prettyButton.getStyleClass().add("button-blue");
		//
		VBox layout2c = new VBox(20);
		layout2c.getChildren().addAll(label2, button2, prettyButton, txtField);
		// layout2.setCenter(label2);
		layout2.setCenter(layout2c);

		// Left pane - Tree View
		TreeManager treeMgr = new TreeManager(window);
		treeView = new TreeView<>();
		treeView.setRoot(treeMgr.getRoot());
		treeView.setShowRoot(false);
		treeView.getSelectionModel().selectedItemProperty().addListener((v, old, newValue) -> handleTreeItem(newValue));
		layout2.setLeft(treeView);

		scene2 = new Scene(layout2, sceneWidth, sceneHeight);
		scene2.getStylesheets().add("DavesViper.css");

		window.setScene(scene1);
		window.show();
	}

	/**
	 * @param cBox
	 * @return
	 */
	private void choiceHandler(ChoiceBox<String> cBox) {
		System.out.println(cBox.getValue());
	}

	private void handleTreeItem(TreeItem<String> item) {
		System.out.println("Tree Item: " + item.getValue() + " from " + item.getParent().getValue());
	}

	private void myTextHandler(TextField field) {
		System.out.println(field.getText());
		try {
			int age = Integer.parseInt(field.getText());
			System.out.println("User is: " + age);
		} catch (NumberFormatException e) {
			System.out.println("Error: " + field.getText() + " is not a number");
		}
	}

	// Switch scenes
	public void sceneHandler(javafx.event.ActionEvent e, Stage stage) {
		System.out.println("scene handler");
		handleOptions(box1, box2);
		handleList(listView);
		if (e.getSource() == button1)
			stage.setScene(scene2);
		else {
			startNewWindow();
			// new DialogBox();
			// if (DialogBox.display("Ready?", "Close to go to stage 1"))
			// stage.setScene(scene1);
		}
	}

	private void startNewWindow() {
		try {
			// Does NOT work - new scene but in old window and errors on exit
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/A.fxml"));
			Parent root1 = (Parent) fxmlLoader.load();
			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initStyle(StageStyle.UNDECORATED);
			Scene sceneX = new Scene(root1);
			sceneX.getStylesheets().add("DavesViper.css");
			// stage.setScene(new Scene(root1));
			stage.setScene(sceneX);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Handle string view list
	private void handleList(ListView<String> list) {
		for (String item : list.getSelectionModel().getSelectedItems())
			System.out.println("Selected list view item: " + item);
	}

	// Handle check box options
	private void handleOptions(CheckBox box1, CheckBox box2) {
		String message = "User selected checkbox is: ";

		if (box1.isSelected())
			message += "Box1\n";

		if (box2.isSelected())
			message += "Box2\n";

		System.out.println(message);
	}

	public void closeProgram(WindowEvent e) {
		e.consume(); // take the event away from windows
		if (DialogBox.display("Close?", "Do you really want to close?"))
			window.close();
	}

	/**
	 * @param e
	 * @return
	 */
	public void closeProgram(ActionEvent e) {
		e.consume();
		window.close();
	}
}