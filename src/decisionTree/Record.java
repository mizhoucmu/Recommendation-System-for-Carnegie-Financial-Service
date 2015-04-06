package decisionTree;

import java.util.*;

public class Record {
	private List<String> values;
	
	/**
	 * Default constructor
	 */
	public Record() {
		this.values = new ArrayList<String>();
	}
	
	/**
	 * constructor with parameters 
	 * @param values
	 */
	public Record(List<String> values) {
		this.values = values;
	}
	
	/**
	 * @param values
	 * set values for all attributes
	 */
	public void setAttribute(String[] values) {
		for (int i = 0; i < values.length; i++) {
			this.values.add(values[i]);
		}
	}
	
	/**
	 * @param index
	 * @return the value of attribute[index]
	 */
	public String getValue(int index) {
		if (index < 0 || index >= values.size()) {
			return "";
		}
		else {
			return values.get(index);
		}
	}
	
	/**
	 * @return all the values of this record
	 */
	public List<String> getValues() {
		return this.values;
	}
	
	public String toString() {
		StringBuilder res = new StringBuilder();
		for (int i = 0; i < values.size(); i++) {
			res.append(values.get(i));
			res.append(" ");
		}
		return res.toString();
	}
	
	/**
	 * verify if this record is compatible with all attributes
	 * for numeric attributes, it will check if the value is a double
	 * for nominal attributes, it will check if the value is valid
	 * @param attributes list
	 * @return true if all values are valid, otherwise return false
	 */
	public boolean verify(List<Attribute> attributes) {
		if ((this.values.size() == attributes.size()) || this.values.size() == (attributes.size() - 1)) {
			for (int i = 0; i < attributes.size() - 1; i++) {
				if (attributes.get(i).verify(values.get(i)) == false) {
					System.out.println("this attribute value is invalid: " + values.get(i) + " for " + attributes.get(i).getName());
					return false;
				}
			}
			return true;
		}
		else {
			System.out.println("value size = " + this.values.size());
			System.out.println("attribute size = " + attributes.size());
			return false;
		}
	}
}
