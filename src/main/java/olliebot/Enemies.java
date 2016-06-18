package olliebot;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Oliver Coulson on 17/06/2016.
 */
public class Enemies {
    private Map<String, Enemy> enemies;

    public Enemies() {
        enemies = new HashMap<>();
    }

    //Adds an enemy to the map, returns true if enemy was already in the map, false if not.
    public boolean scanEnemy(Enemy e) {
        boolean out = false;
        if (enemies.containsKey(e.getName())) {
            out = true;
        }
        enemies.put(e.getName(), e);
        return out;
    }

    public void destroyedEnemy(Enemy e) {
        enemies.remove(e.getName());
    }
}
