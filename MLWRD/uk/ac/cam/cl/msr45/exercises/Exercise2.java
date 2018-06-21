package uk.ac.cam.cl.msr45.exercises;

import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.IExercise2;
import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.Sentiment;
import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.Tokenizer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Exercise2 implements IExercise2 {
    @Override
    public Map<Sentiment, Double> calculateClassProbabilities(Map<Path, Sentiment> trainingSet) throws IOException {
        HashMap<Sentiment,Double> classprobs = new HashMap<Sentiment,Double>();
        double numPos = 0;
        double numNeg = 0;
        double total = 0;
        for(Path p : trainingSet.keySet()){
            if (trainingSet.get(p) ==Sentiment.POSITIVE){
                numPos++;
            }
            else{
                numNeg++;
            }
            total++;
        }
        classprobs.put(Sentiment.POSITIVE,numPos/total);
        classprobs.put(Sentiment.NEGATIVE,numNeg/total);
        return classprobs;
    }
    public Map<String, Map<Sentiment, Double>> countOfWords(Map<Path, Sentiment> trainingSet) throws IOException {
        HashMap<String,Map<Sentiment,Double>> words = new HashMap<String,Map<Sentiment,Double>>();
        for (Path p: trainingSet.keySet()){
            List<String> docWords = Tokenizer.tokenize(p);
            for (String w: docWords){
                if(!words.containsKey(w)){
                    HashMap<Sentiment,Double> sentiments = new HashMap<Sentiment,Double>();
                    sentiments.put(Sentiment.POSITIVE,0.0);
                    sentiments.put(Sentiment.NEGATIVE,0.0);
                    words.put(w,sentiments);
                }
                Map<Sentiment, Double> wordSentiment = words.get(w);
                Sentiment sent = trainingSet.get(p);
                wordSentiment.put(sent, wordSentiment.get(sent)+1);
                words.put(w,wordSentiment);
            }
        }
        return words;
    }
    @Override
    public Map<String, Map<Sentiment, Double>> calculateUnsmoothedLogProbs(Map<Path, Sentiment> trainingSet) throws IOException {
        Map<String, Map<Sentiment, Double>> wordCount = countOfWords(trainingSet);
        double countPos = 0;
        double countNeg = 0;

        for (String w: wordCount.keySet()){
            Map<Sentiment, Double> wordSentiment = wordCount.get(w);
            countPos+= wordSentiment.get(Sentiment.POSITIVE);
            countNeg+= wordSentiment.get(Sentiment.NEGATIVE);
        }
        for (String w: wordCount.keySet()){
            Map<Sentiment, Double> wordSentiment = wordCount.get(w);
            wordSentiment.put(Sentiment.POSITIVE, Math.log(wordSentiment.get(Sentiment.POSITIVE)/countPos));
            wordSentiment.put(Sentiment.NEGATIVE, Math.log(wordSentiment.get(Sentiment.NEGATIVE)/countNeg));
            wordCount.put(w,wordSentiment);

        }

        return wordCount;
    }

    @Override
    public Map<String, Map<Sentiment, Double>> calculateSmoothedLogProbs(Map<Path, Sentiment> trainingSet) throws IOException {
        Map<String, Map<Sentiment, Double>> wordCount = countOfWords(trainingSet);
        double countPos = 0;
        double countNeg = 0;

        for (String w: wordCount.keySet()){
            Map<Sentiment, Double> wordSentiment = wordCount.get(w);
            wordSentiment.put(Sentiment.POSITIVE,wordSentiment.get(Sentiment.POSITIVE)+1);
            wordSentiment.put(Sentiment.NEGATIVE,wordSentiment.get(Sentiment.NEGATIVE)+1);
            countPos+= wordSentiment.get(Sentiment.POSITIVE);
            countNeg+= wordSentiment.get(Sentiment.NEGATIVE);
            wordCount.put(w,wordSentiment);
        }
        for (String w: wordCount.keySet()){
            Map<Sentiment, Double> wordSentiment = wordCount.get(w);
            wordSentiment.put(Sentiment.POSITIVE, Math.log(wordSentiment.get(Sentiment.POSITIVE)/countPos));
            wordSentiment.put(Sentiment.NEGATIVE, Math.log(wordSentiment.get(Sentiment.NEGATIVE)/countNeg));
            wordCount.put(w,wordSentiment);

        }

        return wordCount;
    }

    @Override
    public Map<Path, Sentiment> naiveBayes(Set<Path> testSet, Map<String, Map<Sentiment, Double>> tokenLogProbs, Map<Sentiment, Double> classProbabilities) throws IOException {
        HashMap<Path, Sentiment> predictions = new HashMap<Path, Sentiment>();
        for(Path p: testSet){
            HashMap<Sentiment, Double> logProbs = new HashMap<Sentiment, Double>();
            logProbs.put(Sentiment.POSITIVE,classProbabilities.get(Sentiment.POSITIVE));
            logProbs.put(Sentiment.NEGATIVE,classProbabilities.get(Sentiment.NEGATIVE));

            List<String> docWords = Tokenizer.tokenize(p);
            for (String w: docWords){
                if(tokenLogProbs.containsKey(w)) {
                    Map<Sentiment, Double> wordSentiment = tokenLogProbs.get(w);
                    logProbs.put(Sentiment.POSITIVE, logProbs.get(Sentiment.POSITIVE) + wordSentiment.get(Sentiment.POSITIVE));
                    logProbs.put(Sentiment.NEGATIVE, logProbs.get(Sentiment.NEGATIVE) + wordSentiment.get(Sentiment.NEGATIVE));
                }
            }

            Sentiment docSentiment = (logProbs.get(Sentiment.POSITIVE)>=logProbs.get(Sentiment.NEGATIVE)) ? Sentiment.POSITIVE : Sentiment.NEGATIVE;
            predictions.put(p,docSentiment);
        }
        return predictions;
    }
}
