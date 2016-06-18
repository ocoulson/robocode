package rapture;

import robocode.*;
import java.util.*;

/** an inner class that holds the reconi information regarding an
  * enemy at a given point in time. This information is normally
  * gathered from scanned event's and then can be used to make
  * decisions regarding aiming, and movement */
class Recon extends Object
{

    private String name       = null; // holds the enemy name
    private Coordinate coord  = null; // holds the enemy known coordinates
    private double life       = -1;   // holds the enemies life
    private double angle      = -1;   // holds the heading angle the enemy is facing
    private double distance   = -1;   // holds the distance to the enemy, redudant with coord
    private double velocity   = -1;   // holds the last known velocity of the enemy
    private double bearing    = -1;   // holds the enemy bearing to us at the time of the scan
    private long timeCreated  = -1;   // holds the time this recon object was created
    private short direction   = 1;    // holds our direction. Can only be set when added to ReconCollection
    private int roundNumber   = -1;   // holds the round this information was collected in

    /** main constructor for this object */
    public Recon( AdvancedRobot in_owner )
    {
        timeCreated = in_owner.getTime();
    }
    

    /* ************************** */
    /*        GET METHODS         */
    /* ************************** */
    public String getName() { return name; }
    public Coordinate getCoordinate() { return coord; }
    public double getLife() { return life; }
    public double getAngle() { return (direction == Rapture.BACKWARDS) ? RaptureMath.normalAbsoluteAngle(angle + Math.PI) : angle; }
    public double getBearing() { return bearing; }
    public double getDistance() { return distance; }
    public double getVelocity() { return velocity; }
    public double getX() { return null != coord ? coord.getX() : -1; }
    public double getY() { return null != coord ? coord.getY() : -1; }
    public long getTime() { return timeCreated; }
    public short getDirection() { return direction; }
    public int getRoundNum() { return roundNumber; }

    /* ************************** */
    /*        SET METHODS         */
    /* ************************** */
    public void setName( String in_name ) { name = in_name; }
    public void setCoordinate( Coordinate in_coord ) { coord = in_coord; }
    public void setLife( double in_life ) { life = in_life; }
    public void setAngle( double in_angle ) { angle = in_angle; }
    public void setBearing( double in_bearing ) { bearing = in_bearing; }
    public void setDistance( double in_distance ) { distance = in_distance; }
    public void setVelocity( double in_velocity ) { velocity = Math.abs(in_velocity); }
    public void setDirection( short in_direction ) { direction = in_direction; }
    public void setRoundNum( int in_num ) { roundNumber = in_num; }
}