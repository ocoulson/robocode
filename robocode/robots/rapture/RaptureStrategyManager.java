package rapture;

import robocode.*;
import java.util.*;

/** an abstract class that provides the basic functionality for each of the strategy managers. */
abstract class RaptureStrategyManager extends Object
{
    /** member variable which maintains the list of strategies used */
    private List m_availableStrategies;

    /** member which holds our current strategy */
    private ConditionalRaptureStrategy m_currentStrategy = null;

    /** member variable which holds the object which created this manager */
    protected Rapture m_owner;

    /** constructor for the strategy manager */
    public RaptureStrategyManager( Rapture in_owner ) 
    { 
        m_owner                  = in_owner;
        m_availableStrategies    = new Vector(); 
    }

    /** method used to add strategies to the strategy manager */
    public void addStrategy( ConditionalRaptureStrategy in_strat ) { m_availableStrategies.add( in_strat ); }

    /** method used to override the operate function which retreives the best strategy, and instead simply
      * set's it, overriding what we were using before. Useful, for things like wall movement strategies, which
      * need to occur regardless of what else is going on */
    public void setStrategy( ConditionalRaptureStrategy in_strat ) 
    { 
        //m_owner.debug( "(" + this.getClass() + ") Strategy Set To: " + in_strat.getClass() );
        resetStrategy();                        // resets our strategy so everything's at base value
        m_currentStrategy = in_strat;           // set's it
        operateStrategy( m_currentStrategy );   // calls it
    }

    /** method called by rapture to act and do whatever this strategy manager is
      * supposed to do */
    public void operate()
    {
        /* if we don't have a strategy, or it's expired, then we loop */
        if( null == m_currentStrategy || m_currentStrategy.expiredStrategy() )
        {
            selectBestStrategy();
        }

        if( null != m_currentStrategy ) { operateStrategy( m_currentStrategy ); }
    }

    /** method used to select the best strategy in our collection */
    public void selectBestStrategy()
    {
            m_currentStrategy               = null;                         // if expired, then set to null
            int numberOfStrategies          = m_availableStrategies.size(); // get's the number of strategies
            List subList                    = new ArrayList();              // holds a list of all the possible strategies
            int subListSize                 = 0;                            // holds the size of our sublist
            ConditionalRaptureStrategy temp = null;                       // holds the current strategy in the loop

            for( int iCounter = 0; iCounter < numberOfStrategies; iCounter++ )
            {
                temp = (ConditionalRaptureStrategy)m_availableStrategies.get( iCounter );
                if( temp.shouldUseStrategy() ) { subList.add( temp ); }
            };

            subListSize = subList.size();   // get's the sub-list once so we don't need to keep retreiving it

            /* if we didn't find any candidates, use the first one in the list */
            if( m_availableStrategies.size() > 0 && subListSize == 0 ) { m_currentStrategy = (ConditionalRaptureStrategy)m_availableStrategies.get(0);}
            /* if we found more than one candidate */
            else if( subListSize > 0 )
            {
                for( int iCounter = 0; iCounter < subListSize; iCounter++ )
                {
                    temp = (ConditionalRaptureStrategy)subList.get(iCounter);

                    if( null == m_currentStrategy ) { m_currentStrategy = temp; }
                    else if( temp.getStrategyDominence() > m_currentStrategy.getStrategyDominence() ) { m_currentStrategy = temp; }
                };

                resetStrategy();
                //m_owner.debug( "(" + this.getClass() + ") Changed Strategy To: " + m_currentStrategy.getClass() );
            };
    }

    /** method used to retreive our current strategy. May return null if not currently using a strategy */
    public ConditionalRaptureStrategy getStrategy() { return m_currentStrategy; }

    /** method which all implementations must call to operate the strategy, however
      * this is done */
    public abstract void operateStrategy( ConditionalRaptureStrategy in_strat );

    /** method which is used to reset a strategy to it's initial positions. For example, if move, then
      * reset would setAhead and setTurnRight to 0 */
    public abstract void resetStrategy();
}