package rapture;

import robocode.*;
import java.util.*;

/** an inner class used as an interface for all conditions, so that our custom event handler method doesn't get to bogged down
  * with if/else, and switch type of handlers, instead it can simply cast it to a custom rapture event and call the handleEvent method
  * to perform whatever is necessary */
interface CustomRaptureEvent
{
    /** method that is called to perform functionality when the
      * test condition returns true. */
    public void handleEvent();
}