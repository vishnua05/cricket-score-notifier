package scorenotifierplugin.provider;

import java.util.ArrayList;
import java.util.List;

public class TestScoreProvider implements IScoreProvider {

	private List<ScoreNode> scoreNodes = new ArrayList<ScoreNode>();
	public static final String cricBuzzURL = "http://www.cricbuzz.com/";
	private static final List<String> urls = new ArrayList<String>();
	{
		urls.add(cricBuzzURL);
	}

	int currentIndex = 0;
	
	public TestScoreProvider() {
		scoreNodes.add(new ScoreNode("Test 0/0 (0.0)", null, cricBuzzURL));
		scoreNodes.add(new ScoreNode("Test 1/0 (0.1)", scoreNodes.get(scoreNodes.size() -1), cricBuzzURL));
		scoreNodes.add(new ScoreNode("Test 1/0 (0.2)", scoreNodes.get(scoreNodes.size() -1), cricBuzzURL));
		scoreNodes.add(new ScoreNode("Test 2/0 (0.2)", scoreNodes.get(scoreNodes.size() -1), cricBuzzURL));
		scoreNodes.add(new ScoreNode("Test 6/0 (0.3)", scoreNodes.get(scoreNodes.size() -1), cricBuzzURL));
		scoreNodes.add(new ScoreNode("Test 6/0 (0.4)", scoreNodes.get(scoreNodes.size() -1), cricBuzzURL));
		scoreNodes.add(new ScoreNode("Test 6/1 (0.5)", scoreNodes.get(scoreNodes.size() -1), cricBuzzURL));
		scoreNodes.add(new ScoreNode("Test 7/1 (0.6)", scoreNodes.get(scoreNodes.size() -1), cricBuzzURL));
		scoreNodes.add(new ScoreNode("Test 9/1 (1.1)", scoreNodes.get(scoreNodes.size() -1), cricBuzzURL));
		scoreNodes.add(new ScoreNode("Test 11/1 (1.1)", scoreNodes.get(scoreNodes.size() -1), cricBuzzURL));
		scoreNodes.add(new ScoreNode("Test 17/1 (1.2)", scoreNodes.get(scoreNodes.size() -1), cricBuzzURL));
		scoreNodes.add(new ScoreNode("Test 17/1 (1.3)", scoreNodes.get(scoreNodes.size() -1), cricBuzzURL));
		scoreNodes.add(new ScoreNode("Test 17/1 (1.4)", scoreNodes.get(scoreNodes.size() -1), cricBuzzURL));
		scoreNodes.add(new ScoreNode("Test 18/1 (1.5)", scoreNodes.get(scoreNodes.size() -1), cricBuzzURL));
		scoreNodes.add(new ScoreNode("Test 20/1 (2.0)", scoreNodes.get(scoreNodes.size() -1), cricBuzzURL));
		scoreNodes.add(new ScoreNode("Test 24/1 (2.1)", scoreNodes.get(scoreNodes.size() -1), cricBuzzURL));
		scoreNodes.add(new ScoreNode("Test 25/1 (2.2)", scoreNodes.get(scoreNodes.size() -1), cricBuzzURL));
		scoreNodes.add(new ScoreNode("Test 30/1 (2.2)", scoreNodes.get(scoreNodes.size() -1), cricBuzzURL));
		scoreNodes.add(new ScoreNode("Test 31/1 (2.3)", scoreNodes.get(scoreNodes.size() -1), cricBuzzURL));
		scoreNodes.add(new ScoreNode("Test 32/2 (2.4)", scoreNodes.get(scoreNodes.size() -1), cricBuzzURL));
		scoreNodes.add(new ScoreNode("Test 32/2 (2.5)", scoreNodes.get(scoreNodes.size() -1), cricBuzzURL));
		scoreNodes.add(new ScoreNode("Test 32/2 (2.6)", scoreNodes.get(scoreNodes.size() -1), cricBuzzURL));
		scoreNodes.add(new ScoreNode("Test 32/2 (3.1)", scoreNodes.get(scoreNodes.size() -1), cricBuzzURL));
		scoreNodes.add(new ScoreNode("Test 32/2 (3.2)", scoreNodes.get(scoreNodes.size() -1), cricBuzzURL));
		scoreNodes.add(new ScoreNode("Test 32/2 (3.3)", scoreNodes.get(scoreNodes.size() -1), cricBuzzURL));
		scoreNodes.add(new ScoreNode("Test 32/3 (3.4)", scoreNodes.get(scoreNodes.size() -1), cricBuzzURL));
		scoreNodes.add(new ScoreNode("Test 32/3 (3.5)", scoreNodes.get(scoreNodes.size() -1), cricBuzzURL));
		scoreNodes.add(new ScoreNode("Test 32/3 (3.6)", scoreNodes.get(scoreNodes.size() -1), cricBuzzURL));

	}
	
	public List<String> getMatchURLs() {
		return urls;
	}

	public ScoreNode getScore(String matchURL) throws EndOfScoreException {
		if (currentIndex != scoreNodes.size()) {
			return scoreNodes.get(currentIndex++);
		}
		throw new EndOfScoreException();
	}

	@Override
	public String toString() {
		return "Test Score Provider";
	}

	public void cleanupNodes(String matchURL) {
		
	}

	public void refreshLiveMatches() {
		
	}

}
