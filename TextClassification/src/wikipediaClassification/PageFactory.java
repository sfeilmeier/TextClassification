package wikipediaClassification;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.tartarus.snowball.SnowballStemmer;

/*
 * This class is used to generate page-objects from Wikipedia dumps and to generate "arff"-files for
 * usage with the naiveBayes package
 */
public class PageFactory {
	// at which percentage should we split training and testing data?
	private static final double TRAINING_DATA_PERCENT = 0.75;
	
	/**
	 * Get for a each category in a list a certain amount of pages belonging to the category.
	 * Number of pages may be limited by maxIdsPerCategory-parameter, to shorten total execution time of the function.
	 * 
	 * @param pathCategorylinks		Path to categorylinks.sql file from Wikipedia 
	 * @param categoryTitles		List of category titles
	 * @param maxIdsPerCategory		limit number of pages
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Map<String, Set<Integer>> getLinkedPageIdMap(String pathCategorylinks, Set<String> categoryTitles) throws FileNotFoundException, IOException {
		Map<String, Set<Integer>> returnMap = new HashMap<String, Set<Integer>>();
		for(String categoryTitle : categoryTitles) {
			returnMap.put(categoryTitle, new HashSet<Integer>());
		}
		
		String marker = "INSERT INTO `categorylinks` VALUES";
		Pattern tuplePattern = Pattern.compile("\\((?<from>\\d+?),\'(?<to>\\S+?)\',.+?\\)");
		
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(pathCategorylinks)))) {
			String line;
			int counter=0;
			while( (line = br.readLine()) != null && categoryTitles.size()>0) { 
    			counter++;
    			if(counter % 10 == 0) {
					System.out.print(".");
				}
				if(line.startsWith(marker)) {	// search for "INSERT INTO `categorylinks` VALUES"
					Matcher matcher = tuplePattern.matcher(line.substring(marker.length()));
	    			while (matcher.find()) {
	    				if(categoryTitles.contains(matcher.group("to"))) {
	    					Set<Integer> idSet = returnMap.get(matcher.group("to"));
	    					idSet.add(Integer.parseInt(matcher.group("from")));
	    				}
	    		    }
				}
			}
		}
		return returnMap;
	}

	/*
	 * Enum for the current element in the xml file
	 */
	private enum PageCurrentStartElement {
		PAGE_TITLE, 
		PAGE_ID,
		PAGE_NAMESPACE, 
		PAGE_TEXT,
	}
	
	/*
	 * Enum for the namespace (accoring to Wikipedia) in which the current page is
	 * (in general only "ARTICLE" will be interesting for this purpose) 
	 */
	public enum PageNamespace {
		MEDIUM("-2"),
		SPECIAL("-1"),
		ARTICLE("0"),
		DISCUSSION("1"),
		USER("2"),
		USER_DISCUSSION("3"),
		WIKIPEDIA("4"),
		WIKIPEDIA_DISCUSSION("5"),
		FILE("6"),
		FILE_DISCUSSION("7"),
		MEDIAWIKI("8"),
		MEDIAWIKI_DISCUSSION("9"),
		TEMPLATE("10"),
		TEMPLATE_DISCUSSION("11"),
		HELP("12"),
		HELP_DISCUSSION("13"),
		CATEGORY("14"),
		CATEGORY_DISCUSSION("15"),
		PORTAL("100"),
		PORTAL_DISCUSSION("101"),
		MODULE("828"),
		MODULE_DISCUSSION("829");
		
		private PageNamespace(final String text) {
			this.text = text;
		}
		private final String text;
		public String toString() {
			return text;
		}
	}
	
