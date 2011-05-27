package scorenotifierplugin.util;

import org.eclipse.swt.graphics.Color;

public class ColorMapper {

	static Color linkColor = ColorCache.getColor(255, 0, 0);
	static Color commentaryColor = ColorCache.getColor(238, 44, 44);
	
	private static Color greenForeGround = ColorCache.getColor(240, 255, 240);
	private static Color greenBackGround = ColorCache.getColor(152, 251, 152);
	private static Color greenBorder = ColorCache.getColor(0, 139, 0);

	private static Color sixForeGround = ColorCache.getColor(245, 245, 245);
	private static Color sixBackGround = ColorCache.getColor(175, 175, 175);
	private static Color sixBorder = ColorCache.getColor(0, 0, 0);

	private static Color redForeGround = ColorCache.getColor(255, 240, 245);
	private static Color redBackGround = ColorCache.getColor(200, 90, 90);
	private static Color redBorder = ColorCache.getColor(139, 0, 0);

	private static Color overEndForeGround = ColorCache.getColor(255, 250, 205);;
	private static Color overEndBackGround = ColorCache.getColor(238, 220, 130);
	private static Color overEndBorder = ColorCache.getColor(139, 54, 15);

	private static Color blueForeGround = ColorCache.getColor(226, 239, 249);
	private static Color blueBackGround = ColorCache.getColor(177, 211, 243);
	private static Color blueBorder = ColorCache.getColor(40, 73, 97);
	

	private static Color dotBallForeGround = ColorCache.getColor(255, 255, 255);
	private static Color dotBallBackGround = ColorCache.getColor(255, 255, 255);
	private static Color dotBallBorder = ColorCache.getColor(0, 0, 0);
	


	private static boolean test = true;

	public static Color getForeGround(ScoreEvent event) {
		test = false;
		if (test) {
			return ColorCache.getColor(255, 255, 255);
		}
		if (event == ScoreEvent.FOUR) {
			return greenForeGround;
		} else if (event == ScoreEvent.WICKET) {
			return redForeGround;
		} else if (event == ScoreEvent.END_OF_OVER) {
			return overEndForeGround;
		} else if (event == ScoreEvent.SIX) {
			return sixForeGround;
		} else if (event == ScoreEvent.DOT_BALL) {
			return dotBallForeGround;
		}
		return blueForeGround;
	}

	public static Color getBackGround(ScoreEvent event) {
		if (test) {
			return ColorCache.getColor(255, 255, 255);
		}
		if (event == ScoreEvent.FOUR) {
			return greenBackGround;
		} else if (event == ScoreEvent.WICKET) {
			return redBackGround;
		} else if (event == ScoreEvent.END_OF_OVER) {
			return overEndBackGround;
		} else if (event == ScoreEvent.SIX) {
			return sixBackGround;
		} else if (event == ScoreEvent.DOT_BALL) {
			return dotBallBackGround;
		}
		return blueBackGround;
	}

	public static Color getBorder(ScoreEvent event) {
		if (test) {
			return ColorCache.getColor(0, 0, 0);
		}
		if (event == ScoreEvent.FOUR) {
			return greenBorder;
		} else if (event == ScoreEvent.WICKET) {
			return redBorder;
		} else if (event == ScoreEvent.END_OF_OVER) {
			return overEndBorder;
		} else if (event == ScoreEvent.SIX) {
			return sixBorder;
		} else if (event == ScoreEvent.DOT_BALL) {
			return dotBallBorder;
		}
		return blueBorder;
	}

	public static Color getFontColor(ScoreEvent event) {
		if (test) {
			return ColorCache.getColor(0, 0, 0);
		}
		if (event == ScoreEvent.FOUR) {
			return greenBorder;
		} else if (event == ScoreEvent.WICKET) {
			return redBorder;
		} else if (event == ScoreEvent.END_OF_OVER) {
			return overEndBorder;
		} else if (event == ScoreEvent.SIX) {
			return sixBorder;
		} else if (event == ScoreEvent.DOT_BALL) {
			return dotBallBorder;
		}
		return blueBorder;
	}

}
