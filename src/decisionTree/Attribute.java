package decisionTree;

public abstract class Attribute {
	private String name; // name of the attribute
	
	
	/**
	 * Default constructor
	 */
	public Attribute() {
	}
	
	/**
	 * Constructor with parameter: name
	 * @param name: name of this attribute
	 */
	public Attribute(String name) {
		this.name = name;
	}
	
	/**
	 * Getter for name
	 * @return the name of this attribute
	 */
	
	public String getName() {
		return this.name;
	}
	
	/**
	 * Setter for name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Verify if this value is valid for the attribute
	 * @param value: verify if this value is valid
	 * @return true if this value is valid, otherwise return false
	 */
	abstract public boolean verify(String value);
	
	/**
	 * Given the value and splitValue, return the name of its child
	 * @param recValue: record's value on this attribute
	 * @param splitValue: split value on this attribute
	 * @return if it is a nominal value, it will return "recValue" directly, 
	 * because nominal attribute split at every possible value
	 * if it is a numeric value, it will return "lower" or "greater"
	 */
	abstract public String getChildValue(String recValue, double splitValue);
}
