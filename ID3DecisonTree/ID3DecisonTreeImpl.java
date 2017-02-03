import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 
 */

public class ID3DecisonTreeImpl {
	public static Map<Integer, String> leftHeadRow;
	public static Map<Integer,String> rightHeadRow;
	private static Map<Integer, Integer> mapMaxGain = new LinkedHashMap<>();

	public static void BuildDecisonTree(Integer[][] dat, Map<Integer, String> headRow, int posNode, String entType)
			throws Exception {

		if (posNode == 1) {
			Tree.setDecTree(new LinkedHashMap<Integer, String>());
		}
		// Calculate the root Entropy (Class Label)
		double rootEntropy = EntropyCalc.calcEntropy(dat, entType);

		if (rootEntropy == 0)
			Tree.getDecTree().put(posNode, "" + dat[0][dat[0].length - 1]);
		else if (headRow.size() == 1) {
			int lneg = 0, lpos = 0;
			for (int i = 0; i < dat.length; i++) {
				if (dat[i][dat[i].length - 1] == 0)
					lneg++;
				else
					lpos++;
			}
			if (lneg > lpos)
				Tree.getDecTree().put(posNode, "0");
			else
				Tree.getDecTree().put(posNode, "1");
		} else {
			// Initialize gain and entropy values
			double maxGain = -2;
			int maxGainAttr = -1;
			int lftNodeBestGain = -1;
			int rightNodeBestGain = -1;
			int maxGainMajority = -1;
			for (int i = 0; i < dat[0].length - 1; i++) {
				if (!headRow.containsKey(i))
					continue;

				int negativeCount = 0, positiveCount = 0, trueNegative = 0, falsePositive = 0, falseNegative = 0, truePositive = 0;
				for (int j = 0; j < dat.length; j++) {
					if (dat[j][i] == 0) {
						negativeCount++;
						if (dat[j][dat[j].length - 1] == 0)
							trueNegative++; 
						else
							falsePositive++;
					} else {
						positiveCount++;
						if (dat[j][dat[j].length - 1] == 1)
							truePositive++; 

						else
							falseNegative++; 
					}

				}

				double negEnt = 0, posEnt = 0, finalEnt = 0, infGain = 0;
				negEnt = EntropyCalc.calculateEntropy(trueNegative, falsePositive, negativeCount, entType);
				posEnt = EntropyCalc.calculateEntropy(falseNegative, truePositive, positiveCount, entType);
				if (negEnt == 0 && posEnt == 0) {
					finalEnt = 0;
				} else {
					finalEnt = ((double) negativeCount / (negativeCount+positiveCount)) * negEnt
							+ ((double) positiveCount / (negativeCount + positiveCount)) * posEnt;
				}
				infGain = rootEntropy - finalEnt;
				if (infGain > maxGain) {

					maxGain = infGain;
					maxGainAttr = i;
					lftNodeBestGain = negativeCount;
					rightNodeBestGain = positiveCount;
					maxGainMajority = ((trueNegative + falsePositive) > (falseNegative + truePositive) ? 0 : 1);

				}
			}
			System.out.println("Maximumgain"+maxGain);
			mapMaxGain.put(posNode, maxGainMajority);
			System.out.println("Maximum gain attribute" + maxGainAttr + "pos" + posNode);
			Tree.getDecTree().put(posNode, headRow.get(maxGainAttr));
			/* if (lftNodeBestGain != -1 && rightNodeBestGain != -1) { */
			Integer[][] leftChild = new Integer[lftNodeBestGain][dat[0].length];
			Integer[][] rightChild = new Integer[rightNodeBestGain][dat[0].length];

			 leftHeadRow = (LinkedHashMap<Integer, String>) ((LinkedHashMap<Integer, String>) headRow)
					.clone();
			leftHeadRow.remove(maxGainAttr);

			rightHeadRow = (LinkedHashMap<Integer, String>) ((LinkedHashMap<Integer, String>) headRow)
					.clone();
			rightHeadRow.remove(maxGainAttr);
			// headRow.remove(maxGainAttr);
			int lftArrCnt = 0, rghtArrCnt = 0;
			for (int i = 0; i < dat.length; i++) {
				if (dat[i][maxGainAttr] == 0) {
					for (int j = 0; j < dat[i].length; j++) {
						leftChild[lftArrCnt][j] = dat[i][j];
					}
					lftArrCnt++;
				} else {
					for (int j = 0; j < dat[i].length; j++) {
						rightChild[rghtArrCnt][j] = dat[i][j];
					}
					rghtArrCnt++;
				}
			}
			if (lftNodeBestGain == 0)

				Tree.getDecTree().put(posNode * 2, "" + maxGainMajority);

			else

				BuildDecisonTree(leftChild, leftHeadRow, posNode * 2, entType);

			if (rightNodeBestGain == 0)
				Tree.getDecTree().put(posNode * 2 + 1, "" + maxGainMajority);
			else
				BuildDecisonTree(rightChild, rightHeadRow, posNode * 2 + 1, entType);

		}

	}
	//}

