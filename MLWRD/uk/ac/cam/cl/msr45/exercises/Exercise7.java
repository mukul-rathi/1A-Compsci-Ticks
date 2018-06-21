package uk.ac.cam.cl.msr45.exercises;

import uk.ac.cam.cl.mlrd.exercises.markov_models.*;
import uk.ac.cam.cl.mlrd.exercises.markov_models.IExercise7;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class Exercise7 implements IExercise7 {
    private int mStates;

    @Override
    public HiddenMarkovModel<DiceRoll, DiceType> estimateHMM(Collection<Path> sequenceFiles) throws IOException {

        List<HMMDataStore<DiceRoll, DiceType>> trainData = HMMDataStore.loadDiceFiles(sequenceFiles);

        Map<DiceType, Map<DiceType, Double>> transitionMatrix = new HashMap<DiceType, Map<DiceType, Double>>();
        Map<DiceType, Map<DiceRoll, Double>> emissionMatrix =  new HashMap<DiceType, Map<DiceRoll, Double>>();

        //initialise transition matrix
        for(DiceType i: DiceType.values()){
            Map<DiceType, Double> transition  =new HashMap<DiceType, Double>();
            for(DiceType j: DiceType.values()){
                transition.put(j,0.0);
            }
            transitionMatrix.put(i, transition);
        }
        for(DiceType i: DiceType.values()){
            Map<DiceRoll, Double> transition  =new HashMap<DiceRoll, Double>();
            for(DiceRoll j: DiceRoll.values()){
                transition.put(j,0.0);
            }
            emissionMatrix.put(i, transition);
        }

        for(HMMDataStore<DiceRoll,DiceType> seq : trainData){
            for(int i=0; i<seq.hiddenSequence.size();i++){
                DiceType currentState = seq.hiddenSequence.get(i);
                if(i<(seq.hiddenSequence.size()-1)) {
                    //counts for transition matrix
                    DiceType nextState = seq.hiddenSequence.get(i + 1);


                    Map<DiceType, Double> nextTransition = transitionMatrix.get(currentState);
                    nextTransition.put(nextState, nextTransition.get(nextState) + 1);
                    transitionMatrix.put(currentState, nextTransition);
                }


                //counts for emission matrix
                DiceRoll currentRoll = seq.observedSequence.get(i);


                Map<DiceRoll, Double> nextEmission = emissionMatrix.get(currentState);
                nextEmission.put(currentRoll, nextEmission.get(currentRoll) + 1);
                emissionMatrix.put(currentState,nextEmission);

            }

        }


        for(DiceType i: transitionMatrix.keySet()){
            Map<DiceType, Double> nextTransition = transitionMatrix.get(i);
            double totalCount =0;
            for(DiceType j : nextTransition.keySet()){
                totalCount+=nextTransition.get(j);
            }
            if (totalCount!=0){
                for(DiceType j : nextTransition.keySet()){
                    nextTransition.put(j,nextTransition.get(j)/totalCount);
                }
            }
            transitionMatrix.put(i,nextTransition);

        }
        for(DiceType i: emissionMatrix.keySet()){
            Map<DiceRoll, Double> nextEmission = emissionMatrix.get(i);
            double totalCount =0;
            for(DiceRoll j : nextEmission.keySet()){
                totalCount+=nextEmission.get(j);
            }
            if (totalCount!=0){
                for(DiceRoll j : nextEmission.keySet()){
                    nextEmission.put(j,nextEmission.get(j)/totalCount);
                }
            }
            emissionMatrix.put(i,nextEmission);

        }







        return new HiddenMarkovModel<>(transitionMatrix,emissionMatrix);
    }

}
