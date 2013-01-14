package googlesearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.widgets.Widget;

public class SWTResourceUtil {

	private static Map<Widget, List<Resource>> widgetDisposablesMap = new HashMap<Widget, List<Resource>>();
		
	/**
	 * 
	 * @param widget
	 * @param resources which has to be disposed when the widget using it is disposed
	 */
	public static void cleanupOnDispose(Widget widget, List<? extends Resource> resources) {
		Assert.isNotNull(widget);
		List<Resource> disposables = widgetDisposablesMap.get(widget);
		if (disposables == null) {
			disposables = new ArrayList<Resource>();
			widgetDisposablesMap.put(widget, disposables);
			widget.addDisposeListener(new DisposeListener() {
				
				public void widgetDisposed(DisposeEvent e) {
					Widget source = (Widget) e.getSource();
					List<Resource> disposables = widgetDisposablesMap.get(source);
					for (Resource resource : disposables) {
						if (resource != null) {
							resource.dispose();
						}
					}
					source.removeDisposeListener(this);
					widgetDisposablesMap.remove(source);
				}
			});
		}
		disposables.addAll(resources);
	}

	public static void cleanupOnDispose(Widget widget, Resource resource) {
		List<Resource> resources = new ArrayList<Resource>();
		resources.add(resource);
		cleanupOnDispose(widget, resources);
	}	
	
}
