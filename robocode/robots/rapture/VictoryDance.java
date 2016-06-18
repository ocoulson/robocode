package rapture;

import robocode.*;
import java.util.*;

class VictoryDance extends AbstractMovementClass
{
    public short m_turning = 1;

    /** constructor for this object */
    public VictoryDance( Rapture in_owner ) { super( in_owner ); }

    /** a method that defines what we need to do to move */
    public void move()
    {
        if( m_owner.getTurnRemaining() == 0 )
        {
            m_owner.setTurnLeftRadians( 2 * Math.PI * m_turning );
            m_owner.setTurnGunRightRadians( 4 * Math.PI * m_turning);
            m_owner.setTurnRadarLeftRadians( 6 * Math.PI * m_turning );
            m_turning *= -1;
        }
    }

    public boolean overrideWallCheck() { return true; }

    /** a method that defines if we should use this strategy given
      * the circumstances */
    public boolean shouldUseStrategy() { return (m_owner.getOthers() == 0); }

    /** a method that returns a boolean to see if this has expired */
    public boolean expiredStrategy() { return !shouldUseStrategy(); }  
};