package naiveBayes;

import java.util.HashMap;

/*
 * Class: Label
 * 
 * Classification of a record set.
 * (e.g. class "c22")
 */
public class Label {
	private String name;
	HashMap<Attribute,Integer> attributes = new HashMap<Attribute,Integer>();
	private int totalNoOfAttributes = 0;
	private int totalNoOfDistinctAttributes = 0;
	private int totalNoOfRecords = 0;

	public Label(String name) {
		super();
		this.name = name;
	}

	@Override
	public String toString() {
		return "Label [name=" + name + "]";
	}

	public int getAttributeCount(Attribute attribute) {
		Integer attributeCount = attributes.get(attribute);
		if(attributeCount == null) attributeCount = 0;
		return attributeCount;
	}
	public String getName() {
		return name;
	}
	public int getTotalNoOfAttributes() {
		return totalNoOfAttributes;
	}

	public int getTotalNoOfDistinctAttributes() {
		return totalNoOfDistinctAttributes;
	}

	public int getTotalNoOfRecords() {
		return totalNoOfRecords;
	}

	public void increaseTotalNoOfRecords() {
		totalNoOfRecords++;
	}

	public void learn(Attribute attribute, int count) {
		Integer attributeCount = attributes.get(attribute);
		attributes.put(attribute, (attributeCount == null) ? count : attributeCount + count);
		totalNoOfAttributes += count;
		totalNoOfDistinctAttributes++;
	}
}
