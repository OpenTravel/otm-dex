/**
 * 
 */
package org.opentravel.dex.actions;

import org.opentravel.schemacompiler.validate.ValidationFindings;

import javafx.beans.value.ObservableValue;

/**
 * Actions are invoked by the view controllers to perform <i>actions</i> on the model.
 * <p>
 * They are designed to be set as listeners to FX Observable objects. When the observable value changes, the associated
 * action handler is fired.
 * 
 * 
 * @author dmh
 *
 */
public interface DexAction<T> {
	public T doIt(ObservableValue<? extends T> observable, T oldValue, T newValue);

	public T undo();

	// VETOable event??
	/**
	 * @return true if change is enabled for this application and user.
	 */
	public boolean isEnabled();

	/**
	 * @return true if the requested change is allowed for object in this application and user.
	 */
	public boolean isAllowed(T value);

	/**
	 * @return true if change already made is valid for this object for this application and user.
	 */
	public boolean isValid();

	/**
	 * @return
	 */
	ValidationFindings getVetoFindings();

	// /**
	// * @return true if change is valid for this object for this application and user.
	// */
	// public boolean wouldBeValid(T value);
}
