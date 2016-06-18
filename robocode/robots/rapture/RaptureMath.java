package rapture;

import robocode.*;
import java.util.*;

/** a class used to perform our math operations */
class RaptureMath
{
    /** our maximum velocity, used when calculating aim */
    public static final double MAX_SPEED = 8;

    /** our minimu velocity, used when cacluation aim */
    public static final double MIN_SPEED = 0;

    /** hidden constructor */
    private RaptureMath() {}

    /** method which takes in a double that represents a shot's power and
      * computes the velocity of this shot 
      *
      * @param in_recon the shot velocity */
    public static final double determineShotVelocity( double in_power )
    {
        return 20 - (3 * in_power);
    }

    /** method used to determine the shot angle for a shot. This method factors in the angle at which the
      * enemy is turning */
    public static final double determineCurvedShotAngle( Rapture in_owner, Recon in_target, double in_velocity )
    {
        ReconCollection collection = (ReconCollection)in_owner.m_scannedInformation.get( in_target.getName() );

        if( collection != null && collection.getInformationCount() >= 2 && 
            collection.get(0).getDirection() == collection.get(1).getDirection() )
        {                
            Pattern movePattern          = collection.getPattern();
            double currentDistance       = getDistanceToXYCoord( in_target.getX(), in_target.getY(), in_owner );
            double currentTime           = currentDistance / in_velocity;
            Coordinate estimatedPosition = movePattern.estimatePosition( in_target.getX(), in_target.getY(), in_target.getVelocity(), currentTime, in_target.getAngle(), collection );
            if( estimatedPosition == null ) { return Double.NaN; }
            double estimatedDistance     = getDistanceToXYCoord( estimatedPosition.getX(), estimatedPosition.getY(), in_owner );
            int loopCounter              = 0;

            while( Math.abs( estimatedDistance - currentDistance ) > 3 && loopCounter < 10)
            {
                currentDistance   = estimatedDistance;
                currentTime       = currentDistance / in_velocity;
                estimatedPosition = movePattern.estimatePosition( in_target.getX(), in_target.getY(), in_target.getVelocity(), currentTime, in_target.getAngle(), collection );
                if( estimatedPosition == null ) { return Double.NaN; }

                estimatedDistance = getDistanceToXYCoord( estimatedPosition.getX(), estimatedPosition.getY(), in_owner );
                loopCounter++;
            }

           double theta = Math.atan( (Math.abs(in_owner.getX() - estimatedPosition.getX())) / Math.abs(in_owner.getY() - estimatedPosition.getY()));
           if( (in_owner.getX() >= estimatedPosition.getX()) && (in_owner.getY() >= estimatedPosition.getY()) )      { theta = Math.PI + theta; }
           else if( (in_owner.getX() >= estimatedPosition.getX()) && (in_owner.getY() < estimatedPosition.getY()) )  { theta = (2 * Math.PI) - theta; }
           else if( (in_owner.getX() < estimatedPosition.getX())  && (in_owner.getY() >= estimatedPosition.getY()) ) { theta = Math.PI - theta; }
           else if( (in_owner.getX() < estimatedPosition.getX())  && (in_owner.getY() < estimatedPosition.getY()) )  { /* do nothing */     }

           return theta;
        }

        return Double.NaN;
    }

    public static double factorLife( double in_shotPower, double in_life )
    {
        /* if our shot is over kill */
        if( (in_shotPower * 4) > in_life ) { return in_life / 4; }
        return in_shotPower;
    }

    /** A method which is used to return the normal abosulte angle for
      * a given angle. For example if -10 is passed into this method
      * it will return 350, if 720 is pased into this method it will
      * return 360. All returned values are between 0 and 360
      *
      * @param in_angle - the angle to convert */
    public static final double normalAbsoluteAngle(double in_angle) 
    {
        if( in_angle < 0 )         return (2 * Math.PI) + (in_angle % (2 * Math.PI));
        else if( in_angle >= (2 * Math.PI) ) return in_angle % (2 * Math.PI);
        else return in_angle;
    }

    /** a method which is used to return the normal absolute value for
      * a radians angle */
    public static final double normalAbsoluteAngleRadians( double in_angle )
    {
        double wholeCircle = Math.PI * 2;

        if( in_angle < 0 )                  return wholeCircle + (in_angle % wholeCircle);
        else if( in_angle >= wholeCircle )  return in_angle % wholeCircle;
        else return in_angle;
    }

    /** This function will return a relative angle given an angle. 
      * This means that given an angle of -180 it will return and angle of 
      * 180. It basically returns the shorter path to the angle, if the 
      * angle is 190, it will return -170 so that you can turn your 
      * bot to a certain position faster. */
    public static final double normalRelativeAngle(double angle) 
    {
        double mod = angle % (2 * Math.PI);

        if( mod <= (-1 * Math.PI) )    return Math.PI + (mod % Math.PI);
        else if( mod > Math.PI ) return (-1 * Math.PI) + (mod % Math.PI);
        else return mod;
    }

