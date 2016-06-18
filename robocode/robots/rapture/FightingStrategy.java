package rapture;

import robocode.*;
import java.util.*;

 /** an interface that defines the basic behaviour that a fighting strategy must implement. */
 interface FightingStrategy extends ConditionalRaptureStrategy
 {
     /** method which is used called to perform whatever functions are necessary to determine
       * if we should fire. */
     public void fire();

     /** method which returns a recon object of which is our current best target. if we cannot
       * determine a target, then this method should return null. Depending on the implementation
       * this target may stay locked until dead, or it may simply change as we see fit. */
     public Recon getTarget();

     /** method which determines the shot power for shooting at the enemy represented by the recon
       * information passed in */
     public boolean shouldShoot( Recon in_target );

     /** method which get's the shot power for the target which is passed in as a parameter */
     public double getPower( Recon in_target );
}