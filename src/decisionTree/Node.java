package decisionTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Node {
	private List<Record> dataSet; // all data set list
	private List<Integer> allSamples; // index of the samples in this node
	private int classificationLabelIndex; // index of the "classification" attribute
	private double entropy = -1; // entropy of this node
	private HashMap<String, Integer> classCounts = null; // <classificationName, number of samples in this class>
	private String classification;	// if this node is a leaf node, "classification" attribute will be set as the main class in this node

	/**
	 * Default contstructor
	 */
	public Node() {
	}

	/**
	 * constructor with parameters
	 * @param dataSet
	 * @param labelIndex
	 */
	public Node(List<Record> dataSet, int labelIndex) {
		this.allSamples = new ArrayList<Integer>();
		this.dataSet = dataSet;
		this.classificationLabelIndex = labelIndex;
		this.entropy = 0;
	}

	
	/**
	 * @return all samples' index in this Node
	 */
	public List<Integer> getAllSamples() {
		return this.allSamples;
	}

	/**
	 * @return all data set in this node
	 */
	public List<Record> getDataSet() {
		return this.dataSet;
	}

	/**
	 * add a new sample index into this node
	 * @param index 
	 */
	public void addRecord(int index) {
		allSamples.add(index);
	}

	/**
	 * add many samples index into this node
	 * @param allIndex
	 */
	public void addAllRecord(List<Integer> allIndex) {
		allSamples.addAll(allIndex);
	}

	/**
	 * @return index of "classification" attribute in the attribute list
	 */
	public int getClassificationLabelIndex() {
		return this.classificationLabelIndex;
	}

	/**
	 * @return class counts HashMap<String, Integer>
	 * key = "class name", value = "sample counts in this class"
	 */
	public HashMap<String, Integer> getClassCounts() {
		if (this.classCounts == null || this.classCounts.size() == 0) {
			this.updateClassCounts();
		}
		return classCounts;
	}

	
	/**
	 * count all the classes in this node, 
	 * update classCounts HashMap<String, Integer>
	 * key = "class name", value = "sample counts in this class"
	 */
	public void updateClassCounts() {
		if (this.classCounts == null || this.classCounts.size() == 0) {
			classCounts = new HashMap<String, Integer>();
			for (Integer i : this.allSamples) {
				String curClass = this.dataSet.get(i).getValue(classificationLabelIndex);
				
				if (!classCounts.containsKey(curClass)) {
					classCounts.put(curClass, 1);
				} else {
					classCounts.put(curClass, classCounts.get(curClass) + 1);
				}
			}
		}
	}

	/** calculate and update entropy of this node
	 * first update class Counts
	 * then calculate entropy based on classCounts
	 */
	public void updateEntropy() {
		updateClassCounts();
		double entropy = 0.0;
		int totalCounts = allSamples.size();
		for (Integer i : this.classCounts.values()) {
			double proportion = i * 1.0 / totalCounts;
			entropy += -1.0 * proportion * Math.log(proportion);
		}
		this.entropy = entropy;
	}
	

	/**
	 * @return entropy of this node
	 */
	public double getEntropy() {
		if (this.entropy == -1) {
			this.updateEntropy();
		}
		return this.entropy;
	}


	/**
	 * @return classCounts in this node
	 * classCounts : HashMap<String, Integer>
	 * key = "class name", value = "sample counts in this class"
	 */
	public String getClassification() {
		if (this.classification == null) {
			this.updateClassification();
		}
		return classification;
	}

	/**
	 * update the classification of this node (leaf)
	 * classification of this node is the classification of the majority samples in this node
	 */
	public void updateClassification() {
		int maxCount = 0;
		updateClassCounts();
		for (String name : this.classCounts.keySet()) {
			if (this.classCounts.get(name) > maxCount) {
				maxCount = this.classCounts.get(name);
				this.classification = name;
			}
		}
	}
}