    /** Method used to convert a scanned robot event into the recon information that
      * it represents. The only math that is done in this class is used to determine
      * the X,Y coordinates of the enemy. Otherwise it simply just feeds the set methods
      * into the get methods.
      *
      * @param in_event the robot event that we wish to convert to a recon object
      * @param AdvancedRobot the robot that retreived this event 
      * @return a Recon object representing the scanned robot that was passed in */
    public static Recon convertSCEToRecon( ScannedRobotEvent in_event,
                                           Rapture in_robot )
    {
        Recon out_recon       = new Recon( in_robot );
        ReconCollection col   = (ReconCollection)in_robot.m_scannedInformation.get( in_event.getName() );
        double enemyDirection = in_robot.getHeadingRadians() + in_event.getBearingRadians();
        double distance       = in_event.getDistance();
        double distanceX      = Math.sin( enemyDirection ) * distance;
        double distanceY      = Math.cos( enemyDirection ) * distance;
        short direction       = -1;

        if( in_event.getVelocity() < 0 ) { direction = Rapture.BACKWARDS; }
        else if( in_event.getVelocity() > 0 ) { direction = Rapture.FORWARD; }
        else if( in_event.getVelocity() == 0 && col != null && col.isCurrent() ) { direction = col.get( 0 ).getDirection(); }
        else { direction = Rapture.FORWARD; }

        out_recon.setName( in_event.getName() );
        out_recon.setLife( in_event.getEnergy() );
        out_recon.setAngle( in_event.getHeadingRadians() );
        out_recon.setBearing( in_event.getBearingRadians() );
        out_recon.setDistance( in_event.getDistance() );
        out_recon.setVelocity( in_event.getVelocity() );
        out_recon.setDirection( direction );
        out_recon.setCoordinate( new Coordinate( distanceX + in_robot.getX(), 
                                                 distanceY + in_robot.getY() ) );
        out_recon.setRoundNum( in_robot.getRoundNum() );

        return out_recon;
    }

    /** member which returns a distance to a point */
    public static double getDistanceToXYCoord( double in_x, double in_y, Rapture in_owner )
    {
        return Math.sqrt( Math.pow((in_y - in_owner.getY()), 2) + Math.pow((in_x - in_owner.getX()), 2) );
    }

    /** Method used to retreive an angle to an X/Y coordinate, based
      * on our current position. This is calulated assuming you
      * are facing up, or 0 degrees */
    public static double getAngleToXYCoord( double in_x, double in_y, Rapture in_owner )
    {
        double theta         = -1; // holds the heading angle to this x/y coord
        double currentX      = in_owner.getX(); // holds our current x coord
        double currentY      = in_owner.getY(); // holds our current y coord

        /* determines theta, ie - the angle from the heading, and converts this value
         * from degrees to radians */
        theta       = Math.atan( (Math.abs(currentX - in_x)) / Math.abs(currentY - in_y) );

        /* next we adjust for the planar coordinates, this is done by adding or subtracting
         * the appropriate degrees */
        if( (currentX >= in_x) && (currentY >= in_y) )      { theta = Math.PI + theta; }
        else if( (currentX >= in_x) && (currentY < in_y) )  { theta = (2 * Math.PI) - theta; }
        else if( (currentX < in_x)  && (currentY >= in_y) ) { theta = Math.PI - theta; }
        else if( (currentX < in_x)  && (currentY < in_y) )  { /* do nothing */     }
        
        if( in_owner.m_direction == Rapture.BACKWARDS )                
            return normalRelativeAngle( theta - normalAbsoluteAngle(in_owner.getHeadingRadians() + Math.PI) );
        else
            return normalRelativeAngle( theta - in_owner.getHeadingRadians() );
    }

    /** method used to retrreive an angle to an XY coord */
    public static double getAngleToXYCoord( Coordinate in_coord, Rapture in_owner )
    {
        return getAngleToXYCoord( in_coord.getX(), in_coord.getY(), in_owner );
    }

    /** method used to get an angle to a coord, excluding your facing direction */
    public static double getAngleToXYCoordExcludeFacing( double in_x, double in_y, Rapture in_owner )
    {
        double theta         = -1; // holds the heading angle to this x/y coord
        double currentX      = in_owner.getX(); // holds our current x coord
        double currentY      = in_owner.getY(); // holds our current y coord

        /* determines theta, ie - the angle from the heading, and converts this value
         * from degrees to radians */
        theta       = Math.atan( (Math.abs(currentX - in_x)) / Math.abs(currentY - in_y) );

        /* next we adjust for the planar coordinates, this is done by adding or subtracting
         * the appropriate degrees */
        if( (currentX >= in_x) && (currentY >= in_y) )      { theta = Math.PI + theta; }
        else if( (currentX >= in_x) && (currentY < in_y) )  { theta = (2 * Math.PI) - theta; }
        else if( (currentX < in_x)  && (currentY >= in_y) ) { theta = Math.PI - theta; }
        else if( (currentX < in_x)  && (currentY < in_y) )  { /* do nothing */     }

        return theta;
    }
        

    /** method used to turn to an XY coord. Whatever way is quickest */
    public static void turnToXYCoord( Coordinate in_coord, Rapture in_owner )
    {
        double angle = getAngleToXYCoord( in_coord, in_owner );
        if( angle > (Math.PI / 2) ) { in_owner.reverseDirection(); in_owner.setTurnLeftRadians( Math.PI - angle ); }
        else if( angle < ((Math.PI / 2) * -1) ) { in_owner.reverseDirection(); in_owner.setTurnRightRadians( Math.PI - Math.abs( angle ) ); }
        else { in_owner.setTurnRightRadians( angle ); } ;
    }


};