package game2048;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import gui2048.GuiScreen;
import gui2048.LeaderBoardPanel;
import gui2048.MainMenuPanel;
import gui2048.PlayPanel;
import gui2048.DifficultyPanel;
import java.io.File;

public class Game extends JPanel implements KeyListener, Runnable, MouseListener, MouseMotionListener{

	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 850; // width of the screen
	public static final int HEIGHT = 430; // height of the screen
	public static final Font main = new Font("Time New Roman", Font.PLAIN, 28); // font.plain just refers to is it italicized playing or bold
	private Thread game; 
	private boolean running; // keep track on thread: starting, stopping 
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private GuiScreen screen;
        private Sound soundd;
	
	public Game() {
		setFocusable(true);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		screen = GuiScreen.getInstance();
		screen.add("Menu", new MainMenuPanel());
		screen.add("Play", new PlayPanel());
		screen.add("Resume", new PlayPanel());
                screen.add("Difficulty",new DifficultyPanel());
		screen.add("Leaderboards", new LeaderBoardPanel());
		screen.setCurrentPanel("Menu");
	}

	private void update() {
		screen.update();
                
		Keyboard.update();
	}

	private void render() {
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		screen.render(g);
		g.dispose();

		Graphics2D g2d = (Graphics2D) getGraphics();
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();
	}

	@Override
	public void run() {
		int fps = 0, updates = 0;
		long fpsTimer = System.currentTimeMillis();
		double nsPerUpdate = 1000000000.0 / 60;

		// last update time in nanoseconds
		double then = System.nanoTime();
		double unprocessed = 0;

		while (running) {

			boolean shouldRender = false;

			double now = System.nanoTime();
			unprocessed += (now - then) / nsPerUpdate;
			then = now;

			// Update queue
			while (unprocessed >= 1) {

				// update
				updates++;
				update();
				unprocessed--;
				shouldRender = true;
			}

			// Render
			if (shouldRender) {
				fps++;
				render();
				shouldRender = false;
			}
			else {
				try {
					Thread.sleep(1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// FPS timer
			if (System.currentTimeMillis() - fpsTimer > 1000) {
				System.out.printf("%d fps %d updates", fps, updates);
				System.out.println("");
				fps = 0;
				updates = 0;
				fpsTimer += 1000;
			}
		}
	}

	public synchronized void start() {
		if (running) return;
		running = true;
		game = new Thread(this, "game");
		game.start();
	}

	public synchronized void stop() {
		if (!running) return;
		running = false;
		System.exit(0);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
                soundd = new Sound(new File("res/Press.wav"));
                soundd.play();
		Keyboard.keyPressed(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		Keyboard.keyReleased(e);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
                soundd = new Sound(new File("res/Click.wav"));
                soundd.play();
		screen.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		screen.mouseReleased(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		screen.mouseDragged(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		screen.mouseMoved(e);
	}
}
