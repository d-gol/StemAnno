package backend;

import java.util.ArrayList;

public class Word {
	public String wordId = "";
	public String word = "";
	public String stem = "";
	public ArrayList<String> contexts = new ArrayList<String>();
	public Boolean isSplit = false;
	
	public Word() {
		
	}
	
	public Word(String wid, String w, String s) {
		wordId = wid;
		word = w;
		stem = s;
		isSplit = false;
	}
	
	public Word(String wid, String w, String s, ArrayList<String> cont, Boolean isSpl) {
		wordId = wid;
		word = w;
		stem = s;
		contexts = cont;
		isSplit = isSpl;
	}
	
	public void moveStemLeft() throws Exception {
		if (stem.length() > 0) {
			stem = stem.substring(0, stem.length() - 1);
		}
	}
	
	public void moveStemRight() throws Exception {
		if (stem.length() < word.length()) {
			stem = word.substring(0, stem.length() + 1);
		}
	}
}
