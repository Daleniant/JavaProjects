import java.util.Iterator;

// Secondaty object used to represent a board of a game
// Project mark - 97

public class Board {
    private final int n;
    private int[][] tiles; 
    private int row, col; // position of zero
    private int hamming, manhattan;
    private Board twin;
    
    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        n = tiles.length;
        this.tiles = new int[n][n];
        twin = null;
        
        hamming = 0; 
        manhattan = 0;
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                this.tiles[i][j] = tiles[i][j];
                // get coordinates of zero on the board
                if      (this.tiles[i][j] == 0) { 
                    row = i; 
                    col = j; 
                } 
                else if ((tiles[i][j] - 1) != i*n + j) {
                    hamming++;                                        // calculate hamming distance
                    manhattan += Math.abs((tiles[i][j]-1) / n - i);       // account for vertical distance
                    manhattan += Math.abs((tiles[i][j]-1) % n - j);       // account for horizontal distance
                }
                
            }
        }  
    }
                                           
    // string representation of this board (by project requirement)
    public String toString() {
        StringBuilder stb = new StringBuilder(2*n*n + n + 2);
        
        stb.append(n);
        stb.append("\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                stb.append(tiles[i][j]);
                stb.append(" ");
            }
            stb.append("\n");
        }
        
        return stb.toString();
    }

    // board dimension n
    public int dimension() {
        return n;
    }

    // number of tiles out of place
    public int hamming() {
        return hamming;
    }

    // sum of Manhattan distances between tiles and goal
    // Manhattan distance - sum of the vertical and horizontal distance
    public int manhattan() {
        return manhattan;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hamming == 0;
    }

    // boards are equal only if they have the same dimensions and all of their elements are on the same places
    public boolean equals(Object y) {
        if (y == null || getClass() != y.getClass())
            return false;
        if (this == y) 
            return true;
        
        Board that = (Board) y;
        if (this.n == that.n) {
            for (int i = 0; i < this.n; i++) {
                for (int j = 0; j < this.n; j++) {
                    if (this.tiles[i][j] != that.tiles[i][j]) 
                        return false;
                }
            }
            return true;
        }
        return false;
    } 

    // all neighboring boards
    public Iterable<Board> neighbors() {
        
        return new Iterable<Board>() {
            @Override
            public Iterator<Board> iterator() {
                
                return new Iterator<Board>() {
                    // used to track if we have checked specified positions and if we can check them
                    private boolean[] dir = {row > 0, row < n-1, col > 0, col < n-1};
                    
                    @Override
                    public boolean hasNext() {
                        return dir[0] || dir[1] || dir[2] || dir[3];
                    }
                    
                    @Override
                    public Board next() {
                        if (!hasNext()) throw new java.util.NoSuchElementException();
                        
                        Board temp = new Board(tiles);
                        if (dir[0]) { // switch with tile right above
                            temp.move(row-1, col, row, col);
                            dir[0] = false;
                        }
                        else if (dir[1]) { // switch with tile right below
                            temp.move(row+1, col, row, col);
                            dir[1] = false;
                        }
                        else if (dir[2]) { // switch with tile to the left
                            temp.move(row, col-1, row, col);
                            dir[2] = false;
                        }
                        else if (dir[3]){ // switch with tile to the right
                            temp.move(row, col+1, row, col);
                            dir[3] = false;
                        }
                        return temp;
                    }
                };
            }
        };
    }
    
    private void move(int i, int j, int k, int h) {
        int temp = tiles[i][j];
        tiles[i][j] = tiles[k][h];
        tiles[k][h] = temp;
        
        // remove the manhattan value of original [i][j] element, then add its new manhattan value
        manhattan -= Math.abs((temp-1) / n - i);
        manhattan -= Math.abs((temp-1) % n - j);
        
        manhattan += Math.abs((temp-1) / n - k);
        manhattan += Math.abs((temp-1) % n - h);

        // change hamming only if the element was previuosly in correct place or if it is now in place
        if ((temp-1) == i*n + j)
            hamming++;
        else if ((temp-1) == k*n + h)
            hamming--;
        
        // same calculations for the second element, unless it is zero(twin declaration)
        if (k != row && h != col) {
            temp = tiles[i][j];
            manhattan -= Math.abs((temp-1) / n - k);
            manhattan -= Math.abs((temp-1) % n - h);
            
            manhattan += Math.abs((temp-1) / n - i);
            manhattan += Math.abs((temp-1) % n - j);
            
            if ((temp-1) == k*n + h)
                hamming++;
            else if ((temp-1) == i*n + j)
                hamming--;
        }
        else {
            row = i;
            col = j;
        }
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        /* find the first pair of non-zero value(horizontal for simplicity),
         * create a new board, swap that pair and return */
        if (twin == null) {
            int point = 0;
            while (point == row*n + col || point % n == n-1 || point + 1 == row*n + col)
                point++;
            twin = new Board(tiles);
            twin.move(point / n, point % n, (point+1) / n, (point+1) % n);
        }
        return twin;
    }
}
