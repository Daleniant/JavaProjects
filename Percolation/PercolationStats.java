import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

// Main object made for finding a percolation value through simulation of multiple cells

public class PercolationStats {
    // perform independent trials on an n-by-n grid
    private double[] testResults;
    private int trials;
    
    // input a size of a cell and number of trials to make on it
    public PercolationStats(int n, int trialsPar) {
        if (n < 1 || trialsPar < 1)
            throw new IllegalArgumentException();

        trials = trialsPar;
        testResults = new double[trials];
        for (int i = 0; i < trials; i++) {
            //individual trials. While the system doesn't percolate - open a random point in a cell
            Percolation trial = new Percolation(n);

            while (!trial.percolates()) {
                int s = StdRandom.uniform(n * n);
                int row = s / n + 1, col = s % n + 1;
                trial.open(row, col);
            }
            testResults[i] = (double)trial.numberOfOpenSites() / (n*n);
        }
    }
    // functions used for required statistic analysis
    public double mean() {
        return StdStats.mean(testResults);
    }

    public double stddev() {
        return StdStats.stddev(testResults);
    }

    public double confidenceLo() {
        double x = mean(), s = stddev();

        return x - 1.96 * s / Math.sqrt(trials);
    }

    public double confidenceHi() {
        double x = mean(), s = stddev();

        return x + 1.96 * s / Math.sqrt(trials);
    }

    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);
   
        PercolationStats sim = new PercolationStats(n, trials);
        
        System.out.println("mean                    = " + sim.mean());
        System.out.println("stddev                  = " + sim.stddev());
        System.out.println("95% confidence interval = [" + sim.confidenceLo() + ", " + sim.confidenceHi() + "]");
    }
}
