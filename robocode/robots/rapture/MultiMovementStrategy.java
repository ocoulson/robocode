package rapture;

import robocode.*;
import java.util.*;

/** movement strategy for multiple enemies. Very evasive */
class MultiMovementStrategy extends AbstractMovementClass
{

    /** constructor for this strategy */
    public MultiMovementStrategy( Rapture in_owner ) { super( in_owner ); }

    /** a method that defines how we should move */
    public void move()
    {
        List closestEnemies = determineClosestEnemies();
        m_owner.setMaxVelocity( 8 );


        if( closestEnemies.size() == 0 )
        {
            if( m_owner.getDistanceRemaining() == 0 )
            {
                m_owner.reverseDirection();
                if( m_owner.random.nextBoolean() ) { m_owner.setTurnRightRadians( Math.PI * 6 ); m_owner.setAhead( 300 );}
                else { m_owner.setTurnLeftRadians( Math.PI * 6 ); m_owner.setAhead( 300 ); }
            }
        }
        else
        {
                headToCoordinate( determineBestCoord( closestEnemies ) );
        }
    }

    /** method used to head us to a coordinate */
    private void headToCoordinate( Coordinate in_coordinate )
    {
        in_coordinate.setX( in_coordinate.getX() + m_owner.getX() );
        in_coordinate.setY( in_coordinate.getY() + m_owner.getY() );
        RaptureMath.turnToXYCoord( in_coordinate, m_owner );
        m_owner.setAhead( 100 );
    }

    /** method used to determine a list of all the closes enemies */
    private List determineClosestEnemies()
    {
        Enumeration allEnemys = m_owner.m_scannedInformation.elements();
        List out_allEnemies   = new ArrayList();
        ReconCollection currentInformation = null;

        while( allEnemys.hasMoreElements() )
        {
            currentInformation = (ReconCollection)allEnemys.nextElement();
            if( currentInformation.isCurrent() )
            {
                out_allEnemies.add( currentInformation.get( 0 ) );
            }
        }

        return out_allEnemies;
    }

    /** method used to determine the best corner to go to */
    private Coordinate determineBestCoord( List in_allEnemies )
    {
        Coordinate out_coordinate = new Coordinate();
        int numberOfEnemies       = in_allEnemies.size();
        Recon currentEnemy        = null;

        /* accomodate for all of the walls */
        out_coordinate.setX( (250  / m_owner.getX()) + out_coordinate.getX() );
        out_coordinate.setX( (-250 / (Rapture.FIELD_WIDTH - m_owner.getX())) + out_coordinate.getX() );
        out_coordinate.setY( (250  / m_owner.getY()) + out_coordinate.getY() );
        out_coordinate.setY( (-250 / (Rapture.FIELD_HEIGHT - m_owner.getY())) + out_coordinate.getY() );

        for( int iCounter = 0; iCounter < numberOfEnemies; iCounter++ )
        {
            currentEnemy = (Recon)in_allEnemies.get( iCounter );
            out_coordinate.setX( ((250 / currentEnemy.getDistance()) * Math.sin( currentEnemy.getBearing() + m_owner.getHeadingRadians() + Math.PI )) + out_coordinate.getX() );
            out_coordinate.setY( ((250 / currentEnemy.getDistance()) * Math.cos( currentEnemy.getBearing() + m_owner.getHeadingRadians() + Math.PI )) + out_coordinate.getY() );                
        }           
        return out_coordinate;
    }

    public void gotCollision( HitRobotEvent event ) { }

    /** a method that defines if we should use this strategy given
      * the circumstances */
    public boolean shouldUseStrategy() { return (m_owner.getOthers() > 1); }

    public boolean overrideWallCheck() { return true; }

    /** a method that returns a boolean to see if this has expired */
    public boolean expiredStrategy() { return !shouldUseStrategy(); }   
}