package uk.ac.cam.msr45.oop.tick4;

import java.io.IOException;
import java.io.*;
import java.net.*;
import java.util.*;

public class GameOfLife {
    private World mWorld;
    private PatternStore mStore;
    private ArrayList<World> mCachedWorlds = new ArrayList<World>();

    public GameOfLife(World w){
        mWorld = w;
    }

    public GameOfLife(PatternStore ps){
        mStore = ps;
    }

    public void play() throws IOException {

        String response="";
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Please select a pattern to play (l to list:");
        while (!response.equals("q")) {
            response = in.readLine();
            System.out.println(response);
            if (response.equals("f")) {
                if (mWorld==null) System.out.println("Please select a pattern to play (l to list):");
                else {
                    if(mWorld.getGenerationCount()<(mCachedWorlds.size()-1)){
                        mWorld = mCachedWorlds.get(mWorld.getGenerationCount()+1);
                        print();
                    }
                    else {
                        mWorld = copyWorld(true);
                        mWorld.nextGeneration();
                        mCachedWorlds.add(mWorld);
                        print();
                    }
                }
            }
            else if (response.equals("b")) {
                if (mWorld.getGenerationCount()==0) {
                    print();
                }
                else{
                    mWorld = mCachedWorlds.get(mWorld.getGenerationCount()-1);
                    print();
                }
            }
            else if (response.equals("l")) {
                List<Pattern> names = mStore.getPatternsNameSorted();
                int i=0;
                for (Pattern p : names) {
                    System.out.println(i+" "+p.getName()+"  ("+p.getAuthor()+")");
                    i++;
                }
            }
            else if (response.startsWith("p")) {
                List<Pattern> names = mStore.getPatternsNameSorted();
                String[] input = response.split(" ");
                int patternNumber = Integer.parseInt(input[1]);
                Pattern p = names.get(patternNumber);
                if(p.getWidth()*p.getHeight()>64){
                    mWorld = new ArrayWorld(p);
                }
                else{
                    mWorld = new PackedWorld(p);
                }
                print();
                mCachedWorlds.add(mWorld);
            }

        }
    }

    public void print() {
        System.out.println("- " + mWorld.getGenerationCount());
        for (int row = 0; row < mWorld.getPattern().getHeight(); row++) {
            for (int col = 0; col < mWorld.getPattern().getWidth(); col++) {
                System.out.print(mWorld.getCell(col, row) ? "#" : "_");
            }
            System.out.println();
        }
    }

    private World copyWorld(boolean useCloning) {
        if(!useCloning) {
            if(mWorld instanceof ArrayWorld){
                ArrayWorld newWorld = new ArrayWorld((ArrayWorld)mWorld);
                return newWorld;
            }
            else {
                PackedWorld newWorld = new PackedWorld((PackedWorld)mWorld);
                return newWorld;
            }
        }
        else{
            return (World) mWorld.clone();
        }
    }

    public static void main(String args[]) throws IOException {

        if (args.length!=1) {
            System.out.println("Usage: java GameOfLife <path/url to store>");
            return;
        }

        try {
            PatternStore ps = new PatternStore(args[0]);
            GameOfLife gol = new GameOfLife(ps);
            gol.play();
        }
        catch (IOException ioe) {
            System.out.println("Failed to load pattern store");
        }


    }




}
