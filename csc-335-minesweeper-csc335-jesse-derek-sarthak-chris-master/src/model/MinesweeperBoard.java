package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author Chris Lin, Sarthak Bawal, Derek Tominaga, Jesse Gomez
 * 
 * This class provides basic elements we need for the game board
 *
 */
public class MinesweeperBoard implements Serializable {

	private static final long serialVersionUID = 1L;

	private MinesweeperCell[][] mineSweepBoard;
	private ArrayList<MinesweeperCell> bombsArray;
	private int rows;
	private int cols;
	private int mines;
	private int cellsHidden;
	private int time;
	private ArrayList<Integer> highScore;

	/**
	 * initialize the private variables
	 * @param rows int row of the board
	 * @param cols int column of the board
	 * @param mines int amount of mines on the board
	 * @param cellsHidden int amount of cells hidden on board
	 * @param mineSweepBoard MinesweeperCell[][] the main board
	 * @param bombsArray ArrayList<Integer> the array of score
	 */
	public MinesweeperBoard(int rows, int cols, int mines, int cellsHidden,
			MinesweeperCell[][] mineSweepBoard, ArrayList<MinesweeperCell> bombsArray, ArrayList<Integer> highScore, int time) {
		this.rows = rows;
		this.cols = cols;
		this.mines = mines;
		this.cellsHidden = cellsHidden;
		this.mineSweepBoard = mineSweepBoard;
		this.bombsArray = bombsArray;
		this.highScore = highScore;
		this.time = time;
	}
	
	/**
	 * get rows
	 * @return rows
	 */
	public int getRows() {
		return rows;
	}
	
	/**
	 * get columns
	 * @return cols
	 */
	public int getCols() {
		return cols;
	}
	
	/**
	 * get amount of mines
	 * @return mines
	 */
	public int getMines() {
		return mines;
	}
	
	/**
	 * get the amount of hidden cells
	 * @return cellsHidden
	 */
	public int getCellsHidden() {
		return cellsHidden;
	}
	
	/**
	 * get the main board
	 * @return mineSweepBoard
	 */
	public MinesweeperCell[][] getMineSweepBoard(){
		return mineSweepBoard;
	}
	
	/**
	 * The array of bombs
	 * @return the array of bombs
	 */
	public ArrayList<MinesweeperCell> getBombsArray(){
		return bombsArray;
	}
	
	/**
	 * get the target cell
	 * @param row int row on the board
	 * @param col int column on the board
	 * @return the target cell
	 */
	public MinesweeperCell getCell(int row, int col) {
		return mineSweepBoard[row][col];
	}
	
	/**
	 * get the high score array
	 * @return highScore
	 */
	public ArrayList<Integer> getHighScore(){
		return highScore;
	}
	
	/**
	 * get the time of the board
	 * @return time
	 */
	public int getTime() {
		return time;
	}

}