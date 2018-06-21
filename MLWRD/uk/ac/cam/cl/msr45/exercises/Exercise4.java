package uk.ac.cam.cl.msr45.exercises;

import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.IExercise4;
import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.Sentiment;
import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.Tokenizer;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Exercise4 implements IExercise4 {
    @Override
    public Map<Path, Sentiment> magnitudeClassifier(Set<Path> testSet, Path lexiconFile) throws IOException {
        double weightedVal = 2;
        int threshold = 0;


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
    static BigDecimal nCk(int N, int K) {
        BigDecimal ans = BigDecimal.ONE;
        for (int i =0; i<K; i++) {
            ans = ans.multiply(BigDecimal.valueOf(N-i)).divide(BigDecimal.valueOf(i+1));
        }
        return ans;
    }


    @Override
    public double signTest(Map<Path, Sentiment> actualSentiments, Map<Path, Sentiment> classificationA, Map<Path, Sentiment> classificationB) {
        int Plus =0;
        int Minus = 0;
        int Null = 0;
        for (Path p: actualSentiments.keySet()){
            if(classificationA.get(p).equals(classificationB.get(p))){
                Null++;
            }
            else if(classificationA.get(p).equals(actualSentiments.get(p))){
                Plus++;
            }
            else {
                Minus++;
            }
        }
        int n = Null + Null%2 + Plus + Minus;
        int k = (Null + Null%2)/2 + Math.min(Plus,Minus);
        BigDecimal result = BigDecimal.ZERO;
        for(int i=0;i<=k;i++){
                result = result.add(nCk(n,i));
        }
        BigInteger x = BigInteger.ONE.shiftLeft(n-1);
        result =result.divide(new BigDecimal(x));
        return result.doubleValue();
    }


}
