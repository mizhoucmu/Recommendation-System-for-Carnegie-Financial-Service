package decisionTree;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Tree {
	private List<Attribute> attributes; // all attribute list
	private List<Integer> trainingData = new ArrayList<Integer>(); //index of all training samples
	private List<Integer> testData = new ArrayList<Integer>(); // index of all test samples
	private List<Record> allData; // all data
	private TreeNode root;	 // root of the tree
	
	
	/**
	 * generate a tree based on training data
	 * @param threshhold :  threshhold used to pre-prune the tree
	 */
	public void train(double threshhold) {
		TreeNode root = new TreeNode(allData, attributes.size() - 1,
				attributes, 0, threshhold);
		for (Integer i : trainingData) {
			root.addRecord(i);
		}
		root.split();
		this.root = root;
	}
	

	/**
	 * generate a tree based on all data
	 * @param threshhold : threshhold used to pre-prune the tree
	 */
	public void generateTreeWithThreshhold(double threshhold) {
		this.root = new TreeNode(allData, attributes.size() - 1,
				attributes, 0, threshhold);
		for (int i = 0; i < allData.size(); i++) {
			root.addRecord(i);
		}
		root.split();
	}

	/**
	 * get the entropy of root, this is the upper limit of threshhold
	 * @return the entropy of root 
	 */
	private double getRootEntropy() {
		HashMap<String,Integer> classCount = new HashMap<String, Integer>();
		for (int i = 0; i < this.allData.size(); i++) {
			String curClassification = allData.get(i).getValue(attributes.size() - 1);
			if (classCount.containsKey(curClassification)) {
				classCount.put(curClassification, classCount.get(curClassification) + 1);
			}
			else {
				classCount.put(curClassification, 1);
			}
		}
		double entropy = 0;
		for (Integer i : classCount.values()) {
			entropy -= (i * 1.0 / this.allData.size()) * Math.log(i * 1.0 / this.allData.size());
		}
		return entropy;
		
	}
	
	
	/**
	 * print the tree to console
	 * @param root : root of the tree
	 */
	private void print(TreeNode root) {
		if (root == null) {
			return;
		}
		System.out.println(root.getDisplayPrefix());
		if (root.getChildren() != null) {
			for (TreeNode child : root.getChildren().values()) {
				print(child);
			}
		}
	}
	
	
	/**
	 * 
	 * Print decision tree to file
	 * @param root : root of the tree
	 * @param bw : buffered Writter for the file
	 */
	private void printToFile(TreeNode root, BufferedWriter bw) {
		if (root == null) {
			return;
		}
		try {
			bw.newLine();
			bw.write(root.getDisplayPrefix());
			if (root.getChildren() != null) {
				for (TreeNode child : root.getChildren().values()) {
					printToFile(child,bw);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Print the whole tree to console
	 */
	public void print() {
		this.print(root);
	}
	
	/**
	 * Print the whole tree to given file
	 * @param bw: bufferedWritter for given file
	 */
	public void printToFile(BufferedWriter bw) {
		try {
			printToFile(root,bw);
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

	/**
	 * 
	 * Use this decision tree to predict given record
	 * @param rec : record to predict
	 * @return : classification of the record
	 */
	public String predict(Record rec) {
		StringBuilder res = new StringBuilder();
		if (rec.verify(attributes) == false) {
			res.append("Pls check the values again: values = [");
			for (String value : rec.getValues()) {
				res.append(value);
				res.append(",");
			}
			res.append("]");
			res.append("\n                        Attributes = [");
			for (Attribute attr : this.attributes) {
				res.append(attr.getName());
				res.append(",");
			}
			res.append("\n");
			return res.toString();
		}
		return root.getPrediction(rec);
	}

	/**
	 * Load all data to the tree
	 * @param filename : filename of data
	 */
	public void loadAllData(String filename) {
		try {
			this.attributes = new ArrayList<Attribute>();
			BufferedReader in = new BufferedReader(new FileReader(filename));
			String line;
			boolean startGetData = false;
			while ((line = in.readLine()) != null) {
				line.trim();
				String[] res = line.split(" ");

				if (res[0].equals("@attribute")) {
					if (res[2].charAt(0) == '{'
							&& res[2].charAt(res[2].length() - 1) == '}') {
						// nominal attributes
						String[] values = res[2].substring(1,
								res[2].length() - 1).split(",");
						NomAttribute attribute = new NomAttribute(res[1]);
						for (int i = 0; i < values.length; i++) {
							attribute.addValue(values[i]);
						}
						attributes.add(attribute);
					} else if (res[2].equals("real")) {
						// real - numeric attribute
						NumAttribute attribute = new NumAttribute(res[1]);
						attributes.add(attribute);
					}
				} else if (res[0].equals("@data")) {
					startGetData = true;
					allData = new ArrayList<Record>();
					continue;
				} else if (res[0].equals("@relation")) {
					continue;
				} else {
					if (startGetData) {
						String[] values = line.split(",");
						Record rec = new Record();
						rec.setAttribute(values);
						allData.add(rec);
					}
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param k : cross validation based on k group
	 * @param threshhold : leafnode entropy will be no more than threshhold 
	 * @return accuracy of this cross validation
	 */
	public double crossValidation(int k, double threshhold) {
		HashMap<Integer, List<Integer>> kgroup = new HashMap<Integer, List<Integer>>();
		for (int i = 0; i < allData.size(); i++) {
			
			if (!kgroup.containsKey(i % k)) {
				kgroup.put(i % k, new ArrayList<Integer>());
			}
			kgroup.get((i % k)).add(i);
		}
		
		double avgAccuracy = 0;
		
		for (int i = 0; i < k; i++) {
			testData.clear();
			trainingData.clear();
			for (int j = 0; j < k; j++) {
				if (j == i) {
					testData.addAll(kgroup.get(j));
				} else {
					trainingData.addAll(kgroup.get(j));
				}
			}
			train(threshhold);
			double accuracy = 0;
			int correct = 0;
			int wrong = 0; 
			for (Integer idx : testData) {
				Record rec = this.allData.get(idx);
				if (rec.getValue(attributes.size() - 1).equals(this.predict(rec))) {
					correct++;
				}
				else {
					wrong++;
				} 
			}
			accuracy = correct * 1.0 / (correct + wrong);
			avgAccuracy += accuracy;
		}
		avgAccuracy /= k;
		return avgAccuracy;
	}

	
	
	/**
	 * Generate path and name for cross validation file
	 *  
	 * it is placed in the same path of training data filename
	 * only difference is that it will have name like <input file name>_crossValidationResult.txt
	 * @param filename : filename of the data file
	 * @return new file path and name
	 */
	private String generateCrossValidationFilename(String filename) {
		StringBuilder res = new StringBuilder();
		String[] dir = filename.split("/");
		for (int i = 0; i < dir.length - 1; i++) {
			res.append("/");
			res.append(dir[i]);
		}
		res.append("/");
		String[] prepost = dir[dir.length -1].split("\\.");
		res.append(prepost[0]);
		res.append("_crossValidationResult");
		res.append(".");
		res.append("csv");
		return res.toString();
	}
	
	
	/**
	 * print cross validation result to a file named <inputfile>_crossValidationResult.csv
	 * it is in the same folder as inputfile 
	 * @param k : cross validation based on k group
	 * @param threshhold : use this to stop splitting, leaf node entropy will be no more than threshhold
	 * @param filename : input file name
	 */
	public void printCrossValidationResult(int k, double threshhold, String filename) {

		HashMap<Integer, List<Integer>> kgroup = new HashMap<Integer, List<Integer>>();
		double avgAccuracy = 0.0;

		String crossfile = generateCrossValidationFilename(filename);
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(crossfile));
			for (int i = 0; i < allData.size(); i++) {
				
				if (!kgroup.containsKey(i % k)) {
					kgroup.put(i % k, new ArrayList<Integer>());
				}
				kgroup.get((i % k)).add(i);
			}
			
			for (int i = 0; i < k; i++) {
				bw.write("\n------------------------------cross validation on group: " + i + "--------------------------\n");
				testData.clear();
				trainingData.clear();
				for (int j = 0; j < k; j++) {
					if (j == i) {
						testData.addAll(kgroup.get(j));
					} else {
						trainingData.addAll(kgroup.get(j));
					}
				}
				
				bw.write("\ntraining data: ");
				for (Integer idx : this.trainingData) {
					bw.write(idx + " ");
				}
				bw.write("\n");
				
				bw.write("\ntest data : ");
				for (Integer idx :this.testData) {
					bw.write(idx + " ");
				}
				bw.write("\n");
				
				bw.write("\n");
				for (int idx = 0; idx < this.attributes.size(); idx++) {
					bw.write(this.attributes.get(idx).getName());
					bw.write(",");
				}
				bw.write("classification");
				bw.write("\n");
				
				train(threshhold);
				double accuracy = 0;
				int correct = 0;
				int wrong = 0; 
				
				
				for (Integer idx : testData) {
					Record rec = this.allData.get(idx);
					bw.write("\n");
					for (String value : rec.getValues()) {
						bw.write(value + ",");
					}
					String result = this.predict(rec);
					bw.write(result);
					
					if (rec.getValue(attributes.size() - 1).equals(result)) {
						correct++;
					}
					else {
						wrong++;
					} 
				}
				accuracy = correct * 1.0 / (correct + wrong);
				avgAccuracy += accuracy;
				bw.write("\n=======correct : " + correct + ", wrong : " + wrong + " accuracy: " + accuracy + "\n");
			}
			avgAccuracy /= k;
			bw.write("\naverage accuracy of this cross validation is " + avgAccuracy + "\n");
			bw.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Get the best threshhold so as to get the highest cross validation accuracy 
	 * we will try every threshhold value from start to end for many times, and record the best result
	 * @param tree : all data has been loaded to this tree
	 * @param start : start point for entropy threshhold
	 * @param end : end point for entropy threshhold
	 * @param times: interval of every step = (end - start) / times 
	 * @return a double array, array[0] = highest accuracy, array[1] = best threshhold
	 */
	public double[] getBestThreshhold(double start, double end, int times) {
		double[] res = new double[2];
		double highestAcc = 0;
		double bestThreshHold = 0;
		for (double threshhold = start; threshhold <= end; threshhold += (end - start) / times) {
			double curAcc = this.crossValidation(10,threshhold);
			if (curAcc > highestAcc) {
				bestThreshHold = threshhold;
				highestAcc = curAcc; 
			}
		}
		res[0] = highestAcc;
		res[1] = bestThreshHold;
		return res;
	}
	
	
	/**
	 * Get best threshhold that maximize the accuracy of cross validation 
	 * @param filename : filename of data
	 * @param times :  steps of optimization, usually it is 1000
	 * @return: double[2], [0] = best accuracy [1] = best threshhold
	 */
	public double[] getBestThreshhold(String filename, int times) {
		this.loadAllData(filename);
		double[] res = this.getBestThreshhold(0.0,getRootEntropy(),times);
		return res;
	}
	
	/**
	 * Generate best tree and print it to a file
	 * the new file's name is generated based on given filename
	 * it is placed in the same path of given filename
	 * only difference is that it will have name like <input file name>_result.txt
	 * @param filename : path and filename of the data
	 * @param times : steps used to optimize threshhold
	 */
	public void generateBestTree(String filename, int times) {
		double[] best = getBestThreshhold(filename, times);
		generateTreeWithThreshhold(best[1]);
		printCrossValidationResult(10,best[1], filename);
		String treeFilePath = this.generateTreeFilePath(filename);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(treeFilePath));
			bw.newLine();
			bw.write("highest accuracy = " + best[0] + ", best threshhold = " + best[1]);
			this.printToFile(root,bw);
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Generate path and name for the new file
	 * the new file is used to print the tree 
	 * it is placed in the same path of training data filename
	 * only difference is that it will have name like <input file name>_result.txt
	 * @param filename : filename of the data file
	 * @return new file path and name
	 */
	private String generateTreeFilePath(String filename) {
		StringBuilder res = new StringBuilder();
		String[] dir = filename.split("/");
		for (int i = 0; i < dir.length - 1; i++) {
			res.append("/");
			res.append(dir[i]);
		}
		res.append("/");
		String[] prepost = dir[dir.length -1].split("\\.");
		res.append(prepost[0]);
		res.append("_tree");
		res.append(".");
		res.append("txt");
		return res.toString();
	}
	
	
	/**
	 * Generate path and name for the new file
	 * the new file is used to print prediction result 
	 * it is placed in the same path of test data filename
	 * only difference is that it will have name like <test file name>_result.csv
	 * it can be opend in Excel directly
	 * @param filename : filename of tes data file
	 * @return prediction result file path and name
	 */
	private String generateResultFilePath(String filename) {
		StringBuilder res = new StringBuilder();
		String[] dir = filename.split("/");
		for (int i = 0; i < dir.length - 1; i++) {
			res.append("/");
			res.append(dir[i]);
		}
		res.append("/");
		String[] prepost = dir[dir.length -1].split("\\.");
		res.append(prepost[0]);
		res.append("_result");
		res.append(".");
		res.append("csv");
		return res.toString();
	}
	
	/**
	 * will print classification result to a file named <testfilepath>_result.csv
	 * @param filename of testData
	 */
	public void generateClassification(String filename) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			String line;
			boolean startGetData = false;
			String resultFile = this.generateResultFilePath(filename);
			BufferedWriter out = new BufferedWriter(new FileWriter(resultFile));
			for (int i = 0; i < attributes.size();i++) {
				out.write(attributes.get(i).getName());
				out.write(",");
			}
			
			while ((line = in.readLine()) != null) {
				line.trim();
				String[] res = line.split(" ");
				if (res[0].equals("@data")) {
					startGetData = true;
					continue;
				} else {
					if (startGetData) {
						out.newLine();
						String[] values = line.split(",");
						for (int i = 0; i < attributes.size() - 1; i++) {
							out.write(values[i]);
							out.write(",");
						}
						Record rec = new Record();
						rec.setAttribute(values);
						out.write(this.predict(rec));
					}
				}
			}
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
