package scorenotifierplugin.provider.mobile;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import scorenotifierplugin.ScoreNotifierActivator;
import scorenotifierplugin.provider.EndOfScoreException;
import scorenotifierplugin.provider.IScoreParser;

public class FeedReader {
	private static final String TITLE_REGEX = ".*<title>(.*)</title>.*";
	private static final String COMMENTARY_REGEX = ".*action=\"(.*)\".*Commentary.*";
	private static final String SCORECARD_REGEX = ".*action=\"(.*)\".*Detailed Scorecard.*";

	private static String FEED_URL = "http://synd.cricbuzz.com/onmobile-togo/";

	public static ArrayList<MobileMatch> getAvailableMatches() {
		ArrayList<MobileMatch> availableMatches = new ArrayList<MobileMatch>();

		HttpURLConnection connection = null;
		try {
			URL url = new URL(FEED_URL);
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			InputStreamReader inStream = new InputStreamReader(connection.getInputStream());
			BufferedReader buff = null;
			try {
				buff = new BufferedReader(inStream);
				MobileMatch mobileMatch = null;
				while (true) {
					String nextLine = buff.readLine();
					if (nextLine != null) {
						Pattern pattern = Pattern.compile(TITLE_REGEX);
						Matcher matcher = pattern.matcher(nextLine);
						if (matcher.matches()) {
							mobileMatch = new MobileMatch();
							mobileMatch.setName(matcher.group(1));
							availableMatches.add(mobileMatch);
						} else {
							pattern = Pattern.compile(COMMENTARY_REGEX);
							matcher = pattern.matcher(nextLine);
							if (matcher.matches()) {
								mobileMatch.setCommentaryURL(matcher.group(1));
							} else {
								pattern = Pattern.compile(SCORECARD_REGEX);
								matcher = pattern.matcher(nextLine);
								if (matcher.matches()) {
									mobileMatch.setScoreURL(matcher.group(1));
								}
							}
						}
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
			ScoreNotifierActivator.getDefault().logException(e);
		} finally {
			connection.disconnect();
		}
		return availableMatches;
	}

	public static Map<String, String> getScoreCardDetails(MobileMatch mobileMatch) throws EndOfScoreException {
		Map<String, String> scoreDetails = new HashMap<String, String>();
		HttpURLConnection connection = null;
		try {
			URL url = new URL(mobileMatch.getScoreURL());
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			InputStreamReader inStream = new InputStreamReader(connection.getInputStream());
			BufferedReader buff = null;
			try {
				buff = new BufferedReader(inStream);
				while (true) {
					String nextLine = buff.readLine();
					if (nextLine != null) {
						Pattern pattern = Pattern.compile(IScoreParser.REGEX_BATTING_TEAM);
						Matcher matcher = pattern.matcher(nextLine);
						if (matcher.matches()) {
							if (scoreDetails.get(IScoreParser.BATTING_TEAM) != null) {
								break;
							}
							scoreDetails.put(IScoreParser.BATTING_TEAM, matcher.group(1));
						} else {
							pattern = Pattern.compile(IScoreParser.REGEX_RESULT);
							matcher = pattern.matcher(nextLine);
							if (matcher.matches()) {
								scoreDetails.put(IScoreParser.RESULT, matcher.group(1));
							} else {
								pattern = Pattern.compile(IScoreParser.REGEX_BATSMEN);
								matcher = pattern.matcher(nextLine);
								if (matcher.matches()) {
									String batsMenStats = matcher.group(1) + matcher.group(3);
									int balls = Integer.parseInt(matcher.group(5));
									if (balls != 0) {
										float strikeRate = (Integer.parseInt(matcher.group(4)) * 10000) / balls;
										strikeRate = strikeRate / 100;
										batsMenStats = batsMenStats + " [" + strikeRate + "]";
									}
									String previousStats = scoreDetails.get(IScoreParser.BATSMEN_STATS);
									batsMenStats = previousStats != null ? previousStats + IScoreParser.BATSMEN_DELIMITER + batsMenStats : batsMenStats;
									scoreDetails.put(IScoreParser.BATSMEN_STATS, batsMenStats);
								} else {
									pattern = Pattern.compile(IScoreParser.REGEX_TOTAL);
									matcher = pattern.matcher(nextLine);
									if (matcher.matches()) {
										scoreDetails.put(IScoreParser.TOTAL, matcher.group());
									} else {
										pattern = Pattern.compile(IScoreParser.REGEX_BOWLERS);
										matcher = pattern.matcher(nextLine);
										if (matcher.matches()) {
											String commentary = getCommentary(mobileMatch.getCommentaryURL());
										    String bowler = null;
										    if (commentary != null) {
										    	String bowlerRegex = "[0-9]{1,2}|[0-9]{1,2}\\.[0-6] (.*?) to.*";
										    	Matcher bowlerMatcher = Pattern.compile(bowlerRegex).matcher(commentary);
										    	if (bowlerMatcher.matches()) {
										    		bowler = bowlerMatcher.group(1).trim();
										    	}
										    }
											String currentBowler = matcher.group(1) + matcher.group(2);
											if (bowler != null && currentBowler.contains(bowler)) {
												scoreDetails.put(IScoreParser.BOWLER_STATS, currentBowler);
											}
										} else {
											pattern = Pattern.compile(IScoreParser.REGEX_START_TIME);
											matcher = pattern.matcher(nextLine);
											if (matcher.matches()) {
												scoreDetails.put(IScoreParser.START_TIME, matcher.group(1));
											}
										}
									}
								}
							}
						}
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
			ScoreNotifierActivator.getDefault().logException(e);
			throw new EndOfScoreException(e);
		} finally {
			connection.disconnect();
		}
		scoreDetails.put(IScoreParser.COMMENTARY, getCommentary(mobileMatch.getCommentaryURL()));
		return scoreDetails;
	}

	private static String getCommentary(String commentaryURL) {
		try {
			URL url = new URL(commentaryURL);
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
							Pattern pattern = Pattern.compile(IScoreParser.REGEX_COMMENTARY);
							Matcher matcher = pattern.matcher(nextLine);
							if (matcher.matches()) {
								return matcher.group(1);
							}
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
				ScoreNotifierActivator.getDefault().logException(e);
			} finally {
				connection.disconnect();
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void main(String[] args) {
		List<MobileMatch> availableMatches = FeedReader.getAvailableMatches();
		for (MobileMatch mobileMatch : availableMatches) {
			System.out.println(mobileMatch.getScoreURL());
		}
		for (MobileMatch mobileMatch : availableMatches) {
			System.out.println(mobileMatch.getCommentaryURL());
		}
	}

}
