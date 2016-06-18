package rapture;

import robocode.*;
import java.util.*;

/** movement strategy for a single enemy, ushually set manually when we
  * collide */
class EnemyEvasionStrategy extends MultiMovementStrategy
{
    /** constatn which defines the distance which is considered to be escaped */
    private static final double DEFAULT_ESCAPED_DISTANCE = 200;

    /** holds the enemy we are trying to manuver away from */
    private Recon m_enemy = null;

    /** holds the escape distance we should use */
    private double m_escapeDistance = -1;

    /** method which takes in a recon, which represents the enemy that we
      * are trying to avoid */
    public EnemyEvasionStrategy( Rapture in_owner, Recon in_enemy ) 
    { 
        super( in_owner );
        m_enemy = in_enemy; 
        m_escapeDistance =  DEFAULT_ESCAPED_DISTANCE;
    }

    /** method which takes in a recon, which represents the enemy that we
      * are trying to avoid */
    public EnemyEvasionStrategy( Rapture in_owner, Recon in_enemy, double in_escapeDistance ) 
    { 
        this( in_owner, in_enemy );
        m_escapeDistance = in_escapeDistance;
    }



    /** a method that defines if we should use this strategy given
      * the circumstances */
    public boolean shouldUseStrategy() { return !(expiredStrategy()); }

    public boolean overrideWallCheck() { return true; }

    /** a method that returns a boolean to see if this has expired */
    public boolean expiredStrategy() 
    { 
        ReconCollection col = (ReconCollection)m_owner.m_scannedInformation.get( m_enemy.getName() );
        
        if( col.get( 0 ).getDistance() >= m_escapeDistance || !col.isCurrent() ) { return true; }
        else { return false; }
    }  
}