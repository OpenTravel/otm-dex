/**
 * 
 */
package org.opentravel.objecteditor;

/**
 * Abstract interface for all included OTM-DE FX view controllers. These controllers must be able to "Post" a view of
 * the object type declared as the generic variable.
 * <p>
 * <ul>
 * <li>The FXML file for this controller must be included into another FXML file.
 * <li>The controller must have the same name as the FXML file with "Controller" as a suffix.
 * <li>The controller must be declared with an @FXML in the containing controller.
 * </ul>
 * 
 * @author dmh
 *
 */
public interface DexIncludedController<T> extends DexController {

	/**
	 * Initialize is called by the FXML loader when the FXML file is loaded. These methods must make the controller
	 * ready to "Post" to their view components. Trees must be initialized, table columns set, etc.
	 * <p>
	 * This method should verify that all views and fields have been injected correctly and throw
	 * illegalArgumentException if not.
	 * <p>
	 * Note: parent controller is not known and business data will not be available when this is called.
	 */
	public void initialize();

	/**
	 * Set the parent controller. Included controllers will not have access to the parent controller until this method
	 * is called. An illegalState exception should be thrown if the parent controller is needed for posting data into
	 * the view before the parent is set.
	 * <p>
	 * This method should retrieve all of the resources it needs from the parent such as image or model managers.
	 * 
	 * @param parent
	 */
	public void setParent(DexController parent);

	/**
	 * Post the business data into this controller's view(s). This method is expected to be extended to handle
	 * forward/back navigation.
	 * <p>
	 * This method is expected to be overridden. Implementations <b>must</b> call super.post(businessData) first.
	 * 
	 * @param businessData
	 * @throws Exception
	 *             if business logic throws exceptions or parent controller is needed and not set.
	 */
	public void post(T businessData) throws Exception;

}
