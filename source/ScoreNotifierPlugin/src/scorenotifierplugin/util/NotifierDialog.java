package scorenotifierplugin.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;

import scorenotifierplugin.ScoreNotifierActivator;
import scorenotifierplugin.preferences.ScoreNotifierPreferences;
import scorenotifierplugin.provider.IScoreParser;
import scorenotifierplugin.provider.ScoreNode;
import scorenotifierplugin.provider.TestScoreProvider;

public class NotifierDialog {

	// how long the the tray popup is displayed after fading in (in
	// milliseconds)
	private static int DISPLAY_TIME = 20000;
	// how long each tick is when fading in (in ms)
	private static final int FADE_TIMER = 50;
	// how long each tick is when fading out (in ms)
	private static final int FADE_IN_STEP = 30;
	// how many tick steps we use when fading out
	private static final int FADE_OUT_STEP = 8;

	// how high the alpha value is when we have finished fading in
	private static final int FINAL_ALPHA = 225;

	// contains list of all active popup shells
	private static List<Shell> rightActiveShells = new ArrayList<Shell>();
	private static List<Shell> leftActiveShells = new ArrayList<Shell>();


	// image used when drawing
	private static Image _oldImage;

	/**
	 * Creates and shows a notification dialog with a specific title, message
	 * and a
	 * @param b 
	 * 
	 * @param title
	 * @param message
	 * @param type
	 */
	public static void notify(ScoreNode scoreNode, final String overStats, final ScoreEvent scoreEvent, boolean toggle) {
		final Shell thisShell = new Shell(Display.getDefault().getActiveShell(), SWT.NO_FOCUS | SWT.ON_TOP);
		thisShell.setText(scoreNode.getScore());
		thisShell.setLayout(new FillLayout());
		thisShell.setBackgroundMode(SWT.INHERIT_DEFAULT);
		thisShell.addListener(SWT.Dispose, new Listener() {
			public void handleEvent(Event event) {
				rightActiveShells.remove(thisShell);
			}
		});

		final Composite container = new Composite(thisShell, SWT.NONE);

		GridLayout containerLayout = new GridLayout(2, false);
		containerLayout.marginLeft = 5;
		containerLayout.marginTop = 0;
		containerLayout.marginRight = 5;
		containerLayout.marginBottom = 5;
		containerLayout.verticalSpacing = 0;

		container.setLayout(containerLayout);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		thisShell.addListener(SWT.Resize, new Listener() {

			public void handleEvent(Event e) {
				try {
					// get the size of the drawing area
					Rectangle rect = thisShell.getClientArea();
					// create a new image with that size
					Image newImage = new Image(Display.getDefault(), Math.max(1, rect.width), rect.height);
					// create a GC object we can use to draw with
					GC gc = new GC(newImage);

					// fill background
					gc.setForeground(ColorMapper.getForeGround(scoreEvent));
					gc.setBackground(ColorMapper.getBackGround(scoreEvent));
					gc.fillGradientRectangle(rect.x, rect.y, rect.width, rect.height, true);

					// draw shell edge
					gc.setLineWidth(2);
					gc.setForeground(ColorMapper.getBorder(scoreEvent));
					gc.drawRectangle(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2);
					// remember to dipose the GC object!
					gc.dispose();

					// now set the background image on the shell
					thisShell.setBackgroundImage(newImage);

					// remember/dispose old used iamge
					if (_oldImage != null) {
						_oldImage.dispose();
					}
					_oldImage = newImage;
				} catch (Exception err) {
					err.printStackTrace();
				}
			}
		});

		Composite header = new Composite(container, SWT.NONE);
		GridData headerLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		headerLayoutData.horizontalSpan = 2;
		header.setLayoutData(headerLayoutData);
		GridLayout headerLayout = new GridLayout(2, false);
		headerLayout.marginWidth = 0;
		header.setLayout(headerLayout);

		Label scoreLabel = new Label(header, SWT.NONE);
		scoreLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		scoreLabel.setText(scoreNode.getScore());
		scoreLabel.setForeground(ColorMapper.getFontColor(scoreEvent));
		Font sf = scoreLabel.getFont();
		FontData sfd = sf.getFontData()[0];
		sfd.height = 9;
		sfd.setStyle(SWT.BOLD | SWT.ITALIC);
		scoreLabel.setFont(FontCache.getFont(sfd));
		scoreLabel.setToolTipText(getMultiLine(scoreNode.getCommentary()));

		Hyperlink liveLink = new Hyperlink(header, SWT.NONE);
		liveLink.setUnderlined(true);
		liveLink.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		liveLink.setText("Live");
		liveLink.setForeground(ColorMapper.linkColor);
		liveLink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent event) {
				IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
				try {
					IWebBrowser browser = browserSupport.getExternalBrowser();
					browser.openURL(new URL(TestScoreProvider.cricBuzzURL));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		Font f = liveLink.getFont();
		FontData fd = f.getFontData()[0];
		// fd.setStyle(SWT.BOLD);
		fd.height = 10;
		liveLink.setFont(FontCache.getFont(fd));

		if (scoreNode.getBatsMenStats() != null && !scoreNode.isCommentaryNode()) {
			String batsMenStats = scoreNode.getBatsMenStats();
			StringTokenizer stringTokenizer = new StringTokenizer(batsMenStats, IScoreParser.BATSMEN_DELIMITER);
			while (stringTokenizer.hasMoreElements()) {
				Object object = (Object) stringTokenizer.nextElement();
				createLabel(scoreNode, thisShell, container, object.toString(), false);
			}
		}

		ScoreNotifierPreferences preferences = ScoreNotifierActivator.getDefault().getPreferences();

		if (scoreNode.getDecoration() != "-") {
			String decoration = scoreNode.getDecoration();
			Label messageLabel = new Label(container, SWT.WRAP);
			Font tf = messageLabel.getFont();
			FontData tfd = tf.getFontData()[0];
			tfd.setStyle(SWT.BOLD);
			tfd.height = 11;
			messageLabel.setFont(FontCache.getFont(tfd));
			GridData gd = new GridData(GridData.FILL_BOTH);
			gd.horizontalSpan = 2;
			messageLabel.setLayoutData(gd);
			messageLabel.setForeground(ColorMapper.getFontColor(scoreEvent));
			if (preferences.notifyEveryOver() && !preferences.notifyEveryBall() && !preferences.notifyEveryRun() && scoreNode.isEndOfOver()) {
				decoration = overStats.substring(overStats.indexOf('[') + 1, overStats.indexOf(']'));
			}
			messageLabel.setText(decoration);
			messageLabel.setToolTipText(getMultiLine(scoreNode.getCommentary()));
			messageLabel.addMouseListener(getMouseListener(thisShell));
		}

		if (scoreNode.getCommentary() != null) {
			Label commentaryLabel = new Label(container, SWT.WRAP);
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).hint(200, SWT.DEFAULT).grab(true, false).span(2, 1).applyTo(commentaryLabel);
			commentaryLabel.setText(scoreNode.getCommentary());
			commentaryLabel.setForeground(ColorCache.getColor(139, 69, 0));
			Font cf = commentaryLabel.getFont();
			FontData cfd = cf.getFontData()[0];
			cfd.height = 9;
			cfd.setStyle(SWT.ITALIC);
			commentaryLabel.setFont(FontCache.getFont(cfd));
			commentaryLabel.setToolTipText(getMultiLine(scoreNode.getCommentary()));
			commentaryLabel.addMouseListener(getMouseListener(thisShell));
		}
		
		if (scoreNode.getBowlerStats() != null && !scoreNode.isCommentaryNode()) {
			createLabel(scoreNode, thisShell, container, scoreNode.getBowlerStats(), true);
		}

		if (overStats != null) {
			createLabel(scoreNode, thisShell, container, overStats, false);
		}

		Rectangle clientArea = Display.getDefault().getClientArea();
		boolean invertNotificationLocation = preferences.isInvertNotificationLocation();

		int minWidth = container.computeSize(SWT.DEFAULT, SWT.DEFAULT).x + clientArea.width / 25;
		int minHeight = container.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		thisShell.setSize(minWidth, minHeight);

		int startX = toggle ? 0 : clientArea.width - (minWidth + 2);
		int height_offset = minHeight + 2 + preferences.getNotifierOffset();
		int startY = invertNotificationLocation ? preferences.getNotifierOffset() : clientArea.height - height_offset;
		// move other shells up
		List<Shell> activeShells = toggle ? leftActiveShells : rightActiveShells;
		moveShells(thisShell, clientArea, invertNotificationLocation, activeShells);

		thisShell.setLocation(startX, startY);
		thisShell.setAlpha(0);
		thisShell.setVisible(true);

		if (toggle) {
			leftActiveShells.add(thisShell);
		} else {
			rightActiveShells.add(thisShell);
		}

		container.addMouseListener(getMouseListener(thisShell));
		header.addMouseListener(getMouseListener(thisShell));
		scoreLabel.addMouseListener(getMouseListener(thisShell));
		DISPLAY_TIME = preferences.getNotifierDispalayTime();
		fadeIn(thisShell);
	}

