package uk.ac.cam.msr45.oop.tick3;

import java.io.IOException;
import java.io.*;
import java.net.*;
import java.util.*;

public class GameOfLife {
    private World mWorld;
    private PatternStore mStore;

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
                    mWorld.nextGeneration();
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
