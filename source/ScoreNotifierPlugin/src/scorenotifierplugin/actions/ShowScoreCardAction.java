package scorenotifierplugin.actions;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import scorenotifierplugin.ScoreCardView;


public class ShowScoreCardAction implements IWorkbenchWindowActionDelegate {

	public void dispose() {

	}

	public void init(IWorkbenchWindow window) {

	}

	public void run(IAction action) {
		ScoreCardView.showView();
	}

	public void selectionChanged(IAction action, ISelection selection) {

	}

}
