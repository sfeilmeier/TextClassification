package naiveBayes;

import java.util.HashMap;
import java.util.HashSet;

/*
 * Class: LabeledRecord
 * 
 * One record of testing/training data (e.g. a document) with label
 */
public class LabeledRecord extends Record {
	protected HashSet<String> labels;
	
	public HashSet<String> getLabels() {
		return labels;
	}
	
	public LabeledRecord(HashMap<String, Integer> values, HashSet<String> labels, String title) {
		super(values, title);
		this.labels = labels;
	}
	
	@Override
	public String toString() {
		return "LabeledRecord [values=" + values + ", label=" + labels + "]";
	}
}
