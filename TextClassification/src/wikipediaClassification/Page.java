package wikipediaClassification;

import java.util.SortedMap;
import java.util.TreeMap;

/*
 * This class holds one Wikipedia article (internally called "page").
 * We are not storing the complete text, but only a list of words and their number of occurrences 
 */
public class Page {
	String category;	// category to which the page is belonging
	//TODO: consider more than one category
	int id;			// internal pageId used by Wikipedia
	String title;	// page title
	SortedMap<String,Integer> wordCountMap = null;	// list of words and their number of occurrence within the page
	
	public Page(int id, String title, TreeMap<String,Integer> wordCountMap, String category) {
		super();
		this.id = id;
		this.title = title;
		this.wordCountMap = wordCountMap;
		this.category = category;
	}
	@Override
	public String toString() {
		return "Page [id=" + id + ", title=" + title + ", wordCountMap="
				+ wordCountMap + "]";
	}
		
}
