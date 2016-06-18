package rapture;

import robocode.*;
import java.util.*;

/** a class that describes a basic fight strategy, of targeting the easiest guy,
  * and shooting at him until dead, or otherwise */
class SingleFightStrategy extends AbstractFightingStrategy
{
    /** constructor for this object */
    public SingleFightStrategy( Rapture in_owner ) { super( in_owner ); }

    /** method which returns our current target */
    public Recon getTarget()
    {
        Enumeration allEnemys              = m_owner.m_scannedInformation.elements();
        ReconCollection currentInformation = null;
        Recon bestTarget                   = null;

        while( allEnemys.hasMoreElements() )
        {
            currentInformation = (ReconCollection)allEnemys.nextElement();
            if( currentInformation.isCurrent() )
            {
                if( null == bestTarget ) { bestTarget = currentInformation.get( 0 ); }
                else if( currentInformation.get( 0 ).getLife() < bestTarget.getLife() ) { bestTarget = currentInformation.get( 0 ); }
                else if( currentInformation.get( 0 ).getDistance() < bestTarget.getDistance() && currentInformation.get( 0 ).getLife() == bestTarget.getLife() ) { bestTarget = currentInformation.get( 0 ); }
            };

        };          

        m_owner.m_radarManager.setTarget( bestTarget );
        return bestTarget;
    }


    /** method which get's the power for this shot */
    public double getPower( Recon in_target )
    {
         ReconCollection col = (ReconCollection)m_owner.m_scannedInformation.get( in_target.getName() );
         boolean idle        = col != null ? col.notMovingForTerms( 2 ) : false;

         if ( in_target.getDistance() < 200 || idle) { return 3; }
         else if ( in_target.getDistance() < 200 ) { return 2; }
         else if ( in_target.getDistance() > 500) { return 0.1; }
         else { return 1.1; }
    }

    /** method which tells us if we should shoot at the target passed in */
    public boolean shouldShoot( Recon in_target ) { return true; }

    /** a method that defines if we should use this strategy given
      * the circumstances */
    public boolean shouldUseStrategy() { return (m_owner.getOthers() == 1 || m_owner.getOthers() == 2); }

    /** a method that returns a boolean to see if this has expired */
    public boolean expiredStrategy() { return !shouldUseStrategy(); }
}