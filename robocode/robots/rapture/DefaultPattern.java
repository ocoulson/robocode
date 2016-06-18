package rapture;

import java.util.*;

class DefaultPattern implements Pattern
{
    /** the method which test's to see if this pattern is valid */
    public boolean test( Recon[] in_strand ) { return true; }
    
    /** this method moves is used to determine the next coordinate
      * based on the pattern */
    public Coordinate estimatePosition( double in_x, 
                                        double in_y, 
                                        double in_velocity, 
                                        double in_time, 
                                        double in_heading,
                                        ReconCollection in_collection )
    {
        double secondAngle           = in_collection.get(0).getAngle();
        double firstAngle            = in_collection.get(1).getAngle();
        double angleChange           = RaptureMath.normalRelativeAngle(secondAngle - firstAngle) / (in_collection.get(0).getTime() - in_collection.get(1).getTime());

        double acceleration          = in_collection.get(0).getVelocity() - in_collection.get(1).getVelocity();
        acceleration                 = acceleration > 0 ? 1 : (acceleration == 0 ? 0 : -2);

        Coordinate currentPosition = new Coordinate( in_x, in_y );  // our working variable
        int roundTime              = Math.round( (float)in_time );  // get's the number of whole units of time

        for( int iCounter = 0; iCounter < roundTime; iCounter++ )
        {
            //in_velocity     = in_velocity + acceleration;
            //in_velocity     = in_velocity > RaptureMath.MAX_SPEED ? RaptureMath.MAX_SPEED : (in_velocity < RaptureMath.MIN_SPEED ? RaptureMath.MIN_SPEED : in_velocity);
            in_heading      = RaptureMath.normalAbsoluteAngle( in_heading + angleChange );
            currentPosition.setX( currentPosition.getX() + (Math.sin( in_heading ) * in_velocity) ); 
            currentPosition.setY( currentPosition.getY() + (Math.cos( in_heading ) * in_velocity) );

            /* if we anticipate the robot going out of bounds */
            if( currentPosition.getX() > Rapture.FIELD_WIDTH || 
                currentPosition.getX() < 0 || 
                currentPosition.getY() > Rapture.FIELD_HEIGHT||
                currentPosition.getY() < 0 )
            {
                currentPosition.setX( currentPosition.getX() - ((Math.sin( in_heading ) * in_velocity) * 2)) ;
                currentPosition.setY( currentPosition.getY() - ((Math.cos( in_heading ) * in_velocity) * 2));
                return currentPosition;
            }


        }

        /* adjust for the left over time */
        in_heading = RaptureMath.normalAbsoluteAngle( in_heading + (angleChange * (in_time - roundTime)));
        currentPosition.setX( currentPosition.getX() + (Math.sin( in_heading ) * (in_velocity * (in_time - roundTime))) ); 
        currentPosition.setY( currentPosition.getY() + (Math.cos( in_heading ) * (in_velocity * (in_time - roundTime))) );

        return currentPosition;
    }
}
