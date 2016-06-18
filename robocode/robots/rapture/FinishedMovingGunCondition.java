package rapture;

import robocode.*;
import java.util.*;

/** method which is used to indicate we have finished moveing our gun */
class FinishedMovingGunCondition extends GunTurnCompleteCondition implements CustomRaptureEvent
{
    /* the owner */
    private Rapture m_owner;

    /** constructor for this object */
    public FinishedMovingGunCondition( Rapture in_owner ) { super( in_owner,5 ); m_owner = in_owner; }

    /** method which handles the condition */
    public void handleEvent() {
        m_owner.m_fightManager.operate();
    }
};