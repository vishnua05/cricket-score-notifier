package scorenotifierplugin;

public interface ILogger {
	void log(String matchURL, String log, String score);
	void log(String matchURL, String log);
	void clearStatusBar();
	void updateStatusBar(String score);
}
