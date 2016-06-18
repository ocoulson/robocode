package rapture;

import robocode.*;
import java.util.*;

/** an interface that defines the basic behaviour that a movement strategy must implement */
interface MovementStrategy extends ConditionalRaptureStrategy
{
    /** a method which is called to move our current target */
    public void move();

    /** a method which returns a boolean as to whether this movement overrides any avoid wall
      * movements that we may encounter. returns true if we wish to ignore the proximity to the
      * wall. */
    public boolean overrideWallCheck();
}