package rapture;

import robocode.*;
import java.util.*;
import java.io.*;
import java.awt.Color;

/**<b>A Rapture Series Robot (Version 2.12)</b><br>
  *Features:<br>
  *&nbsp;&nbsp;&nbsp;- Fixed some issues to accomodate 0.97, shipped in packager.<br>
  *&nbsp;&nbsp;&nbsp;- <b>Source Code</b> now included in package.<br>
  *&nbsp;&nbsp;&nbsp;- Pluggable Fighting, and Movement Strategies.<br>
  *&nbsp;&nbsp;&nbsp;- Pluggable Movement Pattern Analyzer (Data Mining).<br>
  *&nbsp;&nbsp;&nbsp;- Transparent Multidirection Movement.<br>
  *&nbsp;&nbsp;&nbsp;- Intelligable Pluggable Aiming Algorithm.<br>
  *&nbsp;&nbsp;&nbsp;- Intelligable Psuedo - Random Moving Algorithm.<br>
  *&nbsp;&nbsp;&nbsp;- Effective in both single and multi-bot battles.<br>
  *&nbsp;&nbsp;&nbsp;- Cached Enemy Information Queue.<br>
  *&nbsp;&nbsp;&nbsp;- Anti RamBot tacktic evasion algorithm.<br>
  *&nbsp;&nbsp;&nbsp;- Almost Never Hits Walls.<br>
  *&nbsp;&nbsp;&nbsp;- Intelligent Radar Scanning.<br>
  *
  *<br>NOTE: The only difference between 2.11 and 2.12 is that 2.12 is shipped 
  * using the robocode packager and some changes have been made to better accomodate 
  * the 0.97 rules changes.<br>
  *
  *<br>Description:</br>
  *&nbsp;&nbsp;&nbsp;A robot which a sound design that can be easily modified to accomodate
  * changes. Does well against most bots, and is more of a multi-bot fighter than a 
  * single bot fighter. Probably the last version change for a while. To install this
  * robot simply place the jar file in the root of the robots directory.<br><br>
  *
  * @author <a href="mailto:chris@shorrockin.com">Chris Shorrock</a>
  * @version 2.12 */
public class Rapture extends AdvancedRobot
{
    /* ********************************************************************************** */
    /*                                   CONSTANTS                                        */
    /* ********************************************************************************** */

    /** the variable which represents a forward movement */
    public static final short FORWARD   = 1;

    /** the variable which represents a backward movement */
    public static final short BACKWARDS = -1;

    /** the static variable which holds the battle field width */
    public static double FIELD_WIDTH;

    /** the static variabel which holds the battle field height */
    public static double FIELD_HEIGHT;


    /* ********************************************************************************** */
    /*                                MEMBER VARIABLES                                    */
    /* ********************************************************************************** */
        
        /* There are a lot of static variables in this block, which is really poor programming,
         * as most of them probably don't need to be, but since this isn't a coding compitition
         * I'm going to leave it as is, for simplicity sake */

        protected static RadarManager m_radarManager    = null;                // the variable which holds a reference to our radar manager
        protected FightingManager m_fightManager        = null;                // the variable which controlls the guns
        protected MoveManager m_moveManager             = null;                // the variable which conrtolls our movement
        protected static Hashtable m_scannedInformation = null;                // holds all of the enemy information
        protected short m_direction                     = FORWARD;             // holds which direction we are going
        protected static boolean debugMode              = true;                // determines whether we should print to console or not
        protected static Random random                  = new Random();        // random generator
        protected static List m_bulletsShots            = null;                // holds all of the bullets that we have shot
        protected static PatternAnalyzer m_analyzer     = null;                // holds a reference to our ananlyzer
        protected static PrintStream raptureStream      = null;
        protected static long gameTime                  = 0;


    /* ********************************************************************************** */
    /*                                RAPTURE METHODS                                     */
    /* ********************************************************************************** */

    /** method which is used to run this robot */
    public void run()
    {
        try
        {

            raptureStream     = out;
            m_bulletsShots    = new ArrayList();

            /* first thing we need to do is set up all of our
             * variables */
            setAdjustGunForRobotTurn( true );
            setAdjustRadarForGunTurn( false );   

            FIELD_WIDTH   = getBattleFieldWidth();
            FIELD_HEIGHT  = getBattleFieldHeight();
            
            m_fightManager = new FightingManager( Rapture.this );
            m_moveManager  = new MoveManager( Rapture.this );

            populateFightManager();
            populateMoveManager();
            setColors( Color.gray,
                       Color.orange,
                       Color.orange);

            setEventPriority( "ScannedRobotEvent", 90 );
            setEventPriority( "HitByBulletEvent", 85 );

            if( null == m_scannedInformation ) { m_scannedInformation = new Hashtable( getOthers() ); Rapture.debug( "Init Enemy Information Hash..."); }
            if( null == m_analyzer ) { m_analyzer = new PatternAnalyzer( Rapture.this ); }
            if( null == m_radarManager ) { m_radarManager = new RadarManager( Rapture.this ); }
                        
            debug( "Adding Custom Events" );
            addCustomEvent( new FinishedScanningCondition( Rapture.this ) );
            addCustomEvent( new AvoidWallCondition( Rapture.this ) );
            addCustomEvent( new FinishedMovingGunCondition( Rapture.this ) );

            m_analyzer.startAnalyzer();
            m_fightManager.selectBestStrategy();
            m_moveManager.selectBestStrategy();

            /* once all variables have been initialized we start out loop */
            while( true )
            {
                gameTime = getTime();
                m_moveManager.operate(); 
                execute();
            }
        }
        finally
        {
            if( null != m_analyzer) { m_analyzer.stopAnalyzer(); }
            printStats(); 
        }
    }

