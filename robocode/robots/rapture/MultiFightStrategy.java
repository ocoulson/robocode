package rapture;

import robocode.*;
import java.util.*;

/** a class that describes our multi person fighting strategy */
class MultiFightStrategy extends SingleFightStrategy
{

    /** constructor for this object */
    public MultiFightStrategy( Rapture in_owner ) { super( in_owner ); }

    /** method which tells us if we should shoot at the target passed in */
    public boolean shouldShoot( Recon in_target ) 
    { 
        if( in_target.getDistance() <= 400 ||
            (in_target.getDistance() <= 500 && in_target.getLife() <= 25) ||
            (in_target.getLife() <= 20 && m_owner.getOthers() <= 3) ) { return true; }
        return false;
    }

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
                else if( currentInformation.get( 0 ).getDistance() < bestTarget.getDistance() ) { bestTarget = currentInformation.get( 0 ); }
            };

        };          

        m_owner.m_radarManager.setTarget( bestTarget );
        return bestTarget;
    }

    /** a method that defines if we should use this strategy given
      * the circumstances */
    public boolean shouldUseStrategy() { return (m_owner.getOthers() > 2); }

    /** a method that returns a boolean to see if this has expired */
    public boolean expiredStrategy() { return !shouldUseStrategy(); }
};