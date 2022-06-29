package main;

public class Controller {
	
	private GUI myGUI;
	private Dictionary d = new Dictionary(this);
	
	public Controller(GUI myGUI) {
		this.myGUI = myGUI;
	}
	
	public String getNewWord() {
		return d.getNewWord();
	}
	
	public void YES(char c, int pos) {
		d.hasLetterIn(c, pos);
	}
	
	public void YNT(char c, int pos) {
		d.hasLetter(c, pos);
	}
	
	public void NOT(char c, int pos) {
		d.doesntHaveLetter(c, pos);
	}
	
	public void notFound(String word) {
		d.removeWord(word);
	}
	
	// Dictionary calls this method to inform that the set of words is empty, probably because of an incorrect user input
	public void emptyWords() {
		myGUI.emptyWords();
	}

}
