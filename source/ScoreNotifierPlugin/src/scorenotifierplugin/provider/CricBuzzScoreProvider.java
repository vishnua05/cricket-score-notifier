package scorenotifierplugin.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CricBuzzScoreProvider implements IScoreProvider {

	private static final String MATCH = "match";

	private Map<String, ScoreNode> previousNodes = new HashMap<String, ScoreNode>();

	private ArrayList<String> matchURLs;

	public ScoreNode getScore(String matchURL) throws EndOfScoreException {
		IScoreParser scoreParser = null;
		try {
			scoreParser = getScoreParser(matchURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (scoreParser != null) {
			ScoreNode previousNode = previousNodes.get(matchURL);
			if (previousNode == null || !previousNode.getScore().equals(scoreParser.getScore())) {
				previousNode = new ScoreNode(scoreParser, previousNode, matchURL);
				previousNodes.put(matchURL, previousNode);
				return previousNode;
			}
		}
		return null;
	}

	public void cleanupNodes(String matchURL) {
		previousNodes.remove(matchURL);
	}

	private IScoreParser getScoreParser(String matchURL) {
		List<Match> matches = getLiveMatches();
		for (Match match : matches) {
			if (match.getUrlLink().equals(matchURL)) {
				String scoreLine = getData(match.getHeader());
				if (!Pattern.matches(IScoreParser.REGEX, scoreLine)) {
					scoreLine = scoreLine + " " + getData(match.getDescription());
				}
				return new ScoreParser(scoreLine);
			}
		}
		return null;
	}

	private String getData(String dataString) {
		int beginIndex = dataString.indexOf("CDATA[") + 6;
		int endIndex = dataString.indexOf("]");
		return dataString.substring(beginIndex, endIndex);

	}

	public List<String> getMatchUIDs() {
		if (matchURLs == null) {
			List<Match> liveMatches = getLiveMatches();
			matchURLs = new ArrayList<String>(liveMatches.size());
			for (Match match : liveMatches) {
				matchURLs.add(match.getUrlLink());
			}
		}
		return new ArrayList<String>(matchURLs);
	}

	private List<Match> getLiveMatches() {
		String startElement = "<" + MATCH + ">";
		String endElement = "</" + MATCH + ">";
		List<Match> matches = new ArrayList<Match>();
		StringBuffer urlFeedBuffer = new StringBuffer();
		try {
			URL url = new URL("http://synd.cricbuzz.com/score-gadget/gadget-scores-feed.xml");
			HttpURLConnection connection = null;
			try {
				connection = (HttpURLConnection) url.openConnection();
				connection.connect();
				InputStreamReader inStream = new InputStreamReader(connection.getInputStream());
				BufferedReader buff = null;
				try {
					buff = new BufferedReader(inStream);
					while (true) {
						String nextLine = buff.readLine();
						if (nextLine != null) {
							urlFeedBuffer.append(nextLine.trim());
						} else {
							break;
						}
					}
				} finally {
					if (buff != null) {
						buff.close();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				connection.disconnect();
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		String urlFeed = urlFeedBuffer.toString();
		while (urlFeed.indexOf(startElement) != -1) {
			String matchText = urlFeed.substring(urlFeed.indexOf(startElement) + startElement.length(), urlFeed.indexOf(endElement));
			matches.add(new Match(matchText));
			urlFeed = urlFeed.substring(urlFeed.indexOf(endElement) + endElement.length(), urlFeed.length());
		}

		return matches;
	}

	@Override
	public String toString() {
		return "Cric Buzz Score Provider";
	}

	public void refreshLiveMatches() {
		matchURLs = null;
	}

}
