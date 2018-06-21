package uk.ac.cam.cl.msr45.exercises;

import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.IExercise6;
import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.NuancedSentiment;
import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.Sentiment;
import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.Tokenizer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class Exercise6 implements IExercise6 {
    @Override
    public Map<NuancedSentiment, Double> calculateClassProbabilities(Map<Path, NuancedSentiment> trainingSet) throws IOException {
        HashMap<NuancedSentiment,Double> classprobs = new HashMap<NuancedSentiment,Double>();
        double numPos = 0;
        double numNeg = 0;
        double numNeutral = 0;
        double total = 0;
        for(Path p : trainingSet.keySet()){
            if (trainingSet.get(p) ==NuancedSentiment.POSITIVE){
                numPos++;
            }
            else if(trainingSet.get(p) ==NuancedSentiment.NEGATIVE){
                numNeg++;
            }
            else{
                numNeutral++;
            }
            total++;
        }
        classprobs.put(NuancedSentiment.POSITIVE,numPos/total);
        classprobs.put(NuancedSentiment.NEGATIVE,numNeg/total);
        classprobs.put(NuancedSentiment.NEUTRAL,numNeutral/total);

        return classprobs;
    }
    public Map<String, Map<NuancedSentiment, Double>> countOfWords(Map<Path, NuancedSentiment> trainingSet) throws IOException {
        HashMap<String,Map<NuancedSentiment,Double>> words = new HashMap<String,Map<NuancedSentiment,Double>>();
        for (Path p: trainingSet.keySet()){
            List<String> docWords = Tokenizer.tokenize(p);
            for (String w: docWords){
                if(!words.containsKey(w)){
                    HashMap<NuancedSentiment,Double> sentiments = new HashMap<NuancedSentiment,Double>();
                    sentiments.put(NuancedSentiment.POSITIVE,0.0);
                    sentiments.put(NuancedSentiment.NEGATIVE,0.0);
                    sentiments.put(NuancedSentiment.NEUTRAL,0.0);
                    words.put(w,sentiments);
                }
                Map<NuancedSentiment, Double> wordSentiment = words.get(w);
                NuancedSentiment sent = trainingSet.get(p);
                wordSentiment.put(sent, wordSentiment.get(sent)+1);
                words.put(w,wordSentiment);
            }
        }
        return words;
    }
    @Override
    public Map<String, Map<NuancedSentiment, Double>> calculateNuancedLogProbs(Map<Path, NuancedSentiment> trainingSet) throws IOException {
        Map<String, Map<NuancedSentiment, Double>> wordCount = countOfWords(trainingSet);
        double countPos = 0;
        double countNeg = 0;
        double countNeutral =0;
        double omega = 1;

        for (String w : wordCount.keySet()) {
            Map<NuancedSentiment, Double> wordSentiment = wordCount.get(w);
            wordSentiment.put(NuancedSentiment.POSITIVE, wordSentiment.get(NuancedSentiment.POSITIVE) + omega);
            wordSentiment.put(NuancedSentiment.NEGATIVE, wordSentiment.get(NuancedSentiment.NEGATIVE) + omega);
            wordSentiment.put(NuancedSentiment.NEUTRAL, wordSentiment.get(NuancedSentiment.NEUTRAL) + omega);

            countPos += wordSentiment.get(NuancedSentiment.POSITIVE);
            countNeg += wordSentiment.get(NuancedSentiment.NEGATIVE);
            countNeutral += wordSentiment.get(NuancedSentiment.NEUTRAL);
            wordCount.put(w, wordSentiment);
        }
        for (String w : wordCount.keySet()) {
            Map<NuancedSentiment, Double> wordSentiment = wordCount.get(w);
            wordSentiment.put(NuancedSentiment.POSITIVE, Math.log(wordSentiment.get(NuancedSentiment.POSITIVE) / countPos));
            wordSentiment.put(NuancedSentiment.NEGATIVE, Math.log(wordSentiment.get(NuancedSentiment.NEGATIVE) / countNeg));
            wordSentiment.put(NuancedSentiment.NEUTRAL, Math.log(wordSentiment.get(NuancedSentiment.NEUTRAL) / countNeutral));
            wordCount.put(w, wordSentiment);

        }

        return wordCount;
    }

    @Override
    public Map<Path, NuancedSentiment> nuancedClassifier(Set<Path> testSet, Map<String, Map<NuancedSentiment, Double>> tokenLogProbs, Map<NuancedSentiment, Double> classProbabilities) throws IOException {
        HashMap<Path, NuancedSentiment> predictions = new HashMap<Path, NuancedSentiment>();
        for(Path p: testSet){
            HashMap<NuancedSentiment, Double> logProbs = new HashMap<NuancedSentiment, Double>();
            logProbs.put(NuancedSentiment.POSITIVE,classProbabilities.get(NuancedSentiment.POSITIVE));
            logProbs.put(NuancedSentiment.NEGATIVE,classProbabilities.get(NuancedSentiment.NEGATIVE));
            logProbs.put(NuancedSentiment.NEUTRAL,classProbabilities.get(NuancedSentiment.NEUTRAL));


            List<String> docWords = Tokenizer.tokenize(p);
            for (String w: docWords){
                if(tokenLogProbs.containsKey(w)) {
                    Map<NuancedSentiment, Double> wordSentiment = tokenLogProbs.get(w);
                    logProbs.put(NuancedSentiment.POSITIVE, logProbs.get(NuancedSentiment.POSITIVE) + wordSentiment.get(NuancedSentiment.POSITIVE));
                    logProbs.put(NuancedSentiment.NEGATIVE, logProbs.get(NuancedSentiment.NEGATIVE) + wordSentiment.get(NuancedSentiment.NEGATIVE));
                    logProbs.put(NuancedSentiment.NEUTRAL, logProbs.get(NuancedSentiment.NEUTRAL) + wordSentiment.get(NuancedSentiment.NEUTRAL));

                }
            }
            NuancedSentiment docSentiment;
            if((logProbs.get(NuancedSentiment.POSITIVE) == logProbs.get(NuancedSentiment.NEGATIVE))||(logProbs.get(NuancedSentiment.NEUTRAL) >= logProbs.get(NuancedSentiment.POSITIVE) && logProbs.get(NuancedSentiment.NEUTRAL) >= logProbs.get(NuancedSentiment.NEGATIVE))) {
                 docSentiment = NuancedSentiment.NEUTRAL;
            }
            else{
                 docSentiment = (logProbs.get(NuancedSentiment.POSITIVE) > logProbs.get(NuancedSentiment.NEGATIVE)) ? NuancedSentiment.POSITIVE : NuancedSentiment.NEGATIVE;
            }
            predictions.put(p,docSentiment);
        }
        return predictions;
    }


    @Override
    public double nuancedAccuracy(Map<Path, NuancedSentiment> trueSentiments, Map<Path, NuancedSentiment> predictedSentiments) {
        double numCorrect = 0;
        double total = 0;
        for (Path p : trueSentiments.keySet()){
            if(trueSentiments.get(p).equals(predictedSentiments.get(p))){
                numCorrect++;
            }
            total++;

        }
        return numCorrect/total;
    }

    @Override
    public Map<Integer, Map<Sentiment, Integer>> agreementTable(Collection<Map<Integer, Sentiment>> predictedSentiments) {
        Map<Integer, Map<Sentiment, Integer>> table = new HashMap<Integer, Map<Sentiment, Integer>>();
        for(Map<Integer, Sentiment> person : predictedSentiments){
            for(Integer review : person.keySet()) {
                if(!table.containsKey(review)){
                    Map<Sentiment, Integer> sents = new HashMap<Sentiment, Integer>();
                    sents.put(Sentiment.POSITIVE,0);
                    sents.put(Sentiment.NEGATIVE,0);
                    table.put(review,sents);
                }
                Map<Sentiment, Integer> sent = table.get(review);
                sent.put(person.get(review),sent.get(person.get(review))+1);
            }
        }
        return table;
    }

    @Override
    public double kappa(Map<Integer, Map<Sentiment, Integer>> agreementTable) {
        double Pa=0;
        double Pe=0;

        int N = agreementTable.keySet().size(); //num of reviews
        int k = 2; //Pos, Neg
        double[][] n = new double[N][k]; //matrix of n_ij s
        int reviewnum =0;
        for(Integer review : agreementTable.keySet()) {
            for (Sentiment reviewSent : agreementTable.get(review).keySet()) {
                n[reviewnum][reviewSent.ordinal()] = agreementTable.get(review).get(reviewSent);
            }
            reviewnum++;
        }
        double[] nprime = new double[N];
        for(int i=0;i<N;i++){
            for(int j=0;j<k;j++){
            nprime[i]+=n[i][j];
            }
        }
        for(int j=0;j<k;j++){
            double temp=0;
            for(int i=0;i<N;i++){
                temp+= n[i][j]/nprime[i];
            }
            temp/= (double) N;
            Pe+= Math.pow(temp,2);
        }
        for(int i=0;i<N;i++){
            double temp=0;
            for(int j=0;j<k;j++){
                temp+= n[i][j]*(n[i][j]-1);
            }
            temp/=(nprime[i]*(nprime[i]-1));
            Pa+=temp;
        }
        Pa/= (double) N;
        System.out.println("Pa " + Pa);
        System.out.println("Pe " + Pe);

        double kappa = (Pa-Pe)/(1-Pe);

        return kappa;
    }
}
