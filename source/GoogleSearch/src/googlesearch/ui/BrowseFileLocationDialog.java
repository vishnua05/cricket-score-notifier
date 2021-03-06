package googlesearch.ui;

import googlesearch.Activator;
import googlesearch.model.BookMarksProviderFactory;
import googlesearch.model.IBookMarksProvider;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.SearchPattern;

public class BrowseFileLocationDialog extends PopupDialog {
	private Set<File> files = new HashSet<File>();
	private String initialText;
	private Text filterText;
	private TreeViewer treeViewer;
	private Text pathText;
	private AutoCompleteField pathAutoCompleteField;
	private File initFile;
	private Thread thread;
	private final static String PREF_LOCATIONS = "googlesearch.ui.BrowseFileLocationDialo.locations";
    private final static String DELIMITER = ":-:";
	
	
	public BrowseFileLocationDialog(String initialText) {
		super(Display.getDefault().getActiveShell(), SWT.RESIZE, true, true, true, true, true, "Open Location", "Select a file path and press 'Enter' to open");
		
		this.initialText = initialText;
		initFile = new File(initialText);
		files.add(initFile);
			
		Runnable bookMarksRunnable = new Runnable() {
			
			public void run() {
				String locationPref = Activator.getDefault().getDialogSettings().get(PREF_LOCATIONS);
				if (locationPref != null) {
					String[] locations = locationPref.split(DELIMITER);
					updateFiles(Arrays.asList(locations));
				}
				
				// update local pref values
				refreshViewer();
				//update from recent docs
				IBookMarksProvider bookMarksProvider = BookMarksProviderFactory.getInstance().getBookMarksProvider();
				Set<String> locations = new HashSet<String>();
				locations.addAll(bookMarksProvider.getFavorites());
				locations.addAll(bookMarksProvider.getRecentDocuments());
				updateFiles(locations);
				refreshViewer();
			}

		};
		thread = new Thread(bookMarksRunnable);
		thread.start();
	}

	public void updateFiles(Collection<String> locations) {
		for (String location : locations) {
			File file = new File(location);
			if (file.exists()) {
				files.add(file);
				if (enterPressed) {
					break;
				}
			}
		}
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setFont(parent.getFont());
		createFilteredTreeViewer(composite);
		return composite;
	}

