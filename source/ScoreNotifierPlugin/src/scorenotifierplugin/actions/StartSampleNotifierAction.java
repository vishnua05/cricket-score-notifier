package scorenotifierplugin.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import scorenotifierplugin.ScoreNotifier;
import scorenotifierplugin.provider.TestScoreProvider;

public class StartSampleNotifierAction implements IViewActionDelegate{
	private static ScoreNotifier scoreNotifier;
	
	public void init(IViewPart view) {
	}

	public void run(IAction action) {
		String url = TestScoreProvider.cricBuzzURL;
		if (scoreNotifier == null || !scoreNotifier.getActiveURLs().contains(url)) {
			scoreNotifier = new ScoreNotifier(new TestScoreProvider());
		} 
		scoreNotifier.toggleNotifier(url);
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
