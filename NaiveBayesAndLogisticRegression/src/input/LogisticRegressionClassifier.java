package input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LogisticRegressionClassifier {
	public static final Double learningRate=0.01;
	public static final Double lamda=.8;
	public  Integer trueCount =0;
	public  Integer falseCount=0;

	/**
	 * Method to create matrix.
	 * @throws IOException
	 */
	public void doLogisticRegression(boolean status) throws IOException {
		DataSet dataSet = new DataSet();
		List<String> stopWordList =null;
		if(!status){
			stopWordList=new ArrayList<>();
		}
		else{
				
			stopWordList=	dataSet.loadIgnorewords("data/stopwords.txt");
		}
		List<Document> documentList = dataSet.populateDocumentList(stopWordList);
		Set<String> wordSet = dataSet.createGlobalVocabulary(documentList);
		String[] distinctWordList = new String[wordSet.size()];
		int k = 0;
		for (String str : wordSet) {

			distinctWordList[k] = str;
			k++;
		}
		File[] filesHamTrain = new File("data\\train\\ham").listFiles();
		List<Document> documentListsHam = populateDocumentList(filesHamTrain,status);
		Integer rowLengthHam = filesHamTrain.length;

		Integer columnLength = wordSet.size() + 2;
		Integer[][] hamDocumentMatrix = new Integer[rowLengthHam][columnLength];
		populateMatrix(hamDocumentMatrix, rowLengthHam, columnLength, distinctWordList, documentListsHam,
				DocumentType.ham);
		File[] filesSpamTrain = new File("data\\train\\spam").listFiles();
		List<Document> documentListsSpam = populateDocumentList(filesSpamTrain,status);

		Integer rowLengthSpam = filesSpamTrain.length;

		Integer[][] spamDocumentMatrix = new Integer[filesSpamTrain.length][columnLength];
		populateMatrix(spamDocumentMatrix, rowLengthSpam, columnLength, distinctWordList, documentListsSpam,
				DocumentType.spam);

		Double[] weightArray = new Double[columnLength];
		intializeWeightArray(weightArray);
		Integer rowlength = rowLengthHam + rowLengthSpam;

		Integer[][] combinedMatrix = combineMatrix(hamDocumentMatrix, spamDocumentMatrix, rowLengthHam, rowLengthSpam,
				columnLength);
		File[] filesHamTest = new File("data\\test\\ham").listFiles();
		Double[] weightMatrix = findGradientAscent(combinedMatrix, rowlength, columnLength, weightArray,
				DocumentType.spam);

		File[] filesSpamTest = new File("data\\test\\spam").listFiles();
        trueCount=0;
        falseCount=0;
		doTest(filesHamTest, weightMatrix, distinctWordList, DocumentType.ham,status);
		doTest(filesSpamTest, weightMatrix, distinctWordList, DocumentType.spam,status);
		System.out.println("True count " + trueCount + " false count " + falseCount);
		double num = trueCount;
		double denom = trueCount + falseCount;
		double accuracy = num / denom;
		System.out.println("Accuracy " + accuracy * 100);

	}
/**
 * Method to combine matrix
 * @param hamDocumentMatrix
 * @param spamDocumentMatrix
 * @param rowlengthHam
 * @param rowLenghtSpam
 * @param columnLength
 * @return
 */
	private Integer[][] combineMatrix(Integer[][] hamDocumentMatrix, Integer[][] spamDocumentMatrix,
			Integer rowlengthHam, Integer rowLenghtSpam, Integer columnLength) {

		Integer rowLength = rowlengthHam + rowLenghtSpam;
		Integer[][] combinedMatrix = new Integer[rowLength][columnLength];
		int i = 0;
		for (; i < rowlengthHam; i++) {
			for (int j = 0; j < columnLength; j++) {
				combinedMatrix[i][j] = hamDocumentMatrix[i][j];
			}

		}
		for (int k = 0; k < rowLenghtSpam; k++) {
			for (int j = 0; j < columnLength; j++) {
				combinedMatrix[i][j] = spamDocumentMatrix[k][j];

			}
			i++;
		}
		return combinedMatrix;
	}
	/**
	 * Method to do test
	 * @param filesTest
	 * @param weightMatrix
	 * @param distinctWordList
	 * @param type
	 * @throws IOException
	 */
	private void doTest(File[] filesTest, Double[] weightMatrix, String[] distinctWordList, String type,boolean status)
			throws IOException {

		List<Document> documentLists = populateDocumentList(filesTest,status);
		Integer rowLength = documentLists.size();
		Integer columnLength = distinctWordList.length + 2;
		Integer[][] testMatrix = new Integer[rowLength][columnLength];
		populateMatrix(testMatrix, rowLength, columnLength, distinctWordList, documentLists, type);
		for (int i = 0; i < rowLength; i++) {
			Double rowProductSum = 0.0;
			for (int j = 0; j < columnLength - 1; j++) {
				Double product = calculateProduct(testMatrix[i][j], weightMatrix[j]);
				rowProductSum = rowProductSum + product;
			}
			Double output = applyLogisticRegression(rowProductSum, type);

			if (output > .5) {
				trueCount++;
			} else {
				falseCount++;
			}
		}

	}
	
	/**
	 * Method to populate document list
	 * @param fileList
	 * @return
	 * @throws IOException
	 */
	private List<Document> populateDocumentList(File[] fileList,boolean inclusionStatus)
			throws IOException {
		List<Document> documents = new ArrayList<>();
		DataSet dataSet = new DataSet();
		
		 List<String>ignoreWordList =new ArrayList<>();
				 //dataSet.loadIgnorewords("data/ignore.txt");
		 List<String> stopWordList=null;
		if (!inclusionStatus) {
			stopWordList = new ArrayList<>();
		}
		else{
			stopWordList = dataSet.loadIgnorewords("data/stopwords.txt");
		}
				
		for (int i = 0; i < fileList.length; i++) {
			Document document = new Document();
			File file = fileList[i];
			Map<String, Integer> wordCountMap = new LinkedHashMap<>();
			String line = null;
			FileReader fileReader1 = new FileReader(file);
			BufferedReader bufferedReader1 = new BufferedReader(fileReader1);
			while ((line = bufferedReader1.readLine()) != null) {
				boolean status = dataSet.isLineContainsIgnoreWords(line, ignoreWordList);

				if (!status) {

					dataSet.calculateWordCount(line, wordCountMap, stopWordList);
				}
			}
			bufferedReader1.close();
			document.tokens = wordCountMap;
			documents.add(document);
		}
		return documents;

	}
	/**
	 * Method to intializeWeightArray
	 * @param weightArray
	 */

	private void intializeWeightArray(Double[] weightArray) {
		for (int i = 0; i < weightArray.length; i++) {
			weightArray[i] = 0.001;
		}

	}

	/**
	 * Method to print elements
	 * @param hamDocumentMatrix
	 * @param rowCount
	 * @param columnCount
	 */
	public void printElements(Integer[][] hamDocumentMatrix, Integer rowCount, Integer columnCount) {
		for (int i = 0; i < rowCount; i++) {
			System.out.println();
			for (int j = 0; j < columnCount; j++) {
				//System.out.println( + j + " th column is " + hamDocumentMatrix[i][j]);
				System.out.print( hamDocumentMatrix[i][j]);
				System.out.print("\t");
			}
		}
	}

	/**
	 * populate matrix which contain all element in the matrix
	 * @param documentMatrix
	 * @param rowCount
	 * @param columnCount
	 * @param wordCountMap
	 * @param distinctWordList
	 */

	public void populateMatrix(Integer[][] documentMatrix, Integer rowCount, Integer columnCount,
			String[] distinctWordList, List<Document> documentList, String type) {
		for (int i = 0; i < rowCount; i++) {
			documentMatrix[i][0] = 1;

			if (type.equals(DocumentType.spam)) {
				documentMatrix[i][columnCount - 1] = 1;
			} else {
				documentMatrix[i][columnCount - 1] = 0;

			}
			Document document = documentList.get(i);
			Map<String, Integer> wordCountMap = document.tokens;
			for (int j = 1; j < columnCount - 1; j++) {

				String word = distinctWordList[j - 1];
				Integer count = wordCountMap.get(word);
				if (count != null) {

					documentMatrix[i][j] = count;
				} else {
					documentMatrix[i][j] = 0;
				}

			}

		}
	}
	/**
	 * Method to find the gradient ascent.
	 * @param dataMatrix
	 * @param rowCount
	 * @param columnCount
	 * @param weightMatrix
	 * @param documentType
	 * @return
	 */
	public Double[] findGradientAscent(Integer[][] dataMatrix, Integer rowCount, Integer columnCount,
			Double[] weightMatrix, String documentType) {

		int z = 1;
		Double[] activationUnits = null;
		while (z < 100) {
			activationUnits = new Double[rowCount];
			for (int i = 0; i < rowCount; i++) {
				Double rowProductSum = 0.0;
				for (int j = 0; j < columnCount - 1; j++) {

					Double product = calculateProduct(dataMatrix[i][j], weightMatrix[j]);
					rowProductSum = rowProductSum + product;
				}
				activationUnits[i] = applyLogisticRegression(rowProductSum, documentType);

			}

			for (int p = 0; p < columnCount - 1; p++) {
				Double originalWeight = weightMatrix[p];

				Double increment = 0.0;
				for (int q = 0; q < rowCount; q++) {
					Integer inputCount = dataMatrix[q][p];

					Integer outputClass = dataMatrix[q][columnCount - 1];

					Double activationUnit = activationUnits[q];

					increment = increment + inputCount * (outputClass - activationUnit);
				}
				increment = increment - lamda * originalWeight;
				Double newWeight = originalWeight + learningRate * increment;
				weightMatrix[p] = newWeight;

			}

			z++;
		}

		return weightMatrix;
	}

	/** 
	 * Method to calculate the product.
	 * @param count
	 * @param weight
	 * @return
	 */
	public Double calculateProduct(Integer count, Double weight) {

		Double result = count * weight;
		return result;
	}
/**
 * Method to apply logistic regression.
 * @param rowPrduct
 * @param type
 * @return
 */
	public Double applyLogisticRegression(Double rowPrduct, String type) {

		Double denom = 1 + Math.exp(rowPrduct);

		Double output = 1 / denom;
		if (type.equals(DocumentType.spam)) {
			output = 1 - output;
		}
		return output;

	}
}
