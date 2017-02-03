public class EntropyCalc {
	public static final String PATH_TO_Training_FILE = "C:\\Users\\aniru\\Desktop\\Spring2016\\MachineLearning\\Assignment\\data_sets1\\Ani_Sample.csv";
	public static final String PATH_TO_Test_FILE = "C:\\Users\\aniru\\Desktop\\Spring2016\\MachineLearning\\Assignment\\data_sets1\\Ani_Sample.csv";
	public static final String PATH_TO_Validate_FILE = "C:\\Users\\aniru\\Desktop\\Spring2016\\MachineLearning\\Assignment\\data_sets1\\Ani_Sample.csv";

	/*public int[][] readInputFile(String fileName) throws IOException {

		int[][] inputData;
		int height = 0, width = 0;
		String dataRecord = null;
		BufferedReader br = new BufferedReader(new FileReader(fileName));

		String header = br.readLine();
		while (br.readLine() != null) {
			height++;
		}
		br.close();
		// tokenize input data
		// Gets attributes and putting it to headRow
		StringTokenizer sToken = new StringTokenizer(header, ",");
		int i = 0;
		String str = null;
		while (sToken.hasMoreTokens()) {
			str = sToken.nextToken();
			Tree.headRow.put(i, str);
			Tree.headRowMap.put(str, i++);
		}
		width = Tree.headRow.size();
		inputData = new int[height][width];
		// Fill training Data
		br = new BufferedReader(new FileReader(fileName));

		br.readLine();
		height = 0;
		while ((dataRecord = br.readLine()) != null) {
			width = 0;
			sToken = new StringTokenizer(dataRecord, ",");
			while (sToken.hasMoreTokens()) {
				inputData[height][width++] = Integer.parseInt(sToken.nextToken());
			}
			height++;
		}

		return inputData;
	}
*/
	public static double calcEntropy(Integer[][] data, String entType) {

		if (data.length == 0)
			return 0;

		int pos = 0, neg = 0;
		for (int i = 0; i < data.length; i++) {
			//System.out.println(data[i][data[i].length - 1]);
			if (data[i][data[i].length - 1] == 0)
				neg++;
			else
				pos++;
		}
		double entropy = 0;
		double sum = 0;
		double mult = 0;
		if (entType.equals(EntropyHeuristic.Gain_Heuristic))
			entropy = (neg == 0 ? 0
					: -(((double) neg / data.length) * Math.log10((double) neg / data.length) / Math.log10(2)))
					+ (pos == 0 ? 0
							: -(((double) pos / data.length) * Math.log10((double) pos / data.length) / Math.log10(2)));
		else {
			sum = neg + pos;
			mult = neg * pos;
			entropy = (mult) / (sum * sum);
		}
		return entropy;
	}

	public static double calculateEntropy(int negCount, int posCount, int totalCount, String entType) throws Exception {

		double entropy = 0;
		double negEntropy = 0;
		double posEntropy = 0;
		if (totalCount == 0)
			return 0;
		if (entType.equals(EntropyHeuristic.Gain_Heuristic)) {
			negEntropy = (negCount == 0 ? 0
					: -(((double) negCount / totalCount) * Math.log10((double) negCount / totalCount) / Math.log10(2)));
			posEntropy = (posCount == 0 ? 0
					: -(((double) posCount / totalCount) * Math.log10((double) posCount / totalCount) / Math.log10(2)));
			entropy = negEntropy + posEntropy;
		} else if (entType.equals(EntropyHeuristic.varience_Impurity)) {
			double num = negCount * posCount;
			double denom = totalCount * totalCount;
			entropy = num / denom;
		} else {
			throw new Exception("The entropy type enterd is invalid.");
		}
		return entropy;
	}

}
