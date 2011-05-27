package scorenotifierplugin.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

public class ScoreNotifierPreferences {

	private static final String LOG_EVERY_BALL = "log_every_ball";
	private static final String NOTIFY_EVERY_BALL = "notify_every_ball";
	private static final String NOTIFY_EVERY_BOUNDARY = "notify_every_boundary";
	private static final String NOTIFY_EVERY_EXTRA = "notify_every_extra";
	private static final String NOTIFY_EVERY_OVER = "notify_every_over";
	private static final String NOTIFY_EVERY_RUN = "notify_every_run";
	private static final String NOTIFY_EVERY_WICKET = "notify_every_wicket";
	private static final String INVERT_NOTIFICATION_LOCATION = "invert_notification_location";
	private static final String NOTIFIER_OFFSET = "notifier_offset";
	private static final String NOTIFIER_DISPLAY_TIME = "notifier_display_time";
	private static final String NOTIFIER_COMMENTARY_UPDATE = "notifier_commentary_update";


	private static final boolean DEFAULT_LOG_EVERY_BALL = true;
	private static final boolean DEFAULT_NOTIFY_EVERY_BALL = true;
	private static final boolean DEFAULT_NOTIFY_EVERY_BOUNDARY = true;
	private static final boolean DEFAULT_NOTIFY_EVERY_EXTRA = false;
	private static final boolean DEFAULT_NOTIFY_EVERY_OVER = true;
	private static final boolean DEFAULT_NOTIFY_EVERY_RUN = false;
	private static final boolean DEFAULT_NOTIFY_EVERY_WICKET = true;
	private static final boolean DEFAUTL_INVERT_NOTIFICATION_LOCATION = true;
	private static final int DEFAUTL_NOTIFIER_OFFSET = getDefaultOffset();
	private static final int DEFAUTL_NOTIFIER_DISPLAY_TIME = 10000;
	private static final boolean DEFAUTL_NOTIFIER_COMMENTARY_UPDATE  = true;



	private final IPreferenceStore store;

	public ScoreNotifierPreferences(IPreferenceStore store) {
		this.store = store;
	}

	public boolean logEveryBall() {
		return store.getBoolean(LOG_EVERY_BALL);
	}

	boolean defaultLogEveryBall() {
		return store.getDefaultBoolean(LOG_EVERY_BALL);
	}

	void setLogEveryBall(boolean log) {
		store.setValue(LOG_EVERY_BALL, log);
	}

	public boolean notifyEveryBall() {
		return store.getBoolean(NOTIFY_EVERY_BALL);
	}

	boolean defaultNotifyEveryBall() {
		return store.getDefaultBoolean(NOTIFY_EVERY_BALL);
	}

	void setNotifyEveryBall(boolean notify) {
		store.setValue(NOTIFY_EVERY_BALL, notify);
	}

	public boolean notifyEveryRun() {
		return store.getBoolean(NOTIFY_EVERY_RUN);
	}

	boolean defaultNotifyEveryRun() {
		return store.getDefaultBoolean(NOTIFY_EVERY_RUN);
	}

	void setNotifyEveryRun(boolean notify) {
		store.setValue(NOTIFY_EVERY_RUN, notify);
	}

	public boolean notifyEveryOver() {
		return store.getBoolean(NOTIFY_EVERY_OVER);
	}

	boolean defaultNotifyEveryOver() {
		return store.getDefaultBoolean(NOTIFY_EVERY_OVER);
	}

	void setNotifyEveryOver(boolean notify) {
		store.setValue(NOTIFY_EVERY_OVER, notify);
	}

	public boolean notifyEveryBoundary() {
		return store.getBoolean(NOTIFY_EVERY_BOUNDARY);
	}

	boolean defaultNotifyEveryBoundary() {
		return store.getDefaultBoolean(NOTIFY_EVERY_BOUNDARY);
	}

	void setNotifyEveryBoundary(boolean notify) {
		store.setValue(NOTIFY_EVERY_BOUNDARY, notify);
	}

	public boolean notifyEveryWicket() {
		return store.getBoolean(NOTIFY_EVERY_WICKET);
	}

	boolean defaultNotifyEveryWicket() {
		return store.getDefaultBoolean(NOTIFY_EVERY_WICKET);
	}

