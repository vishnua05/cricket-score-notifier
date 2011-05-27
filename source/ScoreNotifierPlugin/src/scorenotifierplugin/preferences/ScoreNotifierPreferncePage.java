package scorenotifierplugin.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import scorenotifierplugin.ScoreNotifierActivator;

public class ScoreNotifierPreferncePage extends PreferencePage implements IWorkbenchPreferencePage {

	private ScoreNotifierPreferencesComposite scoreNotifierPreferencesComposite;
	
	private ScoreNotifierPreferences preferences;

	public ScoreNotifierPreferncePage() {
		setDescription("Settings for Score Notifications");
	}

	public void init(IWorkbench workbench) {
		this.preferences = ScoreNotifierActivator.getDefault().getPreferences();
		setPreferenceStore(preferences.getStore());
	}

	protected Control createContents(Composite parent) {
		initializeDialogUnits(parent);
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        scoreNotifierPreferencesComposite = new ScoreNotifierPreferencesComposite(parent, SWT.NONE);

        return scoreNotifierPreferencesComposite;
	}


	@Override
	protected void performDefaults() {
		scoreNotifierPreferencesComposite.performDefaults();
		super.performDefaults();
	}

	@Override
	public boolean performOk() {
		scoreNotifierPreferencesComposite.performOk();
		return super.performOk();
	}

}
