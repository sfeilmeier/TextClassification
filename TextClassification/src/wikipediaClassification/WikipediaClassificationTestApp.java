package wikipediaClassification;

import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.Arrays;
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
	
	/*
	 * Return the top eight categories and there direct subcategories from wikipedia
	 * 
	 * Define categories with subcategories. All pages in subcategories are considered
	 * to belong to the main category
	 * (read from http://de.wikipedia.org/w/index.php?title=Spezial%3AKategorienbaum)
	 */
	private static Map<String, Set<String>> getGermanCategoryTitleMap() {
		Map<String, Set<String>> categoryTitleMap = new HashMap<String, Set<String>>();
		categoryTitleMap.put("Geographie", new HashSet<String>(Arrays.asList(new String[] { 
				"Allgemeine_Geographie",
				"Regionale_Geographie",
				"Angewandte_Geographie",
				"Geographiedidaktik",
				"Geographie_nach_Epoche", 
				"Geographie_nach_räumlicher_Zuordnung", 
				"Geschichte_der_Geographie", 
				"Liste_(Geographie)", 
				"Organisation_(Geographie)", 
				"Person_(Geographie)", 
				"Portal:Geographie_als_Thema",
				"Sachliteratur_(Geographie)", 
				"Veranstaltung_(Geographie)" })));
		categoryTitleMap.put("Geschichte", new HashSet<String>(Arrays.asList(new String[] { 
				"Geschichte_nach_räumlicher_Zuordnung", 
				"Geschichte_nach_Thema",
				"Geschichte_nach_Zeit",
				"Portal:Geschichte_als_Thema",
				"Ereignis", 
				"Historiengemälde", 
				"Historischer_Roman", 
				"Liste_(Geschichte)", 
				"Organisation_(Geschichte)", 
				"Person_(Geschichte)", 
				"Geschichtswissenschaft",
				"Zeitalter", 
				"Portal:Geschichte_als_Thema" })));
		categoryTitleMap.put("Gesellschaft", new HashSet<String>(Arrays.asList(new String[] { 
				"Gesellschaft_nach_räumlicher_Zuordnung", 
				"Organisation_(Gesellschaft)",
				"Person_(Gesellschaft)",
				"Liste_(Gesellschaft)",
				"Portal:Gesellschaft_als_Thema", 
				"Arbeitswelt", 
				"Armut", 
				"Auszeichnungen", 
				"Behinderung", 
				"Bevölkerungsgruppe", 
				"Demonstration",
				"Einsatzorganisationen",
				"Entwicklung",
				"Erwachsenenalter",
				"Familie",
				"Freizeit",
				"Gesellschaftliches_System",
				"Gesellschaftskritik",
				"Gesellschaftsmodell",
				"Ideologie",
				"Informatik_und_Gesellschaft",
				"Katastrophe",
				"Kindheit_und_Jugend",
				"Konflikte",
				"Konsum",
				"Kriminalität",
				"Lebensgemeinschaften",
				"Lebensstil",
				"Literarisches_Leben",
				"Meinungsforschung",
				"Minderheiten",
				"Öffentlichkeit",
				"Partnerschaft",
				"Soziale_Bewegung",
				"Soziale_Bewegung_als_Thema",
				"Soziales_Handeln_und_Verhalten",
				"Soziales_Milieu",
				"Sozialwissenschaft",
				"Technikfolgenabschätzung",
				"Vereinswesen",
				"Versammlungsgebäude" })));
		/*categoryTitleMap.put("Kunst_und_Kultur", new HashSet<String>(Arrays.asList(new String[] { 
				"Organisation_(Kunst_und_Kultur)", 
				"Künstlergruppe",
				"Künstlergruppe_als_Thema",
				"Portal:Kunst_und_Kultur_als_Thema",
				"Film", 
				"Fotografie", 
				"Kultur", 
				"Kunst", 
				"Literatur", 
				"Musik", 
				"Personendarstellung",
				"Sprache",
				"Tanzen",
				"Theater" })));*/
		categoryTitleMap.put("Religion", new HashSet<String>(Arrays.asList(new String[] { 
				"Liste_(Religion)",
				"Religion_nach_räumlicher_Zuordnung",
				"Religiöse_Auszeichnung",
				"Bauwerk_nach_Religion",
				"Bekenntnis",
				"Beruf_(Religion)",
				"Christentum_und_andere_Religionen",
				"Religiöses_Dokument",
				"Einzelreligion",
				"Eschatologie",
				"Esoterik",
				"Religiöse_Ethik",
				"Freireligiöse,_freigeistige_und_Freidenkerbewegung",
				"Glaubenspraxis",
				"Glaubenssystem",
				"Heilige_Schrift",
				"Interreligiöser_Dialog",
				"Religion_im_Kontext",
				"Konversion",
				"Kult",
				"Kultur_(Religion)",
				"Religiöse_Literatur",
				"Religiöse_Minderheit",
				"Mythologie",
				"Naturreligionen",
				"Neuoffenbarung",
				"Religiöses_Objekt",
				"Religiöses_Objekt_als_Thema",
				"Religiöse_Organisationen",
				"Person_(Religion)",
				"Person_(Religion)_als_Thema",
				"Religion_als_Thema",
				"Religion_und_Wirtschaft",
				"Religionsphilosophie",
				"Religionswissenschaft",
				"Sakralarchitektur",
				"Spiritualität",
				"Symbol_(Religion)",
				"Religiöser_Titel",
				"Übernatürliches_Wesen",
				"Religiöse_Veranstaltung",
				"Religiöse_Veranstaltung_als_Thema" })));
		categoryTitleMap.put("Sport", new HashSet<String>(Arrays.asList(new String[] { 
				"Sport_nach_räumlicher_Zuordnung",
				"Sport_nach_Sportart",
				"Sport_nach_Zeit",
				"Liste_(Sport)",
				"Portal:Sport_als_Thema",
				"Sportart",
				"Sportliche_Auszeichnung",
				"Beruf_(Sport)",
				"Betreuungswesen_(Sport)",
				"Betriebssport",
				"Frauensport",
				"Sportgattung",
				"Hochschulsport",
				"Homosexualität_im_Sport",
				"Jugend_und_Sport",
				"Männersport",
				"Medien_und_Sport",
				"Organisation_(Sport)",
				"Organisation_(Sport)_als_Thema",
				"Outdoor",
				"Person_(Sport)",
				"Sportpolitik",
				"Sportrecht",
				"Sport_und_Religion",
				"Seniorensport",
				"Sport_(Kultur)",
				"Sportausrüstung",
				"Sportförderung",
				"Sportstätte",
				"Taktik_(Sport)",
				"Sportteam",
				"Technik_(Sport)",
				"Sportveranstaltungen",
				"Sportwirtschaft",
				"Sportwissenschaft" })));
		categoryTitleMap.put("Technik", new HashSet<String>(Arrays.asList(new String[] { 
				"Antike_Technik",
				"Technik_nach_räumlicher_Zuordnung",
				"Technisches_Fachgebiet",
				"Liste_(Technik)",
				"Bauteil",
				"Technischer_Beruf",
				"Feuer_in_der_Technik",
				"Gerät",
				"Technikgeschichte",
				"Infrastruktur",
				"Ingenieurwissenschaft",
				"Medientechnik",
				"Modellierung_und_Simulation",
				"Nachwachsende_Rohstoffe",
				"Organisation_(Technik)",
				"Person_(Technik)",
				"Produktion",
				"Technischer_Rekord",
				"Standardisierung",
				"Technik_und_Recht",
				"Technikpreis",
				"Technikwettbewerb",
				"Technische_Anlage",
				"Techniktheorie",
				"Ultraschall",
				"Wasser_in_der_Technik",
				"Werkstoff_als_Thema" })));
		/*categoryTitleMap.put("Wissen", new HashSet<String>(Arrays.asList(new String[] { 
				"Wissen_nach_räumlicher_Zuordnung",
				"Archivwesen",
				"Bibliothekswesen",
				"Data-Mining",
				"Freies_Wissen",
				"Wissensmanagement",
				"Modellierung_und_Simulation",
				"Museumswesen",
				"Parawissenschaft",
				"Philosophie",
				"Wissen_(Philosophie)",
				"Wissenssoziologie",
				"Wissenschaft" })));*/
		return categoryTitleMap;
	}
	
	/*
	 * Source: http://ro.wikipedia.org/wiki/Special:Arborele_categoriilor
	 */
	private static Map<String, Set<String>> getRomanianCategoryTitleMap() {
		Map<String, Set<String>> categoryTitleMap = new HashMap<String, Set<String>>();
		categoryTitleMap.put("Artă", new HashSet<String>(Arrays.asList(new String[] { 
				"Imagini_artă",
				"Liste_despre_artă",
				"Artă_după_naționalitate",
				"Artă_după_subiect",
				"Formate_artă",
				"Terminologie_artistică",
				"Lucrări_creative",
				"Artă_efemeră",
				"Antropomorfism",
				"Arte_scenice",
				"Arte_vizuale",
				"Ocupații_în_artă",
				"Artă_pe_calculator",
				"Ceramică",
				"Cosmetică",
				"Estetică",
				"Arta_europeană",
				"Folclor",
				"Fântâni",
				"Grupări_artistice",
				"Arta_împachetării_hârtiei",
				"Istoria_artei",
				"Kitsch_în_artă",
				"Literatură",
				"Modă",
				"Monumente",
				"Organizații_de_artă",
				"Personaje",
				"Premii_artă",
				"Publicistică",
				"Realism_magic",
				"Simbolism_(artă)",
				"Stiluri_artistice",
				"Tehnici_artistice",
				"Vitraliu",
				"Zei_ai_artelor",
				"Zeițe ale artelor",
				"Cioturi_Artă" })));
		categoryTitleMap.put("Cultură", new HashSet<String>(Arrays.asList(new String[] { 
				"Cultura_după_națiuni",
				"Cultură_după_regiune",
				"Oameni_de_cultură",
				"Cultură_după_limbă",
				"Liste_de_cultură",
				"Cultură_după_perioadă",
				"Cultură_după_subiect",
				"Formate_cultură",
				"Arhive",
				"Artă",
				"Cioturi_Cultură",
				"Culturi",
				"Cultură_după_oraș",
				"Curente_culturale",
				"Educație",
				"Etnicitate",
				"Evoluție_socioculturală",
				"Festivaluri",
				"Gastronomie",
				"Generații culturale",
				"Geografie culturală",
				"Ideologie",
				"Instituții_culturale",
				"Interacțiunea_om-animal",
				"Media",
				"Mitologie_după_cultură",
				"Muzeologie",
				"Nume_de_familie_după_cultură",
				"Orientalistică",
				"Patrimoniu_cultural_UNESCO",
				"Personaje",
				"Publicații_de_cultură",
				"Publicistică",
				"Religie",
				"Reviste_de_cultură",
				"Sfere_culturale_de_influență",
				"Simboluri",
				"Situri_web_de_cultură",
				"Stiluri_artistice",
				"Studii_culturale",
				"WikiProiecte_Cultură",
				"Știință_și_cultură" })));
		categoryTitleMap.put("Geografie", new HashSet<String>(Arrays.asList(new String[] { 
				"Formate_geografie",
				"Geografi",
				"Ramuri_ale_geografiei",
				"Cioturi_legate_de_geografie",
				"Liste_despre_geografie",
				"Geografie_după_loc",
				"Portaluri_de_Geografie",
				"Ecoregiuni",
				"Fuse_orare",
				"Geocoduri",
				"Geografie zonală",
				"Istoria_geografiei_după_țară",
				"Istorie_geografică",
				"Locuri",
				"Migrație",
				"Occident",
				"Orient",
				"Peisaj_artificial",
				"Recorduri_geografice",
				"Țări",
				"Terminologie_geografică",
				"Topografiere",
				"Unități_geografice", })));
		categoryTitleMap.put("Istorie", new HashSet<String>(Arrays.asList(new String[] { 
				"Cioturi_Istorie",
				"Formate_istorie",
				"Istorie_după_grup_etnic",
				"Liste_despre_istorie",
				"Istorie_după_perioadă",
				"Istorie_după_regiune",
				"Istorie_după_subiect",
				"Istorici",
				"Congrese",
				"Cărți_de_istorie",
				"Documente",
				"Documente_istorice",
				"Epoci_istorice",
				"Evenimente",
				"Ficțiune_istorică",
				"Filozofia_istoriei",
				"Imperiul_aztec",
				"Legislaturi_istorice",
				"Locuri_istorice",
				"Mari_Mareșali_ai_Palatului_Imperial",
				"Migrație",
				"Monumente_istorice",
				"Muzee_de_istorie",
				"Schimbări_teritoriale",
				"Științe_auxiliare_ale_istoriei" })));
		categoryTitleMap.put("Religie", new HashSet<String>(Arrays.asList(new String[] { 
				"Categorii_după_religie",
				"Formate_religie",
				"Lideri_religioși",
				"Cioturi_legate_de_religie",
				"Liste_legate_de_religie",
				"Antropologia_religiilor",
				"Comportament_religios_și_rutină",
				"Conflicte_religioase",
				"Controverse_religioase",
				"Credințe_religioase,_tradiții_și_mișcări",
				"Critica_religiei",
				"Cultură_religioasă",
				"Exorcism",
				"Geografia_religiei",
				"Istoria_religiilor",
				"Natură_și_religie",
				"Obiecte_religioase",
				"Religie_și_societate",
				"Sociologia_religiilor" })));
		/*categoryTitleMap.put("Societate", new HashSet<String>(Arrays.asList(new String[] { 
				"Cioturi_legate_de_societate",
				"Societate_după_țară",
				"Societate_după_continent",
				"Liste_legate_de_societate",
				"Tipuri_de_societăți",
				"Formate_societate",
				"Afaceri",
				"Armată",
				"Celebritate",
				"Comunicare",
				"Comunicații",
				"Cultură",
				"Dizabilități",
				"Economie",
				"Evenimente",
				"Evenimente_sociale",
				"Familie",
				"Geografie_umană",
				"Grupuri",
				"Grupuri_etnice",
				"Informatică_și_societate",
				"Istorie_socială",
				"Jocuri_de_societate",
				"Mișcări_sociale",
				"Naționalitate",
				"Oameni",
				"Organizații",
				"Politică",
				"Popoare",
				"Religie_și_societate",
				"Scandaluri",
				"Sexualitate_și_societate",
				"Sisteme",
				"Societate_urbană",
				"Sănătate",
				"Știința_în_societate",
				"Științe_sociale" })));*/
		categoryTitleMap.put("Știință", new HashSet<String>(Arrays.asList(new String[] { 
				"Știință_și_tehnologie_după_țară",
				"Știință_și_tehnologie_după_continent",
				"Liste_știință_și_tehnologie",
				"Formate_știință",
				"Discipline_științifice",
				"Domenii_interdisciplinare",
				"Educație",
				"Enunțuri_științifice",
				"Filozofia_științei",
				"Instrumente_științifice",
				"Istoria_științei",
				"Științe_auxiliare_ale_istoriei",
				"Literatură_științifică",
				"Lucrări_științifice",
				"Metoda_științifică",
				"Metode_științifice",
				"Metodologie",
				"Muzee_științifice",
				"Ocupații_științifice",
				"Protoștiință",
				"Reviste_de_știință",
				"Știința_în_societate",
				"Software_științific",
				"Teorii",
				"Teorii_științifice",
				"Terminologie_științifică",
				"Științe_aplicate",
				"Științe_cognitive",
				"Științe_medicale",
				"Cioturi_din_domeniul_științei" })));
		categoryTitleMap.put("Tehnologie", new HashSet<String>(Arrays.asList(new String[] { 
				"Știință_și_tehnologie_după_țară",
				"Știință_și_tehnologie_după_continent",
				"Tehnologie_după_tip",
				"Aerospațial",
				"Astronautică",
				"Biotehnologie",
				"Cioturi_Tehnologie",
				"CNC,_CAD_și_CAM",
				"Companii_din_domeniul_tehnologiei",
				"Echipament",
				"Electrotehnică",
				"Energie",
				"Filme_despre_tehnologie",
				"Gospodărirea_apelor",
				"Gospodărirea_pământului",
				"Industrie",
				"Inginerie",
				"Inovație",
				"Invenții",
				"Istoria_tehnologiei",
				"Mecanică",
				"Motoare",
				"Operații_tehnologice",
				"Organe_de_mașini",
				"Premii_științifice",
				"Rachetologie",
				"Schimbări_tehnologice",
				"Servicii_tehnice_din_construcții",
				"Sisteme_de_tehnologie",
				"Tehnică_militară",
				"Tehnologie_ipotetică",
				"Tehnologii_fictive",
				"Tipuri_de_tehnologie" })));
		categoryTitleMap.put("Sport", new HashSet<String>(Arrays.asList(new String[] { 
				"Liste_despre_sport",
				"Sport_după_continent",
				"Sport_după_oraș",
				"Sporturi_după_tip",
				"Sport_după_țară",
				"Decenii_în_sport",
				"Cioturi_Arene_sportive",
				"Categorii_după_sport",
				"Ocupații_în_sport",
				"Sporturi_aeronautice",
				"Air_hockey",
				"Animale_în_sport",
				"Sportul_în_antichitate",
				"Antrenament_sportiv",
				"Atletism",
				"Badminton",
				"Bowling",
				"Campioni",
				"Ciclism",
				"Cioturi_Competiții_sportive",
				"Cioturi_sport",
				"Cluburi_Sportive_(CS)",
				"Competiții_sportive",
				"Crichet",
				"Croquet",
				"Curse_de_cai",
				"Distincții_sportive",
				"Dresaj",
				"Echipament_sportiv",
				"Echipe_sportive_naționale",
				"Evenimente_sportive",
				"Sport_feminin",
				"Sport_Feminin",
				"Golf",
				"Haltere",
				"Handbal",
				"Sporturi_de_iarnă",
				"Istoria_sportului",
				"Jocuri_Paralimpice",
				"Jocuri_sportive_naționale",
				"Jocurile_Panafricane",
				"Jocurile_Panamericane",
				"Judo",
				"Maraton",
				"Medicină_sportivă",
				"Organizații_sportive",
				"Orientare",
				"Parapantism",
				"Pescuit",
				"Popice",
				"Premii_în_sport",
				"Publicații_sportive",
				"Rugbi",
				"Scandaluri_sportive",
				"Scrimă",
				"Site-uri_web_de_sport",
				"Spectacole",
				"Sportivi",
				"Stadioane",
				"Sumo",
				"Săli_de_sport",
				"Televiziune_de_sport",
				"Tenis",
				"Terminologie_sportivă",
				"Tir_sportiv",
				"Tragedii_în_sport",
				"Volei",
				"Wrestling",
				"Înot",
				"Șah" })));
		return categoryTitleMap;
	}
	
	public static void main(String[] args) throws XMLStreamException, IOException {
		/*
		 * Parameters
		 */
		// German
		String pathCategory = "data/wikipedia/dewiki-20140407-category.sql";
		String pathCategorylinks = "data/wikipedia/dewiki-20140407-categorylinks.sql";
		String pathPagesArticles = "data/wikipedia/dewiki-20140407-pages-articles.xml";
		String pathStopWords = "data/stopwords/german.txt";
		Map<String, Set<String>> categoryTitleMap = getGermanCategoryTitleMap();
		SnowballStemmer stemmer = new germanStemmer();
		
		// Romanian
//		String pathCategory = "data/wikipedia/rowiki-20140507-category.sql";
//		String pathCategorylinks = "data/wikipedia/rowiki-20140507-categorylinks.sql";
//		String pathPagesArticles = "data/wikipedia/rowiki-20140507-pages-articles.xml";
//		String pathStopWords = "data/stopwords/romanian.txt";
//		Map<String, Set<String>> categoryTitleMap = getRomanianCategoryTitleMap();
//		SnowballStemmer stemmer = new romanianStemmer();
		
		// General
		String pathArffTrainingFile = "data/tmp/output-training.arff";
		String pathArffTestingFile = "data/tmp/output-testing.arff";
		String pathLogFile = "data/tmp/WikipediaClassificationTestApp.log";
		int maxPagesPerCategory = 100;
				
		/*
		 * Prepare vocabulary
		 */
		SortedSet<String> globalWordSet = new TreeSet<String>();
		Set<String> stopWordSet = PageFactory.getStopWordSet(pathStopWords);
		
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
			Set<Category> categorySet = CategoryFactory.getCategorySet(pathCategory, categoryTitleMap);
			LOG.info("Categories:");
			for(Category category : categorySet) {
				LOG.info(category.toString());
			}
			
			/*
			 * Get pageIds belonging to categories
			 */
			LOG.info("Receiving Category-Page-Links...");
			Map<String, Set<Integer>> categoryToPageIdMap = CategoryFactory.getLinkedPageIdMap(pathCategorylinks, pathCategory, categorySet);
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
