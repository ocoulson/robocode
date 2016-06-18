package rapture;

import robocode.*;
import java.util.*;

/** a class that is used as our move manager */
class MoveManager extends RaptureStrategyManager
{
    /** constructor for this object */
    public MoveManager( Rapture in_owner ) { super( in_owner ); in_owner.debug("Initializing Move Manager..."); }

    /** method which all implementations must call to operate the strategy, however
      * this is done. In this case, move */
    public void operateStrategy( ConditionalRaptureStrategy in_strat )
    {
        ((MovementStrategy)in_strat).move();
    }

    /** method used to reset this strategy */
    public void resetStrategy()
    {
        m_owner.setAhead( 0 );
        m_owner.setTurnRightRadians( 0 );
    }
}