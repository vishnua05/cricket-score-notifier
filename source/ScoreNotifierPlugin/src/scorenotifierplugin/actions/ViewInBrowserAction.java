package scorenotifierplugin.actions;

import java.util.List;

public class ViewInBrowserAction extends ScorePullDownAction {

	private static final String CRIC_BUZZ_URL = "http://www.cricbuzz.com/";
	private static final String CRIC_BUZZ_HOME = "Cric Buzz Home";
	private static final String CRIC_INFO_URL = "http://www.espncricinfo.com/";
	private static final String CRIC_INFO_HOME = "Cric Info Home";

	private static final String CRIC_BUZZ_FEED_URL = "http://synd.cricbuzz.com/score-gadget/gadget-scores-feed.xml";
	private static final String CRIC_BUZZ_MOBILE_FEED_URL = "http://synd.cricbuzz.com/onmobile-togo/";
	private static final String CRIC_BUZZ_FEED = "Cric Buzz Feed";
	private static final String CRIC_BUZZ_MOBILE_FEED = "Cric Buzz Mobile Feed";

	@Override
	protected List<String> getAvailableItems() {
		List<String> availableItems = super.getAvailableItems();
		availableItems.add(CRIC_BUZZ_URL);
		availableItems.add(CRIC_INFO_URL);
		availableItems.add(CRIC_BUZZ_FEED_URL);
		availableItems.add(CRIC_BUZZ_MOBILE_FEED_URL);
		return availableItems;
	}

	@Override
	protected String getDisplayName(String matchURL) {
		if (matchURL.equals(CRIC_BUZZ_URL)) {
			return CRIC_BUZZ_HOME;
		} else if (matchURL.equals(CRIC_INFO_URL)) {
			return CRIC_INFO_HOME;
		} else if (matchURL.equals(CRIC_BUZZ_FEED_URL)) {
			return CRIC_BUZZ_FEED;
		} else if (matchURL.equals(CRIC_BUZZ_MOBILE_FEED_URL)) {
			return CRIC_BUZZ_MOBILE_FEED;
		}
		return super.getDisplayName(matchURL);
	}

}
