import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class FileUtil {
	/*
	 * This method will read the input from the file and will put the values into a row*column matrix.
	 * row will corresponds to the number of rows in the input and column will corresponds to number of
	 * attribute in the input.
	 */
	public Integer[][] readInputFile(String fileName) throws IOException {

		Integer[][] inputData;
		int row = 0, column = 0;
		String dataRecord = null;
		BufferedReader br = new BufferedReader(new FileReader(fileName));

		String header = br.readLine();
		while (br.readLine() != null) {
			row++;
		}
		br.close();
		StringTokenizer sToken = new StringTokenizer(header, ",");
		int i = 0;
		String input = null;
		while (sToken.hasMoreTokens()) {
			input = sToken.nextToken();
			Tree.headRow.put(i, input);
			Tree.headRowMap.put(input, i++);
		}
		column = Tree.headRow.size();
		inputData = new Integer[row][column];
		// Fill training Data
		br = new BufferedReader(new FileReader(fileName));

		br.readLine();
		row = 0;
		while ((dataRecord = br.readLine()) != null) {
			column = 0;
			sToken = new StringTokenizer(dataRecord, ",");
			while (sToken.hasMoreTokens()) {
				inputData[row][column++] = Integer.parseInt(sToken.nextToken());
			}
			row++;
		}

		return inputData;
	}

}
