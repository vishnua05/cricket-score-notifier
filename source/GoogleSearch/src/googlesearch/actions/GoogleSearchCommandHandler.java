package googlesearch.actions;

import googlesearch.Activator;
import googlesearch.ui.GoogleSearchDialog;

import java.net.URL;
import java.util.HashMap;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

public class GoogleSearchCommandHandler extends AbstractHandler implements IHandler {

	private static final String GOOGLE_SEARCH_URL = "http://www.google.com/search?q=";
	private static final String FIRST_QUERY_URL = "http://google.com/search?btnI=1&q=";
	public static HashMap<String, String> directLinks = new HashMap<String, String>();
	
	static {
		directLinks.put("Eclipse", null);
		directLinks.put("Eclipse Market Place", null);
		directLinks.put("CricInfo", null);
		directLinks.put("CricBuzz", null);
		directLinks.put("Google Reader", null);
		directLinks.put("Facebook", null);
		directLinks.put("Cricket Score Notifier", null);
		directLinks.put("Gmail", null);
	}
	
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbench workbench = PlatformUI.getWorkbench();
		ISelection selection = workbench.getActiveWorkbenchWindow().getActivePage().getSelection();
		String queryText = null;
		if (selection instanceof ITextSelection) {
			queryText = ((ITextSelection) selection).getText();
		}
		Control control = Display.getDefault().getFocusControl();
		if (control instanceof Text) {
             queryText = ((Text) control).getSelectionText();
		}
		GoogleSearchDialog googleSearchDialog = new GoogleSearchDialog(queryText);
		googleSearchDialog.open();
		return null;
	}

	public static void openBrowser(IWorkbench workbench, String queryText) {
		openBrowser(workbench, queryText, directLinks.containsKey(queryText));
	}

	public static void openBrowser(IWorkbench workbench, String queryText, boolean firstResult) {
		queryText = (firstResult ? FIRST_QUERY_URL : GOOGLE_SEARCH_URL) + queryText;
		try {
			IWebBrowser browser = workbench.getBrowserSupport().getExternalBrowser();
			browser.openURL(new URL(queryText));
		} catch (Exception e) {
			Activator.getDefault().logException(e);
		}
	}

}
