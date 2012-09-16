package scorenotifierplugin.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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

import scorenotifierplugin.ScoreNotifier;
import scorenotifierplugin.ScoreNotifierActivator;
import scorenotifierplugin.preferences.ScoreNotifierPreferences;
import scorenotifierplugin.provider.IScoreParser;
import scorenotifierplugin.provider.ScoreNode;

public class NotifierDialog {

	private static final String GOOGLE_QUERY_URL = "http://google.com/search?btnI=1&q=";
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
	public static void notify(final ScoreNode scoreNode, final String overStats, final ScoreEvent scoreEvent, boolean toggle) {
		final Shell thisShell = new Shell(Display.getDefault().getActiveShell(), SWT.NO_FOCUS | SWT.ON_TOP);
		thisShell.setText(scoreNode.getScore());
		thisShell.setLayout(new FillLayout());
		thisShell.setBackgroundMode(SWT.INHERIT_DEFAULT);
		thisShell.addListener(SWT.Dispose, new Listener() {
			public void handleEvent(Event event) {
				rightActiveShells.remove(thisShell);
			}
		});

		final Composite container = new Composite(thisShell, SWT.BORDER);
		GridLayout containerLayout = new GridLayout(3, false);
		containerLayout.marginTop = 0;
		containerLayout.verticalSpacing = 0;

		container.setLayout(containerLayout);
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		container.setLayoutData(layoutData);
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

		Rectangle clientArea = Display.getDefault().getClientArea();
		int shellWidth = clientArea.width / 5;
		Label scoreLabel = new Label(container, SWT.NONE);
		scoreLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		scoreLabel.setText(scoreNode.getScore());
		scoreLabel.setForeground(ColorMapper.getFontColor(scoreEvent));
		Font sf = scoreLabel.getFont();
		FontData sfd = sf.getFontData()[0];
		sfd.height = 9;
		sfd.setStyle(SWT.BOLD | SWT.ITALIC);
		scoreLabel.setFont(FontCache.getFont(sfd));

		Hyperlink liveLink = new Hyperlink(container, SWT.NONE);
		liveLink.setUnderlined(true);
		liveLink.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		liveLink.setText("Live");
		liveLink.setForeground(ColorMapper.linkColor);
		liveLink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent event) {
				String searchURL = ScoreNotifier.getMatchURL(scoreNode.getMatchURL());
				openBrowser(searchURL);
			}
		});
		Font f = liveLink.getFont();
		FontData fd = f.getFontData()[0];
		// fd.setStyle(SWT.BOLD);
		fd.height = 10;
		liveLink.setFont(FontCache.getFont(fd));
		
		Button closeButton = new Button(container, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.RIGHT, SWT.CENTER).applyTo(closeButton);
		closeButton.setText("X");
		closeButton.setForeground(container.getForeground());
		closeButton.setBackground(container.getBackground());
		closeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				disposeShell(thisShell);
			}
		});
		
		if (scoreNode.getBatsMenStats() != null && !scoreNode.isCommentaryNode()) {
			String batsMenStats = scoreNode.getBatsMenStats();
			StringTokenizer stringTokenizer = new StringTokenizer(batsMenStats, IScoreParser.BATSMEN_DELIMITER);
			while (stringTokenizer.hasMoreElements()) {
				Object object = (Object) stringTokenizer.nextElement();
				createLabel(thisShell, container, object.toString(), false);
			}
		}

		ScoreNotifierPreferences preferences = ScoreNotifierActivator.getDefault().getPreferences();

		if (scoreNode.getDecoration() != "-") {
			String decoration = scoreNode.getDecoration();
			Label messageLabel = new Label(container, SWT.NONE);
			Font tf = messageLabel.getFont();
			FontData tfd = tf.getFontData()[0];
			tfd.setStyle(SWT.BOLD);
			tfd.height = 10;
			messageLabel.setFont(FontCache.getFont(tfd));
			GridData gd = new GridData(SWT.LEFT, SWT.CENTER, false, false);
			gd.horizontalSpan = 3;
			messageLabel.setLayoutData(gd);
			messageLabel.setForeground(ColorMapper.getFontColor(scoreEvent));
			if (preferences.notifyEveryOver() && !preferences.notifyEveryBall() && !preferences.notifyEveryRun() && scoreNode.isEndOfOver()) {
				if (overStats != null) {
					decoration = overStats.substring(overStats.indexOf('[') + 1, overStats.indexOf(']'));
				}
			}
			messageLabel.setText(decoration);
			messageLabel.addMouseListener(getMouseListener(thisShell));
		}

		String commentary = scoreNode.getCommentary();
		if (commentary != null) {
			Label commentaryLabel = new Label(container, SWT.WRAP);
			Font cf = commentaryLabel.getFont();
			FontData cfd = cf.getFontData()[0];
			cfd.height = 9;
			cfd.setStyle(SWT.ITALIC);
			Font font = FontCache.getFont(cfd);
			commentaryLabel.setFont(font);
			
			GC gc = new GC(thisShell);
			gc.setFont(font);
			Point stringExtent = gc.stringExtent(commentary);
			int y_hint = stringExtent.x * stringExtent.y / (shellWidth);
			y_hint = (y_hint/stringExtent.y  + 1)*stringExtent.y;
			y_hint = Math.min(y_hint, 150);
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).hint(SWT.DEFAULT, y_hint).span(3, 1).applyTo(commentaryLabel);
			commentaryLabel.setText(commentary);
			commentaryLabel.setForeground(ColorCache.getColor(139, 69, 0));
			commentaryLabel.addMouseListener(getMouseListener(thisShell));
			commentaryLabel.setToolTipText(commentary);
			gc.dispose();
		}
		
		if (scoreNode.getBowlerStats() != null && !scoreNode.isCommentaryNode()) {
			createLabel(thisShell, container, scoreNode.getBowlerStats(), true);
		}

		if (overStats != null) {
			Label overStatsLabel = new Label(container, SWT.NONE);
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(3, 1).applyTo(overStatsLabel);
			overStatsLabel.setText(overStats);
			Font cf = overStatsLabel.getFont();
			FontData cfd = cf.getFontData()[0];
			cfd.height = 11;
			overStatsLabel.setFont(FontCache.getFont(cfd));
			overStatsLabel.addMouseListener(getMouseListener(thisShell));
		}

		boolean invertNotificationLocation = preferences.isInvertNotificationLocation();

		int minHeight = container.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		thisShell.setSize(shellWidth, minHeight);

		int startX = toggle ? 0 : clientArea.width - (shellWidth + 2);
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
	
	private static void openBrowser(String url) {
		IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
		try {
			IWebBrowser browser = browserSupport.getExternalBrowser();
			browser.openURL(new URL(url));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void createLabel(final Shell thisShell, final Composite container, final String labelText, boolean bowler) {
		Hyperlink liveLink = new Hyperlink(container, SWT.NONE);
		Font font = liveLink.getFont();
		FontData tlfd = font.getFontData()[0];
		tlfd.height = 10;
		if (bowler) {
			tlfd.setStyle(SWT.NORMAL);
		}
		liveLink.setFont(FontCache.getFont(tlfd));
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		gd.horizontalSpan = 3;
		liveLink.setLayoutData(gd);
		liveLink.setText(labelText);
		liveLink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				Matcher matcher = Pattern.compile(REGEX_PLAYER).matcher(labelText);
				if (matcher.matches()) {
					String playerName = matcher.group(1) + " Cricinfo";
					String url = GOOGLE_QUERY_URL + playerName;
					openBrowser(url);
				}
			}
		});
		liveLink.setToolTipText("Click to view player profile");
	}

	private static String REGEX_PLAYER = "(.*)( ([0-9]{1,3})).*";
	
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


}
