package a10;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Example extends JPanel implements ActionListener, MouseListener {

	private static final long serialVersionUID = 1L;
	private Timer timer;
	private Random rand;
	private ArrayList<Actor> actors; // Plants and zombies all go in here
	BufferedImage plantImage; // Maybe these images should be in those classes, but easy to change here.
	BufferedImage zombieImage;
	BufferedImage ninjaImage;
	BufferedImage rangerImage;
	private JLabel resource;
	int numRows;
	int numCols;
	int cellSize;
	int gold;
	int wait;
	int rateOfChange;
	int rate;

	private static boolean knightClicked;
	private static boolean rangerClicked;

	/**
	 * Setup the basic info for the example
	 */
	public Example() {
		super();

		// Define some quantities of the scene
		numRows = 5;
		numCols = 7;
		cellSize = 75;
		rand = new Random();
		setPreferredSize(new Dimension(50 + numCols * cellSize, 50 + numRows * cellSize));

		// Resources
		resource = new JLabel();
		int gold = 0;
		resource.setText("GOLD: " + gold);
		this.add(resource);

		// Game Timers
		wait = 0;
		rateOfChange = 0;
		rate = 100;

		// Store all the plants and zombies in here.
		actors = new ArrayList<>();

		// Load images
		try {
			plantImage = ImageIO.read(new File("src/a10/Character - Icons/Knight.png"));
			zombieImage = ImageIO.read(new File("src/a10/Character - Icons/Zombie.png"));
			ninjaImage = ImageIO.read(new File("src/a10/Character - Icons/Ninja.png"));
			rangerImage = ImageIO.read(new File("src/a10/Character - Icons/Ranger.png"));
		} catch (IOException e) {
			System.out.println("A file was not found");
			System.exit(0);
		}

		// The timer updates the game each time it goes.
		// Get the javax.swing Timer, not from util.
		timer = new Timer(30, this);
		timer.start();

	}

	/***
	 * Implement the paint method to draw the plants
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (Actor actor : actors) {
			actor.draw(g, 0);
			actor.drawHealthBar(g);
		}
	}

	/**
	 * 
	 * This is triggered by the timer. It is the game loop of this test.
	 * 
	 * @param e
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		// This method is getting a little long, but it is mostly loop code
		// Increment their cooldowns and reset collision status
		for (Actor actor : actors) {
			actor.update();
		}

		// Check for Game over
		for (Actor actor : actors) {
			if (actor instanceof Zombie) {
				Point2D.Double position = actor.getPosition();
				if (position.x <= 0) {
					timer.stop();
					resource.setText("GAME OVER");
				}
			}
		}

		// Place Zombie
		if (rand.nextInt(rate) > 98) {
			int row = rand.nextInt(numRows + 1);

			int y = row * cellSize;
			// Make new Zombie
			Actor zombie = new Zombie(new Point2D.Double(575, y),
					new Point2D.Double(plantImage.getWidth(), plantImage.getHeight()), zombieImage, 100, 50, -2, 10);
			// Make Ninja
			Ninja ninja = new Ninja(new Point2D.Double(575, y),
					new Point2D.Double(ninjaImage.getWidth(), ninjaImage.getHeight()), ninjaImage, 75, 25, -3, 2);

			// Threshold for which enemy to spawn
			if (rand.nextInt(50) > 22) {
				actors.add(zombie);
			} else {
				actors.add(ninja);
			}
		}

		// Try to attack
		for (Actor actor : actors) {
			for (Actor other : actors) {
				actor.attack(other);
			}
		}

		// Remove plants and zombies with low health
		ArrayList<Actor> nextTurnActors = new ArrayList<>();
		for (Actor actor : actors) {
			if (actor.isAlive())
				nextTurnActors.add(actor);
			else {
				actor.removeAction(actors); // any special effect or whatever on removal
			}
		}
		actors = nextTurnActors;

		// Check for collisions between zombies and plants and set collision status
		for (Actor actor : actors) {
			for (Actor other : actors) {
				actor.setCollisionStatus(other);
			}
		}

		// Move the actors.
		for (Actor actor : actors) {
			actor.move(); // for Zombie, only moves if not colliding.
		}

		// Set Resources
		wait++;
		if (wait >= 30) {
			gold += 6;
			resource.setText("GOLD: " + gold);
		}
		wait %= 30;

		// Change Difficulty
		rateOfChange++;
		if (rateOfChange >= 450) {
			rate += 1;
		}
		rateOfChange %= 450;

		// Redraw the new scene
		repaint();
	}

	/**
	 * Make the game and run it
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame app = new JFrame("Plant and Zombie Test");
				app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				Example panel = new Example();
				panel.addMouseListener(panel);

				// Knight Button
				JButton plant1 = new JButton("Knight");
				plant1.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						knightClicked = true;
					}
				});
				panel.add(plant1);

				// Ranger Button
				JButton plant2 = new JButton("Ranger");
				plant2.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						rangerClicked = true;
					}
				});
				panel.add(plant2);

				// Display
				app.setContentPane(panel);
				app.pack();
				app.setVisible(true);
			}
		});
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		// Knight Button Event
		if (knightClicked) {
			int row = y / cellSize;
			int col = x / cellSize;

			boolean taken = false;
			for (Actor actor : actors) {
				if (col * cellSize == actor.getPosition().x && row * cellSize == actor.getPosition().y) {
					taken = true;
					break;
				}
			}

			if (!taken && gold > 10) {
				Plant plant = new Plant(new Point2D.Double(col * cellSize, row * cellSize),
						new Point2D.Double(plantImage.getWidth(), plantImage.getHeight()), plantImage, 100, 5, 1);
				actors.add(plant);

				knightClicked = false;
				gold -= 10;
			}
		}

		// Ranger Button Event
		if (rangerClicked) {
			int row = y / cellSize;
			int col = x / cellSize;

			boolean taken = false;
			for (Actor actor : actors) {
				if (col * cellSize == actor.getPosition().x && row * cellSize == actor.getPosition().y) {
					taken = true;
					break;
				}
			}

			if (!taken && gold > 25) {
				Ranger ranger = new Ranger(new Point2D.Double(col * cellSize, row * cellSize),
						new Point2D.Double(rangerImage.getWidth(), rangerImage.getHeight()), rangerImage, 125, 60, 25);
				actors.add(ranger);

				rangerClicked = false;
				gold -= 25;
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}