package backend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.TreeMap;

public class FileTokenizer {
	public final static String errorMessage = "ERROR in FileTokenizer: ";
	private int maxLength = 0;
	private static ArrayList<String> tokensList = new ArrayList<String>(); // all tokens, including punctuation
	private static String allText;
	private static String invalidTokenRegex = "[\\…\\p{Punct}\\s]+";
	private static String stopContextRegex = "[\\…\\.?!]+"; // regex to stop when calculating contexts on both sides
	
	public FileTokenizer() {
		
	}
	
	public TreeMap<String, ArrayList<String>> getAllContexts(ArrayList<String> words) {
		TreeMap<String, ArrayList<String>> res = new TreeMap<String, ArrayList<String>>();
		
		for (String word: words) {
			res.put(word, getWordContexts(word));
		}
		
		return res;
	}
	
	private Boolean isValidToken(String token) {
		// Is punctuation or space
		if (token.matches(invalidTokenRegex)) {
			return false;
		}
		String onlyNumbersAndPunct = "[\\…\\p{Punct}\\s\\d]+";
		if (token.matches(onlyNumbersAndPunct)) {
			return false;
		}
		// Is number
		try {
			Double.parseDouble(token);
		} catch (NumberFormatException nfe) {
		    return true;
		}  
		return false;
	}
	
	public static ArrayList<String> getWordContexts(String word) {
		ArrayList<String> res = new ArrayList<String>();
		
		for (int i = 0; i < tokensList.size(); i++) {
			if (tokensList.get(i).toLowerCase().equals(word.toLowerCase())) {
				res.add(getContextByIndex(i));
			}
		}
		
		return res;
	}
	
	private static String getContextByIndex(Integer ind) {
		return getLeft(ind) + " " + tokensList.get(ind) + " " + getRight(ind);
	}
	
	public static String getLeft(Integer ind) {
		String res = "";
		Integer start = (ind >= 5)? ind - 5: 0;
		
		//for (int i = start; i < ind; i++) {
		for (int i = ind - 1; i >= start; i--) {
			if (tokensList.get(i).matches(stopContextRegex)) {
				break;
			}
			res = tokensList.get(i) + res;
			if (i != start) {
				res = " " + res;
			}
		}
		
		return res;
	}
	
	public static String getRight(Integer ind) {
		String res = "";
		Integer end = (ind <= tokensList.size() - 6)? ind + 6: tokensList.size(); 
		
		for (int i = ind + 1; i < end; i++) {
			if (tokensList.get(i).matches(stopContextRegex)) {
				res += tokensList.get(i);
				break;
			}
			res += tokensList.get(i);
			if (i != end - 1) {
				res += " ";
			}
		}
		
		return res;
	}
	
