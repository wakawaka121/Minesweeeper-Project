package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * 
 * @author Chris Lin, Sarthak Bawal, Derek Tominaga, Jesse Gomez
 * 
 * The model contains all the basic element we need for the game minesweeper.
 * Including the board, everything we need in a cell with MinesweeperCell class, and all
 * the information we need to know about the board. The game board is also getting setup 
 * in this class. 
 * 
 *
 */
@SuppressWarnings("deprecation")
public class MinesweeperModel {
	private static final int DEFAULT_SIZE = 10;
	private MinesweeperCell[][] mineSweepBoard;
	private ArrayList<MinesweeperCell> bombsArray;
	private int rows;
	private int cols;
	private int mines;
	private int cellsHidden;
	private ArrayList<Integer> highScore;
	private int time;

	/**
	 * A default board 10x10 with 10 mines
	 * 
	 */
	public MinesweeperModel() {
		buildBoard(DEFAULT_SIZE, DEFAULT_SIZE, DEFAULT_SIZE); // Default board if no parameters are mentioned. (10 by 10
								// with 10 mines)
	}

	/**
	 * A custom board with indicating number of rows, columns and mines
	 * @param rows int number of rows
	 * @param cols int number of columns
	 * @param mines int number of mines
	 */
	public MinesweeperModel(int rows, int cols, int mines) {
		buildBoard(rows, cols, mines);
	}

