package a10;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class Ranger extends Plant {

	/**
	 * The Ranger is an extenstion of the {lant class with the abilty to sheild
	 * bash. This does alot of damage but at the cost of attack speed. However, to
	 * compensate for this the Ranger has more health.
	 * 
	 * @param startingPosition - starting x and y postiton
	 * @param initHitbox       - imported size of hitbox from image
	 * @param img              - imported image from file
	 * @param health           - amount of health
	 * @param coolDown         - time it takes to attack again
	 * @param attackDamage     - how much damage it deals to other actors
	 */
	public Ranger(Point2D.Double startingPosition, Point2D.Double initHitbox, BufferedImage img, int health,
			int coolDown, int attackDamage) {
		super(startingPosition, initHitbox, img, 125, 60, 25);
	}
}
