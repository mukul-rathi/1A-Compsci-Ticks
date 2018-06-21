package uk.ac.cam.cl.msr45.exercises;

import uk.ac.cam.cl.mlrd.exercises.social_networks.IExercise10;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Exercise10 implements IExercise10 {
    @Override
    public Map<Integer, Set<Integer>> loadGraph(Path graphFile) throws IOException {
        List<String> edges = Files.readAllLines(graphFile);
        Map<Integer, Set<Integer>> graph = new HashMap<Integer, Set<Integer>>();
        for(String e: edges){
           String[] nodes = e.split(" ");
           int nodeFrom = Integer.parseInt(nodes[0]);
           int nodeTo = Integer.parseInt(nodes[1]);

           //create an empty Set if there isn't already one
           if(!graph.containsKey(nodeFrom)){
               Set<Integer> nodeFromAdj = new HashSet<>();
               graph.put(nodeFrom,nodeFromAdj);
           }
            if(!graph.containsKey(nodeTo)){
                Set<Integer> nodeToAdj = new HashSet<>();
                graph.put(nodeTo,nodeToAdj);
            }

            //add nodeTo to nodeFrom's adjacency list and vice versa since undirected graph
            Set<Integer> nodeFromAdj = graph.get(nodeFrom);
            nodeFromAdj.add(nodeTo);
            graph.put(nodeFrom,nodeFromAdj);


            Set<Integer> nodeToAdj = graph.get(nodeTo);
            nodeToAdj.add(nodeFrom);
            graph.put(nodeTo,nodeToAdj);

        }

        return graph;
    }

    @Override
    public Map<Integer, Integer> getConnectivities(Map<Integer, Set<Integer>> graph) {
        Map<Integer, Integer> nodeDegrees = new HashMap<Integer, Integer>();
        for(Integer node: graph.keySet()){
            nodeDegrees.put(node,graph.get(node).size());
        }

        return nodeDegrees;
    }

    @Override
    public int getDiameter(Map<Integer, Set<Integer>> graph) {
        int diameter = 0;
        for(Integer source: graph.keySet()) {
            //run Breadth-first search for that node
            Set<Integer> visitedNodes = new HashSet<Integer>();
            Map<Integer,Integer> distFromSource = new HashMap<Integer, Integer>();
            int maxDist =0;

            Queue<Integer> toExplore = new ArrayDeque<Integer>();
            toExplore.add(source);
            visitedNodes.add(source);
            distFromSource.put(source,0);

            while(!toExplore.isEmpty()){
                Integer v = toExplore.remove();
                for(Integer w: graph.get(v)){
                    if(!visitedNodes.contains(w)){
                        toExplore.add(w);
                        visitedNodes.add(w);
                        distFromSource.put(w,distFromSource.get(v)+1);
                        if(distFromSource.get(w)>maxDist){
                            maxDist = distFromSource.get(w);
                        }
                    }
                }
            }
            if(maxDist>diameter){
                diameter=maxDist;
            }

        }
        return diameter;
    }
}
