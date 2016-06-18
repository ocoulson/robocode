package rapture;
import java.util.*;

/** a pattern class that test to see when an enemy is strafing */
class StrafingPattern implements Pattern 
{
    /** constant which holds our allowable time difference */
    private static final long ALLOWABLE_TIME_DIFFERENCE = 10;
    private static final float ALLOWABLE_MATCH_PERCENTAGE = 0.7f;
    private Map data = new Hashtable();

   
    /** this method moves is used to determine the next coordinate
      * based on the pattern */
    public Coordinate estimatePosition( double in_x, 
                                        double in_y, 
                                        double in_velocity, 
                                        double in_time, 
                                        double in_heading,
                                        ReconCollection in_collection )
    {
        long time                              = ((StrafingPatternData)data.get( in_collection.get( 0 ).getName() )).strafTurnTime;
        CriticalPoint iLastDirectionChange     = findDirectionChange( in_collection.toArray(), 0 );

        if( null != iLastDirectionChange )
        {
            long lastDirectionChangeTime = iLastDirectionChange.zeroTime;
            long turnCountDown           = time - (in_collection.get(0).getTime() - lastDirectionChangeTime );
            double secondAngle           = in_collection.get(0).getAngle();
            double firstAngle            = in_collection.get(1).getAngle();
            double angleChange           = RaptureMath.normalRelativeAngle(secondAngle - firstAngle) / (in_collection.get(0).getTime() - in_collection.get(1).getTime());

            double acceleration          = in_collection.get(0).getVelocity() - in_collection.get(1).getVelocity();
            acceleration                 = acceleration > 0 ? 1 : (acceleration == 0 ? 0 : -2);

            Coordinate currentPosition = new Coordinate( in_x, in_y );  // our working variable
            int roundTime              = Math.round( (float)in_time );  // get's the number of whole units of time

            if( in_collection.notMovingForTerms( 2 ) ) { return new Coordinate( in_collection.get( 0 ).getX(), in_collection.get( 0 ).getY() ); }

            for( int iCounter = 0; iCounter < roundTime; iCounter++ )
            {
                if( turnCountDown <= 0 && in_velocity <= 0) 
                { 
                    in_heading = RaptureMath.normalAbsoluteAngle( in_heading + Math.PI );
                    acceleration = 1;
                    in_velocity  = 0;
                    turnCountDown = time;
                }
                else
                {
                    if( in_velocity + (-2 * turnCountDown) >= 0 ) { acceleration = -2; }
                    else { acceleration = 1; }
                }

                in_velocity     = in_velocity + acceleration;
                in_velocity     = in_velocity > RaptureMath.MAX_SPEED ? RaptureMath.MAX_SPEED : (in_velocity < RaptureMath.MIN_SPEED ? RaptureMath.MIN_SPEED : in_velocity);

                in_heading      = RaptureMath.normalAbsoluteAngle( in_heading + angleChange );
                currentPosition.setX( currentPosition.getX() + (Math.sin( in_heading ) * in_velocity) ); 
                currentPosition.setY( currentPosition.getY() + (Math.cos( in_heading ) * in_velocity) );

                turnCountDown--;
            }

            /* adjust for the left over time */
            in_heading = RaptureMath.normalAbsoluteAngle( in_heading + (angleChange * (in_time - roundTime)));
            currentPosition.setX( currentPosition.getX() + (Math.sin( in_heading ) * (in_velocity * (in_time - roundTime))) ); 
            currentPosition.setY( currentPosition.getY() + (Math.cos( in_heading ) * (in_velocity * (in_time - roundTime))) );

            return currentPosition;

        }

        return null;
    }


    /** this method determines when a direction has changed starting
      * at the index which is passed in. Return -1 if cannot find
      * anything */
    private CriticalPoint findDirectionChange( Recon[] in_strand, int in_startingWith )
    {
        if( in_strand[ in_startingWith ] != null )
        {
            short currentDirection = in_strand[ in_startingWith ].getDirection();

            for( int iCounter = in_startingWith; iCounter < in_strand.length; iCounter++ )
            {
                if( in_strand[ iCounter ] != null )
                {
                    if( in_strand[ iCounter ].getDirection() != currentDirection )
                    {
                        CriticalPoint out = new CriticalPoint();
                        out.firstIndex    = iCounter;

                        long currentTime   = in_strand[ iCounter ].getTime();
                        long previousTime  = in_strand[ iCounter - 1 ].getTime();

                        double currentVelocity  = in_strand[ iCounter ].getVelocity();
                        double previousVelocity = in_strand[ iCounter - 1 ].getVelocity();

                        if( currentVelocity == 0 ) { out.zeroTime = currentTime; }
                        else if( currentVelocity > previousVelocity ) { out.zeroTime = (long)(previousTime - previousVelocity); }
                        else if( currentVelocity < previousVelocity ) { out.zeroTime = currentTime + (Math.round(currentVelocity / 2)); }
                        else
                        {
                            double firstEstimate  = currentTime + (2 % currentVelocity);
                            double secondEstimate = previousTime - previousVelocity;
                            out.zeroTime = Math.round((firstEstimate + secondEstimate) / 2);
                        }

                        return out;
                    }
                }
            }   
        }

        return null;
    }

    /** the method which test's to see if this pattern is valid */
    public boolean test( Recon[] in_strand ) 
    {
        CriticalPoint indexOfFirstOccurance = findDirectionChange( in_strand, 0 ); 
        
        if( null != indexOfFirstOccurance )
        {
            CriticalPoint nextInstance = findDirectionChange( in_strand, indexOfFirstOccurance.firstIndex );
            if( null != nextInstance )
            {
                float iNumberOfAnomolies           = 0;
                float iNumberOfMatches             = 0;
                CriticalPoint indexOfLastOccurance = nextInstance;                
                long timeTaken                     = indexOfFirstOccurance.zeroTime - nextInstance.zeroTime;
                long currentTime                   = 0;


                while( null != (nextInstance = findDirectionChange( in_strand, indexOfLastOccurance.firstIndex )))
                {
                    currentTime = indexOfLastOccurance.zeroTime - nextInstance.zeroTime;

                    if( timeTaken - ALLOWABLE_TIME_DIFFERENCE <= currentTime &&
                        timeTaken + ALLOWABLE_TIME_DIFFERENCE >= currentTime ) { iNumberOfMatches++;  }
                    else { iNumberOfAnomolies++; }

                    indexOfLastOccurance = nextInstance;
                };

                if( iNumberOfMatches + iNumberOfAnomolies != 0 )
                {
                    if( (iNumberOfMatches / (iNumberOfMatches + iNumberOfAnomolies)) >= ALLOWABLE_MATCH_PERCENTAGE )
                    {
                        data.put( in_strand[ 0 ].getName(), new StrafingPatternData( timeTaken ) );
                        return true;
                    };
                };
            };
        };

        return false;
    }


    /** an inner class which holds the critical point. A critical point is the point that
      * the enemy is at zero velocity, this is an approximated value */
    public class CriticalPoint
    {
        public long zeroTime      = -1; // holds the time the enemy is at 0
        public int firstIndex     = -1; // holds the last index where the direction was not changed.
    }


    /** an inner class used to hold pattern data for a specific
      * robot. */
    private class StrafingPatternData
    {
        public long strafTurnTime = 0;

        public StrafingPatternData( long time ) { strafTurnTime = time; }
    };
}
