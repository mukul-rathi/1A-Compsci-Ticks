package uk.ac.cam.msr45.oop.tick4;

import java.io.*;
import java.net.*;
import java.util.*;

public class PatternStore {

    private List<Pattern> mPatterns = new LinkedList<>();
    //Map Authors' names to a list of Patterns
    private Map<String,List<Pattern>> mMapAuths = new HashMap<>();
    //Map the name to the Pattern
    private Map<String,Pattern> mMapName = new HashMap<>();

    public PatternStore(String source) throws IOException {
        if (source.startsWith("http://")) {
            loadFromURL(source);
        }
        else {
            loadFromDisk(source);
        }
    }

    public PatternStore(Reader source) throws IOException {
        load(source);
    }

    private void load(Reader r) throws IOException {
        BufferedReader b = new BufferedReader(r);
        String line = b.readLine();
        while ( line != null) {
            try{

                Pattern newPattern = new Pattern(line);
                mPatterns.add(newPattern);


                String Author =newPattern.getAuthor();
                if(mMapAuths.containsKey(Author)) {
                    mMapAuths.get(Author).add(newPattern);
                }
                else{
                    List<Pattern> newAuthorList = new LinkedList<>();
                    newAuthorList.add(newPattern);
                    mMapAuths.put(Author,newAuthorList);

                }

                mMapName.put(newPattern.getName(),newPattern);

            }
            catch (PatternFormatException e) {
                System.out.println(line);
            }
            line=b.readLine();
        }
    }


    private void loadFromURL(String url) throws IOException {
        URL destination = new URL(url);
        URLConnection conn = destination.openConnection();
        Reader r = new java.io.InputStreamReader(conn.getInputStream());
        load(r);

    }

    private void loadFromDisk(String filename) throws IOException {
        Reader r = new FileReader(filename);
        load(r);
    }

    public static void main(String args[]) throws IOException{
        PatternStore p = new PatternStore(args[0]);
    }

    public List<Pattern> getPatternsNameSorted() {
        List<Pattern> copy = new LinkedList<Pattern>(mPatterns);
        Collections.sort(copy);
        return copy;
    }

    public List<Pattern> getPatternsAuthorSorted() {
        List<Pattern> copy = new LinkedList<Pattern>(mPatterns);
        Collections.sort(copy, new Comparator<Pattern>() {
            public int compare(Pattern p1, Pattern p2) {
                int compareVal = (p1.getAuthor()).compareTo(p2.getAuthor());
                if (compareVal!=0){
                    return compareVal;
                }
                else{
                    return (p1.getName()).compareTo(p2.getName());
                }
            }
        });
        return copy;
    }

    public List<Pattern> getPatternsByAuthor(String author) throws PatternNotFound {

        if (mMapAuths.containsKey(author)) {
            List<Pattern> copy = new LinkedList<Pattern>(mMapAuths.get(author));
            Collections.sort(copy);
            return copy;
        }
        else {
            throw new PatternNotFound();
        }
    }

    public Pattern getPatternByName(String name) throws PatternNotFound {
        if (mMapName.containsKey(name)) {
            return mMapName.get(name);
        }
        else {
            throw new PatternNotFound();
        }

    }

    public List<String> getPatternAuthors() {
        List<String> l = new LinkedList<String>(mMapAuths.keySet());
        Collections.sort(l);
        return l;
    }

    public List<String> getPatternNames() {
        List<String> l = new LinkedList<String>(mMapName.keySet());
        Collections.sort(l);
        return l;
    }
}