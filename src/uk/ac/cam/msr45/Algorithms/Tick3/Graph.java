package uk.ac.cam.msr45.Algorithms.Tick3;

import uk.ac.cam.rkh23.Algorithms.Tick3.GraphBase;
import uk.ac.cam.rkh23.Algorithms.Tick3.MaxFlowNetwork;
import uk.ac.cam.rkh23.Algorithms.Tick3.TargetUnreachable;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Graph extends GraphBase {
    private Set<Integer> nodesPushFlowTo;
    private int flow[][];
    private Map<Integer,Set<Integer>> edges;
    private Map<Integer,Set<Integer>> reverseEdges;


    public Graph(URL url) throws IOException {
        super(url);
    }

    public Graph(String file) throws IOException {
        super(file);
    }

    public Graph(int[][] adj) {
        super(adj);
    }

    @Override
    public List<Integer> getFewestEdgesPath(int src, int target) throws TargetUnreachable {

        //initialise BFS data structures
        List<Integer> path = new ArrayList<Integer>();
        Map<Integer,Integer> predecessor = new HashMap<Integer, Integer>(); //used to backtrack and construct aug path after BFS complete
        boolean nodeVisited[] = new boolean[mN];
        Queue<Integer> toExplore = new ArrayDeque<Integer>();

        nodeVisited[src] = true;
        toExplore.add(src);

        while(!toExplore.isEmpty()){
            Integer v = toExplore.remove();
            Set<Integer> neighboursV = new HashSet<Integer>();
            //in the residual graph there is an edge from u->v if u->v = original graph edge or reverse edge
            neighboursV.addAll(reverseEdges.get(v));
            neighboursV.addAll(edges.get(v));
            for(int w: neighboursV){ //we iterate through v's neighbours
                if (!nodeVisited[w]){

                    if((flow[v][w] < mAdj[v][w]) ||(flow[w][v]>0)) { //check if valid edge in residual graph

                        toExplore.add(w);
                        nodeVisited[w] = true;
                        predecessor.put(w, v);
                        if(w==target){ //we're done - found a path from s->t
                            break;
                        }
                    }
                }
            }
        }
        if(!predecessor.containsKey(target)){ //no path from s->t
            throw new TargetUnreachable();
        }
        else{ //construct BFS path
            path.add(target);
            while(predecessor.get(path.get(0))!=src){
                path.add(0,predecessor.get(path.get(0)));
            }
            path.add(0,src);
        }
        return path;
    }

    @Override
    public MaxFlowNetwork getMaxFlow(int s, int t) {

        flow = new int[mN][mN];
        //create edges & reverseEdges map to iterate over in BFS and also to easily check if u->v is an edge or reverse-edge
        edges = new HashMap<Integer, Set<Integer>>();
        reverseEdges = new HashMap<Integer, Set<Integer>>();

        for(int u=0; u<mN; u++){
            Set<Integer> adjU = new HashSet<Integer>();
            Set<Integer> revAdjU = new HashSet<Integer>();

            for(int v=0; v<mN; v++){
                if(mAdj[u][v]>0){
                    adjU.add(v);
                }
                if(mAdj[v][u]>0){
                    revAdjU.add(v);

                }            }
            edges.put(u,adjU);
            reverseEdges.put(u, revAdjU);

        }

        while(true){
            List<Integer> augPath = null;
                try {
                    augPath = getFewestEdgesPath(s,t);
                    int minCapacity =Integer.MAX_VALUE; //the amount by which we augment flow



                for(int i=0; i<augPath.size()-1;i++){
                    int u = augPath.get(i);
                    int v = augPath.get(i+1);
                    //u, v just for ease of notation
                    if(edges.get(u).contains(v)){  // u->v is an edge
                        if((mAdj[u][v]-flow[u][v])<minCapacity){ //check if residual capacity (amount we can increase flow by) less than minCapacity
                            minCapacity = mAdj[u][v]-flow[u][v];
                        }
                    }
                   else { //v->u is an edge so residual capacity of u->v is flow v->u
                        minCapacity = (flow[v][u]<minCapacity)?flow[v][u]:minCapacity;
                    }

                }
                for(int i=0; i<augPath.size()-1;i++){
                    int u = augPath.get(i);
                    int v = augPath.get(i+1);
                    if(edges.get(u).contains(v)) { //u->v is an edge so increase flow along it
                        flow[u][v] += minCapacity;
                    }
                    else { //v->u is an edge - since we are pushing more flow the other way this is equiv to reducing flow from v->u
                        flow[v][u]-=minCapacity;
                    }


                }

            }
            catch(TargetUnreachable e) { //no more augmenting paths
                break;
            }
        }
        int maxFlow = 0;
        for(int i=0; i<mN; i++){ //sum all of flows out of s
            maxFlow+=flow[s][i];
        }
        return new MaxFlowNetwork(maxFlow,this);

    }
}
