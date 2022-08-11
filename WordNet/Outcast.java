import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

// Algorithm for determining outcast from a given list of nouns
// Project mark - 97

public class Outcast {
    private final WordNet wordnet;
    
    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {         
        this.wordnet = wordnet;
    }
    
    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) { 
        // just put starting value so that java doesn't complain
        String outcast = nouns[0];
        int dt = 0;
        for (String n : nouns) {
            int temp = 0;
            for (String m : nouns)
                temp += wordnet.distance(n, m);
            if (temp > dt) {
                dt = temp;
                outcast = n;
            }
        }
        return outcast;
    }
    
    // see test client below
    public static void main(String[] args) { 
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