	void setNotifyEveryWicket(boolean notify) {
		store.setValue(NOTIFY_EVERY_WICKET, notify);
	}

	public boolean notifyEveryExtra() {
		return store.getBoolean(NOTIFY_EVERY_EXTRA);
	}

	boolean defaultNotifyEveryExtra() {
		return store.getDefaultBoolean(NOTIFY_EVERY_EXTRA);
	}

	void setNotifyEveryExtra(boolean notify) {
		store.setValue(NOTIFY_EVERY_EXTRA, notify);
	}

	public boolean isInvertNotificationLocation() {
		return store.getBoolean(INVERT_NOTIFICATION_LOCATION);
	}
	
	boolean defaultInvertNotificationLocation() {
		return store.getDefaultBoolean(INVERT_NOTIFICATION_LOCATION);
	}

	public void setInvertNotificationLocation(boolean invertLocation) {
		store.setValue(INVERT_NOTIFICATION_LOCATION, invertLocation);
	}

	public void setNotifierOffset(int notifierOffset) {
		store.setValue(NOTIFIER_OFFSET, notifierOffset);
	}

	int defaultNotifierOffset() {
		return store.getDefaultInt(NOTIFIER_OFFSET);
	}
	
	public int getNotifierOffset() {
		return store.getInt(NOTIFIER_OFFSET);
	}
	
	public void setNotifierDispalayTime(int notifierDispalayTime) {
		store.setValue(NOTIFIER_DISPLAY_TIME, notifierDispalayTime);
	}

	int defaultNotifierDispalayTime() {
		return store.getDefaultInt(NOTIFIER_DISPLAY_TIME);
	}
	
	public int getNotifierDispalayTime() {
		return store.getInt(NOTIFIER_DISPLAY_TIME);
	}
	

	public boolean notifyCommentaryUpdate() {
		return store.getBoolean(NOTIFIER_COMMENTARY_UPDATE);
	}
	
	boolean defaultNotifyCommentaryUpdate() {
		return store.getDefaultBoolean(NOTIFIER_COMMENTARY_UPDATE);
	}

	public void setNotifyCommentaryUpdate(boolean notifyCommentaryUpdate) {
		store.setValue(NOTIFIER_COMMENTARY_UPDATE, notifyCommentaryUpdate);
	}
	
	public void setDefaults() {
		store.setDefault(LOG_EVERY_BALL, DEFAULT_LOG_EVERY_BALL);
		store.setDefault(NOTIFY_EVERY_BALL, DEFAULT_NOTIFY_EVERY_BALL);
		store.setDefault(NOTIFY_EVERY_BOUNDARY, DEFAULT_NOTIFY_EVERY_BOUNDARY);
		store.setDefault(NOTIFY_EVERY_EXTRA, DEFAULT_NOTIFY_EVERY_EXTRA);
		store.setDefault(NOTIFY_EVERY_OVER, DEFAULT_NOTIFY_EVERY_OVER);
		store.setDefault(NOTIFY_EVERY_RUN, DEFAULT_NOTIFY_EVERY_RUN);
		store.setDefault(NOTIFY_EVERY_WICKET, DEFAULT_NOTIFY_EVERY_WICKET);
		store.setDefault(NOTIFIER_OFFSET, DEFAUTL_NOTIFIER_OFFSET);
		store.setDefault(INVERT_NOTIFICATION_LOCATION, DEFAUTL_INVERT_NOTIFICATION_LOCATION);
        store.setDefault(NOTIFIER_DISPLAY_TIME, DEFAUTL_NOTIFIER_DISPLAY_TIME);
        store.setDefault(NOTIFIER_COMMENTARY_UPDATE, DEFAUTL_NOTIFIER_COMMENTARY_UPDATE);
	}

	private static int getDefaultOffset() {
		String os = System.getProperty("os.name").toLowerCase();
		boolean isLinux = os.indexOf("nux") >= 0;
		if (isLinux) {
			Rectangle clientArea = Display.getDefault().getClientArea();
			return clientArea.height / 25;
		}
		return 0;
	}
	
	public IPreferenceStore getStore() {
		return store;
	}

}
