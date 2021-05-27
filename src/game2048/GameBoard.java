package game2048;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import gui2048.DifficultyPanel;
import gui2048.PlayPanel;

public class GameBoard {

	public static final int ROWS = 4;
	public static final int COLS = 4;

	private final int startingTiles = 2;
	private Tile[][] board;
	private boolean dead;
	private boolean won;
	private BufferedImage gameBoard;
	private int x;
	private int y;
	private static int SPACING = 10;
	public static int BOARD_WIDTH = (COLS + 1) * SPACING + COLS * Tile.WIDTH;
	public static int BOARD_HEIGHT = (ROWS + 1) * SPACING + ROWS * Tile.HEIGHT;
	private static long time;

	private long elapsedMS;
	private long startTime;
	private boolean hasStarted;

	private ScoreManager score;
	private LeaderBoards lBoard;

	private int saveCount = 0;

	private StackArr undo;

	public GameBoard(int x, int y) {
		this.x = x;
		this.y = y;
		board = new Tile[ROWS][COLS];
		gameBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
		undo = new StackArr(PlayPanel.undoTime);
		createBoardImage();
		lBoard = LeaderBoards.getInstance();
		lBoard.loadScore();
		score = new ScoreManager(this);
		score.loadGame();
		score.setBestTime(lBoard.getFastestTime());
		score.setCurrentTopScore(lBoard.getHighScore());
		if (score.newGame()) {
			start();
			score.saveGame();
		} else {
			for (int i = 0; i < score.getBoard().length; i++) {
				if (score.getBoard()[i] == 0)
					continue;
				spawn(i / ROWS, i % COLS, score.getBoard()[i]);
			}
			// not calling setDead because we don't want to save anything
			dead = checkDead();
			// not coalling setWon because we don't want to save the time
			won = checkWon();
		}
	}

	public void reset() {
		board = new Tile[ROWS][COLS];
		start();
		score.saveGame();
		dead = false;
		won = false;
		hasStarted = false;
		startTime = System.nanoTime();
		elapsedMS = 0;
		saveCount = 0;
	}

	private void start() {
		for (int i = 0; i < startingTiles; i++) {
			if (DifficultyPanel.difficulty == true) {
				spawnBarrier();
			}
			spawnRandom();
		}
	}

