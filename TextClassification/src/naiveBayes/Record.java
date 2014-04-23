package naiveBayes;

import java.util.HashMap;

/*
 * Class: Record
 * 
 * One record set with data (e.g. a document); without label 
 */
public class Record {

	protected HashMap<String, Integer> values;

	public Record(HashMap<String, Integer> values) {
		super();
		this.values = values;
	}

	public HashMap<String, Integer> getValues() {
		return values;
	}

	@Override
	public String toString() {
		return "Record [values=" + values + "]";
	}

}