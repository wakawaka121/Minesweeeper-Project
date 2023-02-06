package controller;

import java.util.ArrayList;
import java.util.Collections;

import jdk.jfr.Timespan;
import model.MinesweeperCell;
import model.MinesweeperModel;

/**
 * 
 * @author Chris Lin, Sarthak Bawal, Derek Tominaga, Jesse Gomez
 * 
 * This class is the controller of the Minesweeper. It contains the method we need for the playing the game
 * and any function we need during the game.
 *
 */
public class MinesweeperController {
	
	private MinesweeperModel model;
	private MinesweeperCell[][] refToBoard;
	private boolean gameOver;
	private int mineCount;
	private int flagCount;
	private int cellsHidden;
	private boolean gameWon;
	
	/**
	 * initialize variables
	 * @param model MinesweeperModel the model class
	 */
	public MinesweeperController(MinesweeperModel model) {
		this.model = model;
		refToBoard = model.getBoard();
		gameOver = false;
		gameWon = false;
		cellsHidden = model.getRow() * model.getCol();
		flagCount = 0;
	}
	
	/**
	 * check if we have won the game and store the winning time
	 * @param timeInSeconds int the playing time
	 * @return true or false
	 */
	public boolean gameWon() {
		return gameWon;
	}
	
	/**
	 * flag or unflag a cell on the board and reduce or add the flag count
	 * @param row int row
	 * @param col int column
	 */
	public void flagCell(int row, int col) {
		MinesweeperCell curCell = refToBoard[row][col];
		curCell.setFlagged();
		if(curCell.isFlagged()) {
			flagCount++;
		} else {
			flagCount--;
		}
	}
	
	/**
	 * the method for player move
	 * @param row int row
	 * @param col int column
	 */
	public void playMove(int row, int col) {
		MinesweeperCell  curMove = refToBoard[row][col];
		if(curMove.isHidden() && !curMove.isFlagged() && !gameOver) {
			if(model.getCellsHidden() == model.getRow() * model.getCol()) {
				model.setBombs(row, col);
				revealCells(row, col);
			} 
			else if(curMove.isMined()) {
				gameOver = true;
				showBombs();
			}
			else {
				revealCells(row, col);
			}
		}
		isGameOver();

	}
	
	/**
	 * Reveal all cells after a move is made.
	 * @param row int row 
	 * @param col int column
	 */
	// Reveal all cells after a move is made.
	// If a cells has 0 mines around it, it will recursively reveal all cells until it stops.
	private void revealCells(int row, int col) {
		if(row < 0 || col < 0 || row >= model.getRow() || col >= model.getCol()) {
			return;
		}
		
		if(!refToBoard[row][col].isHidden()) {
			return;
		}
		
		refToBoard[row][col].setHidden();
		model.decCellsHidden();

		if(refToBoard[row][col].getMines() == 0) {
			revealCells(row, col - 1);
			revealCells(row, col + 1);
			revealCells(row - 1, col);
			revealCells(row - 1, col - 1);
			revealCells(row - 1, col + 1);
			revealCells(row + 1, col);
			revealCells(row + 1, col - 1);
			revealCells(row + 1, col + 1);
		}
	}

	/**
	 * Show all the bombs 
	 */
	public void showBombs() {
		ArrayList<MinesweeperCell> bombsArray = model.getBombs();
		for(int i=0; i<bombsArray.size(); i++) {
			bombsArray.get(i).setHidden();
		}
	}
	
	/**
	 * return the cell object
	 * @param row int row
	 * @param col int column
	 * @return the cell object
	 */
	public MinesweeperCell getCellClue(int row, int col) {
		return refToBoard[row][col];
		
	}
	
	/**
	 * Check if the game is over
	 * @return true or false
	 */
	public boolean isGameOver() {
		if(model.getCellsHidden() == model.countOfMines()) {
			gameOver = true;
			gameWon = true;
			return gameOver;
		} else if(!gameOver) {
			return false;
		} else {
			return gameOver;
		}
	}
	
	/**
	 * the String representation of high score
	 * @return the String representation of high score
	 */
	public String getHighScoreString(){
		ArrayList<Integer> highScore = model.getHighScore();
		String highScoreString = "";
		if(highScore.size() != 0) {
			Collections.sort(highScore);
			for(int score = 0; score < highScore.size(); score++) {
				int curScore = highScore.get(score);
				String time = String.format("%2d:%02d", curScore / 60, curScore % 60);
				highScoreString += (score+1) + ": " + time + "\n";
			}
		}
		return highScoreString;
		
			
	}
}

