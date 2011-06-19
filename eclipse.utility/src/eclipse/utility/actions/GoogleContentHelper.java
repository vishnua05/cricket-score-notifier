package eclipse.utility.actions;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import eclipse.utility.Activator;

public class GoogleContentHelper {
	private static final String GOOGLE_SUGGEST_URL = "http://google.com/complete/search?output=toolbar&q=";
	private static final String NODE_SUGGESTION = "suggestion";
	private static final String ATTR_DATA = "data";

	public static synchronized List<String> getContentProposals(String input) {
		List<String> contentProposals = new ArrayList<String>();
		try {
			input = input.replace(" ", "+");
			URL url = new URL(GOOGLE_SUGGEST_URL + input);
			HttpURLConnection connection = null;
			try {
				connection = (HttpURLConnection) url.openConnection();
				connection.connect();
				InputStreamReader inStream = new InputStreamReader(connection.getInputStream());
				BufferedReader buff = null;
				try {
					buff = new BufferedReader(inStream);
					while (true) {
						String nextLine = buff.readLine();
						if (nextLine != null) {
							buildProposals(nextLine, contentProposals);
						} else {
							break;
						}
					}
				} finally {
					if (buff != null) {
						buff.close();
					}
				}
			} catch (IOException e) {
				Activator.getDefault().logException(e);
			} finally {
				connection.disconnect();
			}

		} catch (Exception e) {
			Activator.getDefault().logException(e);
		}

		return contentProposals;
	}

	private static void buildProposals(String xml, List<String> contentProposals) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document document = dBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
		document.getDocumentElement().normalize();
		int length = document.getElementsByTagName(NODE_SUGGESTION).getLength();
		for (int i = 0; i< length ; i ++) {
			Node node = document.getElementsByTagName(NODE_SUGGESTION).item(i);
			Node attributes = node.getAttributes().getNamedItem(ATTR_DATA);
			contentProposals.add(attributes.getNodeValue());
		}
	}

	public static void main(String[] args) {
		List<String> contentProposals = GoogleContentHelper.getContentProposals("test");
		for (String string : contentProposals) {
			System.out.println(string);
		}
	}

}
