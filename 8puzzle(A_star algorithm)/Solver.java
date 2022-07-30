import java.util.Comparator;
import java.util.Iterator;
import edu.princeton.cs.algs4.MinPQ; // Minimum-oriented priority queue(project requirement)
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

// Main part of a solution. Input the board to this object
// Project mark - 97

public class Solver {
    private Node first;
    private int moves;
    private boolean solv;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException();
        
        moves = 0;      
        solv = true;
        first = null;
        
        // custom comparator based on weight: moves made for a board + it's Manhattan value. The less the better
        Comparator<QueueNode> comparator = new Comparator<QueueNode>() {
            @Override
            public int compare(QueueNode b1, QueueNode b2) {
                return (b1.moves + b1.manhattan) - (b2.moves + b2.manhattan);
            }
        };
        
        // using twin system to find whether the board is solvable - create a sepate queue for the twin
        MinPQ<QueueNode> pq, pqTwin;
        pq = new MinPQ<QueueNode>(comparator);
        pqTwin = new MinPQ<QueueNode>(comparator);
        pq.insert(new QueueNode(initial, 0, initial.manhattan(), new QueueNode(null, 0, 0, null)));
        pqTwin.insert(new QueueNode(initial.twin(), 0, initial.twin().manhattan(), new QueueNode(null, -1, -1, null)));
        
        QueueNode cur;
        while (true) {
            // solving original board
            cur = pq.delMin();
            // if board is solved - trace back the boards that led to solution and put them in stack
            if (cur.node.isGoal()) {
                while (cur.prev.node != null) {
                    addToGameTree(cur.node);
                    moves++;
                    cur = cur.prev;
                }
                addToGameTree(cur.node);
                break;
            }
            // add all the neighbors (that are not a copy of a previous board)
            for (Board neighbor : cur.node.neighbors()) {
                if (!neighbor.equals(cur.prev.node)) 
                    pq.insert(new QueueNode(neighbor, cur.moves + 1, neighbor.manhattan(), cur));
            }
            
            // solve the twin and see if it is solvable. Only 1 of the 2 boards is solvable
            cur = pqTwin.delMin();
            if (cur.node.isGoal()) {
                solv = false; // twin is solvable == original is not
                moves = -1;   // account for requirements of output for unsolvable board
                break;
            }
            for (Board neighbor : cur.node.neighbors()) {
                if (!neighbor.equals(cur.prev.node)) 
                    pqTwin.insert(new QueueNode(neighbor, cur.moves + 1, neighbor.manhattan(), cur));
            }
        }
    }
    // used for stacking a solution
    private class Node {
        Board board;
        Node next;
        
        public Node (Board board, Node next) {
            this.board = board;
            this.next = next;
        }
    }
    // prev' is the previous board, used as QueueNode to allow backtracking once solution is found
    private class QueueNode {
        Board node;
        int moves;
        int manhattan; // caching manhattan value
        QueueNode prev;
        
        public QueueNode(Board node, int moves, int manhattan, QueueNode prev) {
            this.node = node;
            this.moves = moves;
            this.manhattan = manhattan;
            this.prev = prev;
        }
    }
    // add solution step to stack
    private void addToGameTree(Board board) {
        Node temp = first;
        first = new Node(board, temp);
    }
    
    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return solv;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return moves;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        
        return new Iterable<Board>() {
            @Override
            public Iterator<Board> iterator() {
                return new Iterator<Board>() {
                    private Node copy = first;
                    @Override
                    public boolean hasNext() {
                        return copy != null;
                    }
                    @Override
                    public Board next() {
                        // iterate through stack if board is solvable, return null otherwise
                        if (!hasNext()) throw new java.util.NoSuchElementException();
                        if (!isSolvable()) {
                            copy = null;
                            return null; }    
                        Board temp = copy.board;
                        copy = copy.next;
                        return temp;

                    }
                };
            }
        };
    }

    // test client
    public static void main(String[] args) {
        // create initial board from file
        int n = StdIn.readInt();
        int[][] tiles = new int[n][n];
        
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = StdIn.readInt();
        
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves() + "\n");
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}