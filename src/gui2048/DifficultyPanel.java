package gui2048;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import game2048.DrawUtils;
import game2048.Game;

public class DifficultyPanel extends GuiPanel {
	
	private Font titleFont = Game.main.deriveFont(90f);
	private String title = "Difficulty";
	private int buttonWidth = 220;
	private int height = 250;
	public static boolean difficulty;

	private final GuiButton EasyButton;
	private final GuiButton HardButton;

	public DifficultyPanel() {
		super();
		EasyButton = new GuiButton(Game.WIDTH / 4 - buttonWidth / 2, height, buttonWidth, 60);
		EasyButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				difficulty = false;
				GuiScreen.getInstance().setCurrentPanel("Play");
			}
		});
		EasyButton.setText("Easy");
		add(EasyButton);

		HardButton = new GuiButton(3 * Game.WIDTH / 4 - buttonWidth / 2, height, buttonWidth, 60);
		HardButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				difficulty = true;
				GuiScreen.getInstance().setCurrentPanel("Play");
			}
		});
		HardButton.setText("Hard");
		add(HardButton);
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
	}

}
