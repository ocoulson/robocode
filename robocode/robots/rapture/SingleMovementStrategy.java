package rapture;

import robocode.*;
import java.util.*;

/** a class that describes simple movement strategy */
class SingleMovementStrategy extends AbstractMovementClass implements EnemyFiredListener
{   
    /** holds the distance we should start to run from our current
      * target at */
    private static final long RUNNING_DISTANCE = 100;

    /** holds a reference to our current target */
    private Recon m_currentTarget = null;
    
    /** constructor for this object */
    public SingleMovementStrategy( Rapture in_owner ) { super( in_owner ); ReconCollection.setEnemyFireListener( this ); }

    /** method that holds a simple move alternator */
    double moveAlternator = 1;
    
    /** a method that defines how we should move */
    public void move()
    {
        m_currentTarget     = ((FightingStrategy)m_owner.m_fightManager.getStrategy()).getTarget();
        ReconCollection col = m_currentTarget != null ? (ReconCollection)m_owner.m_scannedInformation.get( m_currentTarget.getName() ) : null;



        if( m_currentTarget != null )
        {
            if( m_owner.m_direction == Rapture.BACKWARDS )
            {
                m_owner.setTurnRightRadians( RaptureMath.normalRelativeAngle(RaptureMath.getAngleToXYCoord(m_currentTarget.getX(), m_currentTarget.getY(), m_owner) - (Math.PI / 2) ) );
            }
            else
            {
                m_owner.setTurnRightRadians( RaptureMath.normalRelativeAngle(RaptureMath.getAngleToXYCoord(m_currentTarget.getX(), m_currentTarget.getY(), m_owner) + (Math.PI / 2) )  );
            }
        }
        else if( m_owner.getTurnRemaining() == 0 )
        {
            if( m_owner.random.nextBoolean() ) { m_owner.setTurnRightRadians( Math.PI * 6 ); m_owner.setAhead( 300 );}
            else { m_owner.setTurnLeftRadians( Math.PI * 6 ); m_owner.setAhead( 300 ); }
        }

        if( m_owner.getDistanceRemaining() == 0 &&
            m_owner.getVelocity() == 0 )
        {
            m_owner.setAhead( 300 );
        }
    }

    /** called when we realize an enemy has fired 
      *
      * @param in_enemy - the enemy that fired at us */
    public void enemyFired( Recon in_enemy )
    {
        if( null != m_currentTarget)
        {
            if( m_currentTarget.getName().equals( in_enemy.getName() ) )
            {
                if( Math.abs(m_owner.getVelocity()) == 8 )
                {
                    if( Rapture.random.nextBoolean() ) { m_owner.reverseDirection(); }
                    else { m_owner.setAhead( 0 ); }
                }
            }
        }
    }



    /** method that is called when we collide with someone */
    public void gotCollision( HitRobotEvent event ) 
    { 
        ReconCollection col = (ReconCollection)m_owner.m_scannedInformation.get( event.getName() );
        if( col != null && col.isCurrent() )
        {
            m_owner.m_moveManager.setStrategy( new EnemyEvasionStrategy( m_owner, col.get( 0 ) ) );
        }
        else
        {
            m_owner.reverseDirection(); 
        }
    }

    /** a method that defines if we should use this strategy given
      * the circumstances */
    public boolean shouldUseStrategy() { return (m_owner.getOthers() == 1); }

    /** a method that returns a boolean to see if this has expired */
    public boolean expiredStrategy() { return !shouldUseStrategy(); }       
}