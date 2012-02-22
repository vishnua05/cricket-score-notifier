package googlesearch.model;

import java.util.ArrayList;
import java.util.List;

public class DefaultBookMarksProvider implements IBookMarksProvider {

	private List<String> empty = new ArrayList<String>();
	
	public List<String> getFavorites() {
		return empty;
	}

	
	public List<String> getRecentDocuments() {
		return empty;
	}

	
	public List<String> getBrowserFavorites() {
		return empty;
	}

}
