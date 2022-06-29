package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

public class Dictionary {
	
	private Controller myController;

	private static final String DEFAULT_DICTIONARY_RUTE = "resources/spanish/dictionary.txt";
	private static final int DEFAULT_NUMLETTERS = 5;

	private Random rand = new Random();
	private WordEvaluator evaluator = new WordEvaluator();
	private static final int NUM_EVAL_SEARCHES = 100; // number of searches before lowering the eval value

	private Map<Integer, String> words;
	private int numLetters;
	private char[] guessedWord;

	/*------------------------------------*/
	public Dictionary(Controller c) {
		this(c, DEFAULT_DICTIONARY_RUTE, DEFAULT_NUMLETTERS);
	}

	public Dictionary(Controller c, int numLetters) {
		this(c, DEFAULT_DICTIONARY_RUTE, numLetters);
	}

	public Dictionary(Controller c, String rute) {
		this(c, rute, DEFAULT_NUMLETTERS);
	}

	public Dictionary(Controller c, String rute, int numLetters) {
		myController = c;
		this.numLetters = numLetters;

		words = readFile(rute, numLetters);
		guessedWord = new char[numLetters];

		for (int i = 0; i < numLetters; i++) {
			guessedWord[i] = '0';
		}
	}
	/*------------------------------------*/

	/*
	 * Creates a set with all the words with numLetters letters from a text file.
	 */
	private Map<Integer, String> readFile(String rute, int numLetters) {
		// Create the set
		HashMap<Integer, String> words = new HashMap<>();

		// Read all the words from the file
		try {
			File f = new File(rute);
			Scanner scan = new Scanner(f, "UTF-8");
			int index = 0;

			// Read each word
			while (scan.hasNext()) {
				String word = scan.nextLine();

				// Check if it has the right number of letters
				if (word.length() == numLetters) {

					// Check if it doesn't have accents or a space
					if (!Pattern.matches(".*[αινσϊό ].*", word)) {
						words.put(index, word);
						index++;
					}
				}
			}

			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return words;
	}

	/*
	 * Returns a random word from current words.
	 */
	public String getNewWord() {

		// We will use an auxiliary list to avoid choosing a position we already removed
		LinkedList<Integer> aux = new LinkedList<>(words.keySet());

		System.out.println("Current set size: " + aux.size());

		try {
			int pos = rand.nextInt(aux.size());

			// We will jump max in a value between 1-10 to add some randomness
			int random = aux.size() < 10 ? 1 : (rand.nextInt(10) + 1);

			pos = (pos + random) % aux.size();
			String word = words.get(aux.get(pos));

			// Now its time to evaluate how good the word is
			int wordEval = evaluator.eval(word); // value of the first word
			int desiredEval = 10; // desired initial value, will be lowered if nothing is found
			int evaluations = 1; // evaluations done with current desiredEval value
			while (wordEval < desiredEval) {
				if (evaluations == NUM_EVAL_SEARCHES) {
					evaluations = 0;
					desiredEval--;
				}

				pos = (pos + 1) % aux.size();
				word = words.get(aux.get(pos));
				wordEval = evaluator.eval(word);
				evaluations++;
			}

			return word;
		} catch (IllegalArgumentException e) {
			myController.emptyWords();
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * Removes from the words those who have the char c in pos (or in any pos if it
	 * is not in another one yet).
	 */
	public void doesntHaveLetter(char c, int pos) {
		Set<Integer> auxSet = new HashSet<>(words.keySet());

		for (int wordKey : auxSet) {
			// This should be always be true, just in case
			if (words.containsKey(wordKey)) {

				char[] currWord = words.get(wordKey).toCharArray();
				int index = words.get(wordKey).indexOf(c);

				// We know it has the char, but we have to verify if it is still valid
				if (index >= 0) {

					boolean endFor = false;

					// Check if the positions where the char is are valid compared to guessedWord
					for (int i = 0; i < numLetters && !endFor; i++) {
						// Check if the char is in this position
						if (currWord[i] == c) {
							// Check if the guessedWord has the char there
							if (guessedWord[i] != c) {
								// If it is not, remove the word and end
								words.remove(wordKey);
								endFor = true;
							}
						}
					}
				}
			}
		}
	}

	/*
	 * Removes from the words those who doesn't have the char c or have it in
	 * position pos.
	 */
	public void hasLetter(char c, int pos) {
		Set<Integer> auxSet = new HashSet<>(words.keySet());

		for (int wordKey : auxSet) {
			// This should be always be true, just in case
			if (words.containsKey(wordKey)) {
				int index = words.get(wordKey).indexOf(c);
				if (index == -1 || index == pos)
					words.remove(wordKey);
			}
		}
	}

	/*
	 * Removes from the words those who doesn't have the char c in the position pos.
	 */
	public void hasLetterIn(char c, int pos) {
		// First, we must update the guessed word
		guessedWord[pos] = c;

		Set<Integer> auxSet = new HashSet<>(words.keySet());

		for (int wordKey : auxSet) {
			// This should be always be true, just in case
			if (words.containsKey(wordKey)) {
				if (words.get(wordKey).charAt(pos) != c)
					words.remove(wordKey);
			}
		}
	}

	/*
	 * Removes the specified word from the map
	 */
	public void removeWord(String word) {
		Set<Integer> auxSet = new HashSet<>(words.keySet());
		for (int i : auxSet) {
			if (words.get(i).equals(word)) {
				words.remove(i);
				System.out.println("Word \"" + word + "\" correctly removed from set.");
				return;
			}
		}
		System.err.println("Cannot remove the word \"" + word + "\": Not found in set.");
	}

	/*
	 * Creates a test.txt file with all the words that will be stored in the set.
	 */
	private void test() {
		System.out.println(words);

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("resources/test.txt"));
			for (int wordKey : words.keySet()) {
				out.write(words.get(wordKey));
				out.newLine();
			}
			out.close();
		} catch (IOException e) {
		}
	}

	public static void main(String[] args) {

		Dictionary dict = new Dictionary(null, DEFAULT_DICTIONARY_RUTE, 5);
		dict.test();

	}
}
