package main;

import java.util.Scanner;

public class Main {

	public static final String syntaxmsg = "Commands:\n" + "\t new: Get a new word\n"
			+ "\t yes \"character\" \"position\": Wordle returned that the word contains that character in that position.\n"
			+ "\t ynt \"character\" \"position\": Wordle returned that the word contains that character, but not in the specific position.\n"
			+ "\t not \"character\" \\\"position\\\": Wordle returned that the word doesn't contain that character in the specific position.\n"
			+ "\t got: We guessed the word!!!\n"
			+ "*Note: positions values from 0 to 4.\n"
			+ "**Note: Enter first the yes, then the ynt and last the not.";

	private static void solver(Scanner sc) {
		System.out.println("Welcome to WordleSolver console!\n" + syntaxmsg);

		Dictionary d = new Dictionary();

		for (int i = 0; i < 6; i++) {

			String currWord = d.getNewWord();
			System.out.println("Chosen word: " + currWord);
			
			boolean nextIt = false;

			while (!nextIt) {
				String response = sc.nextLine();
				
				if (response.contains("new")) {
					nextIt = true;
				} else if (response.contains("not")) {
					char c = response.charAt(4);
					int pos = response.charAt(6) - '0';
					d.doesntHaveLetter(c, pos);
				} else if (response.contains("ynt")) {
					char c = response.charAt(4);
					int pos = response.charAt(6) - '0';
					d.hasLetter(c, pos);
				} else if (response.contains("yes")) {
					char c = response.charAt(4);
					int pos = response.charAt(6) - '0';
					d.hasLetterIn(c, pos);
				} else if (response.contains("got")) {
					System.out.println("Nice!!! We got it!!!");
					return;
				} else {
					System.err.println("ERROR: Sequence not valid, please, check the syntax.\n" + syntaxmsg);
				}
			}

		}
		
		System.out.println("We did not get it, sorry :(");
	}
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		solver(sc);
		sc.close();	
	}
}
