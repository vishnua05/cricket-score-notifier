package scorenotifierplugin.provider;

public interface IScoreParser {

	String REGEX = ".* ([0-9]{1,3})/([0-9]{1,2}) .*\\(([0-9]*\\.[0-6]).*";
	String REGEX_BATTING_TEAM = ".*Batting: (.*)</p>.*";
    String REGEX_RESULT = ".*Result:(.*)</p>.*";
	String REGEX_BATSMEN = ".*<p>(.*) (notout|batting)( ([0-9]{1,3})\\(([0-9]{1,3})\\)).*";
	String REGEX_TOTAL = ".*Total:.* ([0-9]{1,3})/([0-9]{1,2}).* ([0-9]{1,2}|[0-9]{1,2}\\.[0-6]) .*";
    String REGEX_BOWLERS = ".*<p>(.*)( [0-9]{1,2}.*-[0-9]{1,2}-[0-9]{1,2}).*";
    String REGEX_START_TIME = ".*<p>(.* ([0-9]*:[0-9]*) .*)</p>.*"; 
	String REGEX_COMMENTARY = ".*<p><!\\[CDATA\\[(.*)\\]\\]></p>.*"; 	


    String BATTING_TEAM = "batting_team";
    String BATSMEN_STATS = "batsmen_stat";
    String TOTAL = "Total";
    String BOWLER_STATS = "bowler_stats";
    String RESULT = "result";
    String START_TIME = "start_time";
    String BATSMEN_DELIMITER = "$";
    String COMMENTARY = "commentary";
	
    String getScore();

	int getTotalRuns();

	int getWickets();

	int getBalls();
	
	String getBatsMenStats();
	
	String getBowlerStats();
	
	String getCommentary();

}