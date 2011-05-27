package scorenotifierplugin.provider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScoreParser implements IScoreParser {
	private String scoreLine;
	private int totalRuns = -1;
	private int wickets = -1;
	private int balls = -1;
	
	public ScoreParser(String scoreLine) {
		this.scoreLine = scoreLine;
		if (Pattern.matches(REGEX, scoreLine)) {
			Pattern compile = Pattern.compile(REGEX);
			Matcher matcher = compile.matcher(scoreLine);
			matcher.find();
			totalRuns = Integer.parseInt(matcher.group(1));
			wickets = Integer.parseInt(matcher.group(2));
			balls = (int) (Float.parseFloat(matcher.group(3)) * 10);
		}
	}

	public String getScore() {
		return scoreLine;
	}
	
	public int getTotalRuns() {
		return totalRuns;
	}
	
	public int getWickets() {
		return wickets;
	}
	
	public int getBalls() {
		return balls;
	}

	public String getBatsMenStats() {
		return null;
	}

	public String getCommentary() {
		return null;
	}

	public String getBowlerStats() {
		return null;
	}
	
}
