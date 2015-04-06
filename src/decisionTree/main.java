package decisionTree;

public class main {
	public static void main(String[] args) {

//		 String dataFile = "/Users/mizhou/Documents/data/B/trainProdIntro.binary.arff";
//		 String testFile = "/Users/mizhou/Documents/data/B/testProdIntro.binary.arff";
		
//		String dataFile = "/Users/mizhou/Documents/data/A/trainProdSelection.arff";
//		String testFile = "/Users/mizhou/Documents/data/A/testProdSelection.arff";
//		Tree tree = new Tree();
//		tree.generateBestTree(dataFile, 1000);
//		tree.generateClassification(testFile);

		Tree tree = new Tree();
		if (args.length == 2) {
			String dataFile = args[0];
			String testFile = args[1];
			tree.generateBestTree(dataFile, 1000);
			tree.generateClassification(testFile);
		}
		else {
			System.out.println("Please check your arguments, first argument should be the path to training data, second argument should be the path to test data");
		}
	}
}
