package uk.ac.cam.cl.msr45.exercises;

import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.IExercise1;
import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.Sentiment;
import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.Tokenizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Exercise1 implements IExercise1 {


    @Override
    public Map<Path, Sentiment> simpleClassifier(Set<Path> testSet, Path lexiconFile) throws IOException {
        List<String> words = Files.readAllLines(lexiconFile);


        HashSet<String> posWords =new HashSet<String>();
        HashSet<String> negWords =new HashSet<String>();
        for(String s: words){
            String[] features = s.split(" ");
            String[] word = features[0].split("=");
            String[] polarity = features[2].split("=");
            if(polarity[1].equals("positive")){
                posWords.add(word[1]);
            }
            else{
                negWords.add(word[1]);
            }
        }

        HashMap<Path,Sentiment> pred = new HashMap<Path,Sentiment>();

        for(Path p : testSet){
            List<String> test = Tokenizer.tokenize(p);
            int sentiment = 0;
            for (String word : test){
                if(posWords.contains(word)){
                    sentiment++;
                }
                else if (negWords.contains(word)){
                    sentiment--;
                }
            }
            Sentiment s = (sentiment>=0)? Sentiment.POSITIVE : Sentiment.NEGATIVE;
            pred.put(p,s);
        }

        return pred;
    }

    @Override
    public double calculateAccuracy(Map<Path, Sentiment> trueSentiments, Map<Path, Sentiment> predictedSentiments) {
        double numCorrect = 0;
        double total = 0;
        for (Path p : trueSentiments.keySet()){
            if(trueSentiments.get(p)==predictedSentiments.get(p)){
                numCorrect++;
            }
            total++;

        }
        return numCorrect/total;
    }

    @Override
    public Map<Path, Sentiment> improvedClassifier(Set<Path> testSet, Path lexiconFile) throws IOException {
        double weightedVal = 5;
        int threshold = 30;


        List<String> words = Files.readAllLines(lexiconFile);

        HashMap<String,Double> posWords =new HashMap<String,Double>();
        HashMap<String,Double> negWords =new HashMap<String,Double>();
        for(String s: words){
            String[] features = s.split(" ");
            String[] word = features[0].split("=");
            String[] intensity = features[1].split("=");
            double weight = 1;
            if (intensity[1].equals("strong")){
                weight = weightedVal;
            }
            String[] polarity = features[2].split("=");
            if(polarity[1].equals("positive")){
                posWords.put(word[1],new Double(weight));
            }
            else{
                negWords.put(word[1],new Double(weight));
            }
        }

        HashMap<Path,Sentiment> pred = new HashMap<Path,Sentiment>();

        for(Path p : testSet){
            List<String> test = Tokenizer.tokenize(p);
            int sentiment = 0;
            for (String word : test){
                if(posWords.containsKey(word)){
                    sentiment+=posWords.get(word);
                }
                else if (negWords.containsKey(word)){
                    sentiment-=negWords.get(word);
                }
            }
            Sentiment s = (sentiment>=threshold)? Sentiment.POSITIVE : Sentiment.NEGATIVE;
            pred.put(p,s);
        }

        return pred;
    }
}
