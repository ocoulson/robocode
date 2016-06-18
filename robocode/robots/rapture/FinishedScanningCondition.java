package rapture;

import robocode.*;
import java.util.*;

/** a method that returns true when we finish scanning. Normally it should just scan the
  * area again */
class FinishedScanningCondition extends Condition implements CustomRaptureEvent
{
    /** member which holds our owner */
    private Rapture m_owner = null;

    /** constructor for this object */
    public FinishedScanningCondition( Rapture in_bot ) { m_owner = in_bot; }

    /** test to see if we're done spinning our radar */
    public boolean test() { return m_owner.getRadarTurnRemaining() == 0.0D; }

    /** method used to handle the condition when we finish scanning the area */
    public void handleEvent() { m_owner.m_radarManager.instructRadar(); }
}