	public void readVocabularies(String inputFilename, Vocabulary empty, Vocabulary stemmed) throws Exception {
		// Read all text as a string, for reading contexts later on
		File file = new File(inputFilename);
		
		if(!file.exists()) { 
			throw new Exception(errorMessage + "input file does not exist.");
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFilename), "UTF-8"));
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();
	    while (line != null) {
	        sb.append(line);
	        sb.append("\r\n");
	        line = br.readLine();
	    }
	    br.close();
		
		allText = sb.toString();
		String[] allTokens = allText.split("[\\s]+");
		
		for (String token: allTokens) {
			tokensList.add(token);
		}
		
		// Add stemmed words
		int lastIndxDot = inputFilename.lastIndexOf('.');
		if (lastIndxDot == -1) {
			lastIndxDot = inputFilename.length();
		}
		String filenameMap = inputFilename.substring(0, lastIndxDot) + "_word_stem_map.txt";
		File fileMap = new File(filenameMap);
		
		if(fileMap.exists()) { 
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filenameMap), "UTF-8"));
		    line = br.readLine();
		    
		    while (line != null) {
		        String[] tokens = line.toLowerCase().split("\\t");
		        if (tokens.length != 3) {
		        	br.close();
		        	throw new Exception(errorMessage + "error during map file reading, length of a row is not 3.");
		        }
		        
		        String wordId = tokens[0];
		        if (isValidToken(wordId)) {
			        String stem = tokens[1];
			        //Boolean isSplit = wordId.endsWith("_\\d+");
			        Boolean isSplit = wordId.matches(".*_\\d+");
			        String wordValue = isSplit? wordId.split("_")[0] : wordId;
			        String wordContexts[] = tokens[2].split("\\|");
			        
			        Word word = new Word(wordId, wordValue, stem, new ArrayList<String>(Arrays.asList(wordContexts)), isSplit);
			        stemmed.addWord(wordId, word);
			        
			        if (wordValue.length() > maxLength) {
						maxLength = tokens[0].length();
					}
		        }
		        line = br.readLine();
		    }
		    br.close();
		}
		
		// Add empty words
		//String[] tokens = allText.toLowerCase().split("[\\…\\p{Punct}\\s]+");
		
		for (String wordValue: tokensList) {
			wordValue = wordValue.toLowerCase();
			if (isValidToken(wordValue)) {
				if (!stemmed.containsWord(wordValue)) {
					String stem = wordValue;
					// Empty words will never be split!
					//Boolean isSplit = wordId.endsWith("_\\d+");
			        //String wordValue = isSplit? wordId.split("_")[0] : wordId;
			        
			        Word word = new Word(wordValue, wordValue, stem, getWordContexts(wordValue), false);
			        empty.addWord(wordValue, word);
			        if (wordValue.length() > maxLength) {
						maxLength = wordValue.length();
					}
				}
			}
		}
	}
	
	public static void saveToFiles(String inputFilename, Vocabulary voc) throws Exception {
		// Save word stem context file
		int lastIndxDot = inputFilename.lastIndexOf('.');
		
		if (lastIndxDot == -1) {
			lastIndxDot = inputFilename.length();
		}
		Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(inputFilename.substring(0, lastIndxDot) + "_word_stem_map.txt"), "UTF8"));
		
        for(Entry<String, Word> entry: voc.getWordsInfo().entrySet()){
        	Word word = entry.getValue();
            out.append(word.wordId + "\t" + word.stem + "\t"); // Every word has at least one context
            for (int i = 0; i < word.contexts.size(); i++){
            	out.append(word.contexts.get(i));
            	if (i < word.contexts.size() - 1) {
            		out.append("|");
            	}
            }
            out.append("\r\n");
        }

        out.close();
        
        // Save original file with replacements
        saveReplaceWordsByStems(inputFilename.substring(0, lastIndxDot) + "_replace.txt", voc);
	}
	
	private static void saveReplaceWordsByStems(String outputFilename, Vocabulary voc) throws Exception {
	    Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFilename), "UTF8"));

	    String[] lines = allText.split("\r\n");
    	String toWrite = "";
    	Integer tokensSoFar = 0;
    	
    	for (int k = 0; k < lines.length; k++) {
    		String line = lines[k];
    		String[] tokens = line.toLowerCase().split("[\\s]+");
	    	for (int i = 0; i < tokens.length; i++) {
	    		String wordValue = tokens[i];
	    		if (voc.containsWord(wordValue)) {
	    			ArrayList<Word> words = voc.getAllWordsByValue(wordValue);
	    			// If there is only one such a word, normal case
	    			if (words.size() == 1) {
	    				//toWrite = toWrite.replaceAll(wordValue, words.get(0).stem);
	    				tokens[i] = words.get(0).stem;
	    			}
	    			else if (words.size() > 1) { // If there is a word that had been split
	    				// Check which context is matching
						String contextInOriginalFile = getContextByIndex(tokensSoFar + i);
	    				for (Word word: words) {
	    					if (word.contexts.contains(contextInOriginalFile)) {
	    						tokens[i] = word.stem;
	    						break;
	    					}
	    				}
	    			}
	    		}
	    	}
	    	tokensSoFar += tokens.length;
	    	line = "";
	    	for (String token: tokens) {
	    		line += token + " ";
	    	}
	    	lines[k] = line.substring(0, line.length() - 1);
    	}
    	
    	for (String line: lines) {
    		toWrite = toWrite + line + "\r\n";
    	}
    	
        out.append(toWrite);
        
	    out.close();
	}
	
	public int getMaxLength() {
		return maxLength;
	}
}