	/**
	 * Generate page-objects from a Wikipedia dump according to a set of Page-IDs
	 * 
	 * @param pathPagesArticles	Path to "pages-articles.xml"-file from Wikipedia
	 * @param pageIdSet			Integer-Set of Page-IDs according to pages-articles-file  
	 * @param globalWordSet		Global vocabulary of words - Set will be extended with new words 
	 * @param stopWordSet		List of stop-words (may be generated by getStopWordSet function)
	 * @return
	 * @throws XMLStreamException
	 * @throws FileNotFoundException
	 */
	public static Set<Page> getPageSetFromIds(String pathPagesArticles,
			Map<Integer, String> pageIdToCategoryMap, Map<String, 
			Set<Integer>> categoryToPageIdMap, int maxPagesPerCategory, 
			SortedSet<String> globalWordSet, Set<String> stopWordSet, 
			SnowballStemmer stemmer) throws XMLStreamException, FileNotFoundException {
		/*
		 * Preparations
		 */
		InputStream in = new FileInputStream(pathPagesArticles);
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader parser = factory.createXMLStreamReader( in );
		Set<Page> pageSet = new HashSet<Page>();
		
		Set<Integer> pageIdSet = pageIdToCategoryMap.keySet();
		Map<String, Integer> categoryPagesCounter = new HashMap<String, Integer>();
		
		int counter = 0;
		String curTitle = null;
		String curNamespace = null;
		Integer curId = null;
		String curText = null;
		PageCurrentStartElement curStartElement = null;
		boolean ignoreThisPage = false;
		
		/*
		 * Parse XML stream
		 */
		while(parser.hasNext() && pageIdSet.size()>0) {
			switch (parser.getEventType()) {
			case XMLStreamConstants.START_DOCUMENT:
			case XMLStreamConstants.NAMESPACE:
				// ignore
				break;
			
	    	case XMLStreamConstants.END_DOCUMENT:
	    		// end of xml file
	    		parser.close();
	    		break;

	    	case XMLStreamConstants.START_ELEMENT:
	    		// found xml tag; check if we can use it
	    		if (parser.getLocalName().compareTo("page")==0) {
	    			// found page-tag: initialize everything
	    			curId = null;
	    			curTitle = null;
	    			curText = null;
	    			curNamespace = null;
	    			curStartElement = null;
	    			ignoreThisPage = false;
	    		} else if (ignoreThisPage==true) {
	    			// page shall be ignored
					break;
				} else {
		    		switch (parser.getLocalName()) {	    			
		    		case "title":
		    			// found title-tag: store it
		    			curStartElement = PageCurrentStartElement.PAGE_TITLE;
		    			break;
		    		
		    		case "id":
		    			// found id-tag
		    			if(curStartElement==PageCurrentStartElement.PAGE_NAMESPACE) {
		    				// get only the page-id - which is coming after <ns>-tag
		    				curStartElement = PageCurrentStartElement.PAGE_ID;
		    			}
		    			break;
		    			
		    		case "text":
		    			// found text-tag: store it
		    			curStartElement = PageCurrentStartElement.PAGE_TEXT;
		    			break;
		    			
		    		case "ns":
		    			// found namespace-tag: store it
		    			curStartElement = PageCurrentStartElement.PAGE_NAMESPACE;
		    			break;
		    			
		    		default:
		    			// no relevant tag found: initialize
		    			curStartElement = null;
		    		}
				}
	    		break;

	    	case XMLStreamConstants.CHARACTERS:
	    		// found character content
	    		if(curStartElement==null || parser.isWhiteSpace() || ignoreThisPage==true) break;
	    		
	    		switch(curStartElement) {
	    		case PAGE_TITLE:
	    			curTitle = parser.getText().intern();
	    			break;
		    	
	    		case PAGE_ID:
	    			curId = Integer.parseInt(parser.getText());
	    			if(!pageIdSet.contains(curId)) {
		    			// don't read any other values if page_id not in set
	    				ignoreThisPage = true;
	    			}
	    			break;
	    			
	    		case PAGE_NAMESPACE:
	    			curNamespace = parser.getText().intern();
	    			break;
	    			
	    		case PAGE_TEXT:
	    			curText += parser.getText();
		    		break;
		    		
				default:
					break;	    			
	    		}
	    		break;
	    		
	        case XMLStreamConstants.END_ELEMENT:
	        	// found closing xml tag
	    		switch (parser.getLocalName()) {
	    		case "page":
	    			// page-tag was closed
	    			// print some status feedback because this process is taking a long time
	    			counter++;
	    			if(counter % 100000 == 0) {
	    				System.out.println();
	    				System.out.print(counter / 100000);
	    			}
	    			if(counter % 1000 == 0) {
    					System.out.print(".");
    				}
	    			if(ignoreThisPage==false) {
		    			if(curNamespace.compareTo(PageNamespace.ARTICLE.toString())==0) {
		    				// page is in namespace "ARTICLE"
		    				
		    				// convert the text in a wordCountMap (how often does every word occur in the text?)
		    				TreeMap<String,Integer> wordCountMap = getWordCountMap(curText.substring("null".length()), globalWordSet, stopWordSet, stemmer);
		    				
		    				// create page object
		    				String category = pageIdToCategoryMap.get(curId).intern();
		    				Page page = new Page(curId, curTitle, wordCountMap, category);
		    				pageSet.add(page);
		    				
		    				// make sure to stop when limit (maxPagesPerCategory) is reached
		    				int count = 1;
		    				if(categoryPagesCounter.containsKey(category)) {
		    					count = categoryPagesCounter.get(category) + 1;
		    				}
		    				categoryPagesCounter.put(category, count);
		    				if(count >= maxPagesPerCategory) {
		    					pageIdSet.removeAll(categoryToPageIdMap.get(category));
		    				}
		    			}
	    			}
	    			break;
	    		}
	            break;
			}
			parser.next();
		}
		return pageSet;
	}
	
