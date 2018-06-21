package uk.ac.cam.cl.msr45.exercises;

import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.IExercise5;
import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.Sentiment;
import uk.ac.cam.cl.msr45.exercises.Exercise2;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.*;

public class Exercise5 implements IExercise5 {
    @Override
    public List<Map<Path, Sentiment>> splitCVRandom(Map<Path, Sentiment> dataSet, int seed) {
        ArrayList<Path> keys = new ArrayList<>(dataSet.keySet());
        Collections.shuffle(keys,new Random(seed));
        ArrayList<Map<Path, Sentiment>> random =new ArrayList<Map<Path, Sentiment>>();
        for(int i=0;i<10;i++){
                random.add(new HashMap<Path, Sentiment>());
        }
        for(int i=0;i<keys.size();i++){
            random.get(i%10).put(keys.get(i),dataSet.get(keys.get(i)));

        }
        return random;
    }

    @Override
    public List<Map<Path, Sentiment>> splitCVStratifiedRandom(Map<Path, Sentiment> dataSet, int seed) {
        ArrayList<Path> posKeys = new ArrayList<>();
        ArrayList<Path> negKeys = new ArrayList<>();
        for (Path p : dataSet.keySet()){
            if(dataSet.get(p).equals(Sentiment.POSITIVE)){
                posKeys.add(p);
            }
            else{
                negKeys.add(p);
            }
        }
        Collections.shuffle(posKeys,new Random(seed));
        Collections.shuffle(negKeys,new Random(seed));

        ArrayList<Map<Path, Sentiment>> stratRandom =new ArrayList<Map<Path, Sentiment>>();
        for(int i=0;i<10;i++){
            stratRandom.add(new HashMap<Path, Sentiment>());
        }
        for(int i=0; i<posKeys.size() && i<negKeys.size();i++) {
            stratRandom.get(i%10).put(posKeys.get(i),dataSet.get(posKeys.get(i)));
            stratRandom.get(i%10).put(negKeys.get(i),dataSet.get(negKeys.get(i)));

        }
        return stratRandom;
    }

    @Override
    public double[] crossValidate(List<Map<Path, Sentiment>> folds) throws IOException {
        double ans[] = new double[10];
        Exercise2 classifier = new Exercise2();
        for(int i=0;i<10;i++){
            HashMap<Path, Sentiment> trainingSet = new HashMap<Path, Sentiment>();
            HashMap<Path, Sentiment> testSet = new HashMap<Path, Sentiment>();
            for(int k=0;k<10;k++){
                if(k==i){
                    testSet.putAll(folds.get(k));
                }
                else{
                    trainingSet.putAll(folds.get(k));

                }

            }
            Map<Sentiment, Double> classProbabilities = classifier.calculateClassProbabilities(trainingSet);
            Map<String, Map<Sentiment, Double>> tokenLogProbs = classifier.calculateSmoothedLogProbs(trainingSet);
            Map<Path, Sentiment> predictions = classifier.naiveBayes(testSet.keySet(), tokenLogProbs, classProbabilities);
            ans[i] = new Exercise1().calculateAccuracy(testSet,predictions);

        }
        return ans;
    }

    @Override
    public double cvAccuracy(double[] scores) {
        double sum = 0;
        for(double s : scores){
            sum+=s;
        }
        sum/=scores.length;
        return sum;
    }

    @Override
    public double cvVariance(double[] scores) {
        double sum = 0;
        double mean = cvAccuracy(scores);
        for(double s: scores){
            sum+=Math.pow((s-mean),2);
        }
        sum/= ( scores.length);
        return sum;
    }
}