	protected void createFilteredTreeViewer(Composite parent) {
		GridDataFactory.fillDefaults().grab(true, true).hint(400, 200).applyTo(parent);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(parent);
		int styleBits = SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.MULTI;
		
		Label filterLabel = new Label(parent, SWT.NONE);
		filterLabel.setText("Filter: ");
		GridDataFactory.swtDefaults().applyTo(filterLabel);
		
		filterText = new Text(parent, SWT.BORDER);
		filterText.setText(initFile.getName());
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(filterText);
		
		treeViewer = new TreeViewer(parent, styleBits);
		GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(treeViewer.getTree());
		treeViewer.setContentProvider(iTreeContentProvider);
		treeViewer.setLabelProvider(new CustomStyleLabelProvider(new LabelProvider()));
		treeViewer.setSorter(new CustomSorter());
		treeViewer.setInput(files);
		treeViewer.setFilters(new ViewerFilter[] {viewerFilter});
		
		
		Label label = new Label(parent, SWT.NONE);
		label.setText("Path: ");
		GridDataFactory.swtDefaults().applyTo(label);
		
		pathText = new Text(parent, SWT.BORDER);
		String[] initialProposals = initFile.isDirectory() ? initFile.list(): new String[0];
		pathAutoCompleteField = new AutoCompleteField(pathText, new TextContentAdapter(), initialProposals);
		pathText.setText(initialText);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(pathText);
		
		addListeners();

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				filterText.selectAll();
				filterText.setFocus();
			}
		});
		
		
	}

	public void addListeners() {
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				handleEnter();
			}
		});
		
		filterText.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				refreshViewer();
			}
			
		});
		
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection(); {
					if (selection instanceof IStructuredSelection) {
						Object firstElement = ((IStructuredSelection) selection).getFirstElement();
						if (firstElement instanceof File) {
							pathText.setText(((File) firstElement).getAbsolutePath());
						}
					}
				}
			}
		});
		
		
		filterText.addKeyListener(enterListener);
		pathText.addKeyListener(enterListener);
		filterText.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				filterText.selectAll();
			}
		});
		
		pathText.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				pathText.setSelection(pathText.getText().length());
			}
		});
		
		pathText.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				String text = pathText.getText();
				File file = new File(text);
				File[] children = null;
				if (file.isDirectory()) {
					children = file.listFiles();
				} else if (file.getParentFile() != null) {
					children = file.getParentFile().listFiles();
				}
				if (children != null) {
					List<String> proposals = new ArrayList<String>();
					for (File child : children) {
						proposals.add(child.getAbsolutePath());
					}
					pathAutoCompleteField.setProposals(proposals.toArray(new String[0]));
				}
			}
		});
		
		// key listeners
		
		filterText.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.ARROW_DOWN) {
					selectTree();
				} else if (e.keyCode == SWT.ARROW_UP) {
					pathText.setFocus();
					pathText.setSelection(pathText.getText().length());
				}
			}
			
		});
		
		pathText.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.ARROW_UP) {
					selectTree();
				} else if (e.keyCode == SWT.ARROW_DOWN) {
					filterText.setFocus();
					filterText.setSelection(filterText.getText().length());
				}
			}
		});
	}
	
	public void selectTree() {
		Tree tree = treeViewer.getTree();
		if (tree.getItemCount() > 0) {
			treeViewer.setSelection(new StructuredSelection(tree.getItem(0).getData()));
			tree.setFocus();
		}
	}
	
	private  ViewerFilter viewerFilter = new ViewerFilter() {
		
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			SearchPattern searchPattern = new SearchPattern();
			String patternString = filterText.getText();
			if (!patternString.startsWith("*")) {
				patternString = "*" + patternString;
			}
            searchPattern.setPattern(patternString);
			if (element instanceof File) {
				return searchPattern.matches(((File) element).getName());
			}
			return false;
		}
	};

	KeyAdapter enterListener = new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
			if (e.character == '\r') {
				handleEnter();
			}
		}

	};

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


	private class CustomSorter extends ViewerSorter {
		
		public int category(Object element) {
			if (element instanceof File) {
				if (((File) element).isFile()) {
					return 1;
				}
			}
			return super.category(element);
		}
		
	}
	
	private class CustomStyleLabelProvider extends DelegatingStyledCellLabelProvider implements ILabelProvider {
		public CustomStyleLabelProvider(IStyledLabelProvider labelProvider) {
			super(labelProvider);
		}

		
		public String getText(Object element) {
			return getStyledText(element).toString();
		}
		
	}
	
	private Job refreshJob = new Job("Refresh") {
		
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			Display.getDefault().asyncExec(new Runnable() {
				
				public void run() {
					if (!treeViewer.getTree().isDisposed()) {
						treeViewer.refresh();
					}
				}
			});
			return Status.OK_STATUS;
		}
	};
	
	public void refreshViewer() {
		refreshJob.schedule(200);
	}
	
	private class LabelProvider implements IStyledLabelProvider, ILabelProvider{

		public void addListener(ILabelProviderListener listener) {
			
		}

		public void dispose() {
			
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
			
		}

		
		public StyledString getStyledText(Object element) {
			if (element instanceof File) {
				File file = (File) element;
				String start = file.getName();
				String end = file.getAbsolutePath();
				StyledString styledString = new StyledString();
				styledString.append(start + "-");
				styledString.append(end,  StyledString.QUALIFIER_STYLER);
				return styledString;
			}
			return null;
		}
		
		public String getText(Object element) {
			if (element instanceof File) {
				File file = (File) element;
				String start = file.getName();
				String end = file.getAbsolutePath();
				return start + "-" + end;
			}
			return null;
		}

		
		public Image getImage(Object element) {
			return null;
		}
		
	}
	

	private ITreeContentProvider iTreeContentProvider = new ITreeContentProvider() {

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
			return files.toArray();
		}

		public Object[] getChildren(Object parentElement) {
			return null;
		}
	};


	private boolean enterPressed;
	private void handleEnter() {
		enterPressed = true;
		String selectedPath = pathText.getText();
		File selectedFile = new File(selectedPath);
		if (selectedFile.exists()) {
			files.add(selectedFile);
		}
		String locationPref = "";
		for (File file : files) {
			if (file.isDirectory()) {
				String path = file.getAbsolutePath();
				if (!locationPref.isEmpty()) {
					locationPref = locationPref + DELIMITER + path ;
				} else {
					locationPref = path;
				}
			}
		}
		Activator.getDefault().getDialogSettings().put(PREF_LOCATIONS, locationPref);
		close();
		
		try {
			Desktop.getDesktop().browse(selectedFile.toURI());
		} catch (IOException e) {
			Activator.getDefault().logAndShowException(e);
		}
	}
	
	
}
