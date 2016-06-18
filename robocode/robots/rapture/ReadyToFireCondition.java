package rapture;

import robocode.*;
import java.util.*;

/** an inner class which returns true and then removes it self from memory
  * when we are known that we should be fireing. IE - when the gun stops
  * moving */
class ReadyToFireCondition extends GunTurnCompleteCondition implements CustomRaptureEvent
{
    /** holds the shot power we should use */
    private double m_shotPower = -1;   
    
    /** hold the robot which created this condition */
    private Rapture m_owner;

    /** constructor which takes in the Rapture robot that initiated
      * this condition, and the power to shoot at 
      * 
      * @param in_rapture the robot that spawned this condition
      * @param in_power the shot power to use */
    public ReadyToFireCondition( Rapture in_rapture, double in_power )
    {
        super( in_rapture, 0 );
        m_owner = in_rapture;
        m_shotPower = in_power;
    }

    /** when this method is called we know that we should fire and then
      * remove our selves from the event handler que */
    public void handleEvent()
    {
        m_owner.removeCustomEvent( this );
        
        Bullet shotBullet = m_owner.fireBullet( m_shotPower );
        if( null != shotBullet )
            m_owner.m_bulletsShots.add( shotBullet );


    }
};