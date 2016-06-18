package rapture;

import robocode.*;
import java.util.*;

/** a class that describes our wall avoidance strategy */
class WallAvoidStrategy extends AbstractMovementClass
{
    private boolean alreadyReveresed = false;

    /** constructor for this object */
    public WallAvoidStrategy( Rapture in_owner ) 
    { 
        super( in_owner ); 
    }

    /** a method that defines how we should move */
    public void move() 
    {
        m_owner.setMaxVelocity( 8 );
        if( !alreadyReveresed )
        {
            m_owner.reverseDirection();
            m_owner.setTurnRightRadians( RaptureMath.getAngleToXYCoord( Rapture.FIELD_WIDTH / 2, Rapture.FIELD_HEIGHT / 2, m_owner ) );
            m_owner.setAhead( 50 );
            alreadyReveresed = true;
        }
    }  

    /** a method that defines if we should use this strategy given
      * the circumstances */
    public boolean shouldUseStrategy() { return false; }

    /** a method that returns a boolean to see if this has expired */
    public boolean expiredStrategy() { return (m_owner.getDistanceRemaining() == 0); }
}