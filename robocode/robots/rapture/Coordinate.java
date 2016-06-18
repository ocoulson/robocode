package rapture;

import robocode.*;
import java.util.*;

/** an inner class that is used to maintain an x, y set
  * of coordinates. This is used in a variety of places
  * and as well as in other objects. Made for convienence
  * more than anything else */
class Coordinate extends Object
{
    private double XPos = 0;
    private double YPos = 0;

    /** Empty constructor */
    public Coordinate() {}

    /** constructor that takes in the X and Y coordinates */
    public Coordinate( double in_x, double in_y )
    {
        XPos = in_x;
        YPos = in_y;
    }

    /* ************************** */
    /*        GET METHODS         */
    /* ************************** */
    public double getX() { return XPos; }
    public double getY() { return YPos; }

    /* ************************** */
    /*        SET METHODS         */
    /* ************************** */
    public void setY( double in_y ) { YPos = in_y; }
    public void setX( double in_x ) { XPos = in_x; }
};