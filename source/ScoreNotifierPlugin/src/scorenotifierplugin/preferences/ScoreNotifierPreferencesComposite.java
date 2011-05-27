package scorenotifierplugin.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import scorenotifierplugin.ScoreNotifierActivator;

public class ScoreNotifierPreferencesComposite extends Composite {

	private ScoreNotifierPreferences preferences = ScoreNotifierActivator.getDefault().getPreferences();
	private Button logBallsButton;
	private Button notifyBallButton;
	private Button notifyRunButton;
	private Button notifyOverButton;
	private Button notifyBoundaryButton;
	private Button notifyWicketButton;
	private Button notifyExtrasButton;
	private Button invertNotificationsButton;
	private Spinner notificationOffsetSpinner;
	private Spinner notificationDisplaySpinner;
	private Button notifyCommentaryButton;

	
	public ScoreNotifierPreferencesComposite(Composite parent, int style) {
		super(parent, style);
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		setLayout(layout);
		createContents(this);
	}

	protected void createContents(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout());
		GridData groupData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		group.setLayoutData(groupData);
		group.setText("Preferences");

		invertNotificationsButton = createButton(group, "Show notifications on top");

		Composite spinnersComposite = new Composite(group, SWT.NONE);
		GridData spinnersData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		spinnersComposite.setLayoutData(spinnersData);
		spinnersComposite.setLayout(new GridLayout(2, false));

		Label offsetLabel = new Label(spinnersComposite, SWT.NONE);
		offsetLabel.setText("Offset: ");
		offsetLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
		notificationOffsetSpinner = new Spinner(spinnersComposite, SWT.READ_ONLY | SWT.BORDER);
		notificationOffsetSpinner.setIncrement(10);
		notificationOffsetSpinner.setMinimum(0);
		notificationOffsetSpinner.setMaximum(Display.getDefault().getClientArea().height);
		notificationOffsetSpinner.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

		Label displayTimeLabel = new Label(spinnersComposite, SWT.NONE);
		displayTimeLabel.setText("Display Time: ");
		displayTimeLabel.setToolTipText("The amount of time(seconds) for which the notification has to be displayed");
		displayTimeLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
		notificationDisplaySpinner = new Spinner(spinnersComposite, SWT.READ_ONLY | SWT.BORDER);
		notificationDisplaySpinner.setIncrement(1);
		notificationDisplaySpinner.setMinimum(1);
		notificationDisplaySpinner.setMaximum(600);
		notificationDisplaySpinner.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

		logBallsButton = createButton(group, "Activate Score Card for every ball.");

		notifyCommentaryButton = createButton(group, "Show updates in commentary.");

		Label label = new Label(group, SWT.NONE);
		label.setText("Show notifications for");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

		notifyBallButton = createButton(group, "Every Ball");
		notifyBoundaryButton = createButton(group, "Every Boundary");
		notifyExtrasButton = createButton(group, "Every Extra");
		notifyOverButton = createButton(group, "Every Over");
		notifyRunButton = createButton(group, "Every Run");
		notifyWicketButton = createButton(group, "Every Wicket");

		loadValues();
		attachListeners();
	}

	private void loadValues() {
		invertNotificationsButton.setSelection(preferences.isInvertNotificationLocation());
		notificationOffsetSpinner.setSelection(preferences.getNotifierOffset());
		notificationDisplaySpinner.setSelection(preferences.getNotifierDispalayTime() / 1000);

		notifyBallButton.setSelection(preferences.notifyEveryBall());
		notifyRunButton.setSelection(preferences.notifyEveryRun());
		notifyOverButton.setSelection(preferences.notifyEveryOver());
		notifyBoundaryButton.setSelection(preferences.notifyEveryBoundary());
		notifyWicketButton.setSelection(preferences.notifyEveryWicket());
		notifyExtrasButton.setSelection(preferences.notifyEveryExtra());
		logBallsButton.setSelection(preferences.logEveryBall());
		notifyCommentaryButton.setSelection(preferences.notifyCommentaryUpdate());
		updateAllButtons();
	}

	private void attachListeners() {
		SelectionListener listener = new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				updateAllButtons();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		};
		notifyBallButton.addSelectionListener(listener);
		notifyRunButton.addSelectionListener(listener);
	}

	private Button createButton(Composite parent, String label) {
		Button checkButton = new Button(parent, SWT.CHECK);
		checkButton.setText(label);
		checkButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
		return checkButton;
	}

	private void updateAllButtons() {
		notifyRunButton.setEnabled(!notifyBallButton.getSelection());
		notifyOverButton.setEnabled(!notifyBallButton.getSelection());
		notifyBoundaryButton.setEnabled(!notifyBallButton.getSelection() && !notifyRunButton.getSelection());
		notifyWicketButton.setEnabled(!notifyBallButton.getSelection());
		notifyExtrasButton.setEnabled(!notifyBallButton.getSelection() && !notifyRunButton.getSelection());
	}

	void performDefaults() {
		invertNotificationsButton.setSelection(preferences.defaultInvertNotificationLocation());
		notificationOffsetSpinner.setSelection(preferences.defaultNotifierOffset());
		notificationDisplaySpinner.setSelection(preferences.defaultNotifierDispalayTime() / 1000);

		logBallsButton.setSelection(preferences.defaultLogEveryBall());
		notifyBallButton.setSelection(preferences.defaultNotifyEveryBall());
		notifyBoundaryButton.setSelection(preferences.defaultNotifyEveryBoundary());
		notifyOverButton.setSelection(preferences.defaultNotifyEveryOver());
		notifyRunButton.setSelection(preferences.defaultNotifyEveryRun());
		notifyWicketButton.setSelection(preferences.defaultNotifyEveryWicket());
		notifyExtrasButton.setSelection(preferences.defaultNotifyEveryExtra());
		notifyCommentaryButton.setSelection(preferences.defaultNotifyCommentaryUpdate());

		updateAllButtons();
	}

	@SuppressWarnings("deprecation")
	public void performOk() {
		preferences.setInvertNotificationLocation(invertNotificationsButton.getSelection());
		preferences.setNotifierOffset(notificationOffsetSpinner.getSelection());
		preferences.setNotifierDispalayTime(notificationDisplaySpinner.getSelection() * 1000);
		
		preferences.setLogEveryBall(logBallsButton.getSelection());
		preferences.setNotifyEveryBall(notifyBallButton.getSelection());
		preferences.setNotifyEveryBoundary(notifyBoundaryButton.getSelection());
		preferences.setNotifyEveryOver(notifyOverButton.getSelection());
		preferences.setNotifyEveryRun(notifyRunButton.getSelection());
		preferences.setNotifyEveryWicket(notifyWicketButton.getSelection());
		preferences.setNotifyEveryExtra(notifyExtrasButton.getSelection());
		preferences.setNotifyCommentaryUpdate(notifyCommentaryButton.getSelection());
		ScoreNotifierActivator.getDefault().savePluginPreferences();
	}


}
