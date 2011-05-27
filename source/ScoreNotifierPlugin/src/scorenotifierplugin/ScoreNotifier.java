package scorenotifierplugin;

import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.widgets.Display;

import scorenotifierplugin.preferences.ScoreNotifierPreferences;
import scorenotifierplugin.provider.EndOfScoreException;
import scorenotifierplugin.provider.IScoreParser;
import scorenotifierplugin.provider.IScoreProvider;
import scorenotifierplugin.provider.ScoreNode;
import scorenotifierplugin.util.NotifierDialog;
import scorenotifierplugin.util.ScoreEvent;

public class ScoreNotifier {

	public static final String NONE_AVAILABLE = "No Live Matches Avaiable";
	protected static final String DELIMITER = ", ";
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");
	private ILogger logger;

	private IScoreProvider scoreProvider;
	private Set<String> activeURLs = new LinkedHashSet<String>();
	private Map<String, List<ScoreNode>> overNodesMap = new HashMap<String, List<ScoreNode>>();
	private Map<String, TimerTask> scheduledTasks = new HashMap<String, TimerTask>();

	public ScoreNotifier(IScoreProvider scoreProvider) {
		this.scoreProvider = scoreProvider;
		this.logger = new ViewLogger();
	}

	public List<String> getAvailableURLs() {
		return scoreProvider.getMatchURLs();
	}

	public Set<String> getActiveURLs() {
		return new HashSet<String>(activeURLs);
	}

	public static String getDisplayName(String matchURL) {
		int lastIndexOf = matchURL.lastIndexOf("/");
		String displayName = lastIndexOf != -1 ? matchURL.substring(lastIndexOf + 1, matchURL.length()) : matchURL;
		if (displayName.trim().length() == 0) {
			displayName = "Sample Match";
		}
		return displayName;
	}

	public void toggleNotifier(String matchURL) {
		if (!matchURL.equals(NONE_AVAILABLE)) {
			if (!activeURLs.contains(matchURL)) {
				start(matchURL);
			} else {
				stop(matchURL);
			}
		}
	}

