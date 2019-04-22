/**
 * 
 */
package org.opentravel.common;

import java.io.File;
import java.io.InputStream;

import org.opentravel.application.common.AbstractMainWindowController;
import org.opentravel.application.common.StatusType;
import org.opentravel.schemacompiler.loader.LibraryInputSource;
import org.opentravel.schemacompiler.loader.LibraryLoaderException;
import org.opentravel.schemacompiler.loader.LibraryModelLoader;
import org.opentravel.schemacompiler.loader.impl.LibraryStreamInputSource;
import org.opentravel.schemacompiler.model.TLModel;
import org.opentravel.schemacompiler.repository.ProjectManager;
import org.opentravel.schemacompiler.repository.RepositoryException;
import org.opentravel.schemacompiler.validate.ValidationFindings;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * @author dmh
 *
 */
public class DexFileHandler extends AbstractMainWindowController {

	ValidationFindings findings;

	public ValidationFindings getFindings() {
		return findings;
	}

	TLModel newModel = null;

	public TLModel getNewModel() {
		return newModel;
	}

	public ProjectManager openProject(File selectedProjectFile, OpenProjectProgressMonitor monitor) {
		ProjectManager manager = new ProjectManager(false);
		findings = new ValidationFindings();
		try {
			manager.loadProject(selectedProjectFile, findings, monitor);
			// manager.loadProject(selectedProjectFile, findings);
		} catch (LibraryLoaderException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return manager;
	}

	// public class ProjectProgressMonitor implements LoaderProgressMonitor {
	//
	// @Override
	// public void beginLoad(int libraryCount) {
	// System.out.println("Progress: begin with " + libraryCount);
	// }
	//
	// @Override
	// public void loadingLibrary(String libraryFilename) {
	// System.out.println("Progress: loading " + libraryFilename);
	// }
	//
	// @Override
	// public void libraryLoaded() {
	// System.out.println("Progress: loaded done. ");
	// }
	//
	// @Override
	// public void done() {
	// System.out.println("Progress: done");
	// }
	//
	// }

	public void openFile(File selectedFile) {
		if (selectedFile == null)
			return;

		if (selectedFile.getName().endsWith(".otp")) {
			ProjectManager manager = openProject(selectedFile, null);
			newModel = manager.getModel();
		} else { // assume OTM library file
			LibraryInputSource<InputStream> libraryInput = new LibraryStreamInputSource(selectedFile);
			try {
				LibraryModelLoader<InputStream> modelLoader = new LibraryModelLoader<>();

				findings = modelLoader.loadLibraryModel(libraryInput);
				newModel = modelLoader.getLibraryModel();
			} catch (LibraryLoaderException e) {
				e.printStackTrace();
			}
		}
	}

	public File fileChooser(Stage stage) {
		// TEMP
		File initialDirectory = new File("C:\\Users\\dmh\\workspace\\OTM-DE_TestFiles");

		// UserSettings userSettings = UserSettings.load();
		// File initialDirectory = (modelFile != null) ?
		// modelFile.getParentFile() : userSettings.getLastModelFile().getParentFile();
		FileChooser chooser = newFileChooser("Import from OTP", initialDirectory,
				new String[] { "*.otp", "OTM Project Files (*.otp)" },
				new String[] { "*.otr", "OTM Release Files (*.otr)" },
				new String[] { "*.otm", "OTM Library Files (*.otm)" }, new String[] { "*.*", "All Files (*.*)" });
		File selectedFile = chooser.showOpenDialog(stage);

		return selectedFile;
	}

	/**
	 * @return a list of OTM Project files
	 */
	public File[] getProjectList(File directory) {
		// List<File> projectFiles = new ArrayList<>();
		if (directory == null) {
			directory = new File(System.getProperty("user.home"));
		}
		File[] projectFiles = {};
		if (directory.isDirectory()) {
			projectFiles = directory.listFiles(f -> f.getName().endsWith(".otp"));
		}
		return projectFiles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opentravel.application.common.AbstractMainWindowController#setStatusMessage(java.lang.String,
	 * org.opentravel.application.common.StatusType, boolean)
	 */
	@Override
	protected void setStatusMessage(String message, StatusType statusType, boolean disableControls) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opentravel.application.common.AbstractMainWindowController#updateControlStates()
	 */
	@Override
	protected void updateControlStates() {
		// TODO Auto-generated method stub

	}

}
