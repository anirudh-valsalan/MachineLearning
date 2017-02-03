package input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class DataSet {
	public static Integer globalCount = null;
	public static Integer totalHamCount = null;
	public static Integer totalSpamCount = null;
	public static Map<String, Double> spamMap = null;
	public static Map<String, Double> hamMap = null;
	public static Integer truePositiveCount = 0;
	public static Integer falsePositiveCount = 0;
	public static Integer trueNegativeCount = 0;
	public static Integer falseNegativeCount = 0;

	public DataSet() {
		truePositiveCount = 0;
		falsePositiveCount = 0;
		trueNegativeCount = 0;
		falseNegativeCount = 0;
	}

	public void doNaiveBayes(boolean status ) throws IOException {
		
		NaiveBayesClassifier naiveBayesClassifier = new NaiveBayesClassifier();
	

		List<String> ignoreWordList = new ArrayList<>();
				//loadIgnorewords("data/ignore.txt");
		List<String> stopWordList =null;
		if(!status){
			stopWordList=new ArrayList<>();
		}
		else{
				
			stopWordList=	loadIgnorewords("data/stopwords.txt");
		}

		
		List<Document> documents =populateDocumentList(stopWordList);
        Set<String> globalWordList=createGlobalVocabulary(documents);
        
		globalCount = globalWordList.size();
		List<Document> documentList = naiveBayesClassifier.calculatePriorProbabilities(documents, globalCount);

	//	System.out.println("Global count" + globalCount + "hamcount" + totalHamCount + "totalspam" + totalSpamCount);

		Double hamProbability = (double) totalHamCount / (totalHamCount + totalSpamCount);
		Double logHamProbability = -(Math.log(hamProbability)) / (Math.log(2));
		Double spamProbability = (double) totalSpamCount / (totalHamCount + totalSpamCount);
		Double logSpamProbability = -(Math.log(spamProbability)) / (Math.log(2));
		File[] filesHamTest = new File("data\\test\\ham").listFiles();
		for (int i = 0; i < filesHamTest.length; i++) {
			doTrainingData(filesHamTest[i], ignoreWordList, stopWordList, documentList.get(0).priorProbMap,
					documentList.get(1).priorProbMap, DocumentType.ham, logHamProbability, logSpamProbability);
		}
		/*System.out.println("True positive count" + truePositiveCount);
		System.out.println("False positive count" + falsePositiveCount);*/
		File[] filesSpamTest = new File("data\\test\\spam").listFiles();
		for (int i = 0; i < filesSpamTest.length; i++) {
			doTrainingData(filesSpamTest[i], ignoreWordList, stopWordList, documentList.get(0).priorProbMap,
					documentList.get(1).priorProbMap, DocumentType.spam, logHamProbability, logSpamProbability);
		}
		/*System.out.println("True negative" + trueNegativeCount);
		System.out.println("False negative" + falseNegativeCount);*/
		System.out.println("Correct classification count"+(truePositiveCount + trueNegativeCount));
		System.out.println("False classification count"+(falseNegativeCount+falsePositiveCount));
		System.out.println("total count "+(truePositiveCount + trueNegativeCount + falseNegativeCount + falsePositiveCount));
		Double accuracy = (double) (truePositiveCount + trueNegativeCount)
				/ (truePositiveCount + trueNegativeCount + falseNegativeCount + falsePositiveCount);
		System.out.println("Accuracy" + accuracy*100);
	}
	
	
	public List<Document> populateDocumentList(List<String> stopWordList) throws IOException {
		List<Document> documents = new ArrayList<>();

		List<String> ignoreWordList = new ArrayList<>();
		// loadIgnorewords("data/ignore.txt");

		// loadIgnorewords("data/stopwords.txt");

		Document documentHam = new Document();
		populateHamDocument(documentHam, ignoreWordList, stopWordList);

		documents.add(documentHam);

		Document documentSpam = new Document();
		populateSpamDocument(documentSpam, ignoreWordList, stopWordList);

		documents.add(documentSpam);
		return documents;

	}
	
	/**
	 * 
	 * @throws IOException
	 *//*
	
	public void loaddataForNaiveBayes() throws IOException {
		Document documentHam = new Document();
		List<String> ignoreWordList = loadIgnorewords("data/ignore.txt");
		List<String> stopWordList = new ArrayList<>();
		populateHamDocument(documentHam, ignoreWordList, stopWordList);
	}*/

	/**
	 * Method to populate spam document.
	 * 
	 * @param documentSpam
	 * @param ignoreWordList
	 * @param stopWordList
	 * @throws IOException
	 */
	private void populateSpamDocument(Document documentSpam, List<String> ignoreWordList, List<String> stopWordList)
			throws IOException {
		File[] filesSpamTrain = new File("data\\train\\spam").listFiles();
		Map<String, Integer> wordCountMapSpamTrain = new LinkedHashMap<>();
		populateMap(filesSpamTrain, wordCountMapSpamTrain, ignoreWordList, stopWordList);
		totalSpamCount = calculateTotalCount(wordCountMapSpamTrain);
		documentSpam.tokens = wordCountMapSpamTrain;
		documentSpam.category = DocumentType.spam;
		documentSpam.count = totalSpamCount;

	}

	/**
	 * Method to populate Ham document.
	 * 
	 * @param documentHam
	 * @param ignoreWordList
	 * @param stopWordList
	 * @throws IOException
	 */
	private void populateHamDocument(Document documentHam, List<String> ignoreWordList, List<String> stopWordList)
			throws IOException {
		File[] filesHamTrain = new File("data\\train\\ham").listFiles();
		Map<String, Integer> wordCountMapHamTrain = new LinkedHashMap<>();
		populateMap(filesHamTrain, wordCountMapHamTrain, ignoreWordList, stopWordList);
		totalHamCount = calculateTotalCount(wordCountMapHamTrain);
		documentHam.tokens = wordCountMapHamTrain;
		documentHam.category = DocumentType.ham;
		documentHam.count = totalHamCount;

	}

	/**
	 * Method to do training on the input data.
	 * 
	 * @param file
	 * @param ignoreWordList
	 * @param stopWordList
	 * @param hamMap
	 * @param spamMap
	 * @param type
	 * @param hamProbability
	 * @param spamProbability
	 * @throws IOException
	 */
	public void doTrainingData(File file, List<String> ignoreWordList, List<String> stopWordList,
			Map<String, Double> hamMap, Map<String, Double> spamMap, String type, Double hamProbability,
			Double spamProbability) throws IOException {
		Map<String, Integer> map = new HashMap<>();
		populateMap(file, map, ignoreWordList, stopWordList);
		Double outputHam = hamProbability;
		Double outputSpam = spamProbability;

		for (Map.Entry<String, Integer> entry : map.entrySet()) {

			String key = entry.getKey();
			Integer occurance = entry.getValue();

			Double condProbHam = hamMap.get(key);

			if (condProbHam != null) {
				outputHam = outputHam + occurance *-(Math.log(condProbHam) /Math.log(2));
			}

			Double condProbSpam = spamMap.get(key);
			if (condProbSpam != null) {
				outputSpam = outputSpam + occurance * -(Math.log(condProbSpam) /Math.log(2));
			}

		}

		if (type.equals(DocumentType.ham)) {
			if (outputHam > outputSpam) {
				truePositiveCount++;
			} else {

				falsePositiveCount++;
			}
		}
		if (type.equals(DocumentType.spam)) {
			if (outputHam > outputSpam) {

				falseNegativeCount++;
			} else {

				trueNegativeCount++;
			}
		}
	}

	/**
	 * Method to calculate the total count.
	 * 
	 * @param wordCountMap
	 * @return
	 */
	private Integer calculateTotalCount(Map<String, Integer> wordCountMap) {
		Integer totalCount = 0;
		for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {
			totalCount = totalCount + entry.getValue();

		}

		return totalCount;
	}

	/**
	 * Method to populate the map.
	 * 
	 * @param files
	 * @param wordCountMap
	 * @param ignoreWordList
	 * @throws IOException
	 */

	public void populateMap(File[] files, Map<String, Integer> wordCountMap, List<String> ignoreWordList,
			List<String> stopWordList) throws IOException {
		for (int i = 0; i < files.length; i++) {
			File file = files[i];

			String line = null;
			FileReader fileReader1 = new FileReader(file);
			BufferedReader bufferedReader1 = new BufferedReader(fileReader1);
			while ((line = bufferedReader1.readLine()) != null) {
				/*boolean status = isLineContainsIgnoreWords(line, ignoreWordList);

				if (!status) {*/

					calculateWordCount(line, wordCountMap, stopWordList);
				//}
			}
			bufferedReader1.close();
		}
		
		for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {

			String key = entry.getKey();
			Integer occurance = entry.getValue();
			System.out.println("key is "+key+" value"+occurance);
		}

	}

	/**
	 * Method to populate map.
	 * 
	 * @param file
	 * @param wordCountMap
	 * @param ignoreWordList
	 * @param stopWordList
	 * @throws IOException
	 */
	public void populateMap(File file, Map<String, Integer> wordCountMap, List<String> ignoreWordList,
			List<String> stopWordList) throws IOException {

		String line = null;
		FileReader fileReader1 = new FileReader(file);
		BufferedReader bufferedReader1 = new BufferedReader(fileReader1);
		while ((line = bufferedReader1.readLine()) != null) {
			/*boolean status = isLineContainsIgnoreWords(line, ignoreWordList);

			if (!status) {
*/
				calculateWordCount(line, wordCountMap, stopWordList);
			//}
		}
		bufferedReader1.close();
	}

	/***
	 * Method to check whether line contains Ignore words or not.
	 * 
	 * @param line
	 * @param ignoreWordList
	 * @return
	 */
	public boolean isLineContainsIgnoreWords(String line, List<String> ignoreWordList) {
		boolean status = false;
		for (String igWord : ignoreWordList) {
			if (!line.contains(igWord)) {
				continue;
			} else {
				status = true;
				break;
			}
		}
		return status;
	}

	/**
	 * Method to check whether the word contain stop words or not.
	 * 
	 * @param word
	 * @param stopWordList
	 * @return
	 */
	public boolean isWordContainsStopWords(String word, List<String> stopWordList) {
		boolean status = false;
		for (String stopWord : stopWordList) {
			if (!word.trim().equals(stopWord.trim())) {
				continue;
			} else {
				status = true;
				break;
			}
		}
		return status;
	}

	/**
	 * Method which will find the count of each word in the input.
	 * 
	 * @param line
	 * @param wordCountMap
	 */
	public void calculateWordCount(String line, Map<String, Integer> wordCountMap, List<String> stopWords) {

		String DELIMITERS = "\n _ -:;!@#$%^&*()=+[]{}<>,.?/\\\"'|";

		StringTokenizer stringTokenizer = new StringTokenizer(line, DELIMITERS);
		while (stringTokenizer.hasMoreTokens()) {
			String word = stringTokenizer.nextToken();

			if (word.matches("[- ./ 0-9]+"))
				continue;
			if (isWordContainsStopWords(word, stopWords)) {
				continue;
			}
			if (word.length() < 2) {
				continue;
			}
			Integer count = wordCountMap.get(word);
			if (null != count) {
				count = count + 1;
				wordCountMap.put(word, count);
			} else {
				wordCountMap.put(word, 1);
			}
		}
	}

	/**
	 * Method which will return the ignore wordList.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public List<String> loadIgnorewords(String path) throws IOException {
		FileReader fileReader = new FileReader(path);
		String line = null;
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		List<String> ignoreWordList = new ArrayList<>();
		while ((line = bufferedReader.readLine()) != null) {
			ignoreWordList.add(line);
		}
		bufferedReader.close();
		return ignoreWordList;

	}

	/**
	 * Method to calculate the GlobalMap Size.
	 * 
	 * @param documentList
	 * @return
	 */

	public Set<String>  createGlobalVocabulary(List<Document> documentList) {
		Set<String> globalWordSet = new LinkedHashSet<>();
		Map<String, Integer> hamMap = documentList.get(0).tokens;
		for (Map.Entry<String, Integer> entry : hamMap.entrySet()) {
			globalWordSet.add(entry.getKey());
		}
		Map<String, Integer> spamMap = documentList.get(1).tokens;
		for (Map.Entry<String, Integer> entry : spamMap.entrySet()) {
			globalWordSet.add(entry.getKey());
		}
		return globalWordSet;
		/*Integer size = globalWordSet.size();
		return size;*/
	}

}
