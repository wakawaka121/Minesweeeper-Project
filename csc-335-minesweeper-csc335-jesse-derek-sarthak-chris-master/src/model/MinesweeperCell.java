package model;

import java.io.Serializable;

/**
 * 
 * @author Chris Lin, Sarthak Bawal, Derek Tominaga, Jesse Gomez
 * 
 * This class contains everything we need in a single cell on the board
 *
 */
public class MinesweeperCell implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private boolean hidden;
	private boolean mined;
	private boolean flagged;
	private int adjacentMines;
	private int rowCord;
	private int colCord;
	
	/**
	 * initiate the variables
	 * @param row int row 
	 * @param col int column
	 */
	public MinesweeperCell(int row, int col) {
		hidden = true;
		mined = false;
		flagged = false;
		adjacentMines = 0;
		rowCord = row;
		colCord = col;
	}
	
	/**
	 * Check if the cell is mined
	 * @return true or false
	 */
	public boolean isMined() {
		return mined;
	}
	
	/**
	 * set the value representing the amount of mines around
	 * @param value amount of mines around the cell
	 */
	public void setMine(boolean value) {
		mined = value;
	}
	
	/**
	 * set the value to the adjacent mines
	 * @param value int the amount of mines around
	 */
	public void setAdjacentMines(int value) {
		adjacentMines = value;
	}
	
	/**
	 * get the mines around
	 * @return the amount of adjacent mines
	 */
	public int getMines() {
		return adjacentMines;
	}
	
	/**
	 * increase the mines cell is representing
	 */
	public void increaseMines() {
		adjacentMines++;
	}
	
	/**
	 * check if the cell is still hidden
	 * @return true or false
	 */
	public boolean isHidden() {
		return hidden;
	}
	
	/**
	 * reveal the cell
	 */
	public void setHidden() {
		hidden = false;
	}
	
	/**
	 * check if the cell is being flagged
	 * @return true or false
	 */
	public boolean isFlagged() {
		return flagged;
	}
	
	/**
	 * set the flag on cell if it is not getting flagged
	 */
	public void setFlagged() {
		if(flagged == true) {
			flagged = false;
		} else if(flagged == false) {
			flagged = true;
		}
	}
	
	/**
	 * get the row
	 * @return rowCord
	 */
	public int getRow() {
		return rowCord;
	}
	
	/**
	 * get the column
	 * @return colCord
	 */
	public int getCol() {
		return colCord;
	}

}