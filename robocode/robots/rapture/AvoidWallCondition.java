package rapture;

import robocode.*;
import java.util.*;

/** an inner class used to handle the situations where we hit the wall.
  * When this happens we turn, taking the shortest root to the angle
  * returned by the getAngleToXY method */
class AvoidWallCondition extends Condition 
{
    /** a constant which defines how close we can get to a wall before we need
      * to turn around */
    private static final int MAXIMUM_WALL_DISTANCE          = 70;

    /** member variable which holds our owner */
    private Rapture m_owner = null;

    /* constructor which takes in the robot we are testing with */
    public AvoidWallCondition( Rapture in_robot ) { m_owner = in_robot; }

    /** test's to see if we are too close to the wall */
    public boolean test() 
    { 
        double XPos = m_owner.getX();
        double YPos = m_owner.getY();

        if( !((MovementStrategy)m_owner.m_moveManager.getStrategy()).overrideWallCheck() && 
            m_owner.m_moveManager.getStrategy().getClass() != WallAvoidStrategy.class )
        {
            if( XPos > (Rapture.FIELD_WIDTH - MAXIMUM_WALL_DISTANCE) ||
                XPos < MAXIMUM_WALL_DISTANCE ||
                YPos > (Rapture.FIELD_HEIGHT - MAXIMUM_WALL_DISTANCE) ||
                YPos < MAXIMUM_WALL_DISTANCE )
            {                  
                m_owner.m_moveManager.setStrategy( new WallAvoidStrategy(m_owner) );
            }
        }

        return false;
    }
}