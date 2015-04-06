package decisionTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class TreeNode extends Node {
	private List<Attribute> attributes; // all attributes
	private int nextSplitAttrIdx; // next split attribute index
	private double nextSplitValue; // next spit value on the very attribute
	private double threshhold = 0.0; // entropy threshhold used to stop
										// splitting
	private HashMap<String, TreeNode> children; // all children
	private HashSet<Integer> splittedAttrs; // all nominal attributes that have
											// been used as split attribute
											// before
	private int level; // level of this node, (level of root = 0)
	private String displayPrefix; // used to display tree, eg. |----Type =
									// librarian [C2] out of {C4x1 C2x14}

	/**
	 * Default constructor
	 */
	public TreeNode() {
		super();
	}

	/**
	 * Constructor with parameters
	 * 
	 * @param dataSet
	 * @param labelIndex
	 * @param attributes
	 * @param level
	 * @param threshhold
	 */
	public TreeNode(List<Record> dataSet, int labelIndex,
			List<Attribute> attributes, int level, double threshhold) {
		super(dataSet, labelIndex);
		this.attributes = attributes;
		splittedAttrs = new HashSet<Integer>();
		this.level = level;
		this.displayPrefix = "";
		this.children = null;
		this.threshhold = threshhold;
	}

	/**
	 * split on this node 1. update the entropy of this node, split only when
	 * entropy >= threshhold and there are more than 1 sample in this node 2.
	 * iterate all attributes and all possible split value, find the way that
	 * minimize entropy
	 */
	public void split() {
		this.updateEntropy();

		if (super.getEntropy() > this.threshhold
				&& this.getAllSamples().size() > 1) {
			// going to split
			this.nextSplitAttrIdx = -1;
			this.nextSplitValue = 0.0;
			double minEntropy = Double.MAX_VALUE;
			for (int attrIndex = 0; attrIndex < attributes.size(); attrIndex++) {
				if (splittedAttrs.contains(attrIndex)
						|| attrIndex == super.getClassificationLabelIndex()) {
					continue;
				}
				double[] toSplit = this.getSplittedEntropy(attrIndex);
				if (toSplit[0] < minEntropy) {
					minEntropy = toSplit[0];
					this.nextSplitValue = toSplit[1];
					this.nextSplitAttrIdx = attrIndex;
				}
			}
			splitOnAttribute(this.nextSplitAttrIdx, this.nextSplitValue);
			for (TreeNode child : this.children.values()) {
				child.split();
			}
		} else { // will not split, will only update prefix of this node
			this.updateSelfPrefixClassfication();
		}

	}

	/**
	 * split given Attributes based on given split value
	 * 
	 * @param nextsplitAttrIndex
	 *            : next split attribute index
	 * @param nextSplitValue
	 *            : next split value
	 */
	private void splitOnAttribute(int nextsplitAttrIndex, double nextSplitValue) {
		Attribute splitAttr = attributes.get(nextsplitAttrIndex);
		this.children = new HashMap<String, TreeNode>();
		if (splitAttr.getClass().equals(NumAttribute.class)) {
			// split on numeric attribute : binary split (2 children)
			splitOnNumAttribute(nextsplitAttrIndex, nextSplitValue);
		} else {
			// split on nominal attributes: multiple children
			splitOnNomAttribute(nextsplitAttrIndex);
		}
	}

	/**
	 * split given numeric attribute based on given split value
	 * 
	 * @param nextsplitAttrIndex
	 *            : next split attribute index
	 * @param nextSplitValue
	 *            : next split value
	 */
	private void splitOnNumAttribute(int nextsplitAttrIndex,
			double nextSplitValue) {
		for (Integer i : this.getAllSamples()) {
			Record curRecord = this.getDataSet().get(i);
			double curValue = Double.valueOf(curRecord
					.getValue(nextsplitAttrIndex));
			if (curValue <= nextSplitValue) {
				if (!this.children.containsKey("lower")) {
					TreeNode lower = new TreeNode(super.getDataSet(),
							super.getClassificationLabelIndex(), attributes, this.level + 1,
							this.threshhold);
					lower.addRecord(i);
					lower.displayPrefix = "";
					for (int j = 0; j < this.level; j++) {
						lower.displayPrefix += "|----";
					}
					lower.displayPrefix += attributes.get(nextsplitAttrIndex)
							.getName();
					lower.displayPrefix += " <= ";
					lower.displayPrefix += nextSplitValue;
					children.put("lower", lower);
				} else {
					children.get("lower").addRecord(i);
				}
			} else {
				if (!this.children.containsKey("higher")) {
					TreeNode higher = new TreeNode(super.getDataSet(),
							super.getClassificationLabelIndex(), attributes, this.level + 1,
							this.threshhold);
					higher.addRecord(i);
					higher.displayPrefix = "";
					for (int j = 0; j < this.level; j++) {
						higher.displayPrefix += "|----";
					}
					higher.displayPrefix += attributes.get(nextsplitAttrIndex)
							.getName();
					higher.displayPrefix += " > ";
					higher.displayPrefix += nextSplitValue;
					children.put("higher", higher);
				} else {
					children.get("higher").addRecord(i);
				}
			}
		}
	}

	/**
	 * Split on nominal attribute
	 * 
	 * @param nextsplitAttrIndex
	 *            : given next split attribute index
	 */
	private void splitOnNomAttribute(int nextsplitAttrIndex) {
		for (Integer i : this.getAllSamples()) {
			Record curRecord = this.getDataSet().get(i);
			String curValue = curRecord.getValue(nextsplitAttrIndex);
			if (children.containsKey(curValue)) {
				children.get(curValue).addRecord(i);
			} else {
				TreeNode insert = new TreeNode(super.getDataSet(),
						super.getClassificationLabelIndex(), attributes, this.level + 1,
						threshhold);
				insert.addRecord(i);
				insert.displayPrefix = "";
				for (int j = 0; j < this.level; j++) {
					insert.displayPrefix += "|----";
				}
				insert.displayPrefix += attributes.get(nextsplitAttrIndex)
						.getName();
				insert.displayPrefix += " = ";
				insert.displayPrefix += curValue;
				children.put(curValue, insert);
			}
		}

	}

	/**
	 * Add classification information to display prefix (it only runs when this
	 * node is a leaf) eg. "[C2] out of {C4x1 C2x14}" it means there are 1
	 * sample of C4 class and 14 samples of C2 class and the classification of
	 * this leaf node is [C2]
	 */
	private void updateSelfPrefixClassfication() {
		super.updateClassification();
		this.appendDisplayPrefix(" [");
		this.appendDisplayPrefix(super.getClassification());
		this.appendDisplayPrefix("] out of {");
		for (String name : this.getClassCounts().keySet()) {
			this.appendDisplayPrefix(name);
			this.appendDisplayPrefix("x");
			this.appendDisplayPrefix(this.getClassCounts().get(name).toString());
			this.appendDisplayPrefix(" ");
		}
		this.removeLastSpace();
		this.appendDisplayPrefix("}");
	}

	/**
	 * remove the last space in display prefix, used in
	 * updateSelfPrefixClassfication() to format display prefix
	 */
	private void removeLastSpace() {
		this.displayPrefix = this.displayPrefix.substring(0,
				this.displayPrefix.length() - 1);
	}

	/**
	 * Append string to display prefix
	 * @param append: String to append to the display prefix
	 */
	public void appendDisplayPrefix(String append) {
		this.displayPrefix += append;
	}

	/**
	 * @return display prefix of this node
	 */
	public String getDisplayPrefix() {
		return this.displayPrefix;
	}

	/**
	 * @return children of this node 
	 * structure: HashMap<String, TreeNode> 
	 * key : split value of this branch 
	 * value : pointer to this child
	 */
	public HashMap<String, TreeNode> getChildren() {
		return this.children;
	}

	/**
	 * @param attrIndex: index of the attribute to split
	 * @return double[2] 
	 * [0] = minimal entropy, [1] = bestSplitValue
	 */
	private double[] getSplittedEntropy(int attrIndex) {
		// get splitted Entropy based on attribute: attrIndex
		if (attributes.get(attrIndex).getClass().equals(NomAttribute.class)) {
			// nomial Attribute
			return getSplittedEntropyOnNomAttribute(attrIndex);
		} else {
			// numeric Attribute
			return getSplittedEntropyOnNumAttribute(attrIndex);
		}
	}

	/**
	 * @return hashmap like this:{(c1,0),(c2,1),...,(c5,4)} 
	 * key: c1, c2, .. c5 is the name of the classes 
	 * value: count of samples of this class
	 */
	private HashMap<String, Integer> getClassIndex() {
		HashMap<String, Integer> classesIndex = new HashMap<String, Integer>();
		int index = 0;
		for (String className : super.getClassCounts().keySet()) {
			if (!classesIndex.containsKey(className)) {
				classesIndex.put(className, index++);
			}
		}
		return classesIndex;
	}

	/**
	 * Split based on numeric attribute
	 * 
	 * @param attrIndex : index of the attribute
	 * @return double[2] 
	 * [0] = minimal entropy, [1] = bestSplitValue
	 */
	private double[] getSplittedEntropyOnNumAttribute(int attrIndex) {
		double[] res = new double[2];
		HashMap<Double, int[]> classCountsOfEachValue = new HashMap<Double, int[]>();
		List<Double> sortedValue = new ArrayList<Double>();
		updateSortedValueAndCounts(attrIndex, classCountsOfEachValue, sortedValue);
		HashMap<String, Integer> classCounts = super.getClassCounts();
		int[] lower = new int[classCounts.keySet().size()];
		int[] greater = new int[classCounts.keySet().size()];
		for (int i = 0; i < sortedValue.size(); i++) {
			double curValue = sortedValue.get(i);
			for (int j = 0; j < classCountsOfEachValue.get(curValue).length; j++) {
				greater[j] += classCountsOfEachValue.get(curValue)[j];
			}
		}

		double minEntropy = Double.MAX_VALUE;
		double splitValue = Double.MAX_VALUE;

		// iterate on all values, find the best split value to minimize after
		// entropy
		// split into 2 branches: lower and greater
		for (int i = 0; i < sortedValue.size() - 1; i++) {
			double curValue = sortedValue.get(i);
			int[] distribution = classCountsOfEachValue.get(curValue);

			for (int j = 0; j < distribution.length; j++) {
				lower[j] += distribution[j];
				greater[j] -= distribution[j];
			}

			int lowerSum = 0;
			int greaterSum = 0;
			for (int j = 0; j < distribution.length; j++) {
				lowerSum += lower[j];
				greaterSum += greater[j];
			}

			double lowerRatio = (i + 1) * 1.0 / sortedValue.size();
			double gainRatio = -1.0 * lowerRatio * Math.log(lowerRatio)
					- (1 - lowerRatio) * Math.log(1 - lowerRatio);

			double lowerBranchEntropy = 0;
			double greaterBranchEntropy = 0;
			for (int j = 0; j < distribution.length; j++) {
				if (lower[j] > 0) {
					double lowerProportion = lower[j] * 1.0 / lowerSum;
					lowerBranchEntropy -= lowerProportion
							* Math.log(lowerProportion);
				}
				if (greater[j] > 0) {
					double greaterProportion = greater[j] * 1.0 / greaterSum;
					greaterBranchEntropy -= greaterProportion
							* Math.log(greaterProportion);
				}
			}
			double entropy = lowerBranchEntropy * lowerSum
					/ (lowerSum + greaterSum) + greaterBranchEntropy
					* greaterSum / (lowerSum + greaterSum);
			entropy /= gainRatio;

			if (entropy < minEntropy) {
				minEntropy = entropy;
				splitValue = curValue;
			}
		}

		res[0] = minEntropy;
		res[1] = splitValue;
		return res;
	}
	
	

	/**
	 * Used in getSplittedEntropyOnNumAttribute() to help calculate entropy on possible value
	 * @param attrIndex: index of the attribute that is used to calculate entropy
	 * @param classCountsOfEachValue: a pointer that will be updated in the method
	 * @param sortedValue: a pointer that will be updated in the method
	 */
	private void updateSortedValueAndCounts(int attrIndex,
			HashMap<Double, int[]> classCountsOfEachValue,
			List<Double> sortedValue) {

		HashMap<String, Integer> classesIndex = getClassIndex();
		for (Integer i : super.getAllSamples()) {
			Record curRecord = super.getDataSet().get(i);
			double curAttr = Double.parseDouble(curRecord.getValue(attrIndex));
			int curClassIndex = classesIndex.get(curRecord.getValue(super
					.getClassificationLabelIndex()));

			if (!classCountsOfEachValue.containsKey(curAttr)) {
				int[] counts = new int[super.getClassCounts().keySet().size()];
				counts[curClassIndex]++;
				classCountsOfEachValue.put(curAttr, counts);
			} else {
				classCountsOfEachValue.get(curAttr)[curClassIndex]++;
			}
		}

		for (Double value : classCountsOfEachValue.keySet()) {
			sortedValue.add(value);
		}
		Collections.sort(sortedValue);
	}
	
	

	/**
	 * Split based on nominal attribute
	 * @param attrIndex : index of the attribute
	 * @return : an double array: double[2] 
	 * [0] = minimal entropy, [1] = bestSplitValue
	 * 
	 */
		private double[] getSplittedEntropyOnNomAttribute(int attrIndex) {
		double[] res = new double[2];
		HashMap<String, List<Integer>> map = new HashMap<String, List<Integer>>();
		// for gainRatio calculation
		HashMap<String, Integer> distribution = new HashMap<String, Integer>();

		List<Record> dataSet = super.getDataSet();
		for (Integer dataIndex : super.getAllSamples()) {
			String curAttribute = dataSet.get(dataIndex).getValue(attrIndex);
			if (map.containsKey(curAttribute)) {
				map.get(curAttribute).add(dataIndex);
			} else {
				List<Integer> list = new ArrayList<Integer>();
				list.add(dataIndex);
				map.put(curAttribute, list);
			}

			if (distribution.containsKey(curAttribute)) {
				distribution.put(curAttribute,
						distribution.get(curAttribute) + 1);
			} else {
				distribution.put(curAttribute, 1);
			}
		}
		double entropy = 0.0;
		for (List<Integer> list : map.values()) {
			entropy += calEntropy(list) * list.size()
					/ super.getAllSamples().size();
		}
		double gainRatio = 0;
		for (String attr : distribution.keySet()) {
			double curRatio = distribution.get(attr) * 1.0
					/ super.getAllSamples().size();
			gainRatio = -1.0 * curRatio * Math.log(curRatio);
		}

		if (gainRatio > 0) {
			entropy /= gainRatio;
		} else {
			entropy = Double.MAX_VALUE;
		}

		res[0] = entropy;
		res[1] = 0;
		return res;
	}


	/**
	 * calculate entropy in this node
	 * this method is used once, only to calculate entropy for the root node
	 * @param list : list of samples in this node
	 * @return : entropy of this samples
	 */
	private double calEntropy(List<Integer> list) {
		double entropy = 0.0;
		// first get the distribution of classes for given list
		HashMap<String, Integer> map = getClassCount(list);
		int totalCounts = list.size();

		for (Integer i : map.values()) {
			double proportion = i * 1.0 / totalCounts;
			entropy += -1.0 * proportion * Math.log(proportion);
		}
		return entropy;
	}

	/**
	 * calculate every count in every class
	 * @param list of the samples 
	 * @return HashMap<String, Integer>
	 * key = class name
	 * value = sample counts of this class 
	 */
	private HashMap<String, Integer> getClassCount(List<Integer> list) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (int i : list) {
			String curClass = super.getDataSet().get(i)
					.getValue(super.getClassificationLabelIndex());
			if (map.containsKey(curClass)) {
				map.put(curClass, map.get(curClass) + 1);
			} else {
				map.put(curClass, 1);
			}
		}
		return map;
	}

	/**
	 * predict classification for this record based on decision tree
	 * @param rec : record to be classified
	 * @return : classification for this record
	 */
	public String getPrediction(Record rec) {
		if (this.children == null) {
			return super.getClassification();
		} else {
			String childName = attributes.get(this.nextSplitAttrIdx)
					.getChildValue(rec.getValue(this.nextSplitAttrIdx),
							this.nextSplitValue);
			if (this.children.containsKey(childName)) {
				return this.children.get(childName).getPrediction(rec);
			} else {
				return ("can't find correct path at attribute: " + this.attributes
						.get(this.nextSplitAttrIdx).getName());
			}
		}
	}
}
