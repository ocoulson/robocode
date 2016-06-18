package rapture;

import robocode.*;
import java.util.*;

/** an abstract class for all of the fighting classes to extend */
abstract class AbstractFightingStrategy implements FightingStrategy
{
    /** constant which holds our default fighting strategy dominence */
    public static final int DEFAULT_DOMINENCE = 1;

    /** a constant which defines the maximum allowable angle that a gun may turn
      * in order to fire */
    private static final double MAX_GUN_TURN_FOR_FIRE       = Math.PI / 2;

    /** member variable which holds the owner of this strategy */
    protected Rapture m_owner = null;

    /** method used to create an instance of this object. Takes in the Rapture robot
      * that created it */
    public AbstractFightingStrategy( Rapture in_owner )
    {
        m_owner = in_owner;
    }

    /** our fire method, which is called to fire if we can, or should, this method
      * is declared in teh abstract class so that it's not needed in sub-classes */
    public final void fire()
    {
        /* check to ensure we arn't still moving our gun */
        if( m_owner.getGunTurnRemaining() == 0 )
        {
            Recon m_currentTarget = getTarget(); // retrieves our current target 

            if( null != m_currentTarget )
            {
                double shotPower    = RaptureMath.factorLife( getPower( m_currentTarget ), m_currentTarget.getLife() );
                double shotVelocity = RaptureMath.determineShotVelocity( shotPower );
                double fireAngle    = RaptureMath.determineCurvedShotAngle( m_owner, m_currentTarget, shotVelocity );

                if( !Double.isNaN(fireAngle) )
                {
                    double gunAngle     = RaptureMath.normalRelativeAngle( fireAngle - m_owner.getGunHeadingRadians() );
    
                    m_owner.setTurnGunRightRadians( gunAngle );
                    if( Math.abs( gunAngle ) < MAX_GUN_TURN_FOR_FIRE && shouldShoot( m_currentTarget ) )
                    {
                        m_owner.addCustomEvent( new ReadyToFireCondition(m_owner, shotPower) );
                    }                        
                }
            }
        }
    }

    /** the method to get the strategy dominence, override if you wish to implement
      * this functionality */
    public int getStrategyDominence() { return DEFAULT_DOMINENCE; }

    /** this method is defined so that we don't need to define it super classes. But it
      * may be overridden if we need to take certain precautions if we get shot */
    public void gotShot( HitByBulletEvent in_event ) { }

    /** method called when we are hit by another robot (ie - rammed). It is defined here
      * as empty but may be overridden if we wish to provide functionality for it */
    public void gotCollision( HitRobotEvent in_event ) { }
}