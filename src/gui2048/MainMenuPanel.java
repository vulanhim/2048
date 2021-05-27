package gui2048;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;

import game2048.DrawUtils;
import game2048.Game;

public class MainMenuPanel extends GuiPanel {

	private Font titleFont = Game.main.deriveFont(100f);
	private Font creatorFont = Game.main.deriveFont(24f);
	private String title = "2048";
	private String creator = "";
	private int buttonWidth = 200;
	private int buttonHeight = 60;
//        private ScoreManager score;
	private int height = 250;
	public static boolean newGame = false;
	public GuiButton resumeButton;
	public GuiButton playButton;
	public GuiButton scoreButton;
	public GuiButton quitButton;

	public MainMenuPanel() {
		super();
		resumeButton = new GuiButton(Game.WIDTH / 8 - buttonWidth / 2, height, buttonWidth, buttonHeight);
		playButton = new GuiButton(3 * Game.WIDTH / 8 - buttonWidth / 2, height, buttonWidth, buttonHeight);
		scoreButton = new GuiButton(5 * Game.WIDTH / 8 - buttonWidth / 2, height, buttonWidth, buttonHeight);
		quitButton = new GuiButton(7 * Game.WIDTH / 8 - buttonWidth / 2, height, buttonWidth, buttonHeight);

		resumeButton.addActionListener((ActionEvent e) -> {
			GuiScreen.getInstance().setCurrentPanel("Resume");
		});
		resumeButton.setText("Resume");
		add(resumeButton);

		playButton.addActionListener((ActionEvent e) -> {
			newGame = true;
			GuiScreen.getInstance().setCurrentPanel("Difficulty");
		});
		playButton.setText("Play");
		add(playButton);

		scoreButton.addActionListener((ActionEvent e) -> {
			GuiScreen.getInstance().setCurrentPanel("Leaderboards");
		});
		scoreButton.setText("Score");
		add(scoreButton);
		quitButton.addActionListener((ActionEvent e) -> {
			System.exit(0);
		});
		quitButton.setText("Quit");
		add(quitButton);
	}

	@Override
	public void update() {
	}

	@Override
	public void render(Graphics2D g) {
		super.render(g);
		g.setFont(titleFont);
		g.setColor(Color.black);
		g.drawString(title, Game.WIDTH / 2 - DrawUtils.getMessageWidth(title, titleFont, g) / 2, 150);
		g.setFont(creatorFont);
		g.drawString(creator, 20, Game.HEIGHT - 10);
	}
}