	private static void moveShells(final Shell thisShell, Rectangle clientArea, boolean invertNotificationLocation, List<Shell> shells2Move) {
		if (!shells2Move.isEmpty()) {
			List<Shell> modifiable = new ArrayList<Shell>(shells2Move);
			Collections.reverse(modifiable);
			for (Shell shell : modifiable) {
				if (!shell.isDisposed()) {
					Point curLoc = shell.getLocation();
					int height = thisShell.getBounds().height;
					int y = invertNotificationLocation ? curLoc.y + height : curLoc.y - height;
					shell.setLocation(curLoc.x, y);
					boolean dispose = invertNotificationLocation ? curLoc.y + height > clientArea.height : curLoc.y - height < 0;
					if (dispose) {
						shells2Move.remove(shell);
						shell.dispose();
					}
				}
			}
		}
	}

	private static void createLabel(ScoreNode scoreNode, final Shell thisShell, final Composite container, String labelText, boolean bowler) {
		Label label = new Label(container, SWT.WRAP);
		Font font = label.getFont();
		FontData tlfd = font.getFontData()[0];
		tlfd.height = 10;
		if (bowler) {
			tlfd.setStyle(SWT.NORMAL);
		}
		label.setFont(FontCache.getFont(tlfd));
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		label.setLayoutData(gd);
		label.setText(labelText);
		label.addMouseListener(getMouseListener(thisShell));
		label.setToolTipText(getMultiLine(scoreNode.getCommentary()));
	}

