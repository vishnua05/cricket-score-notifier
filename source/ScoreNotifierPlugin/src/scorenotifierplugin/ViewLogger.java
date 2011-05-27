package scorenotifierplugin;

import org.eclipse.swt.widgets.Display;

public class ViewLogger implements ILogger {

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

}
