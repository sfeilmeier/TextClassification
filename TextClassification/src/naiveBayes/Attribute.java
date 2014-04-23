package naiveBayes;

import java.util.HashMap;

/*
 * Class: Attribute
 * 
 * An Attribute is a word in a document (record). For each attribute we are storing
 * its occurrences per label. 
 */
public class Attribute {
	private String name = "";
	public String getName() {
		return name;
	}

	HashMap<Label,Integer> labels = new HashMap<Label,Integer>();
	private int totalNoOfDistinctRecords = 0;
	private int totalNoOfRecords = 0;
	
	public int getTotalNoOfRecords() {
		return totalNoOfRecords;
	}
	public int getTotalNoOfDistinctRecords() {
		return totalNoOfDistinctRecords;
	}
	public void increaseTotalNoOfDistinctRecords() {
		totalNoOfDistinctRecords++;
	}
	
	public Attribute(String name) {
		super();
		this.name = name;
	}
	
	public void learn(Label label, int count) {
		Integer labelCount = labels.get(label);
		labels.put(label, (labelCount == null) ? count : labelCount + count);
		totalNoOfRecords += count;
	}
	
	public int getLabelCount(Label label) {
		Integer labelCount = labels.get(label);
		if(labelCount == null) labelCount = 0;
		return labelCount;
	}

	@Override
	public String toString() {
		return "'" + name + "'";
	}
}
