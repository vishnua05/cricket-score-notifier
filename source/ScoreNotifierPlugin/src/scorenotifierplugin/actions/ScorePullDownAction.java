package scorenotifierplugin.actions;

import java.util.List;
import java.util.Set;

import org.eclipse.ui.IViewPart;

import scorenotifierplugin.ScoreNotifier;
import scorenotifierplugin.provider.mobile.MobileCricBuzzScoreProvider;

public class ScorePullDownAction extends PullDownAction {
	static ScoreNotifier scoreNotifier;

	final public void init(IViewPart view) {
		if (scoreNotifier == null) {
			scoreNotifier = new ScoreNotifier(new MobileCricBuzzScoreProvider());
		}
	}

	@Override
	protected List<String> getAvailableItems() {
		List<String> availableURLs = scoreNotifier.getAvailableURLs();
		return availableURLs;
	}

	@Override
	public void dispose() {
		super.dispose();
	}
	
	@Override
	protected String getDisplayName(String matchURL) {
		return ScoreNotifier.getDisplayName(matchURL);
	}

	@Override
	protected boolean getSelection(String matchURL) {
		Set<String> activeURLs = scoreNotifier.getActiveURLs();
		return activeURLs.contains(matchURL);
	}

	@Override
	protected void handleSelection(String matchURL) {
		super.handleSelection(matchURL);
	}
}
