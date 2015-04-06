package decisionTree;

public class NumAttribute extends Attribute{
	
	/**
	 * Default constructor
	 */
	public NumAttribute() {
	}
	
	/**
	 * Constructor with parameter: name
	 * @param name : name of this attribute
	 */
	public NumAttribute(String name) {
		super(name);
	}
	
	public String toString() {
		StringBuilder res = new StringBuilder();
		res.append("@attribute ").append(super.getName()).append(" is a numeric attribute");
		return res.toString();
	}
	
	/** 
	 * Implements abstract: verify if this value is valid
	 * @param value : value of this attribute
	 * @return true if this value is a double, otherwise return false
	 */
	public boolean verify(String value) {
		try {
			Double.parseDouble(value);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Given the value and splitValue, return the name of its child
	 * @param recValue: record's value on this attribute
	 * @param splitValue: split value on this attribute
	 * @return if (recValue <= splitValue) it will return "lower" 
	 * otherwise it will return "greater"
	 */
	public String getChildValue(String recValue, double splitValue) {
		if (Double.parseDouble(recValue) <= splitValue) {
			return "lower";
		}
		else {
			return "higher";
		}
	}
}

