/**
 * 
 */
package org.opentravel.dex.repository;

import javafx.concurrent.WorkerStateEvent;

public interface ResultHandlerI {

	public void handle(WorkerStateEvent event);
}
