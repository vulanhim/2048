package game2048;

import java.io.File;

import javax.swing.JFrame;

public class Main {
	
	public static void main(String[] args) {
		Game game = new Game();
		
		JFrame window = new JFrame("2048");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false); // screen not resizable because width & height not change
		window.add(game);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		
		game.start();
		
		Sound soundd;
		soundd = soundd = new Sound(new File("res/Main.wav"));
		soundd.play();
	}
}