	public double testAccuracy(Map<Integer, String> decTree, Integer[][] testData) {

		Boolean isCorrect = false;
		int correctOut = 0, wrongOut = 0;
		for (int i = 0; i < testData.length; i++) {
			isCorrect = checkOutput(decTree, testData[i]);
			if (null != isCorrect) {
				if (isCorrect)
					correctOut++;
				else
					wrongOut++;
			}
		}

		return ((double) correctOut / (correctOut + wrongOut)) * 100;

	}

	private static Boolean checkOutput(Map<Integer, String> decTree, Integer[] testDataRow) {
		int posNode = 1;
		while (null != decTree.get(posNode) && !decTree.get(posNode).isEmpty() && !decTree.get(posNode).equals("0")
				&& !decTree.get(posNode).equals("1")) {
			// System.out.println("before"+posNode);
			int numb = 0;

			numb = Tree.headRowMap.get(decTree.get(posNode));
			posNode = testDataRow[numb] == 0 ? posNode * 2 : posNode * 2 + 1;

			// System.out.println("position"+posNode);
		}
		int value1 = 0;
		int value2 = 0;
		// System.out.println(null == decTree.get(posNode));

		// System.out.println("value" + decTree.get(posNode));
		value1 = Integer.parseInt(decTree.get(posNode).trim());
		value2 = testDataRow[testDataRow.length - 1];

		if (value1 == value2) {

			return true;
		} else {
			String str = " ";
			for (int i = 0; i < testDataRow.length; i++)
				str = str + testDataRow[i] + " ";
			return false;
		}
	}

	

