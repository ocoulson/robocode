package rapture;

import robocode.*;
import java.util.*;

/** a class that is used as our fight manager */
class FightingManager extends RaptureStrategyManager
{
    /** constant for the minimum fire time - IE - the time at which we can fire
      * at */
    private static final long MINIMUM_FIRE_TIME = 20;

    /** constructor for this object */
    public FightingManager( Rapture in_owner ) { super( in_owner ); in_owner.debug("Initializing Fight Manager..."); }

    /** method which all implementations must call to operate the strategy, however
      * this is done. in this case fire */
    public void operateStrategy( ConditionalRaptureStrategy in_strat )
    {
        Recon currentTarget = ((FightingStrategy)in_strat).getTarget();

        if( readyToFight() && 
            null != currentTarget && 
            (m_owner.getTime() - currentTarget.getTime()) == 0) 
        {
            ((FightingStrategy)in_strat).fire();
        }
    }

    /** method which checks to see if we're read to fight */
    public final boolean readyToFight()
    {
        return (m_owner.getTime() > MINIMUM_FIRE_TIME);
    }

    /** method used to reset this strategy */
    public void resetStrategy()
    {
        m_owner.setTurnGunRightRadians( 0 );
    }
}