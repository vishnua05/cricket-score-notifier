package googlesearch.model;

import java.util.List;

public interface IBookMarksProvider {

	 List<String> getFavorites();
	 
	 List<String> getRecentDocuments();
	 
     List<String> getBrowserFavorites();	
	
}
