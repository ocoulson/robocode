package rapture;

import robocode.*;
import java.util.*;

/** an abstract class for all the moving classes to extend */
abstract class AbstractMovementClass implements MovementStrategy
{
    /** constant which holds our default movement strategy dominence */
    public final int DEFAULT_DOMINENCE = 1;

    /** member variable which holds the owner of this strategy */
    protected Rapture m_owner = null;

    /** method used to create an instance of this object. Takes in the Rapture robot
      * that created it */
    public AbstractMovementClass( Rapture in_owner )
    {
        m_owner = in_owner;
    }

    /** a method which returns a boolean as to whether this movement overrides any avoid wall
      * movements that we may encounter. returns true if we wish to ignore the proximity to the
      * wall. Override this method if we wish to avoid the wall, check, otherwise it defaults
      * to using it */
    public boolean overrideWallCheck() { return false; }

    /** this method is defined so that we don't need to define it super classes. But it
      * may be overridden if we need to take certain precautions if we get shot */
    public void gotShot( HitByBulletEvent in_event ) { }

    /** method called when we are hit by another robot (ie - rammed). It is defined here
      * as empty but may be overridden if we wish to provide functionality for it */
    public void gotCollision( HitRobotEvent in_event ) { }

    /** the method to get the strategy dominence, override if you wish to implement
      * this functionality */
    public int getStrategyDominence() { return DEFAULT_DOMINENCE; }
}