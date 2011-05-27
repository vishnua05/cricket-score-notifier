package scorenotifierplugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;

public class ScoreCardView extends ViewPart {

	public static final String ID = "scorenotifierplugin.ScoreCardView";
	private static Map<String, List<String>> logsMap = new HashMap<String, List<String>>();
	private Map<String, TabItem> textTabsMap = new HashMap<String, TabItem>();
	private static final int LOGS_SIZE = 100;
	private TabFolder container;
	
	private static final String NEWLINE = System.getProperty("line.separator");
	
	@Override
	public void createPartControl(Composite parent) {
         parent.setLayout(new GridLayout());
         container = new TabFolder(parent, SWT.NONE);
         container.setLayout(new GridLayout());
         container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}
	

	public void clearConsole(String matchURL) {
		List<String> matchLogs = logsMap.get(matchURL);
		if (matchLogs != null) {
			matchLogs.clear();
			Text text = (Text) getTextTab(matchURL).getControl();
			text.setText("");
		}
	}
	
	

	private TabItem getTextTab(String matchURL) {
		TabItem textTab = textTabsMap.get(matchURL);
		if (textTab == null) {
			textTab = new TabItem(container, SWT.NONE);
			String displayName = ScoreNotifier.getDisplayName(matchURL);
			textTab.setText(displayName);
			textTab.setToolTipText(displayName);
			FormToolkit formToolkit = new FormToolkit(Display.getDefault());
			Text text = formToolkit.createText(container, null, SWT.READ_ONLY | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL);
			text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			textTab.setControl(text);
			textTabsMap.put(matchURL, textTab);
		}
		return textTab;
	}
	
	private void disposeTextTab(String matchURL) {
        logsMap.remove(matchURL);
        TabItem textTab = getTextTab(matchURL);
        if (textTab != null) {
        	textTab.dispose();
        }
        textTabsMap.remove(matchURL);
        container.layout();
	}
	
	
	@Override
	public void setFocus() {
 
	}
	
	public void appendText(String matchURL, String newLog, String score) {
		if (newLog == null) {
			disposeTextTab(matchURL);
			container.pack();
		} else {
			List<String> logs = logsMap.get(matchURL);
			if (logs == null) {
				logs = new ArrayList<String>();
				logsMap.put(matchURL, logs);
			}
			if (logs.size() == LOGS_SIZE) {
				logs.remove(0);
			}
			logs.add(newLog);
			StringBuffer logBuffer = new StringBuffer();
			for (String log : logs) {
				logBuffer.insert(0,NEWLINE);
				logBuffer.insert(0, log);
			}
			TabItem textTab = getTextTab(matchURL);
			Text text = (Text) textTab.getControl();
			if (score != null) {
				textTab.setText(score);
			}
			text.setText(logBuffer.toString());
		}
	}
	
	public static ScoreCardView getScoreCardView() {
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart findView = activePage.findView(ID);
		return (ScoreCardView) findView;
	}
	
	public static void showView() {
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        try {
			activePage.showView(ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

}
