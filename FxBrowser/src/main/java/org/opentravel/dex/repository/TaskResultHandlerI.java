/**
 * 
 */
package org.opentravel.dex.repository;

import javafx.concurrent.WorkerStateEvent;

public interface TaskResultHandlerI {

	public void handleTaskComplete(WorkerStateEvent event);
}
