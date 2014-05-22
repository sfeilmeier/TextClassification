package wikipediaClassification;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.xml.stream.XMLStreamException;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.*;

import wikipediaClassification.PageFactory.ArffFileMode;

public class WikipediaClassificationTestApp {

	private static final Logger LOG = Logger.getLogger(WikipediaClassificationTestApp.class.getName());
	
	public static void main(String[] args) throws XMLStreamException, IOException {
		/*
		 * Parameters
		 */
		// German
		String pathCategory = "data/wikipedia/dewiki-20140407-category.sql";
		String pathCategorylinks = "data/wikipedia/dewiki-20140407-categorylinks.sql";
		String pathPagesArticles = "data/wikipedia/dewiki-20140407-pages-articles.xml";
		String pathStopWords = "data/stopwords/german.txt";
		SnowballStemmer stemmer = new germanStemmer();
		
		// Romanian
//		String pathCategory = "data/wikipedia/rowiki-20140507-category.sql";
//		String pathCategorylinks = "data/wikipedia/rowiki-20140507-categorylinks.sql";
//		String pathPagesArticles = "data/wikipedia/rowiki-20140507-pages-articles.xml";
//		String pathStopWords = "data/stopwords/romanian.txt";
//		SnowballStemmer stemmer = new romanianStemmer();
		
		// General
		String pathArffTrainingFile = "data/tmp/output-training.arff";
		String pathArffTestingFile = "data/tmp/output-testing.arff";
		String pathLogFile = "data/tmp/WikipediaClassificationTestApp.log";
		
		int maxPagesPerCategory = 20;
		
		SortedSet<String> globalWordSet = new TreeSet<String>();
		Set<String> stopWordSet = PageFactory.getStopWordSet(pathStopWords);
		
		/*
		 * Define categories
		 */
		Set<String> categoryStringSet = new HashSet<String>();
		// German
		categoryStringSet.add("Geographie");
		categoryStringSet.add("Geschichte");
		categoryStringSet.add("Gesellschaft");
		categoryStringSet.add("Kunst_und_Kultur");
		categoryStringSet.add("Religion");
		categoryStringSet.add("Sport");
		categoryStringSet.add("Technik");
		categoryStringSet.add("Wissen");
		//categoryStringSet.add("Lesenswert");
		//categoryStringSet.add("Fiktive_Person");
		//categoryStringSet.add("Ang_Lee");
		
		// Romanian
//		categoryStringSet.add("Artă");
//		categoryStringSet.add("Cultură");
//		categoryStringSet.add("Geografie");
//		categoryStringSet.add("Istorie");
//		categoryStringSet.add("Matematică");
//		categoryStringSet.add("Oameni");
//		categoryStringSet.add("Filozofie");
//		categoryStringSet.add("Societate");
//		categoryStringSet.add("Știință");
//		categoryStringSet.add("Tehnologie");

		FileHandler logFh = null;
		try {
			/*
			 * Initialize Logger
			 */
			logFh = new FileHandler(pathLogFile);
			LOG.addHandler(logFh);
			SimpleFormatter formatter = new SimpleFormatter();
			logFh.setFormatter(formatter);
			
			/*
			 * Get categories from file
			 */		
			LOG.info("Receiving Categories...");
			Set<Category> categorySet = CategoryFactory.getCategorySet(pathCategory, categoryStringSet);
			LOG.info("Categories:");
			for(Category category : categorySet) {
				LOG.info("Category: " + category.getTitle() + " (id: " + category.getId() + "; pages: " + category.getPages() + ")");
			}
			
			/*
			 * Prepare category list for next step
			 */
			Set<String> categoryTitles = new HashSet<String>();
			for(Category category : categorySet) {
				categoryTitles.add(category.getTitle());
			}
			
			/*
			 * Get pageIds belonging to categories
			 */
			LOG.info("Receiving Category-Page-Links...");
			Map<String, Set<Integer>> categoryToPageIdMap = PageFactory.getLinkedPageIdMap(pathCategorylinks, categoryTitles);
			LOG.info("Category-Page-Links:");
			for(String category : categoryToPageIdMap.keySet()) {
				LOG.info("Category: " + category + " -> PageIds:" + categoryToPageIdMap.get(category));
			}
			
			/* 
			 * Prepare pageId list for next step
			 */
			Map<Integer, String> pageIdToCategoryMap = new HashMap<Integer, String>();
			for(String category : categoryToPageIdMap.keySet()) {
				for(Integer pageId : categoryToPageIdMap.get(category)) {
					pageIdToCategoryMap.put(pageId, category);
				}
			}
			
			/*
			 * Get pages from pageId list
			 */
			LOG.info("Receiving Pages...");
			Set<Page> pageSet = PageFactory.getPageSetFromIds(pathPagesArticles, pageIdToCategoryMap, categoryToPageIdMap, maxPagesPerCategory, globalWordSet, stopWordSet, stemmer);
			for(Page page : pageSet) {
				LOG.info("Page: " + page.title + " (" + page.category + ") -> " + page.wordCountMap);
			}
			
			/*
			 * Generate Arff file
			 */
			LOG.info("Generating arff-file...");
			//System.out.println(PageFactory.generateArffContent(pageSet, globalWordSet, categorySet));
			PageFactory.writeArffFile(pageSet, globalWordSet, categorySet, pathArffTrainingFile, ArffFileMode.TRAINING_DATA_ONLY);
			PageFactory.writeArffFile(pageSet, globalWordSet, categorySet, pathArffTestingFile, ArffFileMode.TESTING_DATA_ONLY);
			
			LOG.info("Finished!");
		} finally {
			logFh.close();
		}
	}
}
