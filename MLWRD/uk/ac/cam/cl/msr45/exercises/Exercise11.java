package uk.ac.cam.cl.msr45.exercises;

import uk.ac.cam.cl.mlrd.exercises.social_networks.IExercise10;
import uk.ac.cam.cl.mlrd.exercises.social_networks.IExercise11;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class Exercise11 implements IExercise11 {
    IExercise10 imp = new Exercise10();
    @Override
    public Map<Integer, Double> getNodeBetweenness(Path graphFile) throws IOException {
        Map<Integer, Set<Integer>> graph = imp.loadGraph(graphFile);

        Stack<Integer> S = new Stack<Integer>();
        Map<Integer,Double> sigma = new HashMap<Integer, Double>();
        Map<Integer, Double> delta = new HashMap<Integer, Double>();

        Map<Integer,Double> betweenCentrality = new HashMap<Integer,Double>();

        //BFS data structures
        Queue<Integer> toExploreQ = new ArrayDeque<Integer>();
        Map<Integer,List<Integer>> pred = new HashMap<Integer,List<Integer>>();
        Map<Integer, Double> dist = new HashMap<Integer, Double>();

        //initialise betweenCentrality
        for(Integer v : graph.keySet()){
            betweenCentrality.put(v,0.0);
        }

        for(Integer sourceNode: graph.keySet()){

            //initialisation
            for(Integer w: graph.keySet()){
                List<Integer> predW = new ArrayList<>();
                pred.put(w,predW);
                dist.put(w,-1.0); //-1 used for infinity
                sigma.put(w,0.0);
            }
            dist.put(sourceNode,0.0);
            sigma.put(sourceNode,1.0);
            toExploreQ.add(sourceNode);

            while(!toExploreQ.isEmpty()){
                Integer v = toExploreQ.remove();
                S.push(v);
                for(Integer w: graph.get(v)){
                    //path discovery
                    if(dist.get(w)==-1){
                        dist.put(w,dist.get(v)+1);
                        toExploreQ.add(w);
                    }
                    //path counting
                    if(dist.get(w)==(dist.get(v)+1)){
                        sigma.put(w, sigma.get(w)+sigma.get(v));
                        pred.get(w).add(v);
                    }
                }

            }

            //accumulation
            for(Integer v: graph.keySet()){
                delta.put(v,0.0);
            }
            while(!S.empty()){
                Integer w= S.pop();
                for(Integer v: pred.get(w)){
                    double newVal = delta.get(v) + (sigma.get(v)/sigma.get(w))*(1+delta.get(w));
                    delta.put(v, newVal);
                }
                if(w!=sourceNode){
                    betweenCentrality.put(w,betweenCentrality.get(w)+delta.get(w));
                }
            }


        }
        //halve scores since for undirected graph
        for(Integer v: betweenCentrality.keySet()){
            betweenCentrality.put(v,betweenCentrality.get(v)/2);
        }
        return betweenCentrality;
    }
}
