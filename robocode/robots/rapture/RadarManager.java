package rapture;

import robocode.*;

/** A class used to manager our radar movements
  *
  * @author Chris Shorrock
  * @version JDK 1.3 */
public class RadarManager
{
    /** constant which holds our scan mode */
    private static final short THREE_SIXTY_SCANNING = 0;    

    /** constant which holds our targetted scanning mode */
    private static final short TARGETTED = 1;

    /** constatn which holds the movement of our gun, either left
      * or right, and only used in targetting mode */
    private static final short RADAR_MOVING_LEFT = -1;

    /** constant which holds the movement of our radar, either left
      * or right, only used in taretting mode */
    private static final short RADAR_MOVING_RIGHT = 1;

    /** contant which holds the radias we will sweep across our enemy */
    private static final double SWEEP_RADIUS = Math.PI / 8;

    /** holds the number of times we should sweep before we resample the playing field */
    private static final int RESAMPLE_TIME = 15;
    


    /** holds a reference to our scanning mode */
    private short m_scanMode = THREE_SIXTY_SCANNING;

    /** holds a reference to what direction we are moving in targetting 
      * mdoe */
    private short m_targetMovingDirection = RADAR_MOVING_RIGHT;

    /** holds a reference to our current target */
    private Recon m_currentTarget = null;

    /** holds a reference to our owner */
    private Rapture m_owner = null;

    /** holds a reference to our resample counter */
    private int m_ResampleCounter = 0;

    /** constructor which takes in the rapture object that we are
      * to manage the radar for */
    public RadarManager( Rapture in_owner )
    {
        m_owner = in_owner;
    }

    /** method used to move our radar */
    public void instructRadar()
    {
        switch ( m_scanMode )
        {
            case TARGETTED:
            {
                if( !(m_ResampleCounter >= RESAMPLE_TIME && m_targetMovingDirection == RADAR_MOVING_LEFT && m_owner.getOthers() > 1) )
                {
                    ReconCollection col = (ReconCollection)m_owner.m_scannedInformation.get( m_currentTarget.getName() );
                    if( col.isCurrent() )
                    {
                        m_targetMovingDirection *= -1;
                        double bearing           = RaptureMath.getAngleToXYCoordExcludeFacing(m_currentTarget.getX(), m_currentTarget.getY(), m_owner);
                        double expectedRadarAngle = RaptureMath.normalAbsoluteAngle(bearing + (SWEEP_RADIUS * m_targetMovingDirection));
                        m_owner.setTurnRadarRightRadians( RaptureMath.normalRelativeAngle( expectedRadarAngle - m_owner.getRadarHeadingRadians() ) );
                        if( m_owner.getOthers() > 1 ) { m_ResampleCounter++; }
                        return;
                    }
                    else
                    {
                        m_scanMode = THREE_SIXTY_SCANNING;
                        m_ResampleCounter = 0;
                    }
                }
                else
                {
                    m_owner.setTurnRadarRightRadians( Math.PI * 2 );
                    m_ResampleCounter = 0;
                    return;
                }
            };

            case THREE_SIXTY_SCANNING:
            {
                m_owner.setTurnRadarRightRadians( Math.PI * 2 );
                return;
            }
            
            default:
            {
                throw new InternalError( "Scan Mode Is Set To Some Odd Value" );
            }
        }
    }

    /** method used to set our current target */
    public void setTarget( Recon in_target )
    {
        if( null != in_target )
        {
            m_currentTarget = in_target;
        }
    }


    /* ********************************************************** */
    /*                  DELEGATED EVENTS METHODS                  */
    /* ********************************************************** */

    /** method which listens for scanned robot events */
    public void onScannedRobot( ScannedRobotEvent in_event )
    {
        Recon scannedRobot         = RaptureMath.convertSCEToRecon( in_event, m_owner );
        ReconCollection collection = (ReconCollection)m_owner.m_scannedInformation.get( scannedRobot.getName() );

        if( m_owner.getOthers() > 2 ) { m_scanMode = THREE_SIXTY_SCANNING; }
        else if( null != m_currentTarget) { m_scanMode = TARGETTED; }

        /* if we don't have any information about this robot yet, then create a spot
         * and store it in our hashtable */
        if( null == collection ) 
        { 
            collection = new ReconCollection( m_owner ); 
            m_owner.m_scannedInformation.put( scannedRobot.getName(), collection );
        }

        collection.addRecon( scannedRobot );      
        
        if( m_scanMode == TARGETTED && scannedRobot.getName() == m_currentTarget.getName() )
        {
            m_currentTarget = scannedRobot;
        }
    }
}
