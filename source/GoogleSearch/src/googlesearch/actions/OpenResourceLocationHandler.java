package googlesearch.actions;

import googlesearch.Activator;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

public class OpenResourceLocationHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		boolean prompt = false;
		Object selectedObject = getSelectedObject();
		URI locationURI = null;
		IResource resource = null;
		Shell activeShell = Display.getDefault().getActiveShell();
		Shell workbenchShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		if (activeShell == workbenchShell) {
			if (selectedObject instanceof IResource) {
				resource = (IResource) selectedObject;
			} else if (selectedObject instanceof IAdaptable) {
				resource = (IResource) ((IAdaptable) selectedObject).getAdapter(IResource.class);
			}
		}
		if (resource != null) {
			if (resource.getType() == IResource.FILE) {
				resource = resource.getParent();
			}
			locationURI = resource.getLocationURI();
		} else if (locationURI == null) {
			File file = null;
			if (selectedObject instanceof File) {
				file = (File) selectedObject;
			} else if (selectedObject instanceof String && selectedObject.toString().trim().length() > 0) {
				file = new File(selectedObject.toString());
			} else {
				String fileLocation = null;
				Control control = Display.getDefault().getFocusControl();
				if (control instanceof Text) {
					fileLocation = ((Text) control).getSelectionText().trim();
					if (fileLocation.isEmpty()) {
						fileLocation = ((Text) control).getText().trim();
					}
				} else if (control instanceof Label) {
					fileLocation = ((Label) control).getText();
				} else if (control instanceof Tree) {
					TreeItem[] selection = ((Tree) control).getSelection();
					if (selection.length > 0) {
						TreeItem treeItem = selection[0];
						fileLocation = treeItem.getText();
					}
				} else if (control instanceof Table) {
					TableItem[] selection = ((Table) control).getSelection();
					if (selection.length > 0) {
						TableItem tableItem = selection[0];
						fileLocation = tableItem.getText();
					}
				}

				
				if (fileLocation == null) {
					Clipboard clipboard = null;
					// get from clipboard

					try {
						clipboard = new Clipboard(Display.getDefault());
						Object contents = clipboard.getContents(TextTransfer.getInstance());
						if (contents != null && contents.toString().trim().length() == 0) {
							fileLocation = contents.toString().trim();
						}
					} finally {
						if (clipboard != null) {
							clipboard.dispose();
						}
					}
				}
				prompt = activeShell != workbenchShell;
				if (fileLocation == null) {
					if (fileLocation == null) {
						IWorkbenchPart workbenchPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
						if (workbenchPart instanceof IEditorPart) {
							IEditorInput editorInput = ((IEditorPart) workbenchPart).getEditorInput();
							if (editorInput instanceof IURIEditorInput) {
								URI uri = ((IURIEditorInput) editorInput).getURI();
								fileLocation = new File(uri).getParentFile().getPath();
							}
						}
					}
					if (fileLocation == null) {
						fileLocation = ResourcesPlugin.getWorkspace().getRoot().getLocationURI().getPath();
					}
				}
				
				file = new File(fileLocation);
			}
			if (file != null) {
				if (file.exists()) {
					if (file.isFile()) {
						file = file.getParentFile();
					}
				} else {
					prompt = true;
				}
				locationURI = file.toURI();
			}
		}

		if (prompt) {
			String message = "Enter the location you wish to open";
			InputDialog inputDialog = new InputDialog(Display.getDefault().getActiveShell(), "Open Location", message,
					locationURI != null ? locationURI.getPath() : null, null);
			if (inputDialog.open() == IDialogConstants.OK_ID) {
				locationURI = new File(inputDialog.getValue()).toURI();
			} else {
				locationURI = null;
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
