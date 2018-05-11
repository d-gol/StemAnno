package backend;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;


public class Vocabulary {
	// wordId => Word
	private TreeMap<String, Word> wordsInfo = new TreeMap<String, Word>();
	
	public Vocabulary() {
		
	}
	
	public void setWordsInfo(TreeMap<String, Word> newWords) throws Exception {
		wordsInfo = newWords;
	}
	
	public TreeMap<String, Word> getWordsInfo() {
		return wordsInfo;
	}
	
	public Boolean isEmpty() {
		return wordsInfo.isEmpty();
	}
	
	public void addWord(String wordId, Word word) {
		wordsInfo.put(wordId, word);
	}
	
	public Word getWordById(String wordId) {
		return wordsInfo.get(wordId);
	}
	
	public Word getWordByIndex(Integer index) {
		String wordId = (new ArrayList<String>(wordsInfo.keySet())).get(index);
		return getWordById(wordId);
	}
	
	public ArrayList<String> getStems() {
		ArrayList<String> stems = new ArrayList<String>();
		for(Entry<String, Word> entry: wordsInfo.entrySet()) {
			stems.add(entry.getValue().stem);
		}
		return stems;
	}
	
	public ArrayList<String> getWordIds() {
		return new ArrayList<String>(wordsInfo.keySet());
	}
	
	public void removeWord(String wordId) {
		wordsInfo.remove(wordId);
	}
	
	public Boolean containsWord(String wordName) {
		for(Entry<String, Word> entry: wordsInfo.entrySet()){
			if (entry.getValue().word.equals(wordName)) {
				return true;
			}
	    }
		return false;
	}
	
	// Returns all words with specific value
	public ArrayList<Word> getAllWordsByValue(String value) {
		ArrayList<Word> res = new ArrayList<Word>();
		
		for (Entry<String, Word> entry: wordsInfo.entrySet()) {
			Word word = entry.getValue();
			if (word.word.equals(value)) {
				res.add(word);
			}
		}
		
		return res;
	}
	
	public Boolean containsWordId(String wordId) {
		return wordsInfo.containsKey(wordId);
	}
	
	public Integer getSize() {
		return wordsInfo.size();
	}
	
	// Returns the first entry in map
	public Word getFirstWord() {
		if (!wordsInfo.isEmpty()) {
			return wordsInfo.firstEntry().getValue();
		}
		return new Word();
	}
	
	public Word getNextWord(String wordId) throws Exception {
		ArrayList<String> keys = new ArrayList<String>(wordsInfo.keySet());
		Integer index = keys.indexOf(wordId) + 1;
		
		if (index >= keys.size()) {
			if (keys.size() == 1) {
				return new Word();
			}
			else {
				return getFirstWord();
			}
		}
		
		return getWordById(keys.get(index));
	}
	
	public void splitWordContext(String wordId, ArrayList<String> secondWordContexts) throws Exception {
		ArrayList<String> firstWordContexts = wordsInfo.get(wordId).contexts;
		
		// To prevent splitting so that second word takes all contexts
		if (secondWordContexts.size() == 0 || secondWordContexts.size() >= firstWordContexts.size()) {
			throw new Exception();
		}
		
		for (String removingContext: secondWordContexts) {
			if (firstWordContexts.contains(removingContext)) {
				firstWordContexts.remove(removingContext);
			}
		}
		
		Word firstWord = wordsInfo.get(wordId);
		firstWord.contexts = firstWordContexts;
		
		Word secondWord = new Word();
		
		if (firstWord.isSplit) {
			String lastMatching = "";
			for (Entry<String, Word> entry: wordsInfo.entrySet()) {
				Word currentWord = entry.getValue();
				if (currentWord.wordId.matches(firstWord.word + "_\\d+")) {
					lastMatching = currentWord.wordId;
				}
			}
			Integer nextId = Integer.parseInt(lastMatching.split("_")[1]) + 1;
			secondWord = new Word(firstWord.word + "_" + nextId, firstWord.word, firstWord.stem, secondWordContexts, true);
		}
		else {
			firstWord.wordId = firstWord.word + "_1";
			secondWord = new Word(firstWord.word + "_2", firstWord.word, firstWord.stem, secondWordContexts, true);
		}
		
		firstWord.isSplit = true;
		
		wordsInfo.remove(wordId);
		
		wordsInfo.put(firstWord.wordId, firstWord);
		wordsInfo.put(secondWord.wordId, secondWord);
	}
	
	public void mergeWords(ArrayList<String> wordIds) throws Exception {
		if (wordIds.size() <= 1) {
			throw new Exception();
		}
		String base = wordIds.get(0);
		/*if (!base.matches(".*_\\d+")) {
			throw new Exception();
		}
		base = base.split("_")[0];*/
		if (base.matches(".*_\\d+")) {
			base = base.split("_")[0];
		}
		//base = base.split("_")[0];
		ArrayList<String> allContexts = new ArrayList<String>();
		for (String wid: wordIds) { // if any of the selected is not in a form base_word_[digit]
			//if (!wid.matches(base + "_\\d+")) {
			if (!wid.equals(base) && !wid.matches(base + "_\\d+")) {
				throw new Exception();
			}
			allContexts.addAll(getWordById(wid).contexts);
		}
		Word mergedWord = new Word(base, base, base, allContexts, false);
		for (String wid: wordIds) {
			removeWord(wid);
		}
		if (wordsInfo.containsKey(base)) {
			wordsInfo.get(base).contexts.addAll(allContexts);
		}
		else {
			addWord(mergedWord.wordId, mergedWord);
		}
		Integer cnt = 0;
		ArrayList<Entry<String, Word>> entries = new ArrayList<Entry<String, Word>>();
		ArrayList<Entry<String, Word>> toRemove = new ArrayList<Entry<String, Word>>();
		for (Entry<String, Word> entry: wordsInfo.entrySet()) {
			Word word = entry.getValue();
			if (word.word.equals(base)) {
				toRemove.add(entry);
				cnt++;
				word.wordId = base + "_" + cnt.toString();
				word.isSplit = true;
				entries.add(new AbstractMap.SimpleEntry<String, Word>(word.wordId , word));
			}
		}
		for (Entry<String, Word> entry: toRemove) {
			wordsInfo.remove(entry.getKey());
		}
		if (entries.size() == 1) {
			Word newWord = entries.get(0).getValue();
			newWord.wordId = base;
			newWord.isSplit = false;
			wordsInfo.put(newWord.wordId, newWord);
		}
		else {
			for (Entry<String, Word> entry: entries) {
				wordsInfo.put(entry.getKey(), entry.getValue());
			}
		}
	}
	
}