	private static MouseListener getMouseListener(final Shell thisShell) {
		return new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				disposeShell(thisShell);
			}
		};
	}

	private static void fadeIn(final Shell _shell) {
		Runnable run = new Runnable() {

			public void run() {
				try {
					if (_shell == null || _shell.isDisposed()) {
						return;
					}

					int cur = _shell.getAlpha();
					cur += FADE_IN_STEP;

					if (cur > FINAL_ALPHA) {
						_shell.setAlpha(FINAL_ALPHA);
						startTimer(_shell);
						return;
					}

					_shell.setAlpha(cur);
					Display.getDefault().timerExec(FADE_TIMER, this);
				} catch (Exception err) {
					err.printStackTrace();
				}
			}

		};
		Display.getDefault().timerExec(FADE_TIMER, run);
	}

	private static void startTimer(final Shell _shell) {
		Runnable run = new Runnable() {

			public void run() {
				try {
					if (_shell == null || _shell.isDisposed()) {
						return;
					}

					fadeOut(_shell);
				} catch (Exception err) {
					err.printStackTrace();
				}
			}

		};
		Display.getDefault().timerExec(DISPLAY_TIME, run);

	}

	private static void fadeOut(final Shell _shell) {
		final Runnable run = new Runnable() {

			public void run() {
				try {
					if (_shell == null || _shell.isDisposed()) {
						return;
					}

					int cur = _shell.getAlpha();
					cur -= FADE_OUT_STEP;

					if (cur <= 0) {
						disposeShell(_shell);
						return;
					}

					_shell.setAlpha(cur);

					if (_shell.getAlpha() != cur) {
						disposeShell(_shell);
						return;
					}

					Display.getDefault().timerExec(FADE_TIMER, this);

				} catch (Exception err) {
					err.printStackTrace();
				}
			}

		};
		Display.getDefault().timerExec(FADE_TIMER, run);

	}

	private static void disposeShell(final Shell _shell) {
		_shell.setAlpha(0);
		if (_oldImage != null) {
			_oldImage.dispose();
		}
		_shell.dispose();
		if (rightActiveShells.contains(_shell)){
			rightActiveShells.remove(_shell);
		} else {
			leftActiveShells.remove(_shell);
		}
	}

	private static String getMultiLine(String string) {
		string = string == null ? "" : string;
		String actualString = string.trim();
		String multiString = null;
		int length = 60;
		int beginIndex = 0;
		int endIndex;
		final char SPACE = ' ';
		StringBuilder stringBuilder = new StringBuilder(""); //$NON-NLS-1$

		while (true) {
			endIndex = beginIndex + length;
			if (actualString.length() <= length || !actualString.contains(" ")) { //$NON-NLS-1$
				endIndex = actualString.length();
				stringBuilder.append(actualString.substring(beginIndex, endIndex));
				break;
			}

			for (; endIndex > 0; endIndex--) {
				if (actualString.charAt(endIndex) == SPACE)
					break;
				continue;
			}
			boolean noSpace = (endIndex == 0);
			if (noSpace) {
				endIndex = beginIndex + length;
			}
			stringBuilder.append(actualString.substring(beginIndex, endIndex));
			actualString = actualString.substring(endIndex + 1);
			if (!noSpace) {
				stringBuilder.append(System.getProperty("line.separator")); //$NON-NLS-1$
			}
		}
		multiString = stringBuilder.toString();
		return multiString;
	}

}
