package scorenotifierplugin.provider;

public class Match {
	private static final String HEADER = "header";
	private static final String DESC = "description";
	private static final String URL_TEXT = "url-text";
	private static final String URL_LINK = "url-link";

	private String header;
	private String description;
	private String urlText;
	private String urlLink;

	public Match(String matchText) {
		header = getLine(HEADER, matchText);
		description = getLine(DESC, matchText);
		urlText = getLine(URL_TEXT, matchText);
		urlLink = getLine(URL_LINK, matchText);
	}

	private String getLine(String xmlTag, String matchText) {
		String startElement = "<" + xmlTag + ">";
		String endElement = "</" + xmlTag + ">";
		return matchText.substring(matchText.indexOf(startElement) + startElement.length(), matchText.indexOf(endElement));
	}

	public String getHeader() {
		return header;
	}

	public String getDescription() {
		return description;
	}

	public String getUrlText() {
		return urlText;
	}

	public String getUrlLink() {
		return urlLink;
	}

}
