package scorenotifierplugin.actions;

import java.net.URL;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

public class OpenInBrowserAction extends ViewInBrowserAction {

	@Override
	protected void handleSelection(String matchURL) {
		IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
		try {
			IWebBrowser browser = browserSupport.createBrowser("scorenotifierplugin.actions.OpenLiveScoresInternalAction");
			browser.openURL(new URL(matchURL));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
