package googlesearch.ui;

import googlesearch.Activator;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.eclipse.ui.dialogs.SearchPattern;

public class BrowseFileLocationDialog extends PopupDialog {
	private Set<File> files = new HashSet<File>();
	private String initialText;
	private Text filterText;
	private TreeViewer treeViewer;
	private Text pathText;
	private AutoCompleteField pathAutoCompleteField;
	private File initFile;
	private final static String PREF_LOCATIONS = "googlesearch.ui.BrowseFileLocationDialo.locations";
    private final static String DELIMITER = ":-:";
	
	
	public BrowseFileLocationDialog(String initialText) {
		super(Display.getDefault().getActiveShell(), SWT.RESIZE, true, true, true, true, true, "Open Location", "Select a file path and press 'Enter' to open");
		String locationPref = Activator.getDefault().getDialogSettings().get(PREF_LOCATIONS);
		if (locationPref != null) {
			String[] locations = locationPref.split(DELIMITER);
			for (String location : locations) {
				File file = new File(location);
				if (file.exists()) {
					files.add(file);
				}
			}
		}
		
		this.initialText = initialText;
		initFile = new File(initialText);
		files.add(initFile);

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
		int styleBits = SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER;
		
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
		treeViewer.setSorter(new ViewerSorter());
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
				treeViewer.refresh();
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

	private class CustomStyleLabelProvider extends DelegatingStyledCellLabelProvider implements ILabelProvider {
		public CustomStyleLabelProvider(IStyledLabelProvider labelProvider) {
			super(labelProvider);
		}

		
		public String getText(Object element) {
			if (element instanceof File) {
				return ((File) element).getAbsolutePath();
			}
			return getStyledText(element).toString();
		}
		
		
	}
	
	private class LabelProvider implements IStyledLabelProvider{

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

	private void handleEnter() {
		String selectedPath = pathText.getText();
		File selectedFile = new File(selectedPath);
		if (selectedFile.exists()) {
			files.add(selectedFile);
		}
		String locationPref = "";
		for (File file : files) {
			String path = file.getAbsolutePath();
			if (!locationPref.isEmpty()) {
				locationPref = locationPref + DELIMITER + path ;
			} else {
				locationPref = path;
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
