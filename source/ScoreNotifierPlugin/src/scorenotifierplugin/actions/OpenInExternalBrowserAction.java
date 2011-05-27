package scorenotifierplugin.actions;

import java.net.URL;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;


public class OpenInExternalBrowserAction extends ViewInBrowserAction {

	@Override
	protected void handleSelection(String matchURL) {
		IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
		try {
			IWebBrowser browser = browserSupport.getExternalBrowser();
			browser.openURL(new URL(matchURL));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
