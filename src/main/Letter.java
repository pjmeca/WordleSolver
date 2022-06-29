package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class Letter {
	
	private JButton btn;
	private int row, column;
	private GUI myGui;
	private char letter;
	
	private static final Color NOT_COLOR = Color.GRAY;
	private static final Color YNT_COLOR = Color.ORANGE;
	private static final Color YES_COLOR = Color.GREEN;
	private static final Color DEFAULT_COLOR = Color.WHITE;
	
	public static enum STATUS{
		YES, YNT, NOT;
	}
	
	public Letter(GUI myGui, int row, int column) {
		this.row = row;
		this.column = column;
		this.myGui = myGui;
		
		btn = new JButton();
		btn.setFont(new Font("Arial Black", Font.PLAIN, 28));
		btn.setBackground(DEFAULT_COLOR);
		
		// When pressed, the button should change its color (white, orange, green)
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// A button can only change if it letters belongs to the current word
				if(myGui.getCurrentRow() != row)
					return;
				
				if(btn.getBackground() == NOT_COLOR)
					btn.setBackground(YNT_COLOR);
				else if(btn.getBackground() == YNT_COLOR)
					btn.setBackground(YES_COLOR);
				else
					btn.setBackground(NOT_COLOR);
			}
			
		});
	}
	
	public JButton getButton() {
		return btn;
	}
	
	public char getLetter() {
		return letter;
	}
	
	public void setLetter(char letter) {
		
		this.letter = letter;
		
		// Make sure it belongs to our current word
		if(myGui.getCurrentRow() != row) {
			System.err.println("Unable to set letter: It doesn't belong to the current word.");
			return;
		}
		
		btn.setText((letter+"").toUpperCase());
		btn.setBackground(NOT_COLOR);
	}
	
	public STATUS getStatus() {
		Color c = btn.getBackground();
		
		if(c == YES_COLOR)
			return STATUS.YES;
		else if(c == YNT_COLOR)
			return STATUS.YNT;
		else if(c == NOT_COLOR)
			return STATUS.NOT;
		else
			return null;
	}

}