	private void start(final String matchURL) {
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				ScoreNotifierPreferences preferences = ScoreNotifierActivator.getDefault().getPreferences();
				final ScoreNode scoreNode;
				try {
					scoreNode = scoreProvider.getScore(matchURL);
				} catch (EndOfScoreException e) {
					stop(matchURL);
					return;
				} catch (Exception e) {
					if (!(e instanceof SocketTimeoutException)) {
						stop(matchURL);
						ScoreNotifierActivator.getDefault().logException(null, e);
					}
					return;
				}

				if (scoreNode != null && activeURLs.contains(matchURL)) {
					List<ScoreNode> overNodes = overNodesMap.get(matchURL);
					if (overNodes == null) {
						overNodes = new ArrayList<ScoreNode>();
						overNodesMap.put(matchURL, overNodes);
					}
					if (scoreNode.isNewOver()) {
						overNodes.clear();
					} else {
						overNodes.add(scoreNode);
					}
					String date = "[" + dateFormatter.format(new Date()) + "]: ";
					final String commentary = scoreNode.getCommentary();
					if (scoreNode.isCommentaryNode()) {
						logger.log(matchURL, date + commentary);
					}
					String log = date + scoreNode.getScore();
					if (!scoreNode.isCommentaryNode()) {
						if (scoreNode.getRunsOfBall() != -1) {
							log = log + "  {" + scoreNode.getRunsOfBall() + "}";
						}
						logger.log(matchURL, log, scoreNode.getScore());
						if (scoreNode.isEndOfOver()) {

							String batsMenStats = scoreNode.getBatsMenStats();
							if (batsMenStats != null) {
								StringTokenizer stringTokenizer = new StringTokenizer(batsMenStats, IScoreParser.BATSMEN_DELIMITER);
								while (stringTokenizer.hasMoreElements()) {
									logger.log(matchURL, stringTokenizer.nextToken());
								}
							}
							String bowlerStats = scoreNode.getBowlerStats();
							if (bowlerStats != null) {
								logger.log(matchURL, bowlerStats);
							}
							String overStats = getOverStats(overNodes);
							if (overStats != null) {
								logger.log(matchURL, overStats);
							}
							logger.log(matchURL, "--------------------------------------------------------End of Over-----------------------------------------------------");
						}
					}

					boolean canNotify = preferences.notifyEveryBall();
					canNotify = canNotify || preferences.notifyEveryRun() && scoreNode.getRunsOfBall() > 0;
					canNotify = canNotify || preferences.notifyEveryBoundary() && scoreNode.isBoundary();
					canNotify = canNotify || preferences.notifyEveryOver() && scoreNode.isEndOfOver();
					canNotify = canNotify || preferences.notifyEveryWicket() && scoreNode.isWicket();
					canNotify = canNotify || preferences.notifyEveryExtra() && scoreNode.isExtra();
					if (scoreNode.isBallUpdate()) {
						canNotify = canNotify && preferences.notifyCommentaryUpdate();
					}

					final ScoreEvent[] events = new ScoreEvent[1];
					if (scoreNode.isFour()) {
						events[0] = ScoreEvent.FOUR;
					} else if (scoreNode.isSix()) {
						events[0] = ScoreEvent.SIX;
					} else if (scoreNode.isWicket()) {
						events[0] = ScoreEvent.WICKET;
					} else if (scoreNode.isEndOfOver()) {
						events[0] = ScoreEvent.END_OF_OVER;
					} else if (scoreNode.getRunsOfBall() == 0) {
						events[0] = ScoreEvent.DOT_BALL;
					} else {
						events[0] = ScoreEvent.NONE;
					}

					if (canNotify) {
						final ScoreNode notifierNode = scoreNode;
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								int index = 0;
								for (String activeURL : activeURLs) {
									if (activeURL.equals(matchURL)) {
										break;
									}
									index++;
								}
								NotifierDialog.notify(notifierNode, getOverStats(overNodesMap.get(matchURL)), events[0], index % 2 == 1);
							}
						});
					}

					if (scoreNode.isEndOfOver()) {
						overNodes.clear();
					}

				}

			}

		};

		activeURLs.add(matchURL);
		new Timer().schedule(timerTask, 0, 2500);
		scheduledTasks.put(matchURL, timerTask);
		logger.log(matchURL, "Started receiving Score Notifications from: " + getDisplayName(matchURL));
	}

	private String getOverStats(List<ScoreNode> overNodes) {
		boolean hasStats = false;
		StringBuffer sb = new StringBuffer("This Over [");
		if (overNodes.size() > 0) {
			ScoreNode firstNode = overNodes.get(0);
			ScoreNode lastNode = overNodes.get(overNodes.size() - 1);
			int runsOfOver = lastNode.getTotalRuns() - firstNode.getTotalRuns();
			if (firstNode.getRunsOfBall() != -1) {
				runsOfOver = runsOfOver + firstNode.getRunsOfBall();
			}
			int wicketsOfOver = lastNode.getWickets() - firstNode.getWickets() + (firstNode.isWicket() ? 1 : 0);
			sb.append(runsOfOver);
			if (wicketsOfOver != 0) {
				sb.append("/");
				sb.append(wicketsOfOver);
			}
			sb.append("]:   ");
		}

		for (int i = 0; i < overNodes.size(); i++) {
			ScoreNode currentNode = overNodes.get(i);
			if (!currentNode.isCommentaryNode() && currentNode.getRunsOfBall() != -1) {
				hasStats = true;
			}
			if (!currentNode.isCommentaryNode()) {
				sb.append(currentNode);
				if (i != overNodes.size() - 1) {
					sb.append(DELIMITER);
				}
			}
		}
		return hasStats ? sb.toString() : null;
	}

	private void stop(String matchURL) {
		TimerTask timerTask = scheduledTasks.get(matchURL);
		timerTask.cancel();
		overNodesMap.remove(matchURL);
		scoreProvider.cleanupNodes(matchURL);
		activeURLs.remove(matchURL);
		logger.log(matchURL, "Stopped receiving Score Notifications from: " + getDisplayName(matchURL));
		// pass null to indicate end of logging
		logger.log(matchURL, null);
	}

	public void refreshLiveMatches() {
		scoreProvider.refreshLiveMatches();
	}
}
