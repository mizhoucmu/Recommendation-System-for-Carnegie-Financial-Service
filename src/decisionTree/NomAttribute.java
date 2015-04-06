package decisionTree;

import java.util.ArrayList;
import java.util.List;

public class NomAttribute extends Attribute {
	private List<String> valueSet; // all possible values of this nominal attribute
	
	/**
	 * Default constructor
	 */
	public NomAttribute() {
		valueSet = new ArrayList<String>();
	}
	
	
	/**
	 * Constructor with parameter: name
	 * @param name: name of this attribute
	 */
	public NomAttribute(String name) {
		super(name);
		valueSet = new ArrayList<String>();
	}
	
	/**
	 * @return all possible value set of this attribute
	 */
	public List<String> getValueSet() {
		return this.valueSet;
	}
	
	
	/**
	 * Add a new possible value to this attribute
	 * @param value : new possible value for this attribute
	 */
	public void addValue(String value) {
		this.valueSet.add(value);
	}
	
	/**
	 * @return the name of this attribute
	 */
	public String getName() {
		return super.getName();
	}
	
	
	public String toString() {
		StringBuilder res = new StringBuilder();
		res.append("@attribute ");
		res.append(super.getName());
		res.append(" has value set :[");
		for (String s: this.valueSet) {
			res.append(s);
			res.append(" ");
		}
		res.append("]");
		return res.toString();
	}
	
	/**
	 * Verify if this value is valid for the attribute
	 * @param value: verify if this value is valid
	 * @return true if this value is valid, otherwise return false
	 */
	public boolean verify(String value) {
		if (this.valueSet.contains(value)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Given the value and splitValue, return the name of its child
	 * @param recValue: record's value on this attribute
	 * @param splitValue: split value on this attribute
	 * @return "recValue" directly,
	 */
	public String getChildValue(String recValue, double splitValue) {
		return recValue;
	}
}
