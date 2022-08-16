import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.AcyclicSP;

// Project Mark - 100

public class SeamCarver {
    private Picture picture;
    private int width, height;
    private double[][] gradient; // energy gradient for the picture
    
    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new IllegalArgumentException();
        
        width = picture.width();
        height = picture.height();
        this.picture = new Picture(width, height);
        // make immutable copy
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)
                this.picture.set(x, y, picture.get(x, y));
        }
        
        gradient = new double[height][width]; 
        toGradient();
    }

    // current picture
    public Picture picture() {
        // create a copy to make private picture immutable
        Picture copy = new Picture(width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)
                copy.set(x, y, picture.get(x, y));
        }
        return copy;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (( x < 0 || x >= width ) ||( y < 0 || y >= height ))
            throw new IllegalArgumentException();
        
        if ( (x == 0 || x == width-1) || (y == 0 || y == height-1))
            return 1000;
        
        double xGrad = 0.0, yGrad = 0.0;

        xGrad = computeGrad(xGrad, picture.getRGB(x-1, y), picture.getRGB(x+1, y));
        yGrad = computeGrad(yGrad, picture.getRGB(x, y-1), picture.getRGB(x, y+1));
        
        // return the energy of the pixel: root of sum of squared gradients
        return Math.sqrt(xGrad + yGrad);
    }
    
    private double computeGrad(double grad, int colorLeft, int colorRight) {
        int leftTemp, rightTemp;
        // computing gradient as the sum of squares of central differences for each color
        // red colors
        leftTemp = (colorLeft >> 16) & 0xFF;
        rightTemp = (colorRight >> 16) & 0xFF;
        grad += Math.pow(leftTemp-rightTemp, 2);
        // green colors
        leftTemp = (colorLeft >> 8) & 0xFF;
        rightTemp = (colorRight >> 8) & 0xFF;
        grad += Math.pow(leftTemp-rightTemp, 2);
        // blue colors
        leftTemp = (colorLeft >> 0) & 0xFF;
        rightTemp = (colorRight >> 0) & 0xFF;
        grad += Math.pow(leftTemp-rightTemp, 2);
        
        return grad;
    }
    
    private void toGradient() {
        // simply fill gradient array with energy values for each pixel
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                gradient[y][x] = energy(x, y);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        AcyclicSP sp = new AcyclicSP(horizontalGraph(), 0);
        
        int[] seam = new int[width];
        int i = 0;
        // skip first entry to account for virtual point
        // convert linear value into value of a column for that pixel
        for (DirectedEdge e : sp.pathTo(width * height + 1)) {
            if (i > 0)
                seam[i-1] = (e.from()-1) % height;
            i++;
        }
        return seam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        AcyclicSP sp = new AcyclicSP(verticalGraph(), 0);
        
        int[] seam = new int[height];
        int i = 0;
        // skip first entry to account for virtual point
        // convert linear value into value of a row
        for (DirectedEdge e : sp.pathTo(width * height + 1)) {
            if (i > 0)
                seam[i-1] = (e.from()-1) % width;
            i++;
        }
        return seam;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (height <= 1) throw new IllegalArgumentException();
        if (seam == null || !valid(seam, height, width)) 
            throw new IllegalArgumentException();
        /*
         * create new picture of a new size, for each column do the following:
         * go from top to bottom, copy pixels until meet the row value from the seam
         * skip it, for every following pixels copy them into a position in new picture, shifted by 1 up
         */
        Picture carved = new Picture(width, height-1);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (y > seam[x])
                    carved.set(x, y-1, picture.get(x, y));
                else if (y < seam[x])
                    carved.set(x, y, picture.get(x, y));
            }
        }
        // update picture and size values, create new gradient array and fill it with new values
        picture = carved;
        height--;
        gradient = new double[height][width];
        toGradient();
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (width <= 1) throw new IllegalArgumentException();
        if (seam == null || !valid(seam, width, height)) 
            throw new IllegalArgumentException();
        /*
         * create new picture of a new size, for each row do the following:
         * go from left to right, copy pixels until meet the column value from the seam
         * skip it, for every following pixels copy them into a position in new picture, shifted by 1 left
         */
        Picture carved = new Picture(width-1, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x > seam[y])
                    carved.set(x-1, y, picture.get(x, y));
                else if (x < seam[y])
                    carved.set(x, y, picture.get(x, y));
            }
        }
        // update picture and size values, create new gradient array and fill it with new values
        picture = carved;
        width--;
        gradient = new double[height][width];
        toGradient();
    }
    
    // translates image into an edge-weighted graph with virtual points on top and bottom
    private EdgeWeightedDigraph verticalGraph() {
        EdgeWeightedDigraph g = new EdgeWeightedDigraph(width*height + 2); // adjust for virtual points
        // connected virtual start point with all values from first row
        for (int x = 0; x < width; x++) 
            g.addEdge(new DirectedEdge(0, x+1, gradient[0][x]));
        /*
         *  connect pixels following way: 
         *  top pixel is connected to pixel right beneath it, as well as
         *  to the left and right of directly under, if they exist
         */
        for (int y = 0; y < height-1; y++) 
            for (int x = 0; x < width; x++) {
                if (x > 0)
                    g.addEdge(new DirectedEdge(x + y*width + 1, x-1 + (y+1)*width + 1, gradient[y+1][x-1]));
                if (x < width - 1)
                    g.addEdge(new DirectedEdge(x + y*width + 1, x+1 + (y+1)*width + 1, gradient[y+1][x+1]));
                g.addEdge(new DirectedEdge(x + y*width + 1, x + (y+1)*width + 1, gradient[y+1][x]));
            }
        // connect all values from the last row to virtual end-point
        for (int x = 0; x < width; x++) 
            g.addEdge(new DirectedEdge(x + (height-1)*width + 1, width*height + 1, gradient[height-1][x]));
        
        return g;
    }
    
    // translates image into an edge-weighted graph with virtual points on left and right
    private EdgeWeightedDigraph horizontalGraph() {
        EdgeWeightedDigraph g = new EdgeWeightedDigraph(width*height + 2); // adjust for virtual points
        // connected virtual start point with all values from first column
        for (int y = 0; y < height; y++) 
            g.addEdge(new DirectedEdge(0, y+1, gradient[y][0]));
        // same as for vertical, but going from left to right
        for (int x = 0; x < width-1; x++) 
            for (int y = 0; y < height; y++) {
                if (y > 0)
                    g.addEdge(new DirectedEdge(y + x*height + 1, y-1 + (x+1)*height + 1, gradient[y-1][x+1]));
                if (y < height - 1)
                    g.addEdge(new DirectedEdge(y + x*height + 1, y+1 + (x+1)*height + 1, gradient[y+1][x+1]));
                g.addEdge(new DirectedEdge(y + x*height + 1, y + (x+1)*height + 1, gradient[y][x+1]));
            }
        // connect all values from the last column to virtual end-point
        for (int y = 0; y < height; y++) 
            g.addEdge(new DirectedEdge(y + (width-1)*height + 1, width*height + 1, gradient[y][width-1]));
        
        return g;
    }
    // checks if the seam is valid - it's size and each of its value, whether it is in bound and is connected correctly
    private boolean valid(int[] seam, int bound, int size) {
        if (seam.length != size || seam[0] < 0 || seam[0] >= bound) {
            StdOut.println("1");
            return false;
        }
        int prev = seam[0];
        
        for (int i = 1; i < size; i++) {
            if (seam[i] < 0 || seam[i] >= bound) {
                StdOut.println("2");
                return false;
            }
            if ( (seam[i] < prev - 1) || (seam[i] > prev + 1) ) {
                StdOut.println(width + " " + height);
                StdOut.println(seam[i] + " " + prev);
                StdOut.println("3");
                return false;
            }
            prev = seam[i];
        }
        return true;
    }

    //  unit testing (optional)
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Picture pic = new Picture(args[0]);
        SeamCarver sc = new SeamCarver(pic);
        for (int i = 0; i < 50; i++) {
            int[] seam = sc.findHorizontalSeam();
            sc.removeHorizontalSeam(seam);
            
            seam = sc.findVerticalSeam();
            sc.removeVerticalSeam(seam);
        }
        sc.picture().show();            
    }

}
