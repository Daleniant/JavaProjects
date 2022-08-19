import java.util.HashMap;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FordFulkerson;

// Project mark - 100

public class BaseballElimination {
    private HashMap<String, Integer> idByName = new HashMap<String, Integer>();
    private String[] teamNames;
    private int maxWins, teams;
    private int[] wins, lost, left;
    private int[][] games;

    // create a baseball division from given filename in format specified by project description
    public BaseballElimination(String filename) {
        In in = new In(filename);
        teams = in.readInt();
        
        wins  = new int[teams]; 
        lost  = new int[teams];
        left  = new int[teams];
        games = new int[teams][teams];
        teamNames = new String[teams];
        
        maxWins = 0;
        for (int i = 0; i < teams; i++) {
            String name = in.readString();
            teamNames[i] = name;
            idByName.put(name, i);
            
            wins[i]  = in.readInt();
            if (wins[i] > wins[maxWins])
                maxWins = i;
            
            lost[i]  = in.readInt();
            left[i]  = in.readInt();
            for (int j = 0; j < teams; j++)
                games[i][j] = in.readInt();
        }
    }
    
    // number of teams
    public int numberOfTeams() {
        return teams;
    }
    
    // all teams
    public Iterable<String> teams() {
        Queue<String> q = new Queue<String>();
        for (int i = 0; i < teams; i++)
            q.enqueue(teamNames[i]);
        return q;
    }
    
    // number of wins for given team
    public int wins(String team) {
        if (!idByName.containsKey(team)) throw new IllegalArgumentException();
        
        int id = idByName.get(team);
        return wins[id];
    }
    
    // number of losses for given team
    public int losses(String team) {
        if (!idByName.containsKey(team)) throw new IllegalArgumentException();
        
        int id = idByName.get(team);
        return lost[id];
    }
    
    // number of remaining games for given team
    public int remaining(String team) {
        if (!idByName.containsKey(team)) throw new IllegalArgumentException();
        
        int id = idByName.get(team);
        return left[id];
    }
    
    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        if (!idByName.containsKey(team1)) throw new IllegalArgumentException();
        if (!idByName.containsKey(team2)) throw new IllegalArgumentException();
        
        int id1 = idByName.get(team1), id2 = idByName.get(team2);
        return games[id1][id2];
    }
    
    // is given team eliminated?
    public boolean isEliminated(String team) {
        if (!idByName.containsKey(team)) throw new IllegalArgumentException();
        
        int id = idByName.get(team);
        // take care of trivial solution
        if (wins[id] + left[id] < wins[maxWins])
            return true;
        
        FlowNetwork g = createFlowNetwork(id);
        // figure out the maximum outflow of the source
        int maxOutflow = 0;
        for (FlowEdge e : g.adj(0))
            maxOutflow += e.capacity();
        
        FordFulkerson maxFlow = new FordFulkerson(g, 0, (teams-1) + (teams-1)*(teams-2) / 2 + 1);
        /*
         * Due to how we set up the graph, if at least 1 edge coming out of virtual start point isn't full
         * then the team in question will be eliminated. All edges will be full iff maxFLow is equal to the
         * total capacity of the said edges, aka maximum outflow of virtual start point
         */
        if (maxFlow.value() < maxOutflow)
            return true;
        
        return false;
    }
    
    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (!idByName.containsKey(team)) throw new IllegalArgumentException();
        
        if (!isEliminated(team))
            return null;
        
        int id = idByName.get(team);
        Queue<String> q = new Queue<String>();
        
        // if it was eliminated trivially - return only 1 team - the one that eliminated it
        if (wins[id] + left[id] < wins[maxWins])
            q.enqueue(teamNames[maxWins]); 
        
        else {
            int V = (teams-1) + (teams-1)*(teams-2) / 2 + 2, teamStart = V - teams;
    
            FlowNetwork g = createFlowNetwork(id);   
            FordFulkerson maxFlow = new FordFulkerson(g, 0, V-1);
            // go through all vertices of teams and add the ones from the mincut
            for (int i = teamStart; i < V-1; i++) {
                if (maxFlow.inCut(i)) {
                    int index = i - teamStart;
                    index += (index >= id) ? 1 : 0;
                    q.enqueue(teamNames[index]);
                }
            }
        }
        
        return q;
    }
    
    private FlowNetwork createFlowNetwork(int teamId) {
        /*
         * 0th is the virtual start point, with having edges to all games between teams 
         * other than the 1 specified with capacity of amount of this particular games left. 
         * From each game 2 edges of infinite capacity go to the teams that play this game. 
         * From every team there'd be an edge of capacity of [max wins by team X - current wins of team i]
         * coming to a virtual end point. Total number of points - (n-1) + (n-1)*(n-2) / 2 + 2
         * however, there might be unused vertices if a team shouldn't be included in the network
         * 
         * 0 = start point, 1 : (n-1)*(n-2) / 2 + 1 are for games, 
         * total - 2 - n : total - 2 are for teams, total - is virtual end point
         */
        int V = (teams-1) + (teams-1)*(teams-2) / 2 + 2;
        FlowNetwork g = new FlowNetwork(V);
        // value of the first vertex corresponding to teams, pre-calculated for future use
        int teamStart = V - teams;
        // value of max wins by team in question
        int bandwidth = wins[teamId] + left[teamId], gameNumber = 1;
        
        int off1 = 0;
        for (int col = 0; col < teams; col++) {
            if (col == teamId) {
                off1 = -1; continue;
            }
            
            int off2 = 0;
            for (int row = 0; row < col; row++) {
                if (row == teamId) {
                    off2 = -1; continue;
                }
                if (games[row][col] == 0)
                    continue;
                
                FlowEdge e = new FlowEdge(0, gameNumber, games[row][col]);
                g.addEdge(e);
                // vertices of each team
                e = new FlowEdge(gameNumber, teamStart + row + off2, Double.POSITIVE_INFINITY);
                g.addEdge(e);
                e = new FlowEdge(gameNumber, teamStart + col + off1, Double.POSITIVE_INFINITY);
                g.addEdge(e);
                gameNumber++;
            }
            
            FlowEdge e = new FlowEdge(teamStart + col + off1, V-1, bandwidth - wins[col]);
            g.addEdge(e);
        }

        return g;
    }
    
    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
