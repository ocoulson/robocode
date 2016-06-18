package olliebot;
import robocode.*;
//import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * OllieBot - a robot by (your name here)
 */
public class OllieBot extends Robot
{
	/**
	 * run: OllieBot's default behavior
	 */
	Enemies enemies = new Enemies();
	Enemy currentTarget = null;
	public void run() {
		// Initialization of the robot should be put here

		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		// setColors(Color.red,Color.blue,Color.green); // body,gun,radar

		// Robot main loop - Bloop
		while(true) {
			// Replace the next 4 lines with any behavior you would like
			ahead(100);
			turnGunRight(360);
			back(100);
			turnGunRight(360);
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// Replace the next line with any behavior you would like
		Enemy scanned = new Enemy(e.getName(), e.getBearing(),e.getHeading(),e.getEnergy(),
				e.getDistance(),e.getVelocity());

		enemies.scanEnemy(scanned);
		if ( currentTarget == null) {
			currentTarget = scanned;
		}



	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
        if (e.getBearing() > 0) {
            if(e.getBearing() > 90) {
                turnRight(e.getBearing()-90);
            } else {
                turnLeft(90 - e.getBearing());
            }
        } else {
            if(e.getBearing() < -90) {
                turnLeft(Math.abs(e.getBearing()) - 90);
            } else {
                turnRight(90 - Math.abs(e.getBearing()));
            }
        }
        if(Math.random() > 0.5) {
            ahead(50);
        } else {
            back(50);
        }
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		if (e.getBearing() > 0) {
            turnLeft(90);
            ahead(25);
        } else {
            turnRight(90);
            ahead(25);
        }
	}	
}
