package input;

import java.io.IOException;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws IOException {
		Scanner scanner =new Scanner(System.in);
		while (true) {
			System.out.println("Please enter the choice");
			System.out.println("1)NaiveBayes");
			System.out.println("2)NaiveBayes with stop words");
			System.out.println("3)Logistic Regression");
			System.out.println("4)Logistic Regression with stop words");
			System.out.println("5.Exit");
			System.out.println("Please enter the choice");

			int ch = scanner.nextInt();
			DataSet dataSet = new DataSet();
			LogisticRegressionClassifier lc = new LogisticRegressionClassifier();
			if (ch == 1) {

				dataSet.doNaiveBayes(false);
			}
			if (ch == 2) {
				dataSet.doNaiveBayes(true);
			}
			if (ch == 3) {
				lc.doLogisticRegression(false);
			}
			if (ch == 4) {
				lc.doLogisticRegression(true);
			}
			if (ch == 5) {
				break;
			}
			
		}
		
		scanner.close();
	}

}
