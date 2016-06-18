package rapture;

import robocode.*;
import java.util.*;

/** an inner class which is used to hold a collection of all the recon object for a 
  * certain enemy. It may also contain other information, such as a threat level,
  * movement type, etc, etc */
class ReconCollection extends Object
{
    /** a constant which defines the maximum number of turns that can occur before
      * we assume that we have lost our target. */
    private static final int MAX_UNDETECTED_TARGET_TURNS    = 20;

    /** a constant that defines what robots we should avoid, IE other rapture series
      * robots */
    protected static final String RAPTURE_BOT = "rapture";

    private int MAX_STACK_SIZE     = 50;                          // the maximum size of our stack
    private Recon[] allInformation = new Recon[ MAX_STACK_SIZE ]; // all the enemy information
    private int next               = 0;                           // the next index to insert at
    private int size               = 0;                           // holds the number of recons we have
    private Rapture m_owner        = null;                        // holds the object that created this collection
    private Pattern m_movePattern  = PatternAnalyzer.DEFAULT;     // holds a reference to the pattern object
    private static EnemyFiredListener m_listener     = null;

    /** constructor which takes in the robot that created this collection */
    public ReconCollection( Rapture in_owner ) { m_owner = in_owner; }

    /** used to set the pattern */
    public void setPattern( Pattern in_newPattern )
    {
        if( null != in_newPattern ) { m_movePattern = in_newPattern; }
        else { m_movePattern = PatternAnalyzer.DEFAULT; }
    }

    /** used to get the pattern */
    public Pattern getPattern() { return m_movePattern; }

    /** method used to set the fire listener for this class */
    public static void setEnemyFireListener( EnemyFiredListener in_listener )
    {
        m_listener = in_listener;
    }

    /** method used to fire an event to indicate that one of the enemiues that
      * we are monotoring have fired */
    protected void fireEnemyFiredEvent()
    {
        if( m_listener != null )
        {
            m_listener.enemyFired( get(0) );
        }
    }

    /** add's a recon to our queue of objects */    
    public void addRecon( Recon in_new )
    {
        Recon lastRecon = get(0);
        if( null != lastRecon )
        {
            if( lastRecon.getLife() - in_new.getLife() <= 3 &&
                lastRecon.getLife() - in_new.getLife() >= 0.1 )
            {
                fireEnemyFiredEvent();
            }
        }

        size = (size == MAX_STACK_SIZE) ? size : size + 1;
        allInformation[next] = in_new;
        next = (next + 1) % MAX_STACK_SIZE;

    }

    /** method used to get a recon represented by an index. Where 0
      * is the last element inserted */
    public Recon get( int in_index )
    {
        int index = next - in_index - 1;
        if( index < 0 ) index += MAX_STACK_SIZE;
        return allInformation[ index ];
    }

    /** method used to return the array */
    protected Recon[] toArray() 
    { 
        Recon[] out_array = new Recon[ size ];
        for( int iCounter = 0; iCounter < size; iCounter++ )
        {
            out_array[ iCounter ] = get( iCounter );
        }

        return out_array;
    }

    /** method used to clear all recon information currently stored.*/
    public void clear()
    {
        allInformation = new Recon[ MAX_STACK_SIZE ];
        next           = 0;
        size           = 0;
    }

    /** method that returns the number of recon object that we currently 
      * are storing */
    public int getInformationCount() { return size; }

    /** method used to detect if the how many terms this person has
      * had 0 velocity for the number of terms passed in. */
    public boolean notMovingForTerms( int in_numberOfTerms )
    {
        for( int iCounter = 0; iCounter < in_numberOfTerms; iCounter++ )
        {
            if( null == get( iCounter ) || get( iCounter ).getVelocity() != 0 ) { return false; }
        }

        return true;
    }

    /** method which is used to determine if the current information is current. It does so by 
      * checking the time of the last successful scan against the current time to determine
      * if the difference is greater than our pre-defined value */
    public boolean isCurrent()
    {
        if( null != get( 0 )  ) { return (m_owner.getTime() - get( 0 ).getTime()) <= MAX_UNDETECTED_TARGET_TURNS && m_owner.getRoundNum() == get(0).getRoundNum(); }
        else { return false; }
    }       
    
    /** method which determines if this robot is a rapture robot */
    public boolean isRapture()
    {
        if( null != get( 0 ) )
        {
            if( get( 0 ).getName().startsWith( RAPTURE_BOT ) )
            {
                return true;
            }
        }

        return false;
    }

    /** method which get's the longest/latest strand of information possible, returns
      * null if information is not current */
    public Recon[] getLatestStrand()
    {
        if( isCurrent() )
        {
            List out_list    = new ArrayList( MAX_STACK_SIZE );
            int currentIndex = -1;
            Recon[] out_recon = null;
            
            do
            {
                currentIndex++;
                out_list.add( get( currentIndex ) );
            }
            while(  currentIndex + 1 < MAX_STACK_SIZE &&
                    get( currentIndex + 1 ) != null && 
                    get(currentIndex).getTime() - get(currentIndex + 1).getTime() <= MAX_UNDETECTED_TARGET_TURNS &&
                    m_owner.getRoundNum() == get(currentIndex + 1).getRoundNum() );

            out_recon = new Recon[ out_list.size() ];
            for( int iCounter = 0; iCounter < out_list.size(); iCounter++ ) { out_recon[ iCounter ] = (Recon)out_list.get( iCounter ); }
            return out_recon;

        }

        return null;
    }

};
