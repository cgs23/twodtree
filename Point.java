public class Point {
    private int x;
    private int y;

    public Point(int x, int y){
        if (x < 0 || x > 100 || y < 0 || y > 100) {
            throw new IllegalArgumentException(String.format("Unable to create Point with values x:%d y:%d. Minimum value is 0, maximum is 100.", x, y));
        }
        this.x = x;
        this.y = y;
    }

    public void setX(int x){
        this.x = x;
    }

    public void setY(int y){
        this.y = y;
    }

    public int x(){
        return x;
    }

    public int y(){
        return y;
    }

    public double distanceTo(Point z){
        return Math.sqrt((z.y - y) * (z.y - y) + (z.x - x) * (z.x - x));
    }

    public double squaredDistanceTo(Point z){
        return distanceTo(z) * distanceTo(z);
    }

    @Override
    public String toString(){
        return String.format("(%d,%d)", x, y);
    }
}
