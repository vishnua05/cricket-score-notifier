package eclipse.utility.actions;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import eclipse.utility.Activator;

public class OpenResourceLocationHandler extends AbstractHandler {
	
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Object selectedObject = getSelectedObject();
		IResource resource = null;
		if (selectedObject instanceof IResource) {
			resource = (IResource) selectedObject;
		}  else if (selectedObject instanceof IAdaptable) {
			resource = (IResource) ((IAdaptable) selectedObject).getAdapter(IResource.class);
		}
		URI locationURI = null;
		if (resource != null) {
			if (resource.getType() == IResource.FILE) {
				resource = resource.getParent();
			}
			locationURI = resource.getLocationURI();
		} else {
			File file = null;
			if (selectedObject instanceof File) {
				file = (File) selectedObject;
			} else if (selectedObject instanceof String && selectedObject.toString().trim().length() > 0) {
				file = new File(selectedObject.toString());
			} else {
				Clipboard clipboard = null;
				try {
					clipboard = new Clipboard(Display.getDefault());
					Object contents = clipboard.getContents(TextTransfer.getInstance());
					if (contents == null || contents.toString().trim().length() == 0) {
						contents = clipboard.getContents(FileTransfer.getInstance());
						if (contents instanceof String[]) {
							String[] fileLocations  = (String[]) contents;
							if (fileLocations.length > 0) {
								file = new File(fileLocations[0]);
							}
						}
					} else {
						file = new File(contents.toString());
					}
				} finally {
					if (clipboard != null) {
						clipboard.dispose();
					}
				}
			}
			if (file != null && file.exists()) {
				if (file.isFile()) {
					file = file.getParentFile();
				}
				locationURI = file.toURI();
			} 
		} 

		if (locationURI != null) {
			try {
				Desktop.getDesktop().browse(locationURI);
			} catch (IOException e) {
				Activator.getDefault().logAndShowException(e);
			}
		}
		return null;
	}
	
	private Object getSelectedObject() {
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ISelection selection = activePage.getSelection();
		if (selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();
			return element;
		} else if (selection instanceof ITextSelection) {
			return ((ITextSelection) selection).getText();
		}
		return null;
	}

}
