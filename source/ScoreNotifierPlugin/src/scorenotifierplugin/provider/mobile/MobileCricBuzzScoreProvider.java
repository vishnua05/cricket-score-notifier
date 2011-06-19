package scorenotifierplugin.provider.mobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scorenotifierplugin.provider.EndOfScoreException;
import scorenotifierplugin.provider.IScoreParser;
import scorenotifierplugin.provider.IScoreProvider;
import scorenotifierplugin.provider.ScoreNode;

public class MobileCricBuzzScoreProvider implements IScoreProvider {

	private Map<String, ScoreNode> previousNodes = new HashMap<String, ScoreNode>();
	private ArrayList<MobileMatch> liveMatches;

	public void cleanupNodes(String matchURL) {
		previousNodes.remove(matchURL);
	}

	public List<String> getMatchUIDs() {
		if (liveMatches == null) {
			liveMatches = FeedReader.getAvailableMatches();
		}
		List<String> matchURLS = new ArrayList<String>();
		for (MobileMatch mobileMatch : liveMatches) {
			matchURLS.add(mobileMatch.getName());
		}
		return matchURLS;
	}

	public ScoreNode getScore(String matchURL) throws EndOfScoreException {
		MobileMatch interestedMatch = null;
		for (MobileMatch mobileMatch : liveMatches) {
			if (mobileMatch.getName().equals(matchURL)) {
				interestedMatch = mobileMatch;
				break;
			}
		}

		if (interestedMatch != null) {
			Map<String, String> scoreCardDetails = FeedReader.getScoreCardDetails(interestedMatch);
			ScoreNode previousNode = previousNodes.get(matchURL);
			IScoreParser scoreParser = new MobileScoreParser(scoreCardDetails);
			if (previousNode == null || hasChanged(previousNode, scoreParser)) {
				previousNode = new ScoreNode(scoreParser, previousNode, matchURL);
				previousNodes.put(matchURL, previousNode);
				return previousNode;
			} else if (scoreCardDetails.get(IScoreParser.RESULT) != null) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				throw new EndOfScoreException();
			}
		}
		return null;
	}

	private boolean hasChanged(ScoreNode previousNode, IScoreParser scoreParser) {
		boolean sameCommentary = previousNode.getCommentary() == null ? scoreParser.getCommentary() == null : previousNode.getCommentary().equals(scoreParser.getCommentary());
		return scoreParser.getScore() != null && !previousNode.getScore().equals(scoreParser.getScore()) || !sameCommentary;
	}

	public void refreshLiveMatches() {
		liveMatches = null;
	}

}
