/**
 * 
 */
package org.opentravel.dex.tasks;

import javafx.concurrent.WorkerStateEvent;

public interface TaskResultHandlerI {

	public void handleTaskComplete(WorkerStateEvent event);
}
