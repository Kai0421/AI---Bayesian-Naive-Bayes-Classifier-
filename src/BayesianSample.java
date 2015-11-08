import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class BayesianSample {

	private static int totalNumberOfWords = 0;
	private static final String L_IMDB = "LargeIMDB", S_IMDB = "SmallIMDB", POS = "pos.txt", NEG = "neg.txt";
	private static HashMap<String, Double> positive = new HashMap<String, Double>(),
			negative = new HashMap<String, Double>();
	private static Set<String> vocabulary = new HashSet<String>();
	private static final String CHARACTERS[] = {",", ".", "`", "¬", "<", ">", "\\", "{", "}", "£", "€", "%", "^", "&", "*", "?", "@", "\"", ";", ":",
			"~", "#", "_", "-", "|", "!", "(", ")", "'+'", "=", "¦", "'['", "']'", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", " ", "$"};
	private static final String MEANINGLESS_WORDS[] = {"a", "b", "c", "d", "e", "f", "g", "h", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
			"u", "v", "w", "x", "y", "z", "the", "be", "will", "he", "she", "it", "his", "her", "hers", "how", "name", "peace", "fruit", "an", "or",
			" ", "!"};
	
	public static void main(String[] args) {
		
		// Set will contain all unique words from the dataset
		//Set<String> vocabulary = new HashSet<String>();
		
		
		// Acccess all files in the dataset and store the words in the set
		loadFilesFromDirectory("pos", vocabulary);
		loadFilesFromDirectory("neg", vocabulary);
		
		printOut(vocabulary);
		
		loadFilesFromDirectory("pos", vocabulary);
		loadFilesFromDirectory("neg", vocabulary);
		printPosNeg(positive, "Positive");
		printPosNeg(negative, "Negative");
		
		calculateProbabilityOfEachWord(positive);
		createModel(positive, POS);
		System.out.println("Total Number Of words -- " + totalNumberOfWords);
	}
	
	/*
	 * @param dirName, vocabulary
	 * loadFilesFromDirectory
	 */
	private static void loadFilesFromDirectory(String dirName, Set<String> vocabulary) {
		
		String path = ".\\" + S_IMDB +"\\"+dirName+"\\"; 
				 
		String fileName;
		File folder = new File(path);
		
		// Creates an array of File from the specified path and directory
		File[] listOfFiles = folder.listFiles(); 
		 
		// Iterates through all the files in the directory
		for (int i = 0; i < listOfFiles.length; i++) 
		{
		   if (listOfFiles[i].isFile()) 
		   {
				fileName = listOfFiles[i].getName();
				
				// Open the specified file and adds it"s contents into the set 
		        captureFileContents(path+fileName, vocabulary);
		   }
		}
		System.out.println(dirName + " dir, Number of files" + listOfFiles.length);
	}

	/*
	 * @params fileName, vocabulary
	 * captureFileContents
	 */
	private static void captureFileContents(String fileName, Set<String> vocabulary) {
		
		try {
			Scanner reader = new Scanner(new File(fileName) );
			
			// Read each string from the file and add to the set
			while (reader.hasNext())
			{
				String word = reader.next().trim().toLowerCase();
				//System.out.println(word);
				
				if(simpleParsing(word))
					if(vocabulary.add(word))
					{
						if(fileName.contains("pos"))
							positive.put(word, (double) 1);
						
						if(fileName.contains("neg"))
							negative.put(word, (double) 1);
					}
					else if(!(vocabulary.add(word)))
					{
						if(fileName.contains("pos"))
							positive.put(word, (positive.containsKey(word) ? positive.get(word) : 0)+1);
						
						if(fileName.contains("neg"))
							negative.put(word, (negative.containsKey(word) ? negative.get(word) : 0)+1);
					}
				
				totalNumberOfWords++;
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}	
	
	private static boolean simpleParsing(String word){
		
		//Check if it"s not a character then search if its a meaningless word
		if(! (word.length() <= 1))
			if(! isCharacterCheck(word))
				if(! isMeaninglessWordCheck(word))
					if(! isCharacterInFrontOfWord(word))
					return true;
		
		//Else return false
		return false;
	}
	
	//private static boolean wordIs1Len()
	
	private static boolean isCharacterInFrontOfWord(String word){
		
		for (String c : CHARACTERS) {
			if(word.contains(c))
			{
				if(word.startsWith(c, 0))
				{
					String splitWord[] = word.split(c);
					
					for(String sw : splitWord)
						if(! sw.contains(c))
							word = sw;
						
				}
				
				if(word.endsWith(c))
				{
					String splitWord[] = word.split(c);
					
					for(String sw : splitWord)
						if(! sw.contains(c))
							word = sw;
				}
				return true;
			}
				
		}
		
		return false;
	}
	
	private static boolean isMeaninglessWordCheck(String word){
		for (String s : MEANINGLESS_WORDS)
			if(word.equals(s))
				return true;
		
		return false;
	}
	
	private static boolean isCharacterCheck(String word){

		for(String i : CHARACTERS)
			if(i.length() == 1)
				if(word.equals(i))
					return true;
		
		return false;
	}
	
	/*
	 * @params vocabulary
	 * DEBUG : printout unique word in the list
	 */
	private static void printOut(Set<String> vocabulary) {
		int numberOfUniqueWords = 0;
		for (String word : vocabulary) {
			//System.out.println(word);
			numberOfUniqueWords++;
		}
		System.out.println("Number of Unique Words Stored => "+numberOfUniqueWords);
	}
	
	private static void printPosNeg(HashMap<String, Double> posOrNeg, String header){
		System.out.println(header + " list size :" + posOrNeg.size());
//		for(String word : posOrNeg.keySet())
//			System.out.println(word.toString() + "- -" + posOrNeg.get(word).toString());
	}
	
	private static void calculateProbabilityOfEachWord(HashMap<String, Double> posOrNeg){
		
		for(String word : posOrNeg.keySet())
		{
			double pV = (Math.log(Double.parseDouble(posOrNeg.get(word).toString()) + Math.log(posOrNeg.size()))+1);
			posOrNeg.put(word, pV);
		}
		
	}

	private static void createModel(HashMap<String, Double> posOrNeg, String fileName) {
		try {
			File file = new File(fileName);

			if (!file.exists())
				file.createNewFile();
			
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			for(String word : posOrNeg.keySet())
			{
				bw.write(word.toString() + " " + posOrNeg.get(word).toString());
				bw.newLine();
			}
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void populateHashMap(String fileName) throws FileNotFoundException {
		File file = new File(fileName);
		FileReader fr;
		try {
			fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;

			while ((line = br.readLine()) != null)
				splitString(line, fileName);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void splitString(String line, String fileName) {
		String keyValue[] = line.split(" ");

		if (keyValue.length == 2)
			if (fileName.equalsIgnoreCase("pos"))
				positive.put(keyValue[0], Double.parseDouble(keyValue[1]));
			else if (fileName.equalsIgnoreCase("neg"))
				negative.put(keyValue[0], Double.parseDouble(keyValue[1]));
			else
				System.out.println("No such file Exists in the directory");

		else
			System.out.println("Error length");
	}

	private static double calculatePVOfDocument(String fileName, Set<String> wordsInTheDocument, HashMap<String, Double> posOrNeg) {
		double sumOfPV = 0;
		for (String word : wordsInTheDocument)
			if (! ((Set<String>) posOrNeg).add(word))
				sumOfPV += Double.parseDouble(posOrNeg.get(word).toString());
		
		sumOfPV += Math.log(posOrNeg.size())+1;
		sumOfPV /= (totalNumberOfWords + vocabulary.size());
		
		return sumOfPV;
	}

	//Another class
	/*private static void determineThePosOrNegReview() {
		// read single file into the array

		double posPV = calculaPVOfADocument("pos", words, positive);
		double negPV = calculatePVOfADocument("neg", words, negative);

		if (posPV > negPV)
			System.out.println("This File has a positive review");
		else if (negPV > posPV)
			System.out.println("This document has a negative review");
		else if (posPV == negPV)
			System.out.println("This file has a neutral review");
		else
			System.out.println("Error !!-!!");

	}*/
}