	/*public Map<Integer, String> pruneDecTree(Map<Integer, String> decTree, double beforePruningAccuracy,
			Integer[][] testData, int fromNode, int toNode) {
		
		Map<Integer, String> dBest = decTree;
		for (int i = 1; i < toNode; i++) {

			Map<Integer, String> dtemp = decTree;
			Random random = new Random();
			int randomNum = random.nextInt((toNode - fromNode) + 1) + fromNode;

			for (int j = 1; j < randomNum; j++) {

				int countNonLeafNode = countNonLeafNodes(dtemp);
				System.out.println("leaf node count" + countNonLeafNode);
				if (countNonLeafNode > 2) {
					Integer p = random.nextInt(countNonLeafNode) + 1;
					if (p > 2) {

						String attribute = decTree.get(p);
						Integer maxValue = mapMaxGain.get(attribute);
						dtemp.put(p, String.valueOf(maxValue));
						while (p < decTree.size()) {
							int left = 2 * p;
							int right = 2 * p + 1;
							if (left <= decTree.size())
								dtemp.put(left, null);
							if (right <= decTree.size())
								dtemp.put(right, null);
							p = left;

						}
					}
					removeNullKeys(dtemp);
				}

			}

			double pruneAccuracy = testAccuracy(dtemp, testData);
			if (pruneAccuracy > beforePruningAccuracy) {
				dBest = dtemp;
			}
		}

		return dBest;
	}

	public static int countNonLeafNodes(Map<Integer, String> decTree) {
		int nonLeafNodeCount = 0;

		Iterator<Map.Entry<Integer, String>> itr = decTree.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry<Integer, String> pair = itr.next();
			if (pair.getValue() != null && !isNumeric(pair.getValue().trim())) {

				nonLeafNodeCount++;

			}
		}

		return nonLeafNodeCount;
	}

	public static boolean isNumeric(String str) {
		for (char c : str.toCharArray()) {
			if (!Character.isDigit(c))
				return false;
		}
		return true;
	}

	public static int countNonLeafNodes(Integer position, Map<Integer, String> map) {

		int count = 1;
		if (null != map.get(position)) {
			if (map.get(position).equals("0") || map.get(position).equals("0")) {
				return count;
			} else {
				++count;

				countNonLeafNodes(2 * position, map);
				countNonLeafNodes((2 * position + 1), map);
			}
		}
		return count;
	}

	public static void removeNullKeys(Map<Integer, String> map) {
		Iterator<Map.Entry<Integer, String>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, String> e = it.next();
			Integer key = e.getKey();
			String value = e.getValue();
			if (value == null || value.isEmpty()) {
				it.remove();
			}
		}
	}*/
	public  Map<Integer, String> pruneDecTree(int nodesPrune,Map<Integer,String> decTree,Integer [][] validdata) {
		//Performs pruning in Decision Tree
		Map<Integer,String> dtID3 = new LinkedHashMap<Integer,String>();
		//Copy of original decision tree using ID3 algorithm
		dtID3.putAll(decTree);
		//check accuracy using validation data
		double treeAccuracy = testAccuracy(dtID3,validdata);		
		System.out.println("Accuracy for the validation data "+treeAccuracy);
		List<Integer> nonLeafNodes = new ArrayList<Integer>();
	    int nonLeaf =0;
	    Iterator<Integer> nodes =dtID3.keySet().iterator();
	    //Get the non leaf nodes other than at level 1 and 2
		while(nodes.hasNext())
		{
				Integer key = nodes.next();
				String value = dtID3.get(key);
				//To get the non leaf nodes to the ArrayList
				if(!("0".equals(value)||"1".equals(value)))
				{
					nonLeaf++;		
					//To avoid adding the root node and its immediate childs to the arraylist for pruning 
					if(key!=1&&key!=2&&key!=3)
					 nonLeafNodes.add(key);
				}
		}
		//Boundary for Prune number
		int pruneListSize  = nonLeafNodes.size();
		Random ran = new Random();
		int idxToPrune =0;
		int posPruning =0;
		int majorClass =0;
		//Perform for number of nodes to prune
		for(int i=0;i<nodesPrune;i++)
		{
			if(pruneListSize>0)
			{
				//Index to Prune in nonLEaf nodes List
				idxToPrune = ran.nextInt(pruneListSize);
				//Position to Prune in Tree
				posPruning = nonLeafNodes.get(idxToPrune);
				System.out.println("pos pruning"+posPruning);
				//Get the majority class value
				majorClass = mapMaxGain.get(posPruning);
				//Add node to tree with majority class
				dtID3.put(posPruning,""+majorClass);
				//Remove pruned node from nonLeafNodes List
				nonLeafNodes.remove((Integer)posPruning);
				//Prune the subtree for the pruned node
				pruneSubTree(posPruning,dtID3,nonLeafNodes);
				//Update PruneListSize
				pruneListSize  = nonLeafNodes.size();
			}
		}			
		return dtID3;
	}

	private static void pruneSubTree(int nodeChosen, Map<Integer, String> dtID3, List<Integer> nonLeafNodes) {
		// Remove left and right subtree of the pruned node		
		/*if(dtID3.containsKey(2*nodeChosen))
		{
			dtID3.remove(2*nodeChosen);
			nonLeafNodes.remove((Integer)(2*nodeChosen));
			pruneSubTree(2*nodeChosen, dtID3,nonLeafNodes);  //Recursive call to subsequent deletion
			
		}
		if(dtID3.containsKey(2*nodeChosen+1))
		{
			dtID3.remove(2*nodeChosen+1);
			nonLeafNodes.remove((Integer)(2*nodeChosen+1));
			pruneSubTree((2*nodeChosen+1), dtID3,nonLeafNodes);//Recursive call to subsequent deletion
			
		}
		*/
		while (nodeChosen < dtID3.size()) {
			int left = 2 * nodeChosen;
			int right = 2 * nodeChosen + 1;
			if (left <= dtID3.size()) {

				dtID3.put(left, null);
				nonLeafNodes.remove((Integer) (2 * nodeChosen));
			}
			if (right <= dtID3.size()) {
				dtID3.put(right, null);
				nonLeafNodes.remove((Integer) (2 * nodeChosen + 1));
			}
			nodeChosen = left;

		}
		removeNullKeys(dtID3);
		
	}
	
	public static boolean isNumeric(String str) {
		for (char c : str.toCharArray()) {
			if (!Character.isDigit(c))
				return false;
		}
		return true;
	}

	
	public static void removeNullKeys(Map<Integer, String> map) {
		Iterator<Map.Entry<Integer, String>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, String> e = it.next();
			Integer key = e.getKey();
			String value = e.getValue();
			if (value == null || value.isEmpty()) {
				it.remove();
			}
		}
	}
}