	/**
	 * Generate a list of stemmed words (together with their number of occurrence) from a text. 
	 * Words in the stopWordSet are ignored; the globalWordSet is extended, when a new word is found.
	 * 
	 * @param text			Input text
	 * @param globalWordSet	Global vocabulary of words 
	 * @param stopWordSet	Set of stop-words to be ignored
	 * @return
	 */
	public static TreeMap<String,Integer> getWordCountMap(String text, SortedSet<String> globalWordSet, Set<String> stopWordSet, SnowballStemmer stemmer) {
		ArrayList<String> markupPatterns = new ArrayList<String>();
		
		// replace patterns
		markupPatterns.add("'{2,3}(?<text>.*?)'{2,3}"); 	// '''Alan Smithee'''
		markupPatterns.add("\\[\\[(?<text>.*?)\\]\\]"); 	// [[Pseudonym]]
		markupPatterns.add("={2,3} (?<text>.*?) ={2,3}"); 	// == Geschichte == or === Entstehung ===
		markupPatterns.add("<u>(?<text>.*?)</u>"); 			// <u>len</u>
		markupPatterns.add("\\[http://.*? (?<text>.*?)\\]"); // [http://LINK Text]
		for(String markupPattern : markupPatterns){
			text = text.replaceAll(markupPattern, "${text}");
		}	
		
		// remove patterns
		markupPatterns.clear();
		markupPatterns.add("\\s+");	// all whitespaces
		markupPatterns.add("\\<ref.*?\\>.*?\\<\\/ref\\>"); // <ref name="IMDb">[http://www.imdb.com/name/nm0000647/ Eigener Eintrag für Alan Smithee in der IMDb]</ref>
		markupPatterns.add("<.*? />"); //<references />
		markupPatterns.add("\\{\\{.*?\\}\\}"); // {{IMDb Name|ID=0000647|NAME=Alan Smithee}}
		markupPatterns.add("(?U)\\W+");	// all non-word characters (in unicode)
		markupPatterns.add("\\s\\d+\\s"); // Numbers
		markupPatterns.add("(?U)\\s\\d+\\w+?\\s"); // words starting with a number
		
		for(String markupPattern : markupPatterns){
			text = text.replaceAll(markupPattern, " ");
		}
		
		// convert to lowercase
		text = text.toLowerCase();
		
		// generate wordMap
		TreeMap<String,Integer> wordCountMap = new TreeMap<String,Integer>();

		for(String word : text.split(" ")) {
			// ignore stop-words
			if(stopWordSet.contains(word)) continue;
			// apply stemming
			stemmer.setCurrent(word);
			if(!stemmer.stem()) continue;
			word = stemmer.getCurrent().intern();
			// add word to wordCountMap
			if(wordCountMap.containsKey(word)) {
				wordCountMap.put(word, wordCountMap.get(word)+1);
			} else {
				wordCountMap.put(word, 1);
			}
			// add word to globalWordSet
			globalWordSet.add(word);
		}
		return wordCountMap;
	}
	
