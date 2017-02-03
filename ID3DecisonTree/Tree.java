import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 
 */





public class Tree {
	/**
	 * Class for acting as a driver program for the ID3 Algorithm implementation
	 */

	public static int[][] trainingData, testData, validData;
	public static Map<Integer, String> tree = new LinkedHashMap<Integer, String>();
	public static Map<Integer, String> headRow = new LinkedHashMap<Integer, String>();
	public static Map<String, Integer> headRowMap = new LinkedHashMap<String, Integer>();
	public static String log = "ID3Log.txt";
	public static BufferedWriter logWrite;

	public static Map<Integer, String> getDecTree() {
		return tree;
	}

	public static void setDecTree(Map<Integer, String> decTree) {
		Tree.tree = decTree;
	}

	public static int[][] getTrainData() {
		return trainingData;
	}

	public static void setTrainData(int[][] trainData) {
		Tree.trainingData = trainData;
	}

	public static int[][] getTestData() {
		return testData;
	}

	public static void setTestData(int[][] testData) {
		Tree.testData = testData;
	}

	public static int[][] getValidData() {
		return validData;
	}

	public static void setValidData(int[][] validData) {
		Tree.validData = validData;
	}

	public static Map<Integer, String> getHeadRow() {
		return headRow;
	}

	public static void setHeadRow(Map<Integer, String> headRow) {
		Tree.headRow = headRow;
	}

	public static Map<String, Integer> getHeadRowMap() {
		return headRowMap;
	}

	public static void setHeadRowMap(Map<String, Integer> headRowMap) {
		Tree.headRowMap = headRowMap;
	}

	public Tree() throws IOException {
		initPrint();

	}

	private void initPrint() throws IOException {
		logWrite = new BufferedWriter(new FileWriter(log));
		logWrite.write("LOGS FOR DT ALGORITHM");
		logWrite.newLine();
	}

	private void print(String str) throws IOException {
		System.out.println(str);
		logWrite.write(str);
		logWrite.newLine();
	}

	
	public void printDecTree(int posNode, String position, String cond) throws IOException {
		
		if (getDecTree().containsKey(posNode)) {
			if (!getDecTree().containsKey(2 * posNode) && !getDecTree().containsKey(2 * posNode + 1)) {
				String str = position.substring(1) + cond + getDecTree().get(posNode);
				System.out.println(str);
				
				logWrite.write(str);
				logWrite.newLine();
			} else {
				System.out.println((position.length() > 0 ? position.substring(1) : position) + cond);
				logWrite.write((position.length() > 0 ? position.substring(1) : position) + cond);
				logWrite.newLine();
				printDecTree(posNode * 2, position + "| ", getDecTree().get(posNode) + "= 0 :");
				printDecTree(posNode * 2 + 1, position + "| ", getDecTree().get(posNode) + "= 1 :");
			}
		}

	}

	public static void main(String[] args) throws Exception {
		// Triggers ID3 Algorithm Implementation

		EntropyCalc entropyCalc = new EntropyCalc();
		Scanner in;
		int nodesPrune = 0;
		String trainFile = new String();
		String validFile = new String();
		String testFile = new String();
		int out = 1; // Boolean variable to indicate whether to print the model

		// Input of file
		if (args.length > 0) {
			nodesPrune = Integer.parseInt(args[0]);
			trainFile = args[1];
			validFile = args[2];
			testFile = args[3];
			out = Integer.parseInt(args[4]);
		} else {
			in = new Scanner(System.in);
			System.out.println("Enter the number of nodes to prune");
			nodesPrune = in.nextInt();
			//System.out.println("Training File Path");
			//trainFile = in.next();
			trainFile= EntropyCalc.PATH_TO_Training_FILE;
			//System.out.println("Validating File path");
			//validFile = in.next();
			validFile = EntropyCalc.PATH_TO_Validate_FILE;
			//System.out.println("Test File path");
			//testFile = in.next();
			testFile =EntropyCalc.PATH_TO_Test_FILE;
			System.out.println("Print the model or not 0-No print 1-Print");
			out = in.nextInt();
			//out=0;

		}

		Tree dt = new Tree();

		try {
			FileUtil fileUtil = new FileUtil();
			ID3DecisonTreeImpl id3DecisonTreeImpl = new ID3DecisonTreeImpl();
		
			
			// Build Decision Tree
			Integer[][] trainData=fileUtil.readInputFile(trainFile);
			Integer [][] validFileData=fileUtil.readInputFile(validFile);
			Integer[] [] testFileData=fileUtil.readInputFile(testFile);
			ID3DecisonTreeImpl.BuildDecisonTree(trainData, Tree.getHeadRow(), 1, EntropyHeuristic.Gain_Heuristic);
			if (out == 1) {
				dt.print("");
				dt.print("Decision Tree using ID3 Algorithm:Before Pruning");
				dt.printDecTree(1, " ", " ");

			}
		double accuracy= id3DecisonTreeImpl.testAccuracy(Tree.getDecTree(), testFileData);
		Map<Integer,String> treeOutput= id3DecisonTreeImpl.pruneDecTree(nodesPrune,Tree.getDecTree(),testFileData) ;

			// Prune Decision Tree

		 Tree.setDecTree(treeOutput);
			if (out == 1) {
				dt.print(" ");
				dt.print("Decision using ID3: After Pruning");
				dt.printDecTree(1, "", "");
			}

			double accuracyAfterPrune = id3DecisonTreeImpl.testAccuracy(Tree.getDecTree(), testFileData);
			System.out.println(
					"Decison Tree accuracy Before pruning: =  " + accuracy);
			System.out.println(
					"Decison Tree accuracy After pruning:= " + accuracyAfterPrune);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
