package eclipse.utility.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import eclipse.utility.Activator;
import eclipse.utility.actions.GoogleContentHelper;
import eclipse.utility.actions.GoogleSearchCommandHandler;

public class GoogleSearchDialog extends PopupDialog {
	private HashMap<String, String> input = new HashMap<String, String>();
	private String initialText;
	
	public GoogleSearchDialog(String initialText) {
		super(null, SWT.RESIZE, true, true, true, true, true, "Google Search", "Search in google");
		input.putAll(GoogleSearchCommandHandler.directLinks);
		this.initialText = initialText;
	}
	
	
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setFont(parent.getFont());
		createFilteredTreeViewer(composite);
		return composite;
	}


	protected void createFilteredTreeViewer(Composite parent) {
		GridDataFactory.fillDefaults().applyTo(parent);
		GridDataFactory.fillDefaults().grab(true, true).hint(400, 200).applyTo(parent);
		int styleBits = SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER;
		FilteredTree filteredTree = new FilteredTree(parent, styleBits, getPatternFilter(), true);
		filteredTree.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(filteredTree);
		final TreeViewer treeViewer = filteredTree.getViewer();
		treeViewer.setContentProvider(getTreeContentProvider());
		treeViewer.setLabelProvider(getTreeLabelProvider());
		treeViewer.setSorter(new ViewerSorter());
		treeViewer.setInput(input);
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			@SuppressWarnings("rawtypes")
			
			public void doubleClick(DoubleClickEvent event) {
				ISelection selection = event.getSelection();
				if (selection instanceof IStructuredSelection) {
					Object firstElement = ((IStructuredSelection) selection).getFirstElement();
					if (firstElement instanceof Entry) {
                       GoogleSearchCommandHandler.openBrowser(PlatformUI.getWorkbench(), ((Entry) firstElement).getKey().toString());
					}
				}
			}
		});
		
		final Text text = filteredTree.getFilterControl();
		text.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.character == '\r') {
					if ((e.stateMask & SWT.CTRL) != 0) {
						GoogleSearchCommandHandler.openBrowser(PlatformUI.getWorkbench(), text.getText(), true);
					} else  {
						GoogleSearchCommandHandler.openBrowser(PlatformUI.getWorkbench(), text.getText());
					}
				}
			}

		});
		
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				text.addModifyListener(new ModifyListener() {
					
					public void modifyText(ModifyEvent e) {
						List<String> contentProposals = GoogleContentHelper.getContentProposals(text.getText());
						input.clear();
						input.putAll(GoogleSearchCommandHandler.directLinks);
						for (String string : contentProposals) {
							input.put(string, null);
						}
						treeViewer.refresh();
					}
					
				});
				text.setText(initialText);
			}
		});

	}
	
	
	private ILabelProvider getTreeLabelProvider() {
		return new ILabelProvider() {
			
			public void removeListener(ILabelProviderListener listener) {
				
			}
			
			
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}
			
			
			public void dispose() {
				
			}
			
			
			public void addListener(ILabelProviderListener listener) {
				
			}
			
			
			@SuppressWarnings("rawtypes")
			public String getText(Object element) {
				if (element instanceof Entry) {
					Entry entry = (Entry) element;
					String tail = entry.getValue() != null ? "[" + entry.getValue() + "]" : "";
					return entry.getKey() + tail;
				}
				return null;
			}
			
			
			public Image getImage(Object element) {
				return null;
			}
		};
	}

	private ITreeContentProvider getTreeContentProvider() {
		return new ITreeContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}
			
			
			public void dispose() {
				
			}
			
			
			public boolean hasChildren(Object element) {
				return false;
			}
			
			
			public Object getParent(Object element) {
				return null;
			}
			
			
			public Object[] getElements(Object inputElement) {
				return input.entrySet().toArray();
			}
			
			
			public Object[] getChildren(Object parentElement) {
				return null;
			}

		
		};
	}

	protected IDialogSettings getDialogSettings() {
		final IDialogSettings workbenchDialogSettings = Activator.getDefault().getDialogSettings();
		IDialogSettings result = workbenchDialogSettings.getSection(getId());
		if (result == null) {
			result = workbenchDialogSettings.addNewSection(getId());
		}
		return null;
	}

	protected String getId() {
		return "eclipse.utility.ui.GoogleSearchDialog"; //$NON-NLS-1$
	}
	
	private PatternFilter getPatternFilter() {
		return new PatternFilter();
	}

}
