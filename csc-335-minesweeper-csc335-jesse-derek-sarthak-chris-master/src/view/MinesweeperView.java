package view;

import java.awt.TextField;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;

import controller.MinesweeperController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import model.MinesweeperBoard;
import model.MinesweeperCell;
import model.MinesweeperModel;

/**
 * 
 * @author Chris Lin, Sarthak Bawal, Derek Tominaga, Jesse Gomez
 * 
 * This class is the view of the whole program, which handles everything to be visualize in GUI
 *
 */
@SuppressWarnings("deprecation")
public class MinesweeperView extends Application {
	private static final int DEFAULT_SIZE = 10;
	private Text[][] texts;
	private Circle[][] circles;
	private StackPane[][] panes;
	
	private GridPane board;
	private BorderPane window;
	private Stage stage;

	private MinesweeperModel model;
	private MinesweeperController control;
	
	private GridPane tDisplay;
	private Label timer;
	private Label highScore;
	private Timeline solveTime;
	private Integer seconds = 0;


	/**
	 * set up the stage for the view
	 */
	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		stage.setTitle("Minesweeper");
		loadFile();
		BorderPane window = new BorderPane();
		board = new GridPane();
		window.setCenter(board);
		board.setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));
		board.setPadding(new Insets(8));

		EventHandler<MouseEvent> eventHandlerMouseClick = new EventHandler<MouseEvent>() {
			
			public void handle(MouseEvent arg0) {
				if(!control.isGameOver()) {
					double x = arg0.getX() - 8;
					double y = arg0.getY() - 8;
					if(x < 0) {
						x = 0;
					}
					
					if(y < 0) {
						y = 0;
					}
					int row = (int) (y / 26);
					int col = (int) (x / 26);
					
					if(row >= 0 && col >= 0 &&  row < model.getRow() && col < model.getCol()) {
						if(arg0.getButton().toString().equals("PRIMARY")) {
							control.playMove(row, col);
						}
						else if(arg0.getButton().toString().equals("SECONDARY")) {
							control.flagCell(row, col);
						}
					}
					System.out.print(arg0.getButton());
					
					System.out.println("(" + Integer.toString(row) +"," + Integer.toString(col) + ")");
					addStackPanes(board, model.getRow(), model.getCol());
					
					if(control.isGameOver()) {
						solveTime.stop();
						String scoresString = control.getHighScoreString();
						String message = "You lost!\n Standard Game High Scores:\n";
						message = message + scoresString;
						if(control.gameWon()) {
							if(model.getRow() == DEFAULT_SIZE && model.getCol() == DEFAULT_SIZE && model.getBombs().size() == 10) {
								model.updateScores(seconds);
							}
							scoresString = control.getHighScoreString();
							message = "You won!\nStandard Game High Scores:\n";
							message += scoresString;
						}
						
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setContentText(message);
						alert.showAndWait();
					}
				}
			}
		};
		board.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandlerMouseClick);
		MenuBar menuBar = new MenuBar();
		window.setTop(menuBar);
		createMenuItems(menuBar);
		addStackPanes(board, model.getRow(), model.getCol());

		tDisplay = new GridPane();
		timer = new Label();
		highScore = new Label();
		highScore.setTextFill(Color.BLACK);
		highScore.setFont(Font.font(15));
		startTime(timer);
		window.setBottom(tDisplay);
		
		Scene scene = new Scene(window);
		EventHandler<WindowEvent> eventHandlerWindowClose = new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent arg0) {
				try {
					if(!control.isGameOver()) {
						FileOutputStream fos = new FileOutputStream("save_game.dat");
						ObjectOutputStream oos = new ObjectOutputStream(fos);
						model.setTime(seconds);
						oos.writeObject(model.getSerialized());
						fos.close();
						oos.close();
					} 
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		stage.setOnCloseRequest(eventHandlerWindowClose);
		stage.setScene(scene);
		stage.show();
	}
	
	/**
	 * load the data from the last time
	 * @throws ClassNotFoundException
	 */
	private void loadFile() throws ClassNotFoundException {
		try {
			FileInputStream fis = new FileInputStream("save_game.dat");
			ObjectInputStream ois = new ObjectInputStream(fis);
			MinesweeperBoard load = (MinesweeperBoard) ois.readObject();
			model = new MinesweeperModel(load);
			seconds = load.getTime();
			control = new MinesweeperController(model);
			ois.close();
			fis.close();
		} catch (FileNotFoundException e) {
			model = new MinesweeperModel();
			model.setHighScore(new ArrayList<Integer>());
			control = new MinesweeperController(model);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * generate a menu bar for new game
	 * @param menuBar the menu bar
	 */
	private void createMenuItems(MenuBar menuBar) {
		Menu menu = new Menu("File");
		menuBar.getMenus().add(menu);
		MenuItem menuItem1 = new MenuItem("New Standard Board 10X10");
		MenuItem menuItem2 = new MenuItem("New Board 15X15");
		MenuItem menuItem3 = new MenuItem("New Board 20x20");
		MenuItem menuItem4 = new MenuItem("New Custom Board NXM");
		
		menu.getItems().add(menuItem1);
		menu.getItems().add(menuItem2);
		menu.getItems().add(menuItem3);
		menu.getItems().add(menuItem4);
		EventHandler<ActionEvent> eventHandlerNewGame = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				resetGame(DEFAULT_SIZE, DEFAULT_SIZE, DEFAULT_SIZE);
				
			}	
		};
		EventHandler<ActionEvent> eventHandlerOption2 = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				resetGame(15, 15, 40);
				
			}	
		};
		EventHandler<ActionEvent> eventHandlerOption3 = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				resetGame(20, 20, 80);
				
			}	
		};
		EventHandler<ActionEvent> eventHandlerCustome = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				int cusRow;
				int cusCol;
				int cusBomb;
				TextField dimension = new TextField();
				TextInputDialog custom = new TextInputDialog();
				custom.setHeaderText("Input Number of rows.");
				custom.showAndWait();
				while(true) {
					try{
						Integer.valueOf(custom.getEditor().getText());
						break;
					}catch  (NumberFormatException e) {
						custom.setHeaderText("Entry Must Be Integer");
						custom.showAndWait();
					}
				}
				cusRow = Integer.valueOf(custom.getEditor().getText());
				custom.setHeaderText("Input Number of cols.");
				custom.showAndWait();
				while(true) {
					try {
						Integer.valueOf(custom.getEditor().getText());
						break;
					}catch  (NumberFormatException e) {
						custom.setHeaderText("Entry Must Be Integer");
						custom.showAndWait();
					}
				}
				cusCol = Integer.valueOf(custom.getEditor().getText());
				custom.setHeaderText("Input Number of bombs.");
				custom.showAndWait();
				while(true) {
					try {
						if(Integer.valueOf(custom.getEditor().getText()) > cusCol*cusRow) {
							custom.setHeaderText("Too Many Bombs must be less than NXM");
							custom.showAndWait();
						}
						break;
					}catch  (NumberFormatException e) {
						custom.setHeaderText("Entry Must Be Integer");
						custom.showAndWait();
					}
				}
				cusBomb = Integer.valueOf(custom.getEditor().getText());
				resetGame(cusRow,cusCol,cusBomb);
			}	
		};
		menuItem1.addEventHandler(ActionEvent.ANY, eventHandlerNewGame);
		menuItem2.addEventHandler(ActionEvent.ANY, eventHandlerOption2);
		menuItem3.addEventHandler(ActionEvent.ANY, eventHandlerOption3);
		menuItem4.addEventHandler(ActionEvent.ANY, eventHandlerCustome);
		
	}
	
	/**
	 * Reset the whole game when the game restart
	 * @param rows int row
	 * @param cols int column
	 * @param mines int number of mines
	 */
	private void resetGame(int rows, int cols, int mines) {
		ArrayList<Integer> scores = model.getHighScore();
		model = new MinesweeperModel(rows, cols, mines);
		control = new MinesweeperController(model);
		model.setHighScore(scores);
		addStackPanes(board, rows, cols);
		tDisplay.getChildren().remove(timer);
		seconds = 0;
		timer = new Label();
		startTime(timer);
		deleteSaveData();
		stage.sizeToScene();
	}
	
	/**
	 * delete the saved data
	 */
	private void deleteSaveData() {
		File saveData = new File("save_game.dat");
		if (saveData.exists()) {
			saveData.delete();
		}
	}
	
	/**
	 * Add the cells to the board, setting up the board
	 * @param board GridPane the game board
	 * @param rows int row
	 * @param cols int column
	 */
	private void addStackPanes(GridPane board, int rows, int cols) {
		board.getChildren().clear();
		panes = new StackPane[rows][cols];
		texts = new Text[rows][cols];
		circles = new Circle[rows][cols];
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				StackPane pane = new StackPane();
				panes[j][i] = pane;
				MinesweeperCell cur = control.getCellClue(j, i);
				cur = model.getCell(j, i);
				pane.setPadding(new Insets(2));
				pane.setBorder(
						new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
								CornerRadii.EMPTY, BorderWidths.DEFAULT)));
				Circle circle = new Circle(10);
				Text text = new Text();
				texts[j][i] = text;
				circles[j][i] = circle;
				text.setFont(new Font(15));
				text.setFill(Color.RED);
				if (cur.isHidden()) {
					pane.setBackground(new Background(
							new BackgroundFill(Color.DARKGREY, null, null)));
					if (cur.isFlagged()) {
						circle.setFill(Color.RED);
					}
					else {
						circle.setFill(Color.TRANSPARENT);
					}
				} else {
					pane.setBackground(
							new Background(new BackgroundFill(Color.GRAY, null, null)));
					if (cur.isMined()) {
						circle.setFill(Color.BLACK);
					} else {
						if(cur.getMines() != 0) {
							text.setText(String.valueOf(cur.getMines()));
						}
						circle.setFill(Color.TRANSPARENT);
					}
				}
				pane.getChildren().add(circle);
				pane.getChildren().add(text);
				board.add(pane, i, j);
			}
		}
	}
	
	/**
	 * start the timer
	 * @param timer Label the time passed
	 */
	private void startTime(Label timer) {
		timer.setTextFill(Color.BLUE);
		timer.setFont(Font.font(15));
		tDisplay.add(timer, 0 ,0);
		solveTime = new Timeline();
		start();
		
	}
	
	/**
	 * set up the timer, and show it on the GUI
	 */
	private void start() {
		solveTime.setCycleCount(Timeline.INDEFINITE);
		KeyFrame frame = new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				seconds++;
				timer.setText("Time: " + seconds.toString());
			}
		});
		solveTime.getKeyFrames().add(frame);
		solveTime.playFromStart();
	}
	
}


