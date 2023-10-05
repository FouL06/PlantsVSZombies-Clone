package a10;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;

public class Ninja extends Zombie {
	/**
	 * The Ninja is an extestion of the Zombie class with the abilitys of moving
	 * faster and having faster attack speed, at the cost of health and attack
	 * damage.
	 * 
	 * @param startingPosition - starting x and y postiton
	 * @param initHitbox       - imported size of hitbox from image
	 * @param img              - imported image from file
	 * @param health           - amount of health
	 * @param coolDown         - time it takes to attack again
	 * @param speed            - how fast the actor can move on the screen
	 * @param attackDamage     - how much damage it deals to other actors
	 */
	public Ninja(Double startingPosition, Double initHitbox, BufferedImage img, int health, int coolDown, double speed,
			int attackDamage) {
		super(startingPosition, initHitbox, img, 75, 25, -3, 2);
	}
}
