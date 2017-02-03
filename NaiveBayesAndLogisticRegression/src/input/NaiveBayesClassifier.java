package input;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NaiveBayesClassifier {
	/**
	 * calculatePriorProbabilities
	 * 
	 * @param documentList
	 * @param globalCount
	 * @return
	 */
	public List<Document> calculatePriorProbabilities(List<Document> documentList, Integer globalCount) {
		List<Document> docList = new ArrayList<>();
		Document hamDocument = documentList.get(0);
		Map<String, Integer> mapList = hamDocument.tokens;
		Integer countHam = hamDocument.count;
		Integer denominatorHam = globalCount + countHam;
		Map<String, Double> priorMapHam = calculatePriorProbabilities(mapList, denominatorHam);
		hamDocument.priorProbMap = priorMapHam;

		Document spamDocument = documentList.get(1);
		Map<String, Integer> spamMapList = spamDocument.tokens;
		Integer countSpam = spamDocument.count;
		Integer denominatorSpam = globalCount + countSpam;
		Map<String, Double> priorMapSpam = calculatePriorProbabilities(spamMapList, denominatorSpam);
		spamDocument.priorProbMap = priorMapSpam;
		docList.add(hamDocument);
		docList.add(spamDocument);
		return docList;

	}

	/**
	 * Calculate calculatePriorProbabilities
	 * 
	 * @param map
	 * @param denominator
	 * @return
	 */
	public Map<String, Double> calculatePriorProbabilities(Map<String, Integer> map, Integer denominator) {

		Map<String, Double> priorMap = new LinkedHashMap<>();
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			Double numerator = (double) entry.getValue() + 1;

			Double conditionalProbablity = numerator / denominator;
			//Double logValue = -(Math.log(conditionalProbablity) / Math.log(2));
			priorMap.put(entry.getKey(), conditionalProbablity);

		}

		return priorMap;

	}
	
}