	private void createBoardImage() {
		Graphics2D g = (Graphics2D) gameBoard.getGraphics();
		g.setColor(Color.darkGray);
		g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
		g.setColor(Color.lightGray);

		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				int x = SPACING + SPACING * col + Tile.WIDTH * col;
				int y = SPACING + SPACING * row + Tile.HEIGHT * row;
				g.fillRoundRect(x, y, Tile.WIDTH, Tile.HEIGHT, Tile.ARC_WIDTH, Tile.ARC_HEIGHT);
			}
		}
	}

	public void update() {

		saveCount++;
		if (saveCount >= 120) {
			saveCount = 0;
			score.saveGame();
		}
		if (!dead) {
			if (hasStarted) {
				elapsedMS = (System.nanoTime() - startTime) / 10000000;
				score.setTime(elapsedMS);

			} else {
				startTime = System.nanoTime();
			}
		}

		checkKeys();

		if (score.getCurrentScore() > score.getCurrentTopScore()) {
			score.setCurrentTopScore(score.getCurrentScore());
		}

		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				Tile current = board[row][col];
				if (current == null)
					continue;
				current.update();
				resetPosition(current, row, col);
				if (current.getValue() == 2048) {
					setWon(true);
				}
			}
		}
	}

	public void render(Graphics2D g) {
		BufferedImage finalBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) finalBoard.getGraphics();
		g2d.setColor(new Color(0, 0, 0, 0));
		g2d.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
		g2d.drawImage(gameBoard, 0, 0, null);

		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				Tile current = board[row][col];
				if (current == null)
					continue;
				current.render(g2d);
			}
		}
		g.drawImage(finalBoard, x, y, null);
		g2d.dispose();
	}

	private void resetPosition(Tile tile, int row, int col) {
		if (tile == null)
			return;

		int x = getTileX(col);
		int y = getTileY(row);

		int distX = tile.getX() - x;
		int distY = tile.getY() - y;

		if (Math.abs(distX) < Tile.SLIDE_SPEED) {
			tile.setX(tile.getX() - distX);
		}

		if (Math.abs(distY) < Tile.SLIDE_SPEED) {
			tile.setY(tile.getY() - distY);
		}

		if (distX < 0) {
			tile.setX(tile.getX() + Tile.SLIDE_SPEED);
		}
		if (distY < 0) {
			tile.setY(tile.getY() + Tile.SLIDE_SPEED);
		}
		if (distX > 0) {
			tile.setX(tile.getX() - Tile.SLIDE_SPEED);
		}
		if (distY > 0) {
			tile.setY(tile.getY() - Tile.SLIDE_SPEED);
		}
	}

	public int getTileX(int col) {
		return SPACING + col * Tile.WIDTH + col * SPACING;
	}

	public int getTileY(int row) {
		return SPACING + row * Tile.HEIGHT + row * SPACING;
	}

	private boolean checkOutOfBounds(Direction direction, int row, int col) {
		if (direction == Direction.LEFT) {
			return col < 0;
		} else if (direction == Direction.RIGHT) {
			return col > COLS - 1;
		} else if (direction == Direction.UP) {
			return row < 0;
		} else if (direction == Direction.DOWN) {
			return row > ROWS - 1;
		}
		return false;
	}

	private boolean move(int row, int col, int horizontalDirection, int verticalDirection, Direction direction) {
		boolean canMove = false;
		Tile current = board[row][col];
		if (current == null || current.getValue() == 1) return false;
		boolean move = true;
		int newCol = col;
		int newRow = row;
		while (move) {
			newCol += horizontalDirection;
			newRow += verticalDirection;
			if (checkOutOfBounds(direction, newRow, newCol))
				break;
			if (board[newRow][newCol] == null) {
				board[newRow][newCol] = current;
				canMove = true;
				board[newRow - verticalDirection][newCol - horizontalDirection] = null;
				board[newRow][newCol].setSlideTo(new Point(newRow, newCol));
			} else if (board[newRow][newCol].getValue() == current.getValue() && board[newRow][newCol].canCombine()) {
				board[newRow][newCol].setCanCombine(false);
				board[newRow][newCol].setValue(board[newRow][newCol].getValue() * 2);
				canMove = true;
				board[newRow - verticalDirection][newCol - horizontalDirection] = null;
				board[newRow][newCol].setSlideTo(new Point(newRow, newCol));
				board[newRow][newCol].setCombineAnimation(true);
				score.setCurrentScore(score.getCurrentScore() + board[newRow][newCol].getValue());
			} else {
				move = false;
			}
		}
		return canMove;
	}

	public void moveTiles(Direction direction) {
		boolean canMove = false;
		int horizontalDirection = 0;
		int verticalDirection = 0;
		pushToUndo(); //

		if (direction == Direction.LEFT) {
			horizontalDirection = -1;
			for (int row = 0; row < ROWS; row++) {
				for (int col = 0; col < COLS; col++) {
					if (!canMove)
						canMove = move(row, col, horizontalDirection, verticalDirection, direction);
					else
						move(row, col, horizontalDirection, verticalDirection, direction);
				}
			}
		} else if (direction == Direction.RIGHT) {
			horizontalDirection = 1;
			for (int row = 0; row < ROWS; row++) {
				for (int col = COLS - 1; col >= 0; col--) {
					if (!canMove)
						canMove = move(row, col, horizontalDirection, verticalDirection, direction);
					else
						move(row, col, horizontalDirection, verticalDirection, direction);
				}
			}
		} else if (direction == Direction.UP) {
			verticalDirection = -1;
			for (int row = 0; row < ROWS; row++) {
				for (int col = 0; col < COLS; col++) {
					if (!canMove)
						canMove = move(row, col, horizontalDirection, verticalDirection, direction);
					else
						move(row, col, horizontalDirection, verticalDirection, direction);
				}
			}
		} else if (direction == Direction.DOWN) {
			verticalDirection = 1;
			for (int row = ROWS - 1; row >= 0; row--) {
				for (int col = 0; col < COLS; col++) {
					if (!canMove)
						canMove = move(row, col, horizontalDirection, verticalDirection, direction);
					else
						move(row, col, horizontalDirection, verticalDirection, direction);
				}
			}
		} else {
			System.out.println(direction + " is not a valid direction.");
		}

		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				Tile current = board[row][col];
				if (current == null)
					continue;
				current.setCanCombine(true);
			}
		}

		if (canMove) {
			spawnRandom();
			setDead(checkDead());
		}
	}

	// MODIFIED
	private boolean checkDead() {
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				if (board[row][col] == null)
					return false;
				boolean canCombine = checkSurroundingTiles(row, col, board[row][col]);
				if (canCombine) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean checkWon() {
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				if (board[row][col] == null)
					continue;
				if (board[row][col].getValue() >= 2048)
					return true;
			}
		}
		return false;
	}

	private boolean checkSurroundingTiles(int row, int col, Tile tile) {
		if (row > 0) {
			Tile check = board[row - 1][col];
			if (check == null)
				return true;
			if (tile.getValue() == check.getValue())
				return true;
		}
		if (row < ROWS - 1) {
			Tile check = board[row + 1][col];
			if (check == null)
				return true;
			if (tile.getValue() == check.getValue())
				return true;
		}
		if (col > 0) {
			Tile check = board[row][col - 1];
			if (check == null)
				return true;
			if (tile.getValue() == check.getValue())
				return true;
		}
		if (col < COLS - 1) {
			Tile check = board[row][col + 1];
			if (check == null)
				return true;
			if (tile.getValue() == check.getValue())
				return true;
		}
		return false;
	}

	public void pushToUndo() {
		undo.push(changeToArr());
		System.out.println("Push");
	}

	public StackArr getUndo() {
		return undo;
	}

	public void setUndo(StackArr undo) {
		this.undo = undo;
	}

	/** Debug method */
	public void spawn(int row, int col, int value) {
		board[row][col] = new Tile(value, getTileX(col), getTileY(row));
	}

	public int[][] changeToArr() {
		int[][] arr = new int[ROWS][COLS];
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				if (board[row][col] == null) {
					arr[row][col] = 0;
				} else {
					arr[row][col] = board[row][col].getValue();
				}
			}
		}
		return arr;
	}

	public void changeToTile(int[][] arr) {
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				if (arr[row][col] != 0) {
					spawn(row, col, arr[row][col]);
				} else {
					board[row][col] = null;
				}
			}
		}
	}

	private void spawnBarrier() {
		Random random = new Random();
		boolean notValid = true;

		while (notValid) {
			int location = random.nextInt(GameBoard.ROWS * GameBoard.COLS);
			int row = location / ROWS;
			int col = location % COLS;
			Tile current = board[row][col];
			if (current == null) {
				int value = 1;
				Tile tile = new Tile(value, getTileX(col), getTileY(row));
				board[row][col] = tile;
				notValid = false;
			}
			DifficultyPanel.difficulty = false;
		}
	}

	private void spawnRandom() {
		Random random = new Random();
		boolean notValid = true;

		while (notValid) {
			int location = random.nextInt(16);
			int row = location / ROWS;
			int col = location % COLS;
			Tile current = board[row][col];
			if (current == null) {
				int value = random.nextInt(10) < 9 ? 2 : 4;
				Tile tile = new Tile(value, getTileX(col), getTileY(row));
				board[row][col] = tile;
				notValid = false;
			}
		}
	}

	private void checkKeys() {
		if (!Keyboard.pressed[KeyEvent.VK_LEFT] && Keyboard.prev[KeyEvent.VK_LEFT]) {
			moveTiles(Direction.LEFT);
			if (!hasStarted)
				hasStarted = !dead;
		}
		if (!Keyboard.pressed[KeyEvent.VK_RIGHT] && Keyboard.prev[KeyEvent.VK_RIGHT]) {
			moveTiles(Direction.RIGHT);
			if (!hasStarted)
				hasStarted = !dead;
		}
		if (!Keyboard.pressed[KeyEvent.VK_UP] && Keyboard.prev[KeyEvent.VK_UP]) {
			moveTiles(Direction.UP);
			if (!hasStarted)
				hasStarted = !dead;
		}
		if (!Keyboard.pressed[KeyEvent.VK_DOWN] && Keyboard.prev[KeyEvent.VK_DOWN]) {
			moveTiles(Direction.DOWN);
			if (!hasStarted)
				hasStarted = !dead;
		}
	}

	public int getHighestTileValue() {
		int value = 2;
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				if (board[row][col] == null)
					continue;
				if (board[row][col].getValue() > value)
					value = board[row][col].getValue();
			}
		}
		return value;
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		if (!this.dead && dead) {
			lBoard.addTile(getHighestTileValue());
			lBoard.addScore(score.getCurrentScore());
			lBoard.saveScore();
		}
		this.dead = dead;
	}

	public Tile[][] getBoard() {
		return board;
	}

	public void setBoard(Tile[][] board) {
		this.board = board;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public static long getTime() {
		return time;
	}

	public static void setTime(long time) {
		GameBoard.time = time;
	}

	public boolean isWon() {
		return won;
	}

	public void setWon(boolean won) {
		if (!this.won && won && !dead) {
			lBoard.addTime(score.getTime());
			lBoard.saveScore();
		}
		this.won = won;
	}

	public ScoreManager getScore() {
		return score;
	}
}