	/**
	 * Initialize the board
	 * @param rows int number of rows
	 * @param cols int number of columns
	 * @param mines int number of mines
	 */
	private void buildBoard(int rows, int cols, int mines) {
		this.rows = rows;
		this.cols = cols;
		this.mines = mines;
		cellsHidden = rows * cols;
		bombsArray = new ArrayList<MinesweeperCell>();
		mineSweepBoard = new MinesweeperCell[rows][cols];
		time = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				mineSweepBoard[i][j] = new MinesweeperCell(i, j);
			}
		}
	}
	
	/**
	 * set up the bombs randomly on the board 
	 * @param rows int number of rows
	 * @param cols int number of columns
	 */
	public void setBombs(int row, int col) {
		ArrayList<int[][]> bombConfig;

		do {
			bombConfig = newBombConfig(row, col);
		} while (!solveBoard(bombConfig.get(0), row, col));

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (bombConfig.get(1)[i][j] == 1) {
					mineSweepBoard[i][j].setMine(true);
					bombsArray.add(mineSweepBoard[i][j]);
				} else {
					mineSweepBoard[i][j].setAdjacentMines(bombConfig.get(0)[i][j]);
				}
			}
		}
	}

	/**
	 * record the bombs on the board
	 * @param row row
	 * @param col column
	 * @return bombs on the board
	 */
	private ArrayList<int[][]> newBombConfig(int row, int col) {
		Random randRow = new Random();
		Random randCol = new Random();
		int[][] bombs = new int[rows][cols];
		int[][] currentLocations = new int[rows][cols];

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				bombs[i][j] = 0;
				currentLocations[i][j] = 0;
			}
		}

		for (int mine = 0; mine < mines; mine++) {
			int mineRow = randRow.nextInt(rows);
			int mineCol = randCol.nextInt(cols);
			while (currentLocations[mineRow][mineCol] == 1
					|| !validBombLocation(mineRow, mineCol, row, col)) {
				mineRow = randRow.nextInt(rows);
				mineCol = randCol.nextInt(cols);
			}
			currentLocations[mineRow][mineCol] = 1;
			updateAdjacentCells(mineRow, mineCol, bombs);
		}
		ArrayList<int[][]> bombData = new ArrayList<>();
		bombData.add(bombs);
		bombData.add(currentLocations);
		return bombData;
	}

	/**
	 * Check if the location is good to place a bomb
	 * @param mineRow int row of the mine
	 * @param mineCol int column of the mine
	 * @param row row of the board
	 * @param col column of the board
	 * @return true or false
	 */
	private boolean validBombLocation(int mineRow, int mineCol, int row, int col) {
		return (Math.abs(mineRow - row) > 1 && Math.abs(mineCol - col) > 1);
	}

	/**
	 * auto solve the game
	 * @param bombs int[][] the bombs on the board
	 * @param row int row
	 * @param col int column
	 * @return true or false
	 */
	private boolean solveBoard(int[][] bombs, int row, int col) {
		MinesweeperCell[][] board = new MinesweeperCell[rows][cols];
		int[] ref;

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				board[i][j] = new MinesweeperCell(i, j);
				board[i][j].setAdjacentMines(bombs[i][j]);
				if (i == row && j == col) {
					board[i][j].setHidden();
				}
			}
		}
		boolean modifiedCell = false;
		int totalMods = 0;
		while (true) {
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					if (!board[i][j].isHidden() && !board[i][j].isFlagged()) {
						ref = countValidNeighbors(board, i, j);
						if (ref[1] == board[i][j].getMines() && ref[0] != board[i][j].getMines() && board[i][j].getMines() > 0) {
							totalMods = totalMods + flagNeighbors(board, i, j);
							modifiedCell = true;
						} else if (ref[0] == board[i][j].getMines() && ref[1] > board[i][j].getMines()) {
							totalMods = totalMods + playMove(board, i, j, bombs);
							modifiedCell = true;
						}
					}
				}
			}
			if (modifiedCell == false) {
				break;
			}
			modifiedCell = false;
		}
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (board[i][j].isHidden() && !board[i][j].isFlagged()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Take a move for the auto-solver
	 * @param board MinesweeperCell[][] the game board
	 * @param row int row
	 * @param col int col
	 * @param bombs int[][] bombs on the board
	 * @return
	 */
	private int playMove(MinesweeperCell[][] board, int row, int col, int[][] bombs) {
		int modified = 0;
		for (int i = row - 1; i <= row + 1; i++) {
			for (int j = col - 1; j <= col + 1; j++) {
				if (i == row && j == col) {
					continue;
				}

				if (validIndex(i, j)) {
					if (board[i][j].isHidden() && !board[i][j].isFlagged()) {
						modified++;
						board[i][j].setHidden();
					}
				}
			}
		}
		return modified;
	}

	/**
	 * Flag the cell for the auto-solver
	 * @param board MinesweeperCell[][] the game board
	 * @param row int row
	 * @param col int column
	 * @return the cell that gets modified
	 */
	private int flagNeighbors(MinesweeperCell[][] board, int row, int col) {
		int modified = 0;
		for (int i = row - 1; i <= row + 1; i++) {
			for (int j = col - 1; j <= col + 1; j++) {
				if (i == row && j == col) {
					continue;
				}

				if (validIndex(i, j)) {
					if (board[i][j].isHidden() && !board[i][j].isFlagged()) {
						modified++;
						board[i][j].setFlagged();
					}
				}
			}
		}
		return modified;
	}

	/**
	 * count the flagged cells and the hidden cells
	 * @param board MinesweeperCell[][] the game board
	 * @param row int row 
	 * @param col int column
	 * @return an int array with counted flagged cells and hidden cells
	 */
	private int[] countValidNeighbors(MinesweeperCell[][] board, int row, int col) {
		int[] ret = new int[2];

		ret[0] = 0; // Number of surrounding flagged cells
		ret[1] = 0;	// Number of surrounding hidden cells

		for (int i = row - 1; i <= row + 1; i++) {
			for (int j = col - 1; j <= col + 1; j++) {
				if (i == row && j == col) {
					continue;
				}

				if (validIndex(i, j)) {
					if (board[i][j].isHidden()) {
						ret[1]++;
					}

					if (board[i][j].isFlagged()) {
						ret[0]++;
					}
				}
			}
		}

		return ret;
	}

	/**
	 * Verifies that the row and col are within bounds
	 * @param row
	 * @param col
	 * @return true or false
	 */
	private boolean validIndex(int row, int col) {
		return (row >= 0 && col >= 0 && row < rows && col < cols);
	}

	/**
	 * Get the array of bombs
	 * @return the bombsArray
	 */
	public ArrayList<MinesweeperCell> getBombs() {
		return bombsArray;
	}

	/**
	 * Get the game board
	 * @return the game board
	 */
	public MinesweeperCell[][] getBoard() {
		return mineSweepBoard;
	}

	/**
	 * Get the data we serialized
	 * @return a new MinesweeperBoard
	 */
	public MinesweeperBoard getSerialized() {
		return new MinesweeperBoard(rows, cols, mines, cellsHidden, mineSweepBoard,
				bombsArray, highScore, time);
	}

	/**
	 * update the cell around for the auto solving
	 * @param row int row 
	 * @param col int column
	 * @param bombs int number of bombs
	 */
	private void updateAdjacentCells(int row, int col, int[][] bombs) {
		for (int i = row - 1; i <= row + 1; i++) {
			for (int j = col - 1; j <= col + 1; j++) {
				if (i == row && j == col) {
					continue;
				}

				if (validIndex(i, j)) {
					bombs[i][j]++;
				}
			}
		}
	}

	/**
	 * increase the number of bombs indicated near by on the indicate row and column
	 * @param rows int number of rows
	 * @param cols int number of columns
	 */
	private void updateAdjacentBombs(int row, int col) {
		for (int i = row - 1; i <= row + 1; i++) {
			for (int j = col - 1; j <= col + 1; j++) {
				if (i == row && j == col) {
					continue;
				}

				if (validIndex(i, j)) {
					mineSweepBoard[i][j].increaseMines();
				}
			}
		}
	}

	/**
	 * check if the cell has a mine
	 * @param rows int number of rows
	 * @param cols int number of columns
	 * @return true or false
	 */
	private boolean isMine(int row, int col) {
		return mineSweepBoard[row][col].isMined();
	}

	/* Saved Game Constructor */
	/**
	 * Load the game we have saved
	 * @param loadedGame the game we saved
	 */
	public MinesweeperModel(MinesweeperBoard loadedGame) {
		mineSweepBoard = loadedGame.getMineSweepBoard();
		bombsArray = loadedGame.getBombsArray();
		rows = loadedGame.getRows();
		cols = loadedGame.getCols();
		mines = loadedGame.getMines();
		cellsHidden = loadedGame.getCellsHidden();
		highScore = loadedGame.getHighScore();
		time = loadedGame.getTime();
	}

	/**
	 * Get the row of game board
	 * @return rows row of game board
	 */
	public int getRow() {
		return rows;
	}

	/**
	 * Get the row of game column
	 * @return cols column of game board 
	 */
	public int getCol() {
		return cols;
	}

	/**
	 * Get the number of mines on the board
	 * @return number of mines
	 */
	public int countOfMines() {
		return mines;
	}

	/**
	 * get how many hidden cell on the board
	 * @return the amount of hidden cells
	 */
	public int getCellsHidden() {
		return cellsHidden;
	}

	/**
	 * reduce the hidden cell
	 */
	public void decCellsHidden() {
		cellsHidden--;
	}
	
	/**
	 * Get the high score list
	 * @return the high score list
	 */
	public ArrayList<Integer> getHighScore() {
		return highScore;
	}
	
	/**
	 * Setting highScore to the indicate array list
	 * @param scores ArrayList<Integer> an array list of integer scores
	 */
	public void setHighScore(ArrayList<Integer> scores) {
		highScore = scores;
	}
	
	/**
	 * calculate for the top 10 high score
	 * @param time int the time we want to compare with
	 */
	public void updateScores(int time) {
	if(highScore.size() == 10) {
		highScore.remove(Collections.max(highScore));
		highScore.add(time);
	} else {
		highScore.add(time);
		}
	}

	/**
	 * get the cell
	 * @param row int the row of the board
	 * @param col int the column of the board
	 * @return the indicated cell
	 */
	public MinesweeperCell getCell(int row, int col) {
		return mineSweepBoard[row][col];
	}
	
	/**
	 * Set the time of the game
	 * @param the time of the game
	 */
	public void setTime(int time) {
		this.time = time;
	}
	
	/**
	 * get the time of the game
	 * @return the time
	 */
	public int getTime() {
		return time;
	}
}