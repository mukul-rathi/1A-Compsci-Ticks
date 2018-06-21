package uk.ac.cam.cl.msr45.exercises;

import uk.ac.cam.cl.mlrd.exercises.markov_models.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class Exercise8 implements IExercise8 {
    @Override
    public List<DiceType> viterbi(HiddenMarkovModel<DiceRoll, DiceType> model, List<DiceRoll> observedSequence) {
        List<Map<DiceType, DiceType>> psi = new ArrayList<Map<DiceType, DiceType>>();
        List<Map<DiceType, Double>> delta = new ArrayList<Map<DiceType, Double>>();
        for(int i=0; i<observedSequence.size();i++) {
            Map<DiceType, Double> deltaT = new HashMap<DiceType, Double>();
            Map<DiceType, DiceType> psiT = new HashMap<DiceType, DiceType>();
            for(DiceType currentState : DiceType.values()) {
                double maxProb =0;
                DiceType maxPrevState =null;
                for (DiceType prevState : DiceType.values()) {

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
        DiceType maxEndState = null;
        double maxEndProb =0;
        for(DiceType currentState : DiceType.values()){
            double currentEndProb = delta.get(delta.size()-1).get(currentState);
            if(maxEndState==null || currentEndProb>maxEndProb){
                maxEndState = currentState;
                maxEndProb = currentEndProb;
            }
        }
        List<DiceType> hiddenStates = new ArrayList<DiceType>();
        hiddenStates.add(maxEndState);
        for(int i=1;i<observedSequence.size();i++){
            hiddenStates.add(psi.get(observedSequence.size()-i).get(hiddenStates.get(i-1)));
        }
         Collections.reverse(hiddenStates);
        return hiddenStates;
    }

    @Override
    public Map<List<DiceType>, List<DiceType>> predictAll(HiddenMarkovModel<DiceRoll, DiceType> model, List<Path> testFiles) throws IOException {
        Map<List<DiceType>, List<DiceType>> predictions = new HashMap<List<DiceType>, List<DiceType>>();
        for(Path file : testFiles) {
            HMMDataStore<DiceRoll, DiceType> hmmData = HMMDataStore.loadDiceFile(file);
            List<DiceType> trueVals = hmmData.hiddenSequence;
            List<DiceType> predVals = viterbi(model, hmmData.observedSequence);
            predictions.put(trueVals,predVals);

        }
        return predictions;
    }


    @Override
    public double precision(Map<List<DiceType>, List<DiceType>> true2PredictedMap) {
        double truePos = 0;
        double falsePos = 0;
        for(List<DiceType> trueVal : true2PredictedMap.keySet()){
            List<DiceType> predVal = true2PredictedMap.get(trueVal);
            for(int i=0; i<trueVal.size();i++){
                if(predVal.get(i).equals(DiceType.WEIGHTED)){
                    if(trueVal.get(i).equals(DiceType.WEIGHTED)){
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
    public double recall(Map<List<DiceType>, List<DiceType>> true2PredictedMap) {
        double truePos = 0;
        double falseNeg = 0;
        for(List<DiceType> trueVal : true2PredictedMap.keySet()){
            List<DiceType> predVal = true2PredictedMap.get(trueVal);
            for(int i=0; i<trueVal.size();i++){
                if(trueVal.get(i).equals(DiceType.WEIGHTED)){
                    if(predVal.get(i).equals(DiceType.WEIGHTED)){
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
    public double fOneMeasure(Map<List<DiceType>, List<DiceType>> true2PredictedMap) {
        double P = precision(true2PredictedMap);
        double R = recall(true2PredictedMap);
        if(P==0||R==0) return 0;
        return(2*P*R)/(P+R);

    }

/*
    public void crossValidate(List<Path> dataset) throws IOException {

        Collections.shuffle(dataset);
        ArrayList<List<Path>> folds = new ArrayList<List<Path>>();
        for (int i = 0; i < 10; i++) {
            folds.add(new LinkedList<Path>());
        }
        for (int i = 0; i < dataset.size(); i++) {
            folds.get(i % 10).add(dataset.get(i));

        }

        double PRF1[] = new double[3];
        for (int i = 0; i < 10; i++) {
            List<Path> trainingSet = new LinkedList<>();
            List<Path> testSet = new LinkedList<Path>();
            for (int k = 0; k < 10; k++) {
                if (k == i) {
                    testSet.addAll(folds.get(k));
                } else {
                    trainingSet.addAll(folds.get(k));

                }
            }
                IExercise7 implementation7 = (IExercise7) new Exercise7();
                HiddenMarkovModel<DiceRoll, DiceType> model = implementation7.estimateHMM(trainingSet);
                Map<List<DiceType>, List<DiceType>> true2PredictedMap = predictAll(model, testSet);
                PRF1[0]+=precision(true2PredictedMap)/10;
                PRF1[1]+=recall(true2PredictedMap)/10;
                PRF1[2]+=fOneMeasure(true2PredictedMap)/10;



        }
        System.out.println("Prediction precision:");
        System.out.println(PRF1[0]);
        System.out.println();

        System.out.println("Prediction recall:");
        System.out.println(PRF1[1]);
        System.out.println();

        System.out.println("Prediction fOneMeasure:");
        System.out.println(PRF1[2]);
        System.out.println();
    }
    */


}
