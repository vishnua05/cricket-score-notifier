package scorenotifierplugin.actions;

import java.net.URL;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import scorenotifierplugin.ScoreNotifier;

public class OpenInBrowserAction extends ViewInBrowserAction {

	@Override
	protected void handleSelection(String matchURL) {
		IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
		if (scoreNotifier.getAvailableURLs().contains(matchURL)) {
			matchURL = ScoreNotifier.getMatchURL(matchURL);
		}
		try {
			IWebBrowser browser = browserSupport.getExternalBrowser();
			URL url = new URL(matchURL);
			browser.openURL(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
