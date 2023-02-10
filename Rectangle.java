public class Rectangle {
    private int xMin;
    private int xMax;
    private int yMin;
    private int yMax;

    public int getxMax() {
        return xMax;
    }
    public int getxMin() {
        return xMin;
    }
    public int getyMax() {
        return yMax;
    }
    public int getyMin() {
        return yMin;
    }

    public Rectangle(int xMin, int xMax, int yMin, int yMax){
        if (xMin < 0 || xMin > 100 || xMax < 0 || xMax > 100 ||
                yMin < 0 || yMin > 100 ||
                yMax < 0 || yMax > 100)
        {
            throw new IllegalArgumentException("Unable to create Rectangle. Minimum value is 0, maximum is 100.");
        }
        this.xMax = xMax;
        this.xMin = xMin;
        this.yMax = yMax;
        this.yMin = yMin;
    }
    public boolean contains(Point p)
    {
        return p.x() >= xMin
                && p.x() <= xMax
                && p.y() >= yMin
                && p.y() <= yMax;
    }

    public boolean intersects(Rectangle rectangle) {
        return xMax >= rectangle.xMin && yMax >= rectangle.yMin
                && rectangle.xMax >= xMin && rectangle.yMax >= yMin;
    }

    public double distanceTo(Point p)
    {
        int dx = 0;
        int dy = 0;
        if (p.x() < xMin) {
            dx = p.x() - xMin;
        }
        else if (p.x() > xMax) {
            dx = p.x() - xMax;
        }
        if (p.y() < yMin) {
            dy = p.y() - yMin;
        }
        else if (p.y() > yMax) {
            dy = p.y() - yMax;
        }
        return Math.sqrt(dx*dx + dy*dy);
    }

    public double distanceToSquared(Point p){
        return distanceTo(p) * distanceTo(p);
    }

    @Override
    public String toString() {
        return "[" + xMin + ", " + xMax + "] x [" + yMin + ", " + yMax + "]";
    }
}
