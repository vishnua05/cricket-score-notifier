package eclipse.utility.actions;

import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

import eclipse.utility.ui.GoogleSearchDialog;

public class GoogleSearchCommandHandler extends AbstractHandler implements IHandler {

	private static final String GOOGLE_SEARCH_URL = "http://www.google.com/search?q=";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbench workbench = PlatformUI.getWorkbench();
		ISelection selection = workbench.getActiveWorkbenchWindow().getActivePage().getSelection();
		String queryText = null;
		if (selection instanceof ITextSelection) {
			queryText = ((ITextSelection) selection).getText();
		}
		if (queryText != null && queryText.trim().length() > 0) {
			openBrowser(workbench, queryText);
		} else {
			new GoogleSearchDialog(null).open();
		}
		
		return null;
	}

	public static void openBrowser(IWorkbench workbench, String queryText) {
		queryText = GOOGLE_SEARCH_URL + queryText;
		try {
			IWebBrowser browser = workbench.getBrowserSupport().getExternalBrowser();
		    browser.openURL(new URL(queryText)); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

}
