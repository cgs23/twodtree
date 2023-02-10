import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingFormatArgumentException;
import java.util.Scanner;

public class TwoDTree {
    private class TreeNode{
        private Point point;
        private TreeNode right;
        private TreeNode left;
        private int level; // how deep we are in the tree

        // every node of the tree corresponds to a rectangle (as mentioned in the exercise)
        // So we create this rectangle to use for the rangeSearch method. (use of intersects)
        private Rectangle rectangle;

        private TreeNode(Point point, int level, int xmin, int xmax, int ymin, int ymax) {
            this.point = point;
            this.level = level;
            this.rectangle = new Rectangle(xmin, xmax, ymin, ymax);
        }
    }

    private TreeNode head;
    private int size;
    private Point currentNearestPoint;
    private double minimumDistance;

    public void calculateMinimumDistance(Point p){
        if (p == null) {
            throw new IllegalArgumentException();
        }
        if (isEmpty()) {
            System.out.println("Tree is empty. Please insert some points and try again.");
        }
        nearestNeighbor(p);
        if (currentNearestPoint != null){
            System.out.println(String.format("The closest point to %s is %s with Euclidean distance %,.2f", p.toString(), currentNearestPoint.toString(), Math.sqrt(minimumDistance)));
        }
    }
    public TwoDTree() {
        size = 0;
        head = null;
    };

    public boolean isEmpty() {
        return size == 0;
    }

    public int size(){
        return size;
    }

    private int compare(Point point, TreeNode node) {
        if (node.level % 2 == 0) {
            if (point.x() == node.point.x()) {
                return Integer.compare(point.y(), node.point.y());
            } else {
                return Integer.compare(point.x(), node.point.x());
            }
        } else {
            if (point.y() == node.point.y()) {
                return Integer.compare(point.x(), node.point.x());
            } else {
                return Integer.compare(point.y(), node.point.y());
            }
        }
    }

    private TreeNode generateNode(Point p, TreeNode parent) {
        int cmp = compare(p, parent);
        if (cmp < 0) {
            if (parent.level % 2 == 0) {
                return new TreeNode(p, parent.level + 1, parent.rectangle.getxMin(), parent.point.x(), parent.rectangle.getyMin(), parent.rectangle.getyMax());
            } else {
                return new TreeNode(p, parent.level + 1, parent.rectangle.getxMin(), parent.rectangle.getxMax(), parent.rectangle.getyMin(), parent.point.y());
            }
        } else {
            if (parent.level % 2 == 0) {
                return new TreeNode(p, parent.level + 1, parent.point.x(), parent.rectangle.getxMax(), parent.rectangle.getyMin(), parent.rectangle.getyMax());

            } else {
                return new TreeNode(p, parent.level + 1, parent.rectangle.getxMin(), parent.rectangle.getxMax(), parent.point.y(), parent.rectangle.getyMax());

            }
        }
    }

    public void insert(Point point) {
        if (point == null) throw new IllegalArgumentException();
        if (head == null) {
            head = new TreeNode(point, 0, 0, 100, 0, 100);
            size++;
        } else if (!search(point)) {
            insert(point, head);
            size++;
        }
        else {
            System.out.println("Point already exists in the tree.");
        }
    }

    private void insert(Point point, TreeNode node) {
        int cmp = compare(point, node);
        if (cmp < 0) {
            if (node.left == null) {
                node.left = generateNode(point, node);
            } else {
                insert(point, node.left);
            }
        } else if (cmp > 0) {
            if (node.right == null) {
                node.right = generateNode(point, node);
            } else {
                insert(point, node.right);
            }
        }
    }

