package scorenotifierplugin.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import scorenotifierplugin.ScoreNotifierActivator;

public class ScoreNotifierPreferencesInitializer extends AbstractPreferenceInitializer {

	public ScoreNotifierPreferencesInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
         ScoreNotifierActivator.getDefault().getPreferences().setDefaults();
	}

}
