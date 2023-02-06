package JUnitTest;

import static org.junit.jupiter.api.Assertions.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import controller.MinesweeperController;
import model.MinesweeperBoard;
import model.MinesweeperCell;
import model.MinesweeperModel;

/**
 * Test cases for the minesweeper
 * @author Chris Lin, Sarthak Bawal, Derek Tominaga, Jesse Gomez
 *
 */
class JUnitMinesweeper {

	@Test
	void newModelControllerBoard(){
		MinesweeperModel zeroArg = new MinesweeperModel();
		MinesweeperModel threeArg = new MinesweeperModel(10,10,25);
		MinesweeperController controller = new MinesweeperController(zeroArg);
		MinesweeperBoard serialBoard = zeroArg.getSerialized();
		MinesweeperBoard board = new MinesweeperBoard(10,10,5,5, new MinesweeperCell[10][10], new ArrayList<MinesweeperCell>(),new ArrayList<Integer>(),0);
		controller.playMove(0, 0);
		zeroArg.setTime(0);
		assert(zeroArg.getTime() == 0);
		zeroArg.setHighScore(new ArrayList<Integer>());
		zeroArg.updateScores(11);
		assert(zeroArg.getHighScore().size()== 1);
		for(int i = 0; i < 10; i++) {
			zeroArg.updateScores(i);
		}
		assert(zeroArg.getHighScore().size() == 10);
		assert(zeroArg.getCell(0, 0) == serialBoard.getCell(0, 0));
		System.out.println(controller.getHighScoreString());
		
	}
	
	@Test
	void testSavedBoardModel() throws ClassNotFoundException {
		MinesweeperModel model = new MinesweeperModel();
		MinesweeperController control = new MinesweeperController(model);
		try {
			FileInputStream fis = new FileInputStream("save_game.dat");
			ObjectInputStream ois = new ObjectInputStream(fis);
			MinesweeperBoard load = (MinesweeperBoard) ois.readObject();
			load.getCell(0, 0);
			model = new MinesweeperModel(load);
			control = new MinesweeperController(model);
			ois.close();
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			model = new MinesweeperModel(15, 10, 10);
			control = new MinesweeperController(model);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	void testCell() {
		MinesweeperCell aCell = new MinesweeperCell(0,0);
		aCell.setFlagged();
		assert(aCell.getRow() ==0 && aCell.getCol() == 0);
	}
	
	@Test
	void testController() {
		MinesweeperModel model = new MinesweeperModel(10,10,0);
		MinesweeperController controller = new MinesweeperController(model);
		controller.getCellClue(0, 0);
		controller.flagCell(9, 9);
		controller.flagCell(9, 9);
		controller.showBombs();
		controller.playMove(0, 0);
		controller.isGameOver();	
		controller.gameWon();
	}
	
	@Test
	void testBombClick() {
		MinesweeperModel model = new MinesweeperModel(10,10,1);
		MinesweeperController controller = new MinesweeperController(model);
		for(int i = 0; i < model.getRow(); i++) {
			for(int j = 0; j < model.getCol(); j++) {
				controller.playMove(i, j);
			}
		}
	}

}
