/**
 * 
 */
package org.opentravel.objecteditor;

import java.util.function.IntFunction;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * @author dmh
 *
 */
/**
 * TreeTableRow is an IndexedCell, but rarely needs to be used by developers creating TreeTableView instances. The only
 * time TreeTableRow is likely to be encountered at all by a developer is if they wish to create a custom rowFactory
 * that replaces an entire row of a TreeTableView.
 * 
 * https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/TreeTableRow.html
 */
@SuppressWarnings("restriction")
public final class PropertyCellFactory_DoesNotWork<S, T> extends TextFieldTableCell<S, T> {

	private final IntFunction<ObservableValue<Boolean>> editableExtractor;
	private final Callback<?, ?> c = null;

	public PropertyCellFactory_DoesNotWork(IntFunction<ObservableValue<Boolean>> editableExtractor,
			StringConverter<T> converter) {
		super(converter);
		this.editableExtractor = editableExtractor;

		T item = getItem();
	}

	@Override
	public void updateIndex(int i) {
		super.updateIndex(i);
		if (i == -1) {
			editableProperty().unbind();
		} else {
			editableProperty().bind(editableExtractor.apply(i));
		}
	}

	// Based on URL but will not compile
	// https://stackoverflow.com/questions/39566975/tableview-make-specific-cell-or-row-editable
	//
	// public static <U, V> Callback<TreeTableColumn<U, V>, TreeTableCell<U, V>> forTableColumn(
	// IntFunction<ObservableValue<Boolean>> editableExtractor, StringConverter<V> converter) {
	// return column -> new PropertyCellFactory_DoesNotWork<>(editableExtractor, converter);
	// }
	//
	// public static <U> Callback<TreeTableColumn<U, String>, TreeTableCell<U, String>> forTableColumn(
	// IntFunction<ObservableValue<Boolean>> editableExtractor) {
	// return forTableColumn(editableExtractor, new DefaultStringConverter());
	// }

}
