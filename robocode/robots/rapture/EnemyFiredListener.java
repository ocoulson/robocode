package rapture;

/** an interface used to listen to enemies firing */
interface EnemyFiredListener
{
    /** called when we realize an enemy has fired 
      *
      * @param in_enemy - the enemy that fired at us */
    public void enemyFired( Recon in_enemy );
}
