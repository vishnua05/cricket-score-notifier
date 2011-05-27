package scorenotifierplugin.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class RefreshLiveMatchesAction implements IViewActionDelegate {

	public void init(IViewPart view) {

	}

	public void run(IAction action) {
         ScorePullDownAction.scoreNotifier.refreshLiveMatches();
	}

	public void selectionChanged(IAction action, ISelection selection) {

	}

}
