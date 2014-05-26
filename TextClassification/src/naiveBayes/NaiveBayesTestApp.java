package naiveBayes;

// Good source for Naive Baies Classification (and other Information Retrieval algorithms)
// http://www.stanford.edu/class/cs276/

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.Set;

public class NaiveBayesTestApp {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		// parameters:
		int featuresMin, featuresStep, featuresMax;		
		String trainingFile, testingFile;
		
		// choose training-/testing-set
		//trainingFile = "data/bayestest/MultiClass_Training_SVM_100.0.arff"; testingFile = "data/bayestest/MultiClass_Testing_SVM_100.0.arff"; featuresMin=1; featuresStep=1; featuresMax=101;
		//trainingFile = "data/bayestest/MultiClass_Training_SVM_1309.0.arff"; testingFile = "data/bayestest/MultiClass_Testing_SVM_1309.0.arff"; featuresMin=10; featuresStep=100; featuresMax=1311;
		trainingFile = "data/tmp/output-training.arff"; testingFile = "data/tmp/output-testing.arff"; featuresMin=2000; featuresStep=1; featuresMax=2001;
		
		NaiveBayes nb = new NaiveBayes( NaiveBayesFactory.readArffFile(trainingFile) );
		LinkedList<LabeledRecord> testingRecords = NaiveBayesFactory.readArffFile(testingFile);
		LinkedList<LabeledRecord> trainingRecords = NaiveBayesFactory.readArffFile(trainingFile);
		
		PrintWriter logfile = new PrintWriter(trainingFile + "-logfile.csv", "UTF-8");
		logfile.println("Feature Selection\tTraining Examples\tTraining Precitions Correct\tTraining Predictions Error\tTraining Prediction Accuray(%)\tTesting Examples\tTesting Precitions Correct\tTesting Predictions Error\tTesting Prediction Accuray(%)\tTime (ms)");
		
		int noOfTotalTestingExamples = testingRecords.size();
		int noOfTotalTrainingExamples = nb.getTotalNoOfRecords();
		int noOfTestingErrors = 0, noOfTrainingErrors = 0;
		long startTime = 0, endTime = 0;
		System.out.println("Testing learnt NaiveBayes algorithm with different feature sets (from:" + featuresMin + ";step:" + featuresStep + ";to:" + featuresMax + ")");
		for(int i=featuresMin; i<featuresMax; i+=featuresStep) {
			System.out.print(i + " ");
			Set<Attribute> featureSelection = nb.getFeatureSelection(i);
			
			noOfTrainingErrors = 0;
			for(LabeledRecord trainingRecord : trainingRecords) {
				if(!nb.testNaiveBayes(trainingRecord, featureSelection)) noOfTrainingErrors++;
			}
			
			noOfTestingErrors = 0; startTime = 0; endTime = 0;
			startTime = System.currentTimeMillis();
			for(LabeledRecord testingRecord : testingRecords) {
				if(!nb.testNaiveBayes(testingRecord, featureSelection)) noOfTestingErrors++;
			}
			endTime = System.currentTimeMillis();
	
			logfile.println(i
					+ "\t" + noOfTotalTrainingExamples
					+ "\t" + (noOfTotalTrainingExamples-noOfTrainingErrors)
					+ "\t" + noOfTrainingErrors
					+ "\t" + (double)(noOfTotalTrainingExamples-noOfTrainingErrors)/noOfTotalTrainingExamples
					+ "\t" + noOfTotalTestingExamples
					+ "\t" + (noOfTotalTestingExamples-noOfTestingErrors) 
					+ "\t" + noOfTestingErrors 
					+ "\t" + (double)(noOfTotalTestingExamples-noOfTestingErrors)/noOfTotalTestingExamples
					+ "\t" + (endTime-startTime));
		}
		logfile.close();
		
		System.out.println("\n-------------------------");
		System.out.println("Last test results:");
		System.out.println("Training-Data:");
		System.out.println("Total Training records: " + noOfTotalTrainingExamples);
		System.out.println("Predictions correct:    " + (noOfTotalTrainingExamples-noOfTrainingErrors));
		System.out.println("Predictions error:      " + noOfTrainingErrors);
		System.out.println("Accuracy:               " + Math.round((noOfTotalTrainingExamples-noOfTrainingErrors)*100./noOfTotalTrainingExamples) + " %");
		System.out.println("Testing-Data:");
		System.out.println("Total Testing records:  " + noOfTotalTestingExamples);
		System.out.println("Predictions correct:    " + (noOfTotalTestingExamples-noOfTestingErrors));
		System.out.println("Predictions error:      " + noOfTestingErrors);
		System.out.println("Accuracy:               " + Math.round((noOfTotalTestingExamples-noOfTestingErrors)*100./noOfTotalTestingExamples) + " %");
		System.out.println("Duration:               " + (endTime-startTime) + " ms");
	}
}
