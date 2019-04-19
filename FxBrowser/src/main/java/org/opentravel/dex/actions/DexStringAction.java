/**
 * 
 */
package org.opentravel.dex.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
public abstract class DexStringAction implements DexAction<String> {
	private static Log log = LogFactory.getLog(DexStringAction.class);

	public abstract String doIt(ObservableValue<? extends String> observable, String oldValue, String newValue);

	//
	// public T undo();
	//
	// // VETOable event??
	// /**
	// * @return true if change is enabled for this application and user.
	// */
	// public boolean isEnabled();
	//
	// /**
	// * @return true if the requested change is allowed for object in this application and user.
	// */
	// public boolean isAllowed(T value);
	//
	// /**
	// * @return true if change already made is valid for this object for this application and user.
	// */
	// public boolean isValid();
	//
	// // /**
	// // * @return true if change is valid for this object for this application and user.
	// // */
	// // public boolean wouldBeValid(T value);
}
