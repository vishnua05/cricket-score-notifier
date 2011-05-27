package scorenotifierplugin.preferences;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class ScoreNotifierPreferencesDialog extends Dialog{

	public ScoreNotifierPreferencesDialog(Shell parentShell) {
		super(parentShell);
	}

	private ScoreNotifierPreferencesComposite scoreNotifierPreferencesComposite;
	
	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Score Notifier Preferences");
		scoreNotifierPreferencesComposite = new ScoreNotifierPreferencesComposite(parent, SWT.NONE);
		return scoreNotifierPreferencesComposite;
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CLOSE_ID) {
			scoreNotifierPreferencesComposite.performDefaults();
		} else {
			super.buttonPressed(buttonId);
		}
	}
	
	@Override
	protected void okPressed() {
		scoreNotifierPreferencesComposite.performOk();
		super.okPressed();
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		createButton(parent, IDialogConstants.CLOSE_ID, "Restore Defaults", false);
	}
	
}
