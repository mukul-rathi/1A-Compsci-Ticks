package uk.ac.cam.cl.msr45.exercises;

import uk.ac.cam.cl.mlrd.exercises.social_networks.IExercise12;

import java.util.*;

public class Exercise12 implements IExercise12 {


    @Override
    public int getNumberOfEdges(Map<Integer, Set<Integer>> graph) {
        int numEdges = 0;
        for(Integer v : graph.keySet()){
            numEdges+=graph.get(v).size();
        }
        return numEdges/2; // since undirected graph
    }
    @Override
    public List<Set<Integer>> getComponents(Map<Integer, Set<Integer>> graph) {
        List<Set<Integer>> connectedComponents = new ArrayList<Set<Integer>>();
        Set<Integer> visitedNodes = new HashSet<Integer>();
        for(Integer s : graph.keySet()) {
            if(visitedNodes.contains(s)){
                continue;
            }
            Set<Integer> component = new HashSet<Integer>();
            Stack<Integer> toExplore = new Stack<Integer>();
            toExplore.push(s);
            component.add(s);

            while(!toExplore.isEmpty()){
                Integer v = toExplore.pop();
                for(Integer w: graph.get(v)){
                    if(!visitedNodes.contains(w)){
                        toExplore.push(w);
                        component.add(w);
                        visitedNodes.add(w);
                    }
                }
            }
            connectedComponents.add(component);
        }
        return connectedComponents;
    }

    @Override
    public Map<Integer, Map<Integer, Double>> getEdgeBetweenness(Map<Integer, Set<Integer>> graph) {

        Stack<Integer> S = new Stack<Integer>();
        Map<Integer, Double> sigma = new HashMap<Integer, Double>();
        Map<Integer, Double> delta = new HashMap<Integer, Double>();

        Map<Integer, Map<Integer, Double>> betweenCentrality = new HashMap<Integer, Map<Integer, Double>>();

        //BFS data structures
        Queue<Integer> toExploreQ = new ArrayDeque<Integer>();
        Map<Integer, List<Integer>> pred = new HashMap<Integer, List<Integer>>();
        Map<Integer, Double> dist = new HashMap<Integer, Double>();

        //initialise betweenCentrality
        for (Integer v : graph.keySet()) {
            Map<Integer, Double> vCB = new HashMap<Integer, Double>();
            for(Integer w: graph.get(v)) {
                vCB.put(w, 0.0);
            }
            betweenCentrality.put(v,vCB);
        }

        for (Integer sourceNode : graph.keySet()) {

            //initialisation
            for (Integer w : graph.keySet()) {
                List<Integer> predW = new ArrayList<>();
                pred.put(w, predW);
                dist.put(w, -1.0); //-1 used for infinity
                sigma.put(w, 0.0);
            }
            dist.put(sourceNode, 0.0);
            sigma.put(sourceNode, 1.0);
            toExploreQ.add(sourceNode);

            while (!toExploreQ.isEmpty()) {
                Integer v = toExploreQ.remove();
                S.push(v);
                for (Integer w : graph.get(v)) {
                    //path discovery
                    if (dist.get(w) == -1) {
                        dist.put(w, dist.get(v) + 1);
                        toExploreQ.add(w);
                    }
                    //path counting
                    if (dist.get(w) == (dist.get(v) + 1)) {
                        sigma.put(w, sigma.get(w) + sigma.get(v));
                        pred.get(w).add(v);
                    }
                }

            }

            //accumulation
            for (Integer v : graph.keySet()) {
                delta.put(v, 0.0);
            }
            while(!S.isEmpty()){
                Integer w = S.pop();
                for(Integer v: pred.get(w)){
                    double c = (sigma.get(v)/sigma.get(w))*(1+delta.get(w));
                    Map<Integer,Double> vCB = betweenCentrality.get(v);
                    vCB.put(w,vCB.get(w)+c);
                    betweenCentrality.put(v,vCB);
                    delta.put(v,delta.get(v)+c);
                }
            }

        }

        return betweenCentrality;
    }
    @Override
    public List<Set<Integer>> GirvanNewman(Map<Integer, Set<Integer>> graph, int minimumComponents) {
        List<Set<Integer>> connectedComponents = new ArrayList<Set<Integer>>();
        while(connectedComponents.size()<minimumComponents && getNumberOfEdges(graph)>0){
            Map<Integer, Map<Integer, Double>> edgeBetweenness = getEdgeBetweenness(graph);
            double maxBetweenness = 0;
            Map<Integer,Integer> maxEdges = new HashMap<Integer, Integer>();

            //find edges with max edge between-ness
            for(Integer v : edgeBetweenness.keySet()){
                for(Integer w: edgeBetweenness.get(v).keySet()){
                    double edgeB = edgeBetweenness.get(v).get(w);
                    if(edgeB>=(maxBetweenness-1e-6)){
                        if(edgeB>(maxBetweenness+1e-6)){
                            maxEdges.clear();
                            maxBetweenness = edgeB;
                        }
                        maxEdges.put(v,w);
                    }
                }
            }

            //remove edges with highest betweenness
            for(Integer v: maxEdges.keySet()){
                Integer w = maxEdges.get(v);
                graph.get(v).remove(w);
                graph.get(w).remove(v);
            }

            //recalculate connectedComponents
            connectedComponents = getComponents(graph);

        }


        return connectedComponents;
    }
}
