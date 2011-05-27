package scorenotifierplugin.provider.mobile;

import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import scorenotifierplugin.provider.IScoreParser;

public class MobileScoreParser implements IScoreParser {

	private String batsMenStats;
	private String bowlerStats;
	private String commentary;
	private String score;
	private int totalRuns = -1;
	private int wickets = -1;
	private int balls = -1;

	public MobileScoreParser(Map<String, String> scoreCardDetails) {
		String total = scoreCardDetails.get(TOTAL);
		if (total != null) {
			Matcher scoreMatcher = Pattern.compile(REGEX_TOTAL).matcher(total);
			scoreMatcher.find();
			totalRuns = Integer.parseInt(scoreMatcher.group(1));
			wickets = Integer.parseInt(scoreMatcher.group(2));
			balls = (int) (Float.parseFloat(scoreMatcher.group(3)) *10);
			score = scoreCardDetails.get(BATTING_TEAM) + " " + totalRuns + "/" + wickets + " (" + scoreMatcher.group(3) + ")";
		}
		commentary = scoreCardDetails.get(COMMENTARY);
		batsMenStats = scoreCardDetails.get(BATSMEN_STATS);
		bowlerStats = scoreCardDetails.get(BOWLER_STATS);
		String result = scoreCardDetails.get(RESULT);
		score = score != null ? result != null ? score + result : score : result; 
		if (score == null) {
			score = scoreCardDetails.get(START_TIME);
		} 
	}

	public int getBalls() {
		return balls;
	}

	public String getBatsMenStats() {
		return batsMenStats;
	}

	public String getBowlerStats() {
		return bowlerStats;
	}

	public String getCommentary() {
		return commentary;
	}

	public String getScore() {
		return score;
	}

	public int getTotalRuns() {
		return totalRuns;
	}

	public int getWickets() {
		return wickets;
	}

	public static void main(String[] args) {
		String batsMenSample = "<p>Collins Obuya notout 98(129)</p>";
		Matcher batsMenMatcher = Pattern.compile(REGEX_BATSMEN).matcher(batsMenSample);
		System.out.println(batsMenMatcher.matches());
		System.out.println(batsMenMatcher.group(1));
		System.out.println(batsMenMatcher.group(3));
		System.out.println(batsMenMatcher.group(4));
		System.out.println(batsMenMatcher.group(5));

		String teamSample = "<p>Batting: Ken</p>";
		Matcher teamMatcher = Pattern.compile(REGEX_BATTING_TEAM).matcher(teamSample);
		System.out.println(teamMatcher.matches());
		System.out.println(teamMatcher.group(1));

		String resultSample = "<p>Result: Aus won by 60 runs</p>";
		Matcher resultMatcher = Pattern.compile(REGEX_RESULT).matcher(resultSample);
		System.out.println(resultMatcher.matches());
		System.out.println(resultMatcher.group(1));

		String totalSample = "<p>Total: 264/6 in 50 overs</p>";
		Matcher scoreMatcher = Pattern.compile(REGEX_TOTAL).matcher(totalSample);
		System.out.println(scoreMatcher.matches());
		System.out.println(scoreMatcher.group(1));
		System.out.println(scoreMatcher.group(2));
		System.out.println(scoreMatcher.group(3));


		String bowlingSample = "<p>Brett Lee 8-1-26-1</p>";
		Matcher bowlingMatcher = Pattern.compile(REGEX_BOWLERS).matcher(bowlingSample);
		System.out.println(bowlingMatcher.matches());
		System.out.println(bowlingMatcher.group(1));
		System.out.println(bowlingMatcher.group(2));

		String startTimeSample = "<p>Match starts at 14:30 AEST</p>";
		Matcher startTimeMatcher = Pattern.compile(REGEX_START_TIME).matcher(startTimeSample);
		System.out.println(startTimeMatcher.matches());
		System.out.println(startTimeMatcher.group(1));

		String commentarySample = "<p><![CDATA[49.3  Tait to Patel, 1 run, yorker length outside off, Patel squeezes it to third man]]></p>";
		Matcher commentaryMatcher = Pattern.compile(REGEX_COMMENTARY).matcher(commentarySample);
		System.out.println(commentaryMatcher.matches());
		System.out.println(commentaryMatcher.group(1));
		
		StringTokenizer stringTokenizer = new StringTokenizer("$One 1(2)$Two 2(3)$Three 3(4)$", IScoreParser.BATSMEN_DELIMITER); 
		while (stringTokenizer.hasMoreElements()) {
			Object object = (Object) stringTokenizer.nextElement();
			System.out.println(object);
		}


	}

}
