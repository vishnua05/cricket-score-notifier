package scorenotifierplugin;

import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.StatusLineContributionItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchWindow;

public class ViewLogger implements ILogger {

	private StatusLineContributionItem item;
	
	public void log(final String matchURL, final String log, final String score) {
		
		Display.getDefault().asyncExec(new Runnable() {
			
			public void run() {

				if (ScoreNotifierActivator.getDefault().getPreferences().logEveryBall()) {
					ScoreCardView.showView();
				}
				ScoreCardView scoreCardView = ScoreCardView.getScoreCardView();
				if (scoreCardView != null) {
					scoreCardView.appendText(matchURL, log, score);
				}
			}
		});
	}

	public void log(String matchURL, String log) {
           log(matchURL, log, null);		
	}
	
	@SuppressWarnings("restriction")
	public synchronized void updateStatusBar(final String score) {
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				if (item == null) {
					item = new StatusLineContributionItem(ViewLogger.class.getCanonicalName());
					WorkbenchWindow window = (WorkbenchWindow) PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					IStatusLineManager statusLineManager = window.getActionBars().getStatusLineManager();
					item.setParent(statusLineManager);
					statusLineManager.add(item);
					statusLineManager.update(true);
				}
				item.setText(score);
			}
		});
	}
	
	public synchronized void clearStatusBar() {
		if (item != null) {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					IContributionManager parent = item.getParent();
					parent.remove(item);
					parent.update(true);
					item = null;
				}
			});
		}
	}

}