	/**
	 * Read list of stop-words from a file
	 * (Source for the file: http://snowball.tartarus.org/algorithms/german/stop.txt)
	 * 
	 * @param pathStopWords	Path to a stop-word file
	 * @return
	 * @throws IOException
	 */
	public static Set<String> getStopWordSet(String pathStopWords) throws IOException {
		Set<String> stopWordSet = new HashSet<String>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(pathStopWords)))) {
			String line;
			Pattern wordPattern = Pattern.compile("^(?<word>[\\S\\|]+)");
			while( (line = br.readLine()) != null ) {
				Matcher matcher = wordPattern.matcher(line);
	    		while (matcher.find()) {
	    			stopWordSet.add(matcher.group("word"));
				}
			}
		}
		return stopWordSet;
	}

	/*
	 * Enum used for splitting of training and testing data for arff-file
	 */
	public enum ArffFileMode {
		ALL_RECORDS,
		TRAINING_DATA_ONLY,
		TESTING_DATA_ONLY
	}
	
	/*
	 * Write arff file
	 */
	public static void writeArffFile(Set<Page> pageSet, SortedSet<String> globalWordSet, Set<Category> categorySet, String pathArffFile, ArffFileMode arffFileMode) throws IOException {
		try (FileWriter fw = new FileWriter(pathArffFile)) {
			fw.write(generateArffContent(pageSet, globalWordSet, categorySet, arffFileMode));
		}
	}
	
	/* 
	 * Generate contents of arff file, describing a complete set of pages, prepared for usage in naiveBayes algorithm
	 */
	public static String generateArffContent(Set<Page> pageSet, SortedSet<String> globalWordSet, Set<Category> categorySet, ArffFileMode arffFileMode) {
		String ret = "";
		int limitIndex = (int) Math.floor(pageSet.size()*TRAINING_DATA_PERCENT);
		
		// print header
		ret += "#Samples ";
		switch(arffFileMode) {
		case ALL_RECORDS:
			ret += pageSet.size();
			break;
		case TRAINING_DATA_ONLY:
			ret += limitIndex;
			System.out.println("\nTraining-Data: " + limitIndex);
			break;
		case TESTING_DATA_ONLY:
			ret += pageSet.size() - limitIndex;
			System.out.println("Testing-Data: " + (pageSet.size() - limitIndex));
			break;
		}
		ret += "\n";
		ret += "#Attributes " + globalWordSet.size() + "\n";
		ret += "#Topics " + categorySet.size() + "\n";
		ret += "\n";
		
		// print words
		{
			int i=0;
			for(String word : globalWordSet) {
				ret += "@attribute " + word + " " + 0 + " " + ++i +"\n";
			}
		}
		
		// print categories
		for(Category category : categorySet) {
			ret += "@topic " + category.getTitle() + "\n";
		}
		ret += "\n";
				
		// required for printing word-id
		String[] globalWordMapKeys = new String[globalWordSet.size()];
		int pos = 0;
		for (String key : globalWordSet) {
			globalWordMapKeys[pos++] = key;
		}
		
		// print data
		ret += "@data\n\n";
		{
			int i=0;
			PageLoop: for(Page page : pageSet) {
				i++;
				switch(arffFileMode) {
				case ALL_RECORDS:
					break;
				case TRAINING_DATA_ONLY:
					if(i > limitIndex) {
						break PageLoop;
					}
					break;
				case TESTING_DATA_ONLY:
					if(i <= limitIndex) {
						continue PageLoop;
					}
					break;
				}
				for(String word : page.wordCountMap.keySet()) {
					//ret += word + ":" + page.wordCountMap.get(word) + " "; // print word
					ret += Arrays.binarySearch(globalWordMapKeys, word) + ":" + page.wordCountMap.get(word) + " "; // print word-id
				}
				ret += "# " + page.category + "\n";
			}
		}
		return ret;
	}
}
