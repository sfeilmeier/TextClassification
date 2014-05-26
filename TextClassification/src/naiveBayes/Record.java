package naiveBayes;

import java.util.HashMap;

/*
 * Class: Record
 * 
 * One record set with data (e.g. a document); without label 
 */
public class Record {

	protected HashMap<String, Integer> values;
	protected String title;

	public Record(HashMap<String, Integer> values, String title) {
		super();
		this.values = values;
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public HashMap<String, Integer> getValues() {
		return values;
	}

	@Override
	public String toString() {
		return "Record [values=" + values + "]";
	}

}