import java.util.Map;
import java.util.Scanner;

public class ID3Test {

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
			// System.out.println("Training File Path");
			// trainFile = in.next();
			trainFile = EntropyCalc.PATH_TO_Training_FILE;
			// System.out.println("Validating File path");
			// validFile = in.next();
			validFile = EntropyCalc.PATH_TO_Validate_FILE;
			// System.out.println("Test File path");
			// testFile = in.next();
			testFile = EntropyCalc.PATH_TO_Test_FILE;
			System.out.println("Print the model or not 0-No print 1-Print");
			out = in.nextInt();
			// out=0;

		}

		Tree dt = new Tree();

		try {
			FileUtil fileUtil = new FileUtil();
			ID3DecisonTreeImpl id3DecisonTreeImpl = new ID3DecisonTreeImpl();

			// Build Decision Tree
			Integer[][] trainData = fileUtil.readInputFile(trainFile);
			Integer[][] validFileData = fileUtil.readInputFile(validFile);
			Integer[][] testFileData = fileUtil.readInputFile(testFile);
			ID3DecisonTreeImpl.BuildDecisonTree(trainData, Tree.getHeadRow(), 1, EntropyHeuristic.Gain_Heuristic);
			if (out == 1) {
				System.out.println("Decision Tree using ID3 Algorithm:Before Pruning");

				dt.printDecTree(1, " ", " ");

			}
			double accuracy = id3DecisonTreeImpl.testAccuracy(Tree.getDecTree(), testFileData);
			Map<Integer, String> treeOutput = id3DecisonTreeImpl.pruneDecTree(nodesPrune, Tree.getDecTree(),
					validFileData);

			// Prune Decision Tree

			Tree.setDecTree(treeOutput);
			if (out == 1) {
				System.out.println("Decision Tree using ID3 Algorithm : After Pruning");
				dt.printDecTree(1, " ", " ");
			}

			double accuracyAfterPrune = id3DecisonTreeImpl.testAccuracy(Tree.getDecTree(), testFileData);
			System.out.println("Decison Tree accuracy Before pruning: =  " + accuracy);
			System.out.println("Decison Tree accuracy After pruning:= " + accuracyAfterPrune);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
