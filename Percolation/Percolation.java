import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private byte[] cell; // 3 bits - abc. a = isOpen, b = is connected to top, c = is connected to bot
    private WeightedQuickUnionUF sites;
    private int size, open;
    private boolean isPercolating = false;

    public Percolation(int n) {
        if (n < 1) throw new IllegalArgumentException();
        open = 0;
        size = n;
        
        sites = new WeightedQuickUnionUF(n*n);
        
        cell = new byte[n*n];
        for (int i = 0; i < n*n; i++) {
            cell[i] = 0;
        }
    }

    public void open(int row, int col) {
        if (row < 1 || row > size || col < 1 || col > size)
            throw new IllegalArgumentException();
        
        if (!isOpen(row, col)) {
            open++;
            
            int q = (row - 1) * size + col - 1;
            cell[q] |= (1 << 2);
            int prevRoot, curRoot;
            
            if (row > 1) { // if it isn't connected to top row
                if (isOpen(row - 1, col)) { // check a tile straight above
                    prevRoot = sites.find(q - size);
                    curRoot = sites.find(q);
                    sites.union(prevRoot, curRoot); // merge 2 canonical elements
                    cell[sites.find(q)] = (byte) (cell[prevRoot] | cell[curRoot]); // update byto info
                }
            }
            // if connected - make sure it is indicated in canonical's element byte
            else cell[sites.find(q)] |= (1 << 1); 
            
            if (row < size) { // if it isn't connected to bottom row
                if (isOpen(row + 1, col)) { // check a tile straight below
                    prevRoot = sites.find(q + size);
                    curRoot = sites.find(q);
                    sites.union(prevRoot, curRoot);
                    cell[sites.find(q)] = (byte) (cell[prevRoot] | cell[curRoot]); 
                }
            }
            // if connected - make sure it is indicated in canonical's element byte
            else cell[sites.find(q)] |= (1 << 0);
            
            if (col > 1 && isOpen(row, col - 1)) { // if tile to the left exists
                prevRoot = sites.find(q - 1);
                curRoot = sites.find(q);
                sites.union(prevRoot, curRoot);
                cell[sites.find(q)] = (byte) (cell[prevRoot] | cell[curRoot]);
            }
            if (col < size && isOpen(row, col + 1)) { // if tile to the right exists
                prevRoot = sites.find(q + 1);
                curRoot = sites.find(q);
                sites.union(prevRoot, curRoot);
                cell[sites.find(q)] = (byte) (cell[prevRoot] | cell[curRoot]);
            }
            
            // if canonical element connected to both top and bottom - then percolates
            q = sites.find(q);
            if (((cell[q] >> 1) & 1) * ((cell[q] >> 0) & 1) == 1) 
                isPercolating = true;
        }
    }

    public boolean isOpen(int row, int col) {
        if (row < 1 || row > size || col < 1 || col > size)
            throw new IllegalArgumentException();
        
        int q = (row - 1) * size + col - 1;
        
        return ((cell[q] >> 2) & 1) == 1; // return bit 'a'
    }

    public boolean isFull(int row, int col) {
        if (row < 1 || row > size || col < 1 || col > size)
            throw new IllegalArgumentException();
        
        int q = (row - 1) * size + col - 1;
        q = sites.find(q);
        return ((cell[q] >> 1) & 1) == 1; // return canonical element's bit 'b'
    }

    public int numberOfOpenSites() {
        return open;
    }

    public boolean percolates() {
        return isPercolating;
    }
}