package wikipediaClassification;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * This class is used to generate category-objects from a Wikipedia dump
 */
public class CategoryFactory {
	public static Set<Category> getCategorySet(String path, Set<String> categories) throws IOException {
		HashSet<Category> returnSet = new HashSet<Category>();
		String marker = "INSERT INTO `category` VALUES";
		Pattern tuplePattern = Pattern.compile("\\((?<id>\\d+?),\'(?<title>\\S+?)\',(?<pages>\\d+?),\\d+?,\\d+?\\)");
		
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path)))) {
			String line;
			while( (line = br.readLine()) != null && categories.size()>0) {
				if(line.startsWith(marker)) {	//search for "INSERT INTO `category` VALUES"
					Matcher matcher = tuplePattern.matcher(line.substring(marker.length()));
	    			while (matcher.find()) {
	    				if(categories.contains(matcher.group("title"))) {	// test if the category is in our search list
	    					Category category = new Category(Integer.parseInt(matcher.group("id")), matcher.group("title"), Integer.parseInt(matcher.group("pages")));
	    					categories.remove(category.getTitle());
	    					returnSet.add(category);
	    				}
	    		    }
				}
			}
		}
		return returnSet;
	}
}
