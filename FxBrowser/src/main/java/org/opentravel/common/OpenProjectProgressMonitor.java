/**
 * 
 */
package org.opentravel.common;

import org.opentravel.dex.controllers.DexStatusController;
import org.opentravel.schemacompiler.loader.LoaderProgressMonitor;

/**
 * @author dmh
 *
 */
public class OpenProjectProgressMonitor implements LoaderProgressMonitor {

	private DexStatusController controller;
	private double percentDone = 0;
	private double increment;

	/**
	 * @param objectEditorController
	 * 
	 */
	public OpenProjectProgressMonitor(DexStatusController objectEditorController) {
		controller = objectEditorController;
		increment = 0.90F;
	}

	@Override
	public void beginLoad(int libraryCount) {
		// Library count is not always accurate due to includes
		increment = increment / libraryCount * 0.7F;
		// System.out.println("Progress: begin with " + libraryCount + " increment = " + increment);
	}

	@Override
	public void loadingLibrary(String libraryFilename) {
		// System.out.println("Progress: loading " + libraryFilename);
		controller.postStatus("Loading " + libraryFilename);
	}

	@Override
	public void libraryLoaded() {
		// System.out.println("Progress: loaded done. ");
		percentDone += increment;
		controller.postProgress(percentDone);
	}

	@Override
	public void done() {
		// System.out.println("Progress: done");
		controller.postStatus("Done");
	}

}
