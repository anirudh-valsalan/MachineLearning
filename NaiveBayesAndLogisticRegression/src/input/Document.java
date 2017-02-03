package input;

import java.util.HashMap;
import java.util.Map;

public class Document {

	/**
	 * List of token counts
	 */
	public Map<String, Integer> tokens;
	public Map<String,Double> priorProbMap;

	/**
	 * The class of the document
	 */
	public String category;
	public Integer count;

	/**
	 * Document constructor
	 */
	public Document() {
		tokens = new HashMap<>();
	}

}
