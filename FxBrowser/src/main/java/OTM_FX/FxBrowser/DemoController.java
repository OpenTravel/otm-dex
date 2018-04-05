/**
 * 
 */
package OTM_FX.FxBrowser;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import org.opentravel.schemacompiler.repository.RepositoryAvailabilityChecker;
import org.opentravel.schemacompiler.repository.RepositoryException;
import org.opentravel.schemacompiler.repository.RepositoryManager;

/**
 * @author dmh
 *
 */
@SuppressWarnings("restriction")
public class DemoController implements Initializable {

	public class repoTree extends TreeView {

	}

	@FXML
	public TreeView<String> treeView;
	TreeManager treeMgr;

	@FXML
	public TreeTableView<DemoNode> treeTableView;
	TreeTableManager ttMgr;

	@FXML
	public TableView<DemoNode> memberTable;
	TableManager tableMgr;

	@FXML
	public HBox memberEditHbox;
	@FXML
	public Tab memberTab;

	Stage primaryStage = null;

	public void setStage(Stage stage) {
		primaryStage = stage;

		// Load and display tree view in left pane
		treeMgr = new TreeManager(stage);
		treeView.setRoot(treeMgr.getRoot());
		treeView.setShowRoot(false);
		treeView.getSelectionModel().selectedItemProperty().addListener((v, old, newValue) -> handleTreeItem(newValue));

		// Load and add listener for table
		tableMgr = new TableManager();
		// same thing as next line
		// memberTab.setOnSelectionChanged(e -> memberTabSelection(e));
		memberTab.setOnSelectionChanged(this::memberTabSelection);
		tableMgr.build(memberTable);

	}

	private RepositoryManager repositoryManager;
	private RepositoryAvailabilityChecker availabilityChecker;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("Controller - View is now loading!");
		// // Set up repository access
		try {
			repositoryManager = RepositoryManager.getDefault();
			availabilityChecker = RepositoryAvailabilityChecker.getInstance(repositoryManager);
			availabilityChecker.pingAllRepositories(true);

		} catch (RepositoryException e) {
			e.printStackTrace(System.out);
		}

	}

	private void handleTreeItem(TreeItem<String> item) {
		System.out.println("Tree Item: " + item.getValue() + " from " + item.getParent().getValue());
	}

	@FXML
	public void memberTabSelection(Event e) {
		System.out.println("memberTab");
		// boolean enabled = memberTab.isDisabled();
		if (memberTable != null)
			memberTable.setItems(tableMgr.getNodes());
		if (memberEditHbox != null && memberEditHbox.getChildren().isEmpty())
			tableMgr.getEditPane(memberEditHbox);
		// memberEditHbox.getChildren().add(tableMgr.getEditPane());

		// if (cPane != null) {
		// ObservableList<Node> list = FXCollections.observableArrayList();
		// cPane.getChildren().addAll(list);
		// }

		DemoNode nodes = new DemoNode();
		if (treeTableView != null) {
			ttMgr = new TreeTableManager(treeTableView);
			System.out.println("Populate Tree Table View.");
			ttMgr.build(nodes.getNodes());
		} else
			System.out.println("Can't populate Tree Table View.");
	}

	@FXML
	public void deleteProperty(ActionEvent e) {
		System.out.println("Delete Button");
	}

	@FXML
	public void setName(ActionEvent e) {
		System.out.println("set Name");
	}

	@FXML
	public void radioButton1(ActionEvent e) {
		System.out.println("Button1");
	}

	@FXML
	public void radioButton2(ActionEvent e) {
		System.out.println("Button2");
	}

	@FXML
	public void simpleButton(ActionEvent e) {
		System.out.println("simpleButton");
	}

	@FXML
	public void open(ActionEvent e) {
		System.out.println("open");
	}

	@FXML
	public void appExit(ActionEvent e) {
		System.out.println("exit");
		primaryStage.close();

	}

}
