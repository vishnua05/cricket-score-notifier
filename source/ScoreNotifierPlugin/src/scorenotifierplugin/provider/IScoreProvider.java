package scorenotifierplugin.provider;

import java.util.List;

public interface IScoreProvider {

	ScoreNode getScore(String matchURL) throws EndOfScoreException;

	List<String> getMatchURLs();

	void cleanupNodes(String matchURL);
	
	void refreshLiveMatches();

}
