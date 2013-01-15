package googlesearch.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.SearchPattern;
import org.eclipse.ui.part.ViewPart;

/**
 * 
 * Source reference from http://amateras.sourceforge.jp/cgi-bin/fswiki_en/wiki.cgi?page=EclipseHTMLEditor
 * 
 * 
 */
public class IconViewer extends ViewPart implements ISelectionListener {

	public static final String ID = "googlesearch.ui.IconViewer";
	private SashForm sash;
	//	private Table table;
	private TableViewer tableViewer;
	private ScaleableImageCanvas canvas;
	public static final String[] SUPPORTED_IMAGE_TYPES = { "gif", "png", "jpg", "jpeg", "bmp" };
	private IContainer lastSelection;
	private int lastSelectionCount;
	private Map<IFile, IconObject> tableObjectCache = Collections.synchronizedMap(new LinkedHashMap<IFile, IconObject>());
	private Text filterText;

	public IconViewer() {
		super();
	}

	
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		site.getPage().addSelectionListener(this);
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		updateView(selection);
	}

	
	public void dispose() {
		getSite().getPage().removeSelectionListener(this);
		sash.dispose();
		cleanupIcons();
		super.dispose();
	}

	private void updateView(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			if (obj instanceof IContainer) {
				showContainer((IContainer) obj, null);
			} else if (obj instanceof IFile) {
				showContainer(((IFile) obj).getParent(), (IFile) obj);
			} else {
				IResource resource = null;
				if (obj instanceof IAdaptable) {
					resource = (IResource) ((IAdaptable) obj).getAdapter(IResource.class);
				}

				if (resource instanceof IContainer) {
					showContainer((IContainer) resource, null);
				} else if (resource instanceof IFile) {
					IFile file = (IFile) resource;
					showContainer(file.getParent(), file);
				}
			}
		}
	}

	private void showContainer(IContainer folder, IFile selectedFile) {
		try {
			int currentSelectionCount = folder.members().length;
			boolean selectionChanged = !folder.equals(lastSelection) || currentSelectionCount != lastSelectionCount;
			if (selectionChanged) {
				// table.removeAll();
				canvas.setImage(null);
				cleanupIcons();
				tableObjectCache.clear();
				lastSelection = folder;
				lastSelectionCount = currentSelectionCount;
			}

			if (selectionChanged) {
				IResource[] resources = folder.members();
				for (int i = 0; i < resources.length; i++) {
					if (resources[i] instanceof IFile) {
						IFile file = (IFile) resources[i];
						for (int j = 0; j < SUPPORTED_IMAGE_TYPES.length; j++) {
							if (file.getFileExtension().equalsIgnoreCase(SUPPORTED_IMAGE_TYPES[j])) {
								addImage(file);
								break;
							}
						}
					}
				}
				// cancel old job
				loadIconsJob.cancel();
				loadIconsJob.schedule();
				tableViewer.refresh();
			}

			IconObject iconObject = tableObjectCache.get(selectedFile);
			if (iconObject == null && !tableObjectCache.isEmpty()) {
				iconObject = tableObjectCache.values().iterator().next();
			}
			if (iconObject != null) {
				tableViewer.setSelection(new StructuredSelection(iconObject));
				tableViewer.reveal(iconObject);
				iconObject.ensureLoaded();
				canvas.setImage(iconObject.getImage());
			} 

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	public void cleanupIcons() {
		List<IconObject> icons = new ArrayList<IconViewer.IconObject>();
		synchronized (tableObjectCache) {
			icons.addAll(tableObjectCache.values());
		}
		for (IconObject icon : icons) {
			icon.dispose();
		}
	}
	
	private Job loadIconsJob = new Job("Loading icons") {
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			List<IconObject> icons2Load = new ArrayList<IconViewer.IconObject>();
			synchronized (tableObjectCache) {
				icons2Load.addAll(tableObjectCache.values());
			}
			monitor.beginTask("Loading Icons", icons2Load.size());
			
			boolean isCancelled = false;
			for (IconObject iconObject : icons2Load) {
				if (monitor.isCanceled()) {
					isCancelled = true;
					break;
				}
				monitor.subTask(iconObject.getText());
				iconObject.ensureLoaded();
				monitor.worked(1);
			}
			
			if (isCancelled) {
				for (IconObject iconObject : icons2Load) {
					iconObject.dispose();
				}
			}
			
			refreshJob.schedule();
			return Status.OK_STATUS;
		}
	};

	private void addImage(IFile file) {
		try {
			IconObject iconObject = new IconObject(file);
			tableObjectCache.put(file, iconObject);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void createPartControl(Composite parent) {
		sash = new SashForm(parent, SWT.VERTICAL);
		tableViewer = new TableViewer(sash, SWT.BORDER);
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new LabelProvider() {
			
			public Image getImage(Object element) {
				if (element instanceof IconObject) {
					return ((IconObject) element).getIcon();
				}
				return super.getImage(element);
			}
		});
		tableViewer.setInput(tableObjectCache.values());
		tableViewer.setSorter(new ViewerSorter());

		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if (selection instanceof IStructuredSelection) {
					Object firstElement = ((IStructuredSelection) selection).getFirstElement();
					if (firstElement instanceof IconObject) {
						canvas.setImage(((IconObject) firstElement).getImage());
					}
				}
			}
		});
		tableViewer.setFilters(new ViewerFilter[] { tableFilter });
		Composite composite = new Composite(sash, SWT.BORDER);
		GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).spacing(0, 5).applyTo(composite);
		Label label = new Label(composite, SWT.NONE);
		label.setText("Filter: ");
		GridDataFactory.swtDefaults().applyTo(label);

		filterText = new Text(composite, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(filterText);
		filterText.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				refreshJob.schedule();
			}
		});

		canvas = new ScaleableImageCanvas(composite, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(canvas);
	}

	private Job refreshJob = new Job("") {
		{
			setSystem(true);
		}

		
		protected IStatus run(IProgressMonitor monitor) {
			Display.getDefault().asyncExec(new Runnable() {
				
				public void run() {
					tableViewer.refresh();
					tableViewer.getTable().select(0);
				}
			});
			return Status.OK_STATUS;
		}
	};

	public void setFocus() {
		tableViewer.getTable().setFocus();
	}

	private ViewerFilter tableFilter = new ViewerFilter() {

		
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			String toString = String.valueOf(element);
			return matches(toString, filterText.getText());
		}
	};

	private class ScaleableImageCanvas extends Canvas {

		private Image image;
		private int width;
		private int height;
		private int ix = 0;
		private int iy = 0;

		public ScaleableImageCanvas(Composite parent, int style) {
			super(parent, style);
			setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			addPaintListener(new PaintListener() {
				public void paintControl(PaintEvent e) {
					if (image == null) {
						e.gc.fillRectangle(0, 0, getSize().x, getSize().y);
						return;
					}
					int scaledWidth = getSize().x;
					int scaledHeight = getSize().y;

					if (width <= scaledWidth && height <= scaledHeight) {
						scaledWidth = width;
						scaledHeight = height;
					} else {
						double scale = (double) scaledWidth / width;
						if (height * scale <= scaledHeight) {
							scaledHeight = (int) (height * scale);
						} else {
							scale = (double) scaledHeight / height;
							scaledWidth = (int) (width * scale);
						}
					}
					e.gc.drawImage(ScaleableImageCanvas.this.image, 0, 0, width, height, ix, iy, scaledWidth,
							scaledHeight);
				}
			});
		}

		public void setImage(Image image) {
			this.image = image;
			if (image != null) {
				this.width = image.getImageData().width;
				this.height = image.getImageData().height;
			}
			redraw();
		}

		
		public void dispose() {
			super.dispose();
		}

	}

	private class IconObject implements IAdaptable {
		private Image icon;
		private Image image;
		private String text;
		private IFile file;

		public IconObject(IFile file) {
			this.file = file;
		}

		public IFile getFile() {
			return file;
		}

		/**
		 * 
		 * @return 32*32 icon
		 */
		public Image getIcon() {
			return icon != null && !icon.isDisposed() ? icon : null;
		}

		public synchronized void dispose() { 
			if (icon != null) {
				icon.dispose();
				icon = null;
			}
			if (image != null) {
				image.dispose();
				image = null;
			}
		} 
		
		public synchronized void ensureLoaded() {
			if (image == null || image.isDisposed()) {
				ImageData data = new ImageData(file.getLocation().makeAbsolute().toFile().getAbsolutePath());
				image = new Image(Display.getDefault(), data);
				icon = new Image(Display.getDefault(), 32, 32);
				text = file.getName() + " (" + image.getImageData().width + " * " + image.getImageData().height + ")";
				GC gc = new GC(icon);
				gc.drawImage(image, 0, 0, image.getImageData().width, image.getImageData().height, 0, 0, 32, 32);
				gc.dispose();
			}
		}

		public Image getImage() {
			return image != null  && !image.isDisposed() ? image : null;
		}

		public String getText() {
			if (text == null) {
				return file.getName();
			}
			return text;
		}

		
		public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
			if (IResource.class.equals(adapter)) {
				return getFile();
			}
			return null;
		}

		
		public String toString() {
			return getText();
		}

	}

	public static boolean matches(String word, String pattern) {
		SearchPattern searchPattern = new SearchPattern();
		if (!pattern.startsWith("*")) {
			pattern = "*" + pattern;
		}
		searchPattern.setPattern(pattern);
		return searchPattern.matches(word);
	}

}