    /** method which add's all of our moves to the move manager */
    public void populateMoveManager() 
    {
        Rapture.debug( "Populating Movement Strategies" );
        m_moveManager.addStrategy( new SingleMovementStrategy( this ) );
        m_moveManager.addStrategy( new MultiMovementStrategy( this ) );
        m_moveManager.addStrategy( new VictoryDance( this ) );
    }

    /** method which add's all of our fight strategies to the fight manager */
    public void populateFightManager() 
    {
        Rapture.debug( "Populated Fighting Strategies" );
        m_fightManager.addStrategy( new SingleFightStrategy( this ) );
        m_fightManager.addStrategy( new MultiFightStrategy( this ) );
    }

    /** a method which is used to change the direction in which we are heading */
    public void reverseDirection()
    {
        m_direction *= -1;  // changes our direction
        setAhead( Math.abs(getDistanceRemaining()) );
    }

    /** method which prints to the console, used so that we can turn off all console
      * messages when we deploy, without having to delete anything */
    public static void debug( String in_message )
    {
        if( debugMode )
            raptureStream.println( in_message );
    }

    /** method which is called to print the stats */
    public void printStats()
    {
        double bulletsShot        = m_bulletsShots.size();
        double bulletsHit         = 0;
        double bulletsMissed      = 0;
        double lifeLostFromShot   = 0;
        double stillInMotion      = 0;
        double netDamage          = 0;
        Bullet tempBullet         = null;

        for( int iCounter = 0; iCounter < bulletsShot; iCounter++ ) 
        { 
            tempBullet = (Bullet)m_bulletsShots.get( iCounter );
            
            if( tempBullet != null )
            {
                lifeLostFromShot += tempBullet.getPower();
                if( !tempBullet.isActive() )
                {
                    if( tempBullet.getVictim() == null )
                    {
                        bulletsMissed++;
                    }
                    else
                    {
                        netDamage += (4 * tempBullet.getPower() + (tempBullet.getPower() > 1 ? (2 * tempBullet.getPower() - 1) : 0));

                        bulletsHit++;
                    }
                }
                else
                {
                    stillInMotion++;
                }
            }
        }

        debug( "\n----------- Rapture Stats ------------" );
        debug( "Bullets Shot: " + bulletsShot );
        debug( "Bullets Hit: " + bulletsHit + " (" + Math.round( bulletsHit / bulletsShot * 100 ) + "%)" );
        debug( "Bullets Missed: " + bulletsMissed + " (" + Math.round( bulletsMissed / bulletsShot * 100 ) + "%)");
        debug( "Bullets Still In Action: " + stillInMotion + " (" + Math.round( stillInMotion / bulletsShot * 100 ) + "%)");
        debug( "Life Gained From Hitting: " + Math.round( netDamage ) );
        debug( "Life Lost From Shooting: " + Math.round( lifeLostFromShot ) );
        debug( "Net Life Lost/Gained From Shots Fired: " + Math.round(netDamage - lifeLostFromShot) );
        debug( "Net Damage Inflicted: " + Math.round( netDamage ) );
        debug( "--------------------------------------\n" );
    }

    /* ********************************************************************************** */
    /*                                 EVENT LISTENERS                                    */
    /* ********************************************************************************** */

    /** method which listens to custom events. If they are CustomRaptureEvents, then we call
      * handle event. Otherwise we ignore them */
    public void onCustomEvent( CustomEvent in_event )
    {
        if( in_event.getCondition() instanceof CustomRaptureEvent )
            ((CustomRaptureEvent)in_event.getCondition()).handleEvent();
    }

    /** method which listens for scanned robot events */
    public void onScannedRobot( ScannedRobotEvent in_event )
    {
        m_radarManager.onScannedRobot( in_event );  
    }

    /** method which listens for robot collisions */
    public void onHitRobot(HitRobotEvent in_event) 
    {
        m_fightManager.getStrategy().gotCollision( in_event );
        m_moveManager.getStrategy().gotCollision( in_event );
    }

    /** method which listens for when we get struck by a bullet */
    public void onHitByBullet(HitByBulletEvent in_event) 
    {
        m_fightManager.getStrategy().gotShot( in_event );
        m_moveManager.getStrategy().gotShot( in_event );
    }


/* ************************************************************************ */
/*                         OVERRIDDEN METHODS                               */
/* ************************************************************************ */

    /** overriden ahead method to take advantage of our 2 way mobile system that 
      * we use 
      *
      * @param in_distance */
    public void setAhead( double distance )
    {
        distance *= m_direction;
        super.setAhead( distance );
    }

    /** overriden ahead method to take advantage of our 2 way mobile system that 
      * we use 
      *
      * @param in_distance */
    public void setBack( double distance )
    {
        distance *= m_direction;
        super.setBack( distance );
    }


    /** overridden method used to get the name, doesn't do anything yet.
      *
      * @return the name */
    public String getName()
    {
        return " -= Rapture =- ";
    }


}