    public List<Point> rangeSearch(Rectangle rect) {
        if (rect == null) {
            throw new IllegalArgumentException();
        }
        if (isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(rangeSearch(rect, head));
    }

    private ArrayList<Point> rangeSearch(Rectangle rect, TreeNode node) {
        ArrayList<Point> list = new ArrayList<>();
        if (node != null && rect.intersects(node.rectangle)) {
            list.addAll(rangeSearch(rect, node.left));
            list.addAll(rangeSearch(rect, node.right));
            if (rect.contains(node.point)) {
                list.add(node.point);
            }
        }
        return list;
    }


    public Point nearestNeighbor(Point p) {
        currentNearestPoint = null;
        minimumDistance = Double.POSITIVE_INFINITY;
        findNearest(p, head);

        return currentNearestPoint;
    }

    private void findNearest(Point p, TreeNode node) {
        if (node == null) {
            return;
        }
        if (node.rectangle.distanceToSquared(p) <= minimumDistance) {
            double d = node.point.squaredDistanceTo(p);
            if (d < minimumDistance) {
                minimumDistance = d;
                currentNearestPoint = node.point;
            }
            if (node.left != null && node.left.rectangle.contains(p)) {
                findNearest(p, node.left);
                findNearest(p, node.right);
            } else if (node.right != null && node.right.rectangle.contains(p)) {
                findNearest(p, node.right);
                findNearest(p, node.left);
            } else {
                double toLeft = node.left != null ? node.left.rectangle.distanceToSquared(p) : Double.POSITIVE_INFINITY;
                double toRight = node.right != null ? node.right.rectangle.distanceToSquared(p) : Double.POSITIVE_INFINITY;
                if (toLeft < toRight) {
                    findNearest(p, node.left);
                    findNearest(p, node.right);
                } else {
                    findNearest(p, node.right);
                    findNearest(p, node.left);
                }
            }
        }
    }

    public boolean search(Point p) {
        if (p == null) throw new IllegalArgumentException();
        if (isEmpty()) return false;
        return search(head, p, true);
    }

    // CompareWithX -> compares the items based on the X coordinate
    // If it is false, compares with the Y coordinate, to know where to go into the tree
    // if the item is not found
    private boolean search(TreeNode node, Point p, boolean compareWithX) {
        if (isEqual(node, p)) return true;
        if (compareWithX) {
            if (node.point.x() > p.x()) {
                if (node.left == null) {
                    return false;
                } else {
                    return search(node.left, p, false);
                }
            }
            if (node.right == null) {
                return false;
            } else {
                return search(node.right, p, false);
            }
        }
        else {
            if (node.point.y() > p.y()) {
                if (node.left == null) {
                    return false;
                } else {
                    return search(node.left, p, true);
                }
            }
            if (node.right == null) {
                return false;
            } else {
                return search(node.right, p, true);
            }
        }
    }

    private boolean isEqual(TreeNode node, Point p){
        return (node.point.x() == p.x() && node.point.y() == p.y());
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new MissingFormatArgumentException("Please run the program as follows: java TwoDTree path/to/file.txt");
        }
        BufferedReader bufferedReader; // Used to read the file line by line
        TwoDTree tree = new TwoDTree();
        try {
            bufferedReader = new BufferedReader(new FileReader(args[0])); // Read file
            int numberOfPoints = Integer.parseInt(bufferedReader.readLine());
            String line = bufferedReader.readLine();
            int lineCount = 0;
            while (line != null){
                String[] lineArr = line.split(" ");
                int x = Integer.parseInt(lineArr[0]);
                int y = Integer.parseInt(lineArr[1]);
                tree.insert(new Point(x, y));
                lineCount++;
                line = bufferedReader.readLine();
            }
            if (lineCount != numberOfPoints){
                throw new Exception("The number of points are different than specified in the first line of the file");
            }
        }
        catch (Exception ex){
            System.out.println("There was a problem during initialization. Error message: " + ex.getMessage());
            return;
        }
        Scanner sc = new Scanner(System.in);
        System.out.println(String.format("2D-Tree initialized (size %d)", tree.size()));
        PrintCommands();
        int inp;
        do{
            try{
                String inputLine = sc.nextLine();
                inp = Integer.parseInt(inputLine);
            }
            catch (Exception ex){
                System.out.println("Please enter a valid input.");
                inp = 10;
            }

            // value 10 is a random value used to indicate wrong input
            // So we can indicate users to enter a new number
            if(inp == 10) continue;

            if(inp == 1){
                System.out.println(String.format("The size of the tree is %d", tree.size()));
            }
            else if (inp == 2){
                try{
                    System.out.println("Please enter the coordinates of the Point in the following format: X Y");
                    String newPointCoords = sc.nextLine();
                    String[] coordsArr = newPointCoords.split(" ");
                    if (coordsArr.length != 2) {
                        throw new Exception("Please insert two numbers.");
                    }
                    int x = Integer.parseInt(coordsArr[0]);
                    int y = Integer.parseInt(coordsArr[1]);
                    Point p = new Point(x, y);
                    tree.insert(p);
                    System.out.println("Point " + p.toString() + " inserted successfully.");
                }
                catch (Exception ex){
                    System.out.println("Error while inserting into tree: " + ex.getMessage());
                }
            }
            else if (inp == 3){
                try{
                    System.out.println("Please enter the coordinates of the Point in the following format: X Y");
                    String newPointCoords = sc.nextLine();
                    String[] coordsArr = newPointCoords.split(" ");
                    if (coordsArr.length != 2) {
                        throw new Exception("Please insert two numbers.");
                    }
                    int x = Integer.parseInt(coordsArr[0]);
                    int y = Integer.parseInt(coordsArr[1]);
                    Point p = new Point(x, y);
                    boolean result = tree.search(p);
                    if (result){
                        System.out.println("Point " + p.toString() + " exists in the tree!");
                    }
                    else {
                        System.out.println("Point " + p.toString() + " does not exist in the tree.");
                    }
                }
                catch (Exception ex){
                    System.out.println("Error while searching for point: " + ex.getMessage());
                }
            }
            else if (inp == 4){
                try{
                    System.out.println("Please enter the coordinates of the Rectangle in the following format: Xmin Xmax Ymin Ymax");
                    String rectangleCoords = sc.nextLine();
                    String[] coordsArr = rectangleCoords.split(" ");
                    if (coordsArr.length != 4) {
                        throw new Exception("Please insert two numbers.");
                    }
                    int xmin = Integer.parseInt(coordsArr[0]);
                    int xmax = Integer.parseInt(coordsArr[1]);
                    int ymin = Integer.parseInt(coordsArr[2]);
                    int ymax = Integer.parseInt(coordsArr[3]);
                    Rectangle rectangle = new Rectangle(xmin, xmax, ymin, ymax);
                    List results = tree.rangeSearch(rectangle);
                    if(results.isEmpty()){
                        System.out.println("No points were found in the rectangle.");
                    }
                    else {
                        System.out.println("The points found in the rectangle are the following:");
                        System.out.println(results);
                    }
                }
                catch (Exception ex){
                    System.out.println("Error while searching for point: " + ex.getMessage());
                }
            }
            else if (inp == 5){
                try{
                    System.out.println("Please enter the coordinates of the Point in the following format: X Y");
                    String newPointCoords = sc.nextLine();
                    String[] coordsArr = newPointCoords.split(" ");
                    if (coordsArr.length != 2) {
                        throw new Exception("Please insert two numbers.");
                    }
                    int x = Integer.parseInt(coordsArr[0]);
                    int y = Integer.parseInt(coordsArr[1]);
                    Point p = new Point(x, y);
                    tree.calculateMinimumDistance(p);
                }
                catch (Exception ex){
                    System.out.println("Error while calculating minimum distance for point: " + ex.getMessage());
                }
            }
            else if (inp == 9){
                PrintCommands();
            }
            System.out.println("Please enter a new number or input 9 to view the list of commands.");
        } while (inp != 0);
        System.out.println("Exiting..");

    }
    private static void PrintCommands(){
        System.out.println("----------------");
        System.out.println("Please select one of the following options using the numbers:");
        System.out.println("(1): Compute the size of the tree");
        System.out.println("(2): Insert a new point");
        System.out.println("(3): Search if a given point exists in the tree");
        System.out.println("(4): Provide a query rectangle");
        System.out.println("(5): Provide a query point");
        System.out.println("(0): Terminate execution");
        System.out.println("----------------");
    }
}
