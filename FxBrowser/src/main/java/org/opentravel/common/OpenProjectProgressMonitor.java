/**
 * 
 */
package org.opentravel.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.controllers.DexStatusController;
import org.opentravel.schemacompiler.loader.LoaderProgressMonitor;

/**
 * @author dmh
 *
 */
public class OpenProjectProgressMonitor implements LoaderProgressMonitor {
	private static Log log = LogFactory.getLog(OpenProjectProgressMonitor.class);

	private DexStatusController controller;
	private double percentDone = 0;
	private double increment;

	/**
	 * @param objectEditorController
	 * 
	 */
	public OpenProjectProgressMonitor(DexStatusController statusController) {
		controller = statusController;
		increment = 0.90F;
	}

	@Override
	public void beginLoad(int libraryCount) {
		// Library count is not always accurate due to includes
		increment = increment / libraryCount * 0.7F;
		log.debug("Progress: begin with " + libraryCount + " increment = " + increment);
	}

	@Override
	public void loadingLibrary(String libraryFilename) {
		// log.debug("Progress: loading " + libraryFilename);
		controller.postStatus("Loading " + libraryFilename);
	}

	@Override
	public void libraryLoaded() {
		// log.debug("Progress: library loaded. Percent done = " + percentDone);
		percentDone += increment;
		controller.postProgress(percentDone);
	}

	@Override
	public void done() {
		log.debug("Progress: done");
		controller.postStatus("Done");
	}

}
