package olliebot;

/**
 * Created by Oliver Coulson on 17/06/2016.
 */
public class Enemy {
    private String name;
    private double bearing;
    private double heading;
    private double energy;
    private double distance;
    private double velocity;

    public Enemy(String name, double bearing, double heading, double energy, double distance, double velocity) {
        this.name = name;
        this.bearing = bearing;
        this.heading = heading;
        this.energy = energy;
        this.distance = distance;
        this.velocity = velocity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBearing() {
        return bearing;
    }

    public void setBearing(double bearing) {
        this.bearing = bearing;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Enemy enemy = (Enemy) o;

        return name.equals(enemy.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return getName();
    }
}
