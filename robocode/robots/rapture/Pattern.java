package rapture;

import java.util.*;

/** an interface which prescribes a certain movement pattern */
interface Pattern
{
    /** the method which test's to see if this pattern is valid */
    public boolean test( Recon[] in_strand );

    /** this method moves is used to determine the next coordinate
      * based on the pattern */
    public Coordinate estimatePosition( double in_x, 
                                        double in_y, 
                                        double in_velocity, 
                                        double in_time, 
                                        double in_heading,
                                        ReconCollection in_collection );

}
