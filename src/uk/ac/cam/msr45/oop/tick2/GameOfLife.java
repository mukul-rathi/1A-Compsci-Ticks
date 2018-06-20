package uk.ac.cam.msr45.oop.tick2;
import java.io.IOException;

public class GameOfLife {
    private World mWorld;
    public GameOfLife(World w){
        mWorld = w;
    }

    public void play() throws IOException {
        int userResponse = 0;
        while (userResponse != 'q') {
            print();
            userResponse = System.in.read();
            mWorld.nextGeneration();
            }
    }

    public void print(){
        System.out.println("- " + mWorld.getGenerationCount());
        for (int row = 0; row < mWorld.getPattern().getHeight(); row++) {
            for (int col = 0; col < mWorld.getPattern().getWidth(); col++) {
                System.out.print(mWorld.getCell(col, row) ? "#" : "_");
            }
            System.out.println();
        }

    }
    public static void main(String[] args) throws IOException{
        World w = null;
        System.out.println(args[0]);
        System.out.println(args[1]);
        if(args.length == 1){
            w = new ArrayWorld(args[0]);
        }
        else {
            if (args[0].equals("--array")){
                w = new ArrayWorld(args[1]);
            }
            else if(args[0].equals("--packed")){
                w = new PackedWorld(args[1]);
            }
            else {
                throw new IOException("Input not valid");
            }
        }
        GameOfLife go1 = new GameOfLife(w);
        go1.play();

    }




}
