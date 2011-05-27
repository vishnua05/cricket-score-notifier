package scorenotifierplugin;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import scorenotifierplugin.preferences.ScoreNotifierPreferences;

/**
 * The activator class controls the plug-in life cycle
 */
public class ScoreNotifierActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "ScoreCardPlugin";
	
	private ScoreNotifierPreferences preferences;

	// The shared instance
	private static ScoreNotifierActivator plugin;
	
	/**
	 * The constructor
	 */
	public ScoreNotifierActivator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		preferences = new ScoreNotifierPreferences(getPreferenceStore());
		preferences.setDefaults();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		preferences = null;
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ScoreNotifierActivator getDefault() {
		return plugin;
	}
	
	public void logException(String message, Exception e) {
		message = message == null ? e.getMessage() : message;
		getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, message, e));
	}
	
	public ScoreNotifierPreferences getPreferences() {
		return preferences;
	}

	public void logException(IOException e) {
        logException(null, e);		
	}

}
