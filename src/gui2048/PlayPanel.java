package gui2048;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import game2048.DrawUtils;
import game2048.Game;
import game2048.GameBoard;
import game2048.ScoreManager;
import game2048.StackArr;

public class PlayPanel extends GuiPanel {

	private GameBoard board;
	private BufferedImage info; // image: draw score and time
	private ScoreManager score;
	private Font scoreFont;
	private String timeF;
	private String bestTimeF;
	private GuiButton sound;
	private GuiButton menu;

	// Game Over
	private GuiButton tryAgain;
	private GuiButton mainMenu;
	private GuiButton screenShot;

	// Undo & Redo
	private GuiButton redoButton;
	private GuiButton undoButton;
	private boolean canUndo = false;
	private boolean canRedo = false;
	private StackArr undo;
	private StackArr redo;
	public static final int undoTime = 6;

	private int smallButtonWidth = 160;
	private int spacing = 30;
	private int largeButtonWidth = smallButtonWidth * 2 + spacing;
	private int buttonHeight = 50;
	private boolean added;
	private int alpha = 0; // fade effect
	private Font gameOverFont;
	private boolean screenshot;

	public static boolean newGame = false;

	public PlayPanel() {
		scoreFont = Game.main.deriveFont(24f);
		gameOverFont = Game.main.deriveFont(70f);
		board = new GameBoard(Game.WIDTH - GameBoard.BOARD_WIDTH - 20, Game.HEIGHT / 2 - GameBoard.BOARD_HEIGHT / 2);
		score = board.getScore();
		info = new BufferedImage(Game.WIDTH - GameBoard.BOARD_WIDTH - 20, Game.HEIGHT, BufferedImage.TYPE_INT_RGB);

		mainMenu = new GuiButton(Game.WIDTH / 2 - largeButtonWidth / 2, Game.HEIGHT - spacing - buttonHeight,
				largeButtonWidth, buttonHeight);
		tryAgain = new GuiButton(Game.WIDTH / 3 - smallButtonWidth / 2, mainMenu.getY() - spacing - buttonHeight,
				smallButtonWidth, buttonHeight);
		screenShot = new GuiButton(2 * Game.WIDTH / 3 - smallButtonWidth / 2, tryAgain.getY(), smallButtonWidth,
				buttonHeight);
		

		tryAgain.setText("Try Again");
		screenShot.setText("Screenshot");
		mainMenu.setText("Back to Main Menu");

		tryAgain.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				newGame = true;
				newGame();
			}
		});

		screenShot.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				screenshot = true;
			}
		});

		mainMenu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				GuiScreen.getInstance().setCurrentPanel("Menu");
			}
		});
		
		sound = new GuiButton(30, info.getHeight() - 80, smallButtonWidth, buttonHeight);
		menu = new GuiButton(sound.getX() + smallButtonWidth + 30, sound.getY(), smallButtonWidth,buttonHeight);
		sound.setText("Sound");
		menu.setText("Menu");
		
		sound.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		menu.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiScreen.getInstance().setCurrentPanel("Menu");
			}
		});
		
		add(sound);
		add(menu);

		// Undo && Redo
		undo = new StackArr(undoTime);
		redo = new StackArr(undoTime);
		
		undoButton = new GuiButton(30, menu.getY() - 20 - buttonHeight, smallButtonWidth, buttonHeight);
		redoButton = new GuiButton(undoButton.getX() + smallButtonWidth + 30, undoButton.getY(), smallButtonWidth,buttonHeight);
		undoButton.setText("Undo");
		redoButton.setText("Redo");

		undoButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				canUndo = true;
				undo();
			}
		});

		redoButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				canRedo = true;
				redo();
			}
		});

		add(undoButton);
		add(redoButton);
		
	}

	private void drawGui(Graphics2D g) {
		// Format the times
		timeF = DrawUtils.formatTime(score.getTime());
		bestTimeF = DrawUtils.formatTime(score.getBestTime());

		// Draw it
		Graphics2D g2d = (Graphics2D) info.getGraphics();
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, info.getWidth(), info.getHeight());
		g2d.setColor(Color.darkGray);
		g2d.setFont(scoreFont);
		g2d.drawString("Score:      " + score.getCurrentScore(), 30, 40);

		g2d.drawString("Time:       " + timeF, 30, 80);

		g2d.setColor(Color.red);
		g2d.drawString("Best Score: " + score.getCurrentTopScore(), 30, 120);
		g2d.drawString("Fastest:    " + bestTimeF, 30, 160);

		g2d.dispose();
		g.drawImage(info, 0, 0, null);
	}

	public void drawGameOver(Graphics2D g) {
		g.setColor(new Color(222, 222, 222, alpha));
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		g.setColor(Color.red);
		g.setFont(gameOverFont);
		g.drawString("Game Over!", Game.WIDTH / 2 - DrawUtils.getMessageWidth("Game Over!", gameOverFont, g) / 2,
				Game.HEIGHT / 2 - 40);
	}

	@Override
	public void update() {
		if (canUndo == false) {
//			System.out.println("Push");
			board.update();
			setUndo();
		}
		if (true == MainMenuPanel.newGame) {
			newGame = true;
			MainMenuPanel.newGame = false;
		}
		newGame();
		if (board.isDead()) {
			alpha++;
			if (alpha > 180) {
				alpha = 180;
			}
		}
	}

	@Override
	public void render(Graphics2D g) {
		drawGui(g);
		board.render(g);
		if (screenshot) {
			BufferedImage bi = new BufferedImage(Game.WIDTH, Game.HEIGHT, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = (Graphics2D) bi.getGraphics();
			g2d.setColor(Color.white);
			g2d.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
			drawGui(g2d);
			board.render(g2d);
			try {
				ImageIO.write(bi, "gif", new File(System.getProperty("user.home") + "\\Desktop",
						"screenshot" + System.nanoTime() + ".gif"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			screenshot = false;
		}
		if (board.isDead()) {
			if (!added) {
				added = true;
				add(mainMenu);
				add(screenShot);
				add(tryAgain);

				remove(redoButton);
				remove(undoButton);
			}
			drawGameOver(g);
		}
		super.render(g);
	}

	public void newGame() {
		if (newGame) {
			board.getScore().reset();
			board.reset();
			if (added) {
				alpha = 0;
				added = false;

				remove(tryAgain);
				remove(screenShot);
				remove(mainMenu);

				GuiScreen.getInstance().setCurrentPanel("Difficulty");

				add(undoButton);
				add(redoButton);
			}
			newGame = false;
		}
	}
	
	public void undo() {
		if (canUndo == true) {
			if (undo.isEmpty()) {
				System.out.println("U can not undo anymore!");
			} else {
				redo();
				int[][]arr = undo.pop();
				board.changeToTile(arr);
				System.out.println("Undo");
				returnUndo();
			}
		}
		canUndo = false;
	}
	
	public void setUndo() {
		this.undo = board.getUndo();
	}
	
	public void returnUndo() {
		board.setUndo(undo);
	}
	
	public void redo() {
		if (canRedo == true) {
			if (redo.isEmpty()) {
				System.out.println("U can not redo anymore!");
			} else {
				int[][]arr = redo.pop();
				board.changeToTile(arr);
				System.out.println("redo");
			}
			canRedo = false;
		} else {
			redo.push(board.changeToArr());
		}
		
	}
}