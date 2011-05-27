package scorenotifierplugin.provider;

import java.util.regex.Pattern;

public class ScoreNode {

	private int totalRuns = -1;
	private int runsOfBall = -1;
	private int balls;
	private int wickets;
	private boolean wicket;
	private boolean extra;
	private boolean endOfOver;
	private final IScoreParser scoreParser;
	private String matchURL;
	private boolean isInningsStart;
	private boolean isCommentaryNode;
	private boolean isBallUpdate;
	private boolean isNewOver;
	private static String BALLS_REGEX = "([0-9]{1,2}|[0-9]{1,2}\\.[0-6]).*";

	public ScoreNode(String score, ScoreNode oldNode, String matchURL) {
		this(new ScoreParser(score), oldNode, matchURL);
	}

	public ScoreNode(IScoreParser scoreParser, ScoreNode oldNode, String matchURL) {
		this.scoreParser = scoreParser;
		try {
			this.matchURL = matchURL;
			totalRuns = scoreParser.getTotalRuns();
			wickets = scoreParser.getWickets();
			balls = scoreParser.getBalls();
			endOfOver = oldNode != null && balls != 0 && (balls % 10 == 6 || balls % 10 == 0);
			isInningsStart = totalRuns == 0 && balls == 0 && wickets == 0;

			if (oldNode != null) {
				isCommentaryNode = oldNode.getScore().equals(scoreParser.getScore());
				String commentary = getCommentary();
				if (commentary != null) {
					isBallUpdate = isCommentaryNode && Pattern.matches(BALLS_REGEX, commentary);
				}
				if (!isInningsStart && totalRuns != -1) {
					int ballsDiff = balls - oldNode.balls;
					if (isCommentaryNode) {
						runsOfBall = oldNode.getRunsOfBall();
						extra = oldNode.isExtra();
						wicket = oldNode.isWicket();
					} else if (ballsDiff <= 1 || endOfOver) {
						runsOfBall = totalRuns - oldNode.getTotalRuns();
						extra = ballsDiff == 0;
						wicket = wickets - oldNode.getWickets() > 0;
					}
					isNewOver = ballsDiff >= 10;
				}
			}
			isNewOver = isNewOver || isInningsStart;
			endOfOver = endOfOver && !extra;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isExtra() {
		return extra;
	}
	
	public boolean isNewOver() {
		return isNewOver;
	}

	public boolean isWicket() {
		return wicket;
	}

	public int getTotalRuns() {
		return totalRuns;
	}

	public int getBalls() {
		return balls;
	}

	public String getScore() {
		return scoreParser.getScore();
	}

	public int getRunsOfBall() {
		return runsOfBall;
	}

	public int getWickets() {
		return wickets;
	}

	public boolean isCommentaryNode() {
		return isCommentaryNode;
	}

	public boolean isBallUpdate() {
		return isBallUpdate;
	}
	
	public boolean isFour() {
		return isExtra() ? runsOfBall == 5 : runsOfBall == 4;
	}

	public boolean isSix() {
		return isExtra() ? runsOfBall == 7 : runsOfBall == 6;
	}

	public boolean isBoundary() {
		return isFour() || isSix();
	}

	@Override
	public String toString() {
		if (isWicket()) {
			if (runsOfBall != 0) {
				return runsOfBall + "W";
			}
			return "W";
		} else if (isExtra()) {
			return runsOfBall + "x";
		} else if (runsOfBall == -1) {
			return "-";
		}
		return "" + runsOfBall;
	}

	public String getDecoration() {
		String decoration = "";
		if (isSix()) {
			decoration = "It's a Six";
		} else if (isFour()) {
			decoration = "Four";
		} else if (isWicket()) {
			if (runsOfBall != 0) {
				if (isExtra()) {
					decoration = "Stumped!!";
				} else {
					decoration = "Run out!!";
				}
			} else {
				decoration = "Wicket!!!";
			}
		} else if (runsOfBall == 0) {
			decoration = "Dot Ball";
		} else if (runsOfBall == -1) {
			decoration = "-";
		} else {
			decoration = "" + runsOfBall;
		}

		if (isCommentaryNode) {
			decoration = "Commentary:";
		} else if (isExtra()) {
			decoration = decoration + " (Extra)";
		}
		return decoration;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ScoreNode) {
			return ((ScoreNode) obj).getScore().equals(getScore());
		}
		return super.equals(obj);
	}

	public String getBatsMenStats() {
		return scoreParser.getBatsMenStats();
	}

	public String getCommentary() {
		return scoreParser.getCommentary();
	}

	public String getBowlerStats() {
		return scoreParser.getBowlerStats();
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public boolean isEndOfOver() {
		return endOfOver;
	}

	public String getMatchURL() {
		return matchURL;
	}

}
