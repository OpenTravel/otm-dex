/**
 * 
 */
package org.opentravel.dex.repository;

import java.util.function.Function;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

/**
 * https://stackoverflow.com/questions/29489366/how-to-add-button-in-javafx-table-view
 *
 * @author dmh
 **/
public class ActionButtonTreeTableCell<S> extends TreeTableCell<S, Button> {

	private final Button actionButton;

	public ActionButtonTreeTableCell(String label, Function<S, S> function) {
		this.getStyleClass().add("action-button-table-cell");

		this.actionButton = new Button(label);
		this.actionButton.setOnAction((ActionEvent e) -> {
			function.apply(getCurrentItem());
		});
		this.actionButton.setMaxWidth(Double.MAX_VALUE);
	}

	public S getCurrentItem() {
		getTreeTableView().getSelectionModel().getSelectedCells();
		return null;
		// FIXME
		// return (S) getTreeTableView().getItems().get(getIndex());
	}

	public static <S> Callback<TreeTableColumn<S, Button>, TreeTableCell<S, Button>> forTableColumn(String label,
			Function<S, S> function) {
		return param -> new ActionButtonTreeTableCell<>(label, function);
	}

	@Override
	public void updateItem(Button item, boolean empty) {
		super.updateItem(item, empty);

		if (empty) {
			setGraphic(null);
		} else {
			setGraphic(actionButton);
		}
	}
}
