package scorenotifierplugin.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import scorenotifierplugin.ScoreNotifier;
import scorenotifierplugin.ScoreNotifierActivator;
import scorenotifierplugin.provider.IScoreProvider;

public class SwitchProviderAction implements IViewActionDelegate {
	public static final String USE_ALTERNATE_PROVIDER_KEY = "scorenotifierplugin.actions.switchprovideraction.use_alternate_provider_key";
	private IScoreProvider alternateProvider;

	public void init(IViewPart view) {

	}

	public void run(IAction action) {
		if (alternateProvider == null) {
			if (isAlternateProviderUsed()) {
				alternateProvider = ScoreNotifier.mainProvider;
			} else {
				alternateProvider = ScoreNotifier.alternateProvider;
			}
		}
		IScoreProvider newProvider = alternateProvider;
		alternateProvider = ScoreNotifier.getMainInstance().switchProvider(alternateProvider);
		String message = "Score provider has been changed to '" + newProvider
				+ "'. Click on the run button to subscribe for the live matches.";
		MessageDialog.openInformation(null, "Done", message);
		boolean switchProvider = !isAlternateProviderUsed();
		ScoreNotifierActivator.getDefault().getPreferenceStore().setValue(USE_ALTERNATE_PROVIDER_KEY, switchProvider);
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public static boolean isAlternateProviderUsed() {
		return ScoreNotifierActivator.getDefault().getPreferenceStore().getBoolean(USE_ALTERNATE_PROVIDER_KEY);
	}
}
