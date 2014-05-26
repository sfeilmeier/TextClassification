package naiveBayes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class NaiveBayes {
	private int totalNoOfRecords = 0;
	private int totalNoOfAttributes = 0;
	private HashMap<String, Label> labels = new HashMap<String, Label>();
	private HashMap<String, Attribute> attributes = new HashMap<String, Attribute>();
	
	public int getTotalNoOfRecords() {
		return totalNoOfRecords;
	}
	public int getTotalNoOfAttributes() {
		return totalNoOfAttributes;
	}

	public NaiveBayes(LinkedList<LabeledRecord> records) {
		super();
		for(LabeledRecord record : records) {
			learn(record);
		}
		
	}

	public void learn(LabeledRecord record) {
		// values: AttributeName : Value
		HashMap<String, Integer> attributeValues = record.getValues();
		HashSet<String> labelNames = record.getLabels();

		for(String labelName : labelNames) {
			Label label = labels.get(labelName);
			if(label == null) {
				label = new Label(labelName);
				labels.put(labelName, label);
			}
			label.increaseTotalNoOfRecords();
		}
		
		for(String attributeName : attributeValues.keySet()) {
			Attribute attribute = attributes.get(attributeName);
			if(attribute == null) {
				attribute = new Attribute(attributeName);
				attributes.put(attributeName, attribute);
			}
			int count = attributeValues.get(attributeName);
			for(String labelName : labelNames) {
				Label label = labels.get(labelName);
				attribute.learn(label, count);
				label.learn(attribute, count);				
			}
			totalNoOfAttributes += count;
			attribute.increaseTotalNoOfDistinctRecords();
		}
		
		totalNoOfRecords++;
	}
	
	
	/**
	 * Returns a Set of Attributes with length of 'noOfAttributesToBeConsidered'
	 * consisting of the attributes with the highest 'Mutual Information' 
	 * (Calculating A(t,c) according to 
	 * http://nlp.stanford.edu/IR-book/pdf/13bayes.pdf; 13.5.1 Mutual Information)
	 * 
	 * Pseudocode:
	 * For each Label:
	 * 		For each Attribute:
	 * 			Calculate Mutual Information (MI) of the Attribute
	 * 			Update list of MI per Attribute (=infoMap) (insert or add value to existing)
	 * Until infoMap is longer than 'noOfAttributesToBeConsidered':
	 * 		Sort list by MI value
	 * 		Remove smallest MI values
	 * 
	 * @param noOfAttributesToBeConsidered
	 * @return Set of Attributes
	 */
	public Set<Attribute> getFeatureSelection(int noOfAttributesToBeConsidered) {
		// From http://nlp.stanford.edu/IR-book/pdf/13bayes.pdf; 13.5.1 Mutual Information:
		Set<Attribute> featureSelection = new HashSet<Attribute>(); 
		//HashMap<Label, Set<Attribute>> featureSelection = new HashMap<Label, Set<Attribute>>();
		HashMap<Attribute, Double> infoMap = new HashMap<Attribute,Double>();
		for(Label label : labels.values()) {
			for(Attribute attribute : attributes.values()) {
				//N<LabelMatch?><AttributeInRecord?>
				
				//int N = totalNoOfExamples;
				int N = totalNoOfAttributes;
				
				// Attribute ✔
				int N1_ = attribute.getTotalNoOfDistinctRecords();
				//int N1_ = attribute.getTotalNoOfRecords();
				int N0_ = N - N1_;

				// Label ✔  
				//int N_1 = label.getTotalNoOfRecords();
				int N_1 = label.getTotalNoOfAttributes();
				int N_0 = N - N_1;
				
				// Label ✔    Attribute ✔
				int N11 = attribute.getLabelCount(label);

				// Label ✖    Attribute ✔
				int N10 = N1_ - N11;
				
				// Label ✔    Attribute ✖
				int N01 = N_1 - N1_ + N10;
				
				// Label ✔    Attribute ✖
				int N00 = N - N_1 - N10;
				
				// I(U;C)
				double i_1 = ((double)N11/N)*(Math.log((double)(N*N11)/N1_*N_1)/Math.log(2));
				double i_2 = ((double)N01/N)*(Math.log((double)(N*N01)/N0_*N_1)/Math.log(2));
				double i_3 = ((double)N10/N)*(Math.log((double)(N*N10)/N1_*N_0)/Math.log(2));
				double i_4 = ((double)N00/N)*(Math.log((double)(N*N00)/N0_*N_0)/Math.log(2));
				double i =    (Double.isNaN(i_1) ? 0 : i_1) 
							+ (Double.isNaN(i_2) ? 0 : i_2)
							+ (Double.isNaN(i_3) ? 0 : i_3)
							+ (Double.isNaN(i_4) ? 0 : i_4);
				//System.out.println("\t\t" + label.getName() + "\tnot " + label.getName());
				//System.out.println("    " + attribute.getName() + "\t" + N11 + "\t" + N10 + "\t" + N1_);
				//System.out.println("not " + attribute.getName() + "\t" + N01 + "\t" + N00 + "\t" + N0_);
				//System.out.println("\t\t" + N_1 + "\t" + N_0);
				//System.out.println(i);
				//System.out.println("");
				Double infoMapValue = infoMap.get(attribute);
				infoMap.put(attribute, (infoMapValue == null) ? i : infoMapValue + i);				
			}
		}
		if(infoMap.values().size() > noOfAttributesToBeConsidered) {
			// we have more attributes than should be considered -> remove attributes with low info
			Double[] infos = new Double[infoMap.values().size()]; 
			infoMap.values().toArray(infos);
			Arrays.sort( infos );
			Double lastInfoLimit = Double.NEGATIVE_INFINITY;
			for(int infoLimitIndex=1; infoMap.size() > noOfAttributesToBeConsidered; infoLimitIndex++) {
				Double infoLimit;
				if(infoLimitIndex>=infos.length) {
					infoLimit = Double.POSITIVE_INFINITY;
				} else {
					infoLimit = infos[infoLimitIndex];
				}
				if(lastInfoLimit.compareTo(infoLimit)<0) {
					//don't use the same limit twice
					lastInfoLimit = infoLimit;
						
					// copy infoLabelMap-keys, because removing is not possible while iterating
					HashSet<Attribute> infoLabelKeys = new HashSet<Attribute>(infoMap.keySet());
					for(Attribute attribute : infoLabelKeys) {
						if(infoMap.size() <= noOfAttributesToBeConsidered) {
							break;
						}
						if(infoMap.get(attribute) < infoLimit) {
							infoMap.remove(attribute);
						}
					}
				}
			}
		}		
		featureSelection.addAll(infoMap.keySet());
		
		//System.out.println(featureSelection);
		return featureSelection;
	}

	/**
	 * Compares the result label given by NaiveBayes algorithm with the real label.
	 * 
	 * Pseudocode:
	 * For each Label:
	 * 		'probability' <- 0
	 * 		Calculate 'attributeDenominator'
	 * 		For each Attribute:
	 * 			Calculate 'attributeNumerator'
	 * 			Calculate 'attributeProbability' (logarithm)
	 *			'probability' += 'attributeProbability' * 'noOfOccurrences' of Attribute
	 *
	 *		Calculate 'labelProbability'
	 *		'probability' += 'labelProbability'
	 *		if 'probability' is highest probability till now: set as 'maxProbability'
	 * 
	 * If 'maxProbabilityLabel' is one of the real labels:
	 * 		prediction was correct, return true
	 * otherwise
	 * 		misprediction, return false 
	 * 
	 * @param record	One record (document) of a testing set
	 * @param featureSelection	Set of attributes that should be used for classification
	 * @return	true: prediction correct; false: prediction incorrect 
	 */
	public boolean testNaiveBayes(LabeledRecord record, Set<Attribute> featureSelection) {
		// values: AttributeName : Value
		HashMap<String, Integer> values = record.getValues();
		Double maxProbability = Double.NEGATIVE_INFINITY;
		Label maxProbabilityLabel = null;
		
		//System.out.println(record);
		for(Label label : labels.values()) {
			//System.out.println("LABEL " +label.getName() );
			//String s = "";
			double probability = 0;
			int attributeDenominator = label.getTotalNoOfAttributes() + featureSelection.size();
			for(String attributeName : values.keySet()) {
				Attribute attribute = attributes.get(attributeName);
				if(attribute == null || !featureSelection.contains(attribute)) continue;
				// From http://nlp.stanford.edu/IR-book/pdf/13bayes.pdf; equation 13.4 (logarithm) and 13.7 (formula):
				int attributeNumerator = attribute.getLabelCount(label) + 1;
				double attributeProbability = Math.log((double)attributeNumerator/attributeDenominator);
				int noOfOccurrences = values.get(attributeName); 
				probability += attributeProbability * noOfOccurrences;
				//System.out.println("\t\tMatching selection: " + attribute + "\t(" + Math.round(attributeProbability) + "x" + noOfOccurrences + ")");
				//s += "log(" + attributeNumerator + "/" + attributeDenominator + ")x" + values.get(attributeName) + "+";
			}
			int labelNumerator = label.getTotalNoOfRecords();
			int labelDenominator = totalNoOfRecords;
			double labelProbability = Math.log((double)labelNumerator/labelDenominator);
			//s += "log(" + labelNumerator + "/" + labelDenominator + ") ";
			//System.out.println(s);
			probability += labelProbability ;
			//System.out.println("\t" + label.getName() + ": " + Math.round(probability) + " for " + featureSelection);
			
			if(probability > maxProbability) {
				/*TODO: 13.2 If a document’s terms do not provide clear
				evidence for one class versus another, we choose the one that has a higher
				prior probability.*/
				//System.out.println("Set label to " + label.getName());
				maxProbability = probability;
				maxProbabilityLabel = label;
			}
		}
		
		if(record.getLabels().contains(maxProbabilityLabel.getName())) {
			// well predicted
			//System.out.println("✔    " + maxProbabilityLabel.getName());
			return true;
		} else {
			// wrong prediction
//			System.out.println("✖    '" + record.getTitle() + "' - predicted '" + maxProbabilityLabel.getName() + "' instead of '" + record.getLabels() + "'");
			return false;
		}
	}
	
	@Override
	public String toString() {
		String s = "";
		s += "#Samples " + totalNoOfRecords + "\n";
		s += "#Attributes " + attributes.size() + "\n";
		s += "#Topics " + labels.size() + "\n\n";
		for(Attribute attribute : attributes.values()) {
			s += "@attribute " + attribute.getName() + "\t [";
			for(Label label : labels.values()) {
				s += " " + label.getName() + ":" + attribute.getLabelCount(label);
			}
			s += " ]\n"; 
		}
		for(Label label : labels.values()) {
			s += "@topic " + label.getName() + "\t [";
			for(Attribute attribute : attributes.values()) {
				s += " " + attribute.getName() + ":" + label.getAttributeCount(attribute);
			}
			s += " ]\n";
		}		
		return s;
	}
}
