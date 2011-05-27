package scorenotifierplugin.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import scorenotifierplugin.preferences.ScoreNotifierPreferencesDialog;

public class ShowPreferencesAction implements IViewActionDelegate {

	public void init(IViewPart view) {

	}

	public void run(IAction action) {
		ScoreNotifierPreferencesDialog scoreNotifierPreferencesDialog = new ScoreNotifierPreferencesDialog(null);
		scoreNotifierPreferencesDialog.open();

	}

	public void selectionChanged(IAction action, ISelection selection) {

	}

}
