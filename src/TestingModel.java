import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class TestingModel {
	
	private final String  TEST_DATA = "smallTest", POS = "pos.txt", NEG = "neg.txt";
	private HashMap<String, Double> positive = new HashMap<String, Double>(),
			negative = new HashMap<String, Double>();
	private int numberOfPosFile = 0, numberOfNegFile = 0;	
	
	private void loadFilesFromDirectory(String dirName) {

		String path = ".\\" + TEST_DATA + "\\" + dirName + "\\";

		String fileName;
		File folder = new File(path);

		// Creates an array of File from the specified path and directory
		File[] listOfFiles = folder.listFiles();

		// Iterates through all the files in the directory
		for (int i = 0; i < listOfFiles.length; i++) 
			if (listOfFiles[i].isFile()) {
				fileName = listOfFiles[i].getName();
				
				// Open the specified file and adds it"s contents into the set
				captureFileContents(path + fileName);				
			}
		
		System.out.println(dirName + " dir, Number of files" + listOfFiles.length);
	}
	
	private void captureFileContents(String fileName) {
		int totalNumberOfWords = 0;
		double posPV = 0, negPV = 0;
		try {
			Scanner reader = new Scanner(new File(fileName));
			
			
			// Read each string from the file and add to the set
			while (reader.hasNext()) {
				String word = reader.next().trim().toLowerCase();
			
				posPV += calculatePVOfDocument(word, positive);
				negPV += calculatePVOfDocument(word, negative);
				
				totalNumberOfWords++;
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		determineThePosOrNegReview(posPV, negPV);
	}

	private void populateHashMap(String fileName){
		File file = new File(fileName);
		FileReader fr;
		
		try{
			fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;

			while ((line = br.readLine()) != null)
				splitString(line, fileName);

			br.close();
			System.out.println("Done Populating hashmap " + fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void splitString(String line, String fileName) {
		String keyValue[] = line.split(" ");

		if (keyValue.length == 2)
			if (fileName.equalsIgnoreCase(POS))
				positive.put(keyValue[0], Double.parseDouble(keyValue[1]));
			else if (fileName.equalsIgnoreCase(NEG))
				negative.put(keyValue[0], Double.parseDouble(keyValue[1]));
			else
				System.out.println("No such file Exists in the directory");

		else
			System.out.println("Error length");
	}

	private double calculatePVOfDocument(String word, HashMap<String, Double> posOrNeg) {
		
		if (posOrNeg.containsKey(word))
			return Double.parseDouble(posOrNeg.get(word).toString());

		return 0;
	}
	

	private void determineThePosOrNegReview(double posPV, double negPV) {
		
		if (posPV > negPV)
			numberOfPosFile++;
		else if (negPV > posPV)	
			numberOfNegFile++;
	}
	
	private void printPosNeg(HashMap<String, Double> posOrNeg, String header){
		System.out.println(header + " list size :" + posOrNeg.size());
		for(String word : posOrNeg.keySet())
			System.out.println(word.toString() + "- -" + posOrNeg.get(word).toString());
	}
	
	public TestingModel(){	
		//Load Model 
		populateHashMap(POS);
		populateHashMap(NEG);
		
		loadFilesFromDirectory("pos");
		System.out.println("Pos - Number of Pos File " + numberOfPosFile + " " + ((numberOfPosFile*100)/1000) + "%");
		System.out.println("Pos - Number of Neg File " + numberOfNegFile + " " + ((numberOfNegFile*100)/1000) + "%\n\n");
		
		numberOfNegFile = 0;
		numberOfPosFile = 0;
		loadFilesFromDirectory("neg");
		System.out.println("Neg - Number of Pos File " + numberOfPosFile + " " + ((numberOfPosFile*100)/1000) + "%");
		System.out.println("Neg - Number of Neg File " + numberOfNegFile + " " + ((numberOfNegFile*100)/1000) + "%");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new TestingModel();
	}
}
