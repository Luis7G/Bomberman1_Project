package Negocio;

/**
 *
 * @author joelg
 */
public class Bomb {

    private int x, y;
    private boolean exploded;
    private int countToExplode, intervalToExplode = 4;

    public Bomb() {
    }

    public Bomb(int x, int y, boolean exploded, int countToExplode) {
        this.x = x;
        this.y = y;
        this.exploded = exploded;
        this.countToExplode = countToExplode;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean getExploded() {
        return exploded;
    }

    public void setExploded(boolean exploded) {
        this.exploded = exploded;
    }

    public int getCountToExplode() {
        return countToExplode;
    }

    public void setCountToExplode(int countToExplode) {
        this.countToExplode = countToExplode;
    }

    public int getIntervalToExplode() {
        return intervalToExplode;
    }

    public void setIntervalToExplode(int intervalToExplode) {
        this.intervalToExplode = intervalToExplode;
    }
    
}
