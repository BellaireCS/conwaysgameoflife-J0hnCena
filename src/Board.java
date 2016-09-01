import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JPanel;

/**
 * the board for conway's game of life
 */
public class Board extends JPanel implements Runnable{
	
	private static final long serialVersionUID = 8843958625769295509L;
	private volatile boolean[][] evenArray;
	private volatile boolean[][] oddArray;
	private volatile AtomicInteger finished = new AtomicInteger(0);
	private int iter = 0;
	private ExecutorService service;
	public static final int MAX_LENGTH = 100;
	private boolean isEven;
	private int generations;
	public static final int TILE_SIZE = 10;
	
	/**
	 *Sets up the arrays and thread pools 
	 */
	public Board() {
		evenArray = new boolean[MAX_LENGTH][MAX_LENGTH];
		oddArray = new boolean[MAX_LENGTH][MAX_LENGTH];
		randomize();		
		service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
	}
	
	/**
	 * restarts the game
	 */
	public void restart() {
		randomize();
		iter = 0;
	}
	
	/**
	 * seeds the first generation
	 */
	public void randomize() {
		Random random = new Random();
		for(int r = 0; r<MAX_LENGTH; r++) {
			for(int c = 0; c<MAX_LENGTH; c++) {
				evenArray[r][c] = random.nextBoolean();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while(iter < generations) {
			isEven = iter % 2 == 0;
			for(int r = 0; r < MAX_LENGTH; r++) {
				service.execute(new ConwayTask(r, isEven));
			}
			
			while(finished.get() != MAX_LENGTH);

			this.repaint();
			finished.set(0);
			iter++;	
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				return;
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.setBackground(Color.WHITE);
		boolean ref[][];
		ref = isEven ? oddArray : evenArray;
		for(int r = 0; r<MAX_LENGTH; r++) {
			for(int c = 0; c<MAX_LENGTH; c++) {
				g.setColor(ref[r][c] ? Color.BLACK : Color.WHITE);
				g.fillRect(r * TILE_SIZE, c * TILE_SIZE, TILE_SIZE, TILE_SIZE);
			}
		}
	}

	/**
	 * runnable for the thread pools to execute
	 * operates on one row
	 */
	private class ConwayTask implements Runnable {
		private int row;
		private boolean even;
		
		/**
		 * @param row the row to operate on
		 * @param even whether or not it is on the even iteration
		 */
		public ConwayTask(int row, boolean even) {
			this.row = row;
			this.even = even;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			for(int i = 0; i<MAX_LENGTH; i++) {
				int liveNeighbors = getLiveNeighbors(i);

				if(liveNeighbors == 3) {
					setCellState(true, row, i);
				} else if(getCellState(row, i) && liveNeighbors == 2) {
					setCellState(true, row, i);
				} else {
					setCellState(false, row, i);
				}
			}
			finished.incrementAndGet();
		}

		/**
		 * @param row the row of the cell
		 * @param col the col of the cell
		 * @return whether the cell is alive or dead
		 */
		public boolean getCellState(int row, int col) {
			if(even) {
				return evenArray[calculateModulus(row, MAX_LENGTH)][calculateModulus(col, MAX_LENGTH)];
			} else {
				return oddArray[calculateModulus(row, MAX_LENGTH)][calculateModulus(col, MAX_LENGTH)];
			}
		}

		/**
		 * @param state whether the cell is alive or dead
		 * @param row the row of the cell to set
		 * @param col the col of the cell to set
		 */
		public void setCellState(boolean state, int row, int col) {
			if(!even) {
				evenArray[row][col] = state;
			} else {
				oddArray[row][col] = state;
			}
		}

		/**
		 * @param dividend what you are dividing
		 * @param divisor what you are dividing by
		 * @return the modulus(not remainder)
		 */
		public int calculateModulus(int dividend, int divisor) {
			return (dividend % divisor + divisor) % divisor;
		}

		/**
		 * @param col the column number
		 * @return how many alive neighbors are nearby the cell
		 */
		public int getLiveNeighbors(int col) {
			int aliveNeighbors = 0;
			for(int r = -1; r<2; r++) {
				for(int c = -1; c<2; c++) {
					if(r == 0 && c == 0)
						continue;
					if(getCellState(row + r, col + c)) {
						aliveNeighbors++;
					}
				}
			}
			return aliveNeighbors;
		}

	}
	
	/**
	 * @return the generations
	 */
	public int getGenerations() {
		return generations;
	}

	/**
	 * @param generations the generations to set
	 */
	public void setGenerations(int generations) {
		this.generations = generations;
	}

}
