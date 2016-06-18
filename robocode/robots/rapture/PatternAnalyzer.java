package rapture;

import java.util.*;

/** a class used to manage and test all the movement patterns */
class PatternAnalyzer implements Runnable
{
    public static final Pattern DEFAULT         = new DefaultPattern();
    private static final Pattern[] ALL_PATTERNS = { new StrafingPattern() };
    private static final long SLEEP_INTERVAL    = 500;
    public Rapture m_owner                      = null;
    private Thread m_thread                     = null;
    private boolean shouldRun                   = false;

    /** constructor which takes in the owner that we should be
      * checking for */
    public PatternAnalyzer( Rapture in_owner )
    {
        m_owner  = in_owner; // set's our rapture owner
    }

    /** method for starting this analyzer */
    public void startAnalyzer()
    {
        if( !shouldRun )
        {
            shouldRun = true;
            m_thread = new Thread( this );
            m_thread.start();
        };
    }

    /** method for stopping this analyzer */
    public void stopAnalyzer()
    {
        if( shouldRun )
        {
            try
            {
                shouldRun = false;
                m_thread.interrupt();
            }
            catch( SecurityException error )
            {
                Rapture.debug( "WARNING - Could Not Stop Pattern Analyzer, Security Exception" );
            }
        }
    }

    /** method used to run this thread */
    public void run()
    {
        try
        {
            m_owner.debug( "Init Enemy Pattern Analysis System..." );
            Enumeration allElements   = null;
            ReconCollection current   = null;
            Recon[] strand            = null;
            int numberOfPatterns      = ALL_PATTERNS.length;
            int counter               = 0;
            boolean result            = false;

            while( shouldRun )
            {
                m_thread.sleep( SLEEP_INTERVAL );
                allElements = m_owner.m_scannedInformation.elements();
                while( allElements.hasMoreElements() )
                {
                    current = (ReconCollection)allElements.nextElement();
                    strand  = current.getLatestStrand();

                    if( null != strand )
                    {
                        for( counter = (numberOfPatterns - 1); counter >= 0; counter-- )
                        {
                            result = ALL_PATTERNS[ counter ].test( strand );
                            if(  result && ALL_PATTERNS[ counter ].getClass() != current.getPattern().getClass() )
                            {
                                //m_owner.debug( "Pattern For:" + strand[ 0 ].getName() + " set to " + ALL_PATTERNS[ counter ].getClass() );
                                current.setPattern( ALL_PATTERNS[ counter ] );                        
                            }
                            else if ( !result && 
                                      ALL_PATTERNS[ counter ].getClass() == current.getPattern().getClass() &&
                                      current.getPattern() != DEFAULT )
                            {
                                //m_owner.debug( "Reset Pattern To Default:" + strand[ 0 ].getName() );
                                current.setPattern( DEFAULT );
                            }
                        };
                    };
                };
            };
        }
        catch( InterruptedException error )
        {
            m_owner.debug( "Stopping Pattern Analyzer" );
        }
    }
}
