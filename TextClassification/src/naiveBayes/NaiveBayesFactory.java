package naiveBayes;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class NaiveBayesFactory {

	/**
	 * Read arff-file and return NaiveBayes-object
	 * @throws FileNotFoundException 
	 */
	public static LinkedList<LabeledRecord> readArffFile(String path) throws FileNotFoundException {
		LinkedList<LabeledRecord> records = new LinkedList<LabeledRecord>();
	
		Reader fr = new FileReader(path);;
		BufferedReader br = new BufferedReader(fr);
		
		try {
			String line;
			String lastKeyword = "";
			while( (line = br.readLine()) != null ) {
				if(line.isEmpty() | line.startsWith("#")) {
					// Empty or comment -> ignore
					
				} else if(line.startsWith("@attribute")){
					// Found attribute -> ignore
					lastKeyword = "@attribute";

				} else if(line.startsWith("@topic")) {
					// Found topic -> ignore
					lastKeyword = "@topic";

				} else if(line.startsWith("@data")){
					// Found data
					lastKeyword = "@data";

				} else if(lastKeyword == "@data") {
					// read data record				
					HashSet<String> labels = new HashSet<String>();
					HashMap<String, Integer> values = new HashMap<String, Integer>();
					
					String[] lineS = line.split(" ");
					boolean readLabel = false;					
					for(String field : lineS) {
						if(field.compareTo("#")==0) {
							// next will be label
							readLabel = true;
							continue;
						} else if(readLabel) {
							// read label
							labels.add(field);
							// ... and stop -> read only first label
							//break;
						} else {
							// read attributes + add every attribute to list
							String[] fieldS = field.split(":");
							String attribute = fieldS[0];
							// binary mode: "Attribute is in text" vs. "is not in text" 
							//values.put(attribute, 1);
							// weighed mode: "how often is Attribute in text?"
							values.put(attribute, Integer.parseInt(fieldS[1]));
						}
					}
					LabeledRecord record = new LabeledRecord(values, labels);
					records.add(record);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// close file
				br.close();
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return records;
	}

	public static LinkedList<LabeledRecord> readXmlFile(String path) {
		return null;
	}
}
