import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;

// Main DataStructure for WordNet
// Project mark - 97

public class WordNet {
    private Digraph g;          // Adjacency list digraph
    private final SAP sap;      // SAP structure to support distance and ancestor
    private String[] fullNouns; // Correspond all synsets to an index in an array
    private int tail;
    private NodeNoun root;      // BST where key is a solo noun, and value is a list of id's for in which this noun appeared
    
    private class NodeNoun {
        String noun;
        int[] id;         // nouns can repeat in different entries, so store all entries in 1 noun Node
        NodeNoun left, right;
        
        public NodeNoun(String noun, int id){
            this.noun = noun;
            this.id = new int[0];
            addId(id);
            this.left = null; this.right = null;
        }
        // Support additional id's through array size increase. 
        // As such occurrences are very rare and small, doesn't impact performance of large files
        public void addId(int index) {
            int[] temp = new int[this.id.length + 1];
            for (int i = 0; i < this.id.length; i++)
                temp[i] = this.id[i];
            id = temp;
            id[id.length-1] = index;
        }
    }
    private void insertSynset(String synset) {
        fullNouns[tail++] = synset;
        if (tail == fullNouns.length)
            resize(fullNouns.length * 2);
    }
    
    private void resize(int n) {
        String[] temp = new String[n];
        for (int i = 0; i < Math.min(fullNouns.length, n); i++)
            temp[i] = fullNouns[i];
        fullNouns = temp;
    }
    // BST insert done through iteration to avoid StackOverflow for large input
    private void putNoun(String word, int id) {
        NodeNoun cur = root, trail = root;
        while (cur != null) {
            trail = cur;
            int comp = word.compareTo(trail.noun);
            if (comp < 0)
                cur = cur.left;
            else if (comp > 0)
                cur = cur.right;
            else {
                trail.addId(id);
                return;
            }
        }
        if (trail == null)
            root = new NodeNoun(word, id);
        else if (word.compareTo(trail.noun) < 0)
            trail.left = new NodeNoun(word, id);
        else
            trail.right = new NodeNoun(word, id);
    }
    // given the noun, find it's indices from synset
    // use NodeNoun as input isn't a full synset
    private int[] indexFromWord(String word) {
        NodeNoun cur = root;
        while (cur != null) {
            int comp = word.compareTo(cur.noun);
            if (comp == 0) // also check whether we are looking for several nouns of 1 index
                return cur.id;
            else if (comp < 0)
                cur = cur.left;
            else
                cur = cur.right;
        }
        return null;
    }    
    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) throw new IllegalArgumentException();
        
        fullNouns = new String[1];
        tail = 0;
        root = null;
        In in = new In(synsets);
        
        String[] temp;
        // Runs through all entries/vertices
        while (!in.isEmpty()) {
            // Format of each line:  "index,noun,gloss"
            temp = in.readLine().split(",");
            int i = Integer.parseInt(temp[0]); // retrieve index from input line
            // in case we're on the last line -
            //we know the last index, so we know total number of indices to construct an empty graph
            if (!in.hasNextLine())              
                g = new Digraph(i + 1);
            insertSynset(temp[1]);
            // Nouns are stored after 1st comma in for of "a b", where a and b are separate nouns
            for (String s : temp[1].split(" "))
                    putNoun(s, i);
            // gloss was deemed unnecessary for the assignment by specification
        }
        resize(g.V());
        
        in = new In(hypernyms);
        // defines edges in digraph, lines have form "i,j,k...". i is connected 1-directionally to all following indices
        while (in.hasNextLine()) {
            temp = in.readLine().split(",");
            int i = Integer.parseInt(temp[0]);
            // start from 1 as 0th element is the index of hyponym
            for (int k = 1; k < temp.length; k++) 
                g.addEdge(i, Integer.parseInt(temp[k]));                
        }
     
        sap = new SAP(g);
    }

    // returns all WordNet nouns in inorder traversal
    public Iterable<String> nouns(){
        SET<String> nouns = new SET<String>();
        for (int i = 0; i < fullNouns.length; i++) {
            for (String s : fullNouns[i].split(" "))
                nouns.add(s);
        }
        return nouns;
    }

    // is the word a WordNet noun? Simple BST search
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException();
        return indexFromWord(word) != null;
    }

    // distance between nounA and nounB (defined below)
    // if nouns have multiple id entries - find the shortest connected pair
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new IllegalArgumentException();
        int[] indicesA, indicesB;
        indicesA = indexFromWord(nounA); indicesB = indexFromWord(nounB);
        if (indicesA == null || indicesB == null) throw new IllegalArgumentException();
        
        //sap.length requires input of form Iterable<Integer>, so convert arrays to Iterable SET first
        SET<Integer> iter1, iter2;
        iter1 = new SET<Integer>(); iter2 = new SET<Integer>();
        for (int a : indicesA)
            iter1.add(a);
        for (int b : indicesB)
            iter2.add(b);
        
        return sap.length(iter1, iter2);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new IllegalArgumentException();
        int[] indicesA, indicesB;
        indicesA = indexFromWord(nounA); indicesB = indexFromWord(nounB);
        if (indicesA == null || indicesB == null) throw new IllegalArgumentException();
        
        //sap.ancestor requires input of form Iterable<Integer>, so convert arrays to Iterable SET first
        SET<Integer> iter1, iter2;
        iter1 = new SET<Integer>(); iter2 = new SET<Integer>();
        for (int a : indicesA)
            iter1.add(a);
        for (int b : indicesB)
            iter2.add(b);
        
        int ancestor = sap.ancestor(iter1, iter2);
        return fullNouns[ancestor];
    }
    
    public static void main(String[] args) {
        
    }
}
