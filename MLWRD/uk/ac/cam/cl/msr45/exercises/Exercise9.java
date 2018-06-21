package uk.ac.cam.cl.msr45.exercises;

import uk.ac.cam.cl.mlrd.exercises.markov_models.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class Exercise9 implements IExercise9 {
    @Override
    public HiddenMarkovModel<AminoAcid, Feature> estimateHMM(List<HMMDataStore<AminoAcid, Feature>> sequencePairs) throws IOException {

        Map<Feature, Map<Feature, Double>> transitionMatrix = new HashMap<Feature, Map<Feature, Double>>();
        Map<Feature, Map<AminoAcid, Double>> emissionMatrix =  new HashMap<Feature, Map<AminoAcid, Double>>();

        //initialise transition matrix
        for(Feature i: Feature.values()){
            Map<Feature, Double> transition  =new HashMap<Feature, Double>();
            for(Feature j: Feature.values()){
                transition.put(j,0.0);
            }
            transitionMatrix.put(i, transition);
        }
        for(Feature i: Feature.values()){
            Map<AminoAcid, Double> transition  =new HashMap<AminoAcid, Double>();
            for(AminoAcid j: AminoAcid.values()){
                transition.put(j,0.0);
            }
            emissionMatrix.put(i, transition);
        }

        for(HMMDataStore<AminoAcid,Feature> seq : sequencePairs){
            for(int i=0; i<seq.observedSequence.size();i++){
                Feature currentState = seq.hiddenSequence.get(i);
                if(i<(seq.hiddenSequence.size()-1)) {
                    //counts for transition matrix
                    Feature nextState = seq.hiddenSequence.get(i + 1);


                    Map<Feature, Double> nextTransition = transitionMatrix.get(currentState);
                    nextTransition.put(nextState, nextTransition.get(nextState) + 1);
                    transitionMatrix.put(currentState, nextTransition);
                }


                //counts for emission matrix
                AminoAcid currentRoll = seq.observedSequence.get(i);


                Map<AminoAcid, Double> nextEmission = emissionMatrix.get(currentState);
                nextEmission.put(currentRoll, nextEmission.get(currentRoll) + 1);
                emissionMatrix.put(currentState,nextEmission);

            }

        }


        for(Feature i: transitionMatrix.keySet()){
            Map<Feature, Double> nextTransition = transitionMatrix.get(i);
            double totalCount =0;
            for(Feature j : nextTransition.keySet()){
                totalCount+=nextTransition.get(j);
            }
            if (totalCount!=0){
                for(Feature j : nextTransition.keySet()){
                    nextTransition.put(j,nextTransition.get(j)/totalCount);
                }
            }
            transitionMatrix.put(i,nextTransition);

        }
        for(Feature i: emissionMatrix.keySet()){
            Map<AminoAcid, Double> nextEmission = emissionMatrix.get(i);
            double totalCount =0;
            for(AminoAcid j : nextEmission.keySet()){
                totalCount+=nextEmission.get(j);
            }
            if (totalCount!=0){
                for(AminoAcid j : nextEmission.keySet()){
                    nextEmission.put(j,nextEmission.get(j)/totalCount);
                }
            }
            emissionMatrix.put(i,nextEmission);


        }
        return new HiddenMarkovModel<>(transitionMatrix,emissionMatrix);
    }

    @Override
    public List<Feature> viterbi(HiddenMarkovModel<AminoAcid, Feature> model, List<AminoAcid> observedSequence) {
        List<Map<Feature, Feature>> psi = new ArrayList<Map<Feature, Feature>>();
        List<Map<Feature, Double>> delta = new ArrayList<Map<Feature, Double>>();
        for(int i=0; i<observedSequence.size();i++) {
            Map<Feature, Double> deltaT = new HashMap<Feature, Double>();
            Map<Feature, Feature> psiT = new HashMap<Feature, Feature>();
            for(Feature currentState : Feature.values()) {
                double maxProb =0;
                Feature maxPrevState =null;
                for (Feature prevState : Feature.values()) {

                    double currentprob =0;
                    if(i>0) {
                        currentprob += delta.get(i - 1).get(prevState);
                        currentprob += Math.log(model.getTransitions(prevState).get(currentState));
                    }
                    currentprob+= Math.log(model.getEmissions(currentState).get(observedSequence.get(i)));
                    if(maxPrevState==null || currentprob>maxProb){
                        maxPrevState = prevState;
                        maxProb = currentprob;
                    }
                }
                deltaT.put(currentState,maxProb);
                psiT.put(currentState,maxPrevState);
            }
            psi.add(psiT);
            delta.add(deltaT);
        }
        Feature maxEndState = null;
        double maxEndProb =0;
        for(Feature currentState : Feature.values()){
            double currentEndProb = delta.get(delta.size()-1).get(currentState);
            if(maxEndState==null || currentEndProb>maxEndProb){
                maxEndState = currentState;
                maxEndProb = currentEndProb;
            }
        }
        List<Feature> hiddenStates = new ArrayList<Feature>();
        hiddenStates.add(maxEndState);
        for(int i=1;i<observedSequence.size();i++){
            hiddenStates.add(psi.get(observedSequence.size()-i).get(hiddenStates.get(i-1)));
        }
        Collections.reverse(hiddenStates);
        return hiddenStates;
    }

    @Override
    public Map<List<Feature>, List<Feature>> predictAll(HiddenMarkovModel<AminoAcid, Feature> model, List<HMMDataStore<AminoAcid, Feature>> testSequencePairs) throws IOException {
        Map<List<Feature>, List<Feature>> predictions = new HashMap<List<Feature>, List<Feature>>();
        for(HMMDataStore<AminoAcid, Feature> hmmData : testSequencePairs) {
            List<Feature> trueVals = hmmData.hiddenSequence;
            List<Feature> predVals = viterbi(model, hmmData.observedSequence);
            predictions.put(trueVals,predVals);

        }
        return predictions;
    }

    @Override
    public double precision(Map<List<Feature>, List<Feature>> true2PredictedMap) {
        double truePos = 0;
        double falsePos = 0;
        for(List<Feature> trueVal : true2PredictedMap.keySet()){
            List<Feature> predVal = true2PredictedMap.get(trueVal);
            for(int i=0; i<trueVal.size();i++){
                if(predVal.get(i).equals(Feature.MEMBRANE)){
                    if(trueVal.get(i).equals(Feature.MEMBRANE)){
                        truePos++;
                    }
                    else{
                        falsePos++;
                    }
                }

            }
        }
        if(truePos==0) return 0;
        return (truePos)/(truePos+falsePos);
    }

    @Override
    public double recall(Map<List<Feature>, List<Feature>> true2PredictedMap) {
        double truePos = 0;
        double falseNeg = 0;
        for(List<Feature> trueVal : true2PredictedMap.keySet()){
            List<Feature> predVal = true2PredictedMap.get(trueVal);
            for(int i=0; i<trueVal.size();i++){
                if(trueVal.get(i).equals(Feature.MEMBRANE)){
                    if(predVal.get(i).equals(Feature.MEMBRANE)){
                        truePos++;
                    }
                    else{
                        falseNeg++;
                    }
                }

            }
        }
        if(truePos==0) return 0;
        return (truePos)/(truePos+falseNeg);

    }

    @Override
    public double fOneMeasure(Map<List<Feature>, List<Feature>> true2PredictedMap) {
        double P = precision(true2PredictedMap);
        double R = recall(true2PredictedMap);
        if(P==0||R==0) return 0;
        return(2*P*R)/(P+R);

    }
}
