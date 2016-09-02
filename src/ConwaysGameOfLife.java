import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * The front end of the game
 */
public class ConwaysGameOfLife extends JFrame {
	private static final long serialVersionUID = -6266491134743609613L;
	private Board board;
	private JTextField input;
	private JTextField tickRate;
	private JPanel inputPanel;
	private JButton start;
	private JButton pause;
	private JButton quit;
	private JLabel genLabel;
	private JLabel tickLabel;
	private int generations = 0;
	private int tick = 1;
	private Thread game;
	
	public static void main(String args[]) {
		ConwaysGameOfLife game = new ConwaysGameOfLife();
		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.setTitle("Conway's Game of Life");
		game.setSize(new Dimension(800, 1000));
		game.setMinimumSize(new Dimension(400, 600));
		game.pack();
		game.setVisible(true);
	}
	
	/**
	 *Default constructor
	 *Initializes all the swing components 
	 */
	public ConwaysGameOfLife() {
		board = new Board();
		board.setLayout(new BoxLayout(board, BoxLayout.Y_AXIS));
		inputPanel = new JPanel();
		tickRate = new JTextField(3);
		input = new JTextField(3);
		start = new JButton("Start");
		start.addActionListener(e -> {
			toggleGame(false);
		});
		pause = new JButton("Pause");
		pause.addActionListener(e -> {
			toggleGame(true);
		});
		quit = new JButton("Exit");
		quit.addActionListener((e) -> {
			System.exit(0);
		});
		tickLabel = new JLabel("Tick rate:");
		genLabel = new JLabel("Number of generations:");
		inputPanel.add(quit);
		inputPanel.add(genLabel);
		inputPanel.add(input);
		inputPanel.add(start);
		inputPanel.add(pause);
		inputPanel.add(tickLabel);
		inputPanel.add(tickRate);
		add(BorderLayout.WEST,inputPanel);
		board.setPreferredSize(new Dimension(400, 100));
		add(BorderLayout.CENTER, board);
	}
	
	/**toggles the game between pause, resume, and restart
	 * @param pause whether or not to pause the game
	 */
	public void toggleGame(boolean pause) {
		if(pause) {
			game.interrupt();
		} else {
			if(!input.getText().isEmpty() && !tickRate.getText().isEmpty()) {
				generations = Integer.parseInt(input.getText());
				tick = Integer.parseInt(tickRate.getText());
				tickRate.setText("");
				input.setText("");
				board.restart();
				board.setGenerations(generations);
				board.setTickRate(tick);
			}
			game = new Thread(board);
			game.start();
		}
	}

}
