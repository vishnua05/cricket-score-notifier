package scorenotifierplugin.actions;
import scorenotifierplugin.ScoreCardView;


public class ClearScoreCardAction extends ScorePullDownAction {
	@Override
	protected void handleSelection(String matchURL) {
		ScoreCardView scoreCardView = ScoreCardView.getScoreCardView();
		scoreCardView.clearConsole(matchURL);
	}

}
