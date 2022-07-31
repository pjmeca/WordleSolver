package main;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import control.Version;
import main.Letter.STATUS;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class GUI {
	
	public static final String programName = "WordleSolver";
	public static final String downloadURL = "https://github.com/pjmeca/WordleSolver";
	public static final String LOGO_PATH = "resources/images/icon/icon.png";

	private Controller c;

	private static final int NUM_LETTERS = 5;
	private static final int NUM_ROWS = 6;

	private JFrame frame;
	private Letter[][] words;
	private int currentRow = -1;
	private String currentWord = "";
	private boolean win = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = newGame();
					
					// New version prompt
					if (Version.isNewVersionAvailable())
						window.showDialog("New Update!", "A new version of " + programName + " is available!\n"
								+ "Would you like to download it now?", JOptionPane.YES_NO_OPTION, downloadURL);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/*
	 * Starts a new game.
	 */
	private static GUI newGame() {
		GUI window = new GUI();
		window.frame.setVisible(true);
		window.nextWord();
		
		return window;
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		c = new Controller(this);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame(programName);
		frame.setBounds(100, 100, 450, 450);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setMinimumSize(new Dimension(320, 320));
		frame.setLocationRelativeTo(null);

		JTextField txtDefaultPrompt = new JTextField(); // auxiliary textfield for hiding the default prompt
		txtDefaultPrompt.setBounds(0, 0, 0, 0);
		frame.getContentPane().add(txtDefaultPrompt);

		// Icon
		ImageIcon icon = new ImageIcon(LOGO_PATH);
		frame.setIconImage(icon.getImage());

		// Menu
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		JMenu mnNew = new JMenu("New");
		menuBar.add(mnNew);
		JMenuItem mntmNewGame = new JMenuItem("Game");
		mnNew.add(mntmNewGame);
		mntmNewGame.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				newGame();
			}

		});
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		JMenuItem mntmCheckUpdates = new JMenuItem("Check for Updates");
		mntmCheckUpdates.addActionListener(e -> {
			if (Version.isNewVersionAvailable())
				showDialog("New Update!", "A new version of " + programName + " is available!\n"
						+ "Would you like to download it now?", JOptionPane.YES_NO_OPTION, downloadURL);
			else
				showDialog("No Updates", "There are no updates available.\nCurrent version: "+Version.getCurrentVersion(), JOptionPane.INFORMATION_MESSAGE);
		});
		mnHelp.add(mntmCheckUpdates);
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener((e) -> {
			showDialog("About", programName+" "+Version.getCurrentVersion()+
			"\nDeveloped by Pablo Meca", JOptionPane.INFORMATION_MESSAGE);
		});
		mnHelp.add(mntmAbout);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridLayout(6, 5, 0, 0));

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));
		panel.add(buttonsPanel);
		frame.getContentPane().add(panel, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel();
		panel.add(bottomPanel, BorderLayout.SOUTH);

		JButton btnNotFound = new JButton("Word not found");
		bottomPanel.add(btnNotFound);
		btnNotFound.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				notFound();
			}

		});

		JButton btnNext = new JButton("Next Word >>");
		bottomPanel.add(btnNext);
		btnNext.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				nextWord();
			}

		});

		// Create buttons for each letter
		words = new Letter[NUM_ROWS][NUM_LETTERS];
		for (int i = 0; i < NUM_ROWS; i++) {
			for (int j = 0; j < NUM_LETTERS; j++) {
				words[i][j] = new Letter(this, i, j); // aliasing
				buttonsPanel.add(words[i][j].getButton());
			}
		}
	}

	public int getCurrentRow() {
		return currentRow;
	}

	private void setWord(String word) {

		word.replaceAll(" ", "");

		if (word.length() != NUM_LETTERS) {
			System.err.println("Unable to set word: Number of characters mismatch.");
			return;
		}

		char[] wordArray = word.toCharArray();

		for (int j = 0; j < NUM_LETTERS; j++) {
			words[currentRow][j].setLetter(wordArray[j]);
		}
	}

	private void nextWord() {

		if (win || currentRow >= 5) {
			System.err.println("Game ended");
			return;
		}

		int yesCounter = 0;

		// If it is not the first word
		if (currentRow >= 0) {
			// Apply all changes from current word
			// First apply YES
			for (int j = 0; j < NUM_LETTERS; j++) {
				STATUS s = words[currentRow][j].getStatus();
				if (s == STATUS.YES) {
					c.YES(words[currentRow][j].getLetter(), j);
					yesCounter++;
				}
			}

			// Then apply YNT
			for (int j = 0; j < NUM_LETTERS; j++) {
				STATUS s = words[currentRow][j].getStatus();
				if (s == STATUS.YNT) {
					c.YNT(words[currentRow][j].getLetter(), j);
				}
			}

			// Last apply NOT
			for (int j = 0; j < NUM_LETTERS; j++) {
				STATUS s = words[currentRow][j].getStatus();
				if (s == STATUS.NOT) {
					c.NOT(words[currentRow][j].getLetter(), j);
				}
			}
		}

		currentRow++;

		if (yesCounter == NUM_LETTERS) {
			Image img;
			try {
				img = ImageIO.read(new File("resources/images/celebration_emoji.png"));
				img = img.getScaledInstance(30, 30, Image.SCALE_DEFAULT);

				Object[] options = { "Let's go!", "Maybe later" };
				int option = JOptionPane.showOptionDialog(frame, "Yaaaaay we won!!!\nStart a new game?", "Epic win!",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon(img), options,
						options[1]);

				if (option == 0) {
					newGame();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			win = true;
			return;
		}

		// Get a new word
		currentWord = c.getNewWord();
		// Set buttons to new word
		setWord(currentWord);
	}

	/*
	 * Report that current word is not in Wordle's dictionary
	 */
	private void notFound() {

		if (currentWord.isBlank())
			return;

		c.notFound(currentWord);
		// Get a new word
		currentWord = c.getNewWord();
		// Set buttons to new word
		setWord(currentWord);
	}

	/*
	 * Show a dialog reporting that there are no more possible words
	 */
	public void emptyWords() {
		JOptionPane.showMessageDialog(frame, "Oooops! It seems that there are no more possible words :(",
				"No more words", JOptionPane.ERROR_MESSAGE);
	}
	
	
	/*
	 *  Code reused from AnyFind for updates
	 */
	public void showDialog(String title, String message, int messageType, String... url) {
		if (url.length > 0) {
			int selectedOption = JOptionPane.showConfirmDialog(null, message, title, messageType);
			if (selectedOption == JOptionPane.YES_OPTION) {
				try {
					java.awt.Desktop.getDesktop().browse(new java.net.URI(url[0]));
				} catch (Exception e) {
					showDialog("Error", "Cannot open the specified website!\nPlease, try again later or check for program updates if the error persists.", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else
			JOptionPane.showMessageDialog(frame, message, title, messageType);
	}

}
