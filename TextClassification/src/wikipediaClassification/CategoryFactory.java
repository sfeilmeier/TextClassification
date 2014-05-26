package wikipediaClassification;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * This class is used to generate category-objects from a Wikipedia dump
 */
public class CategoryFactory {
	/**
	 * Read Wikipedia categories file and return a Set of categories that match either categoryTitleSet or categoryIdSet
	 * 
	 * @param pathCategory
	 * @param categoryTitleSet
	 * @param categoryIdSet
	 * @return
	 * @throws IOException
	 */
	public static Set<Category> getCategorySet(String pathCategory, Map<String,Set<String>> categoryTitleMap) throws IOException {
		HashSet<Category> returnSet = new HashSet<Category>();
		String marker = "INSERT INTO `category` VALUES";
		Pattern tuplePattern = Pattern.compile("\\((?<id>\\d+?),\'(?<title>\\S+?)\',(?<pages>\\d+?),\\d+?,\\d+?\\)");
		// Create reverseMap for categoryTitleMap
		Map<String,String> reverseCategoryTitleMap = new HashMap<String,String>();
		for(String categoryTitle : categoryTitleMap.keySet()) {
			reverseCategoryTitleMap.put(categoryTitle,categoryTitle);
			for(String subCategoryTitle : categoryTitleMap.get(categoryTitle)) {
				reverseCategoryTitleMap.put(subCategoryTitle,categoryTitle);
			}
		}
		
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(pathCategory)))) {
			String line;
			while( (line = br.readLine()) != null && categoryTitleMap.size()>0 ) {
				if(line.startsWith(marker)) {	//search for "INSERT INTO `category` VALUES"
					Matcher matcher = tuplePattern.matcher(line.substring(marker.length()));
	    			while (matcher.find()) {
	    				if(reverseCategoryTitleMap.keySet().contains(matcher.group("title"))) {	// test if the category is in our search list
	    					Category category = new Category(Integer.parseInt(matcher.group("id")), matcher.group("title"), reverseCategoryTitleMap.get(matcher.group("title")), Integer.parseInt(matcher.group("pages")));
	    					reverseCategoryTitleMap.remove(category.getTitle());
	    					returnSet.add(category);
	    				}
	    		    }
				}
			}
		}
		return returnSet;
	}
	
	/**
	 * Get for a each category in a list a certain amount of pages belonging to the category.
	 * 
	 * @param pathCategorylinks		Path to categorylinks.sql file from Wikipedia 
	 * @param categoryTitles		List of category titles
	 * @param maxIdsPerCategory		limit number of pages
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Map<String, Set<Integer>> getLinkedPageIdMap(String pathCategorylinks, 
			String pathCategory, Set<Category> categorySet)
					throws FileNotFoundException, IOException {
		// Prepare map with category titles
		Map<String,String> reverseCategoryTitleMap = new HashMap<String,String>();
		for(Category category : categorySet) {
			// TODO: this only works for one level of subcategories
			reverseCategoryTitleMap.put(category.getTitle(),category.getParentCategoryTitle());
		}		

		// Prepare return map
		Map<String, Set<Integer>> returnMap = new HashMap<String, Set<Integer>>();
		
		String marker = "INSERT INTO `categorylinks` VALUES";
		Pattern tuplePattern = Pattern.compile("\\((?<from>\\d+?),\'(?<to>\\S+?)\',.+?,\'(?<type>\\w+?)\'\\)");
		
		// search through Wikipedia file
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(pathCategorylinks)))) {
			String line;
			int counter=0;
			while( (line = br.readLine()) != null && reverseCategoryTitleMap.size()>0) { 
    			counter++;
    			if(counter % 10 == 0) {
					System.out.print(".");
				}
				if(line.startsWith(marker)) {	// search for "INSERT INTO `categorylinks` VALUES"
					Matcher matcher = tuplePattern.matcher(line.substring(marker.length()));
	    			while (matcher.find()) {
	    				if(reverseCategoryTitleMap.keySet().contains(matcher.group("to"))) {
	    					// found an entry belonging to a category
    						if(matcher.group("type").compareTo("page")==0) {
    							// found a page belonging to a category
    							String parentCategoryTitle = reverseCategoryTitleMap.get(matcher.group("to"));
    							Set<Integer> idSet = null;
    							if(returnMap.containsKey(parentCategoryTitle)) {
    								idSet = returnMap.get(parentCategoryTitle);
    							} else {
    								idSet = new HashSet<Integer>();
    							}
    							// add id to parent
    							idSet.add(Integer.parseInt(matcher.group("from")));
    							returnMap.put(parentCategoryTitle, idSet);
//    							System.out.println(reverseCategoryTitleMap.get(matcher.group("to")) + " > " + matcher.group("to") + " > " + matcher.group("from"));
		    				}
	    				}
	    		    }
				}
			}
		}
		
		return returnMap;
	}
}
