package rapture;

import robocode.*;
import java.util.*;

/** an interface that is used for any conditional strategy. A condition strategy is
  * a strategy that may be used, or stop being used, given certain circumstances */
interface ConditionalRaptureStrategy
{
    /** method which determines if we should use this strategy. returns true if we should
      * be using it */
    public boolean shouldUseStrategy();

    /** method which determines if our current strategy is no longer valid. Often, simply the
      * inverse of the shouldUseStrategy method */
    public boolean expiredStrategy();

    /** when we are trying to determine a strategy and > 1 strategy returns true from the shouldUse
      * strategy method, the one with the greater value from this method will get used. In the case that
      * both strategies return the same value, then the first one will be used */
    public int getStrategyDominence();

    /** method which is called when we are hit by a bullet. This may or may not influence our
      * targetting mechansim */
    public void gotShot( HitByBulletEvent in_event );    
    
    /** method called when we are hit by another robot (ie - rammed) */
    public void gotCollision( HitRobotEvent in_event );
}