package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WordEvaluator {

	private static final String DEFAULT_FREQUENCY_RUTE = "resources/lang/spanish/frequency.txt";
	private static final int LANGUAGE_NUM_VOWELS = 5;
	private List<Character> frequency;

	public WordEvaluator() {
		this(DEFAULT_FREQUENCY_RUTE);
	}

	public WordEvaluator(String rute) {
		frequency = readFile(rute);
	}

	private List<Character> readFile(String rute) {
		// Create the set
		List<Character> frequency = new LinkedList<>();

		File f = new File(rute);
		Scanner scan;
		try {
			scan = new Scanner(f, "UTF-8");

			if (scan.hasNext())
				scan.nextLine(); // skip first line (source)
			else {
				scan.close();
				throw new IllegalArgumentException("File is empty!");
			}

			// Read each word
			while (scan.hasNext()) {
				char letter = scan.nextLine().toLowerCase().charAt(0);

				frequency.add(letter);
			}

			scan.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return frequency;
	}

	/*
	 * Evaluates how good a word is from 0 to 10.
	 * 
	 */
	public int eval(String word) {

		if (word.length() == 0)
			return 0;

		int value = 0;
		Set<Character> letters = word.chars().mapToObj(chr -> (char) chr).collect(Collectors.toSet());

		// Repeated letters -> bad
		double repeatedLetters = 0;
		for (char c : letters) {
			long count = word.chars().filter(ch -> ch == c).count();
			if (count > 1)
				repeatedLetters++;
		}
		double repeatedPercent = repeatedLetters / letters.size();
		int repeatedValue = 0;
		if (repeatedPercent >= 0.5)
			repeatedValue = 0;
		else
			repeatedValue = 10 - (int) Math.round(repeatedPercent * 10);

		// Different vowels -> good
		// wont take into account accent marks
		double numVowels = (double) (letters.stream().filter(c -> Pattern.matches("[aeiou]", c + ""))
				.collect(Collectors.toSet()).size());
		double vowelsPercent = numVowels / LANGUAGE_NUM_VOWELS;
		int vowelsValue = (int) Math.round(vowelsPercent * 20);
		vowelsValue = vowelsValue > 10 ? 10 : vowelsValue;

		// Lots of common letters -> good
		double commonPercent = 0;
		for (char c : letters) {
			commonPercent += ((double) frequency.indexOf(c)) / frequency.size();
		}
		commonPercent /= letters.size(); // mean
		int commonValue = 10 - (int) Math.round(commonPercent * 10);

		// Final mean
		value = (int) Math.round(repeatedValue * 0.4 + vowelsValue * 0.2 + commonValue * 0.4);

		// System.out.println("Word evaluation = "+value+"\n\tRepetitions =
		// "+repeatedValue+"\n\tVowels = "+vowelsValue+"\n\tCommon = "+commonValue);

		return value;
	}

	public static void main(String[] args) {

		WordEvaluator we = new WordEvaluator();

		Scanner sc = new Scanner(System.in);

		while (sc.hasNext())
			we.eval(sc.nextLine());

		sc.close();
	}

}
