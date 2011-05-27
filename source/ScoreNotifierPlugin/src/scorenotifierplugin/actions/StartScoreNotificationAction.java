package scorenotifierplugin.actions;

import java.util.List;

import org.eclipse.swt.SWT;

import scorenotifierplugin.ScoreNotifier;


public class StartScoreNotificationAction extends  ScorePullDownAction {

	@Override
	protected void handleSelection(String matchURL) {
		scoreNotifier.toggleNotifier(matchURL);
	}
	
	@Override
	protected List<String> getAvailableItems() {
		List<String> availableItems = super.getAvailableItems();
		if (availableItems.size() == 0) {
			availableItems.add(ScoreNotifier.NONE_AVAILABLE);
		}
		return availableItems;
	}
	
	@Override
	protected int getMenuStyle() {
		return SWT.CHECK;
	}
}
