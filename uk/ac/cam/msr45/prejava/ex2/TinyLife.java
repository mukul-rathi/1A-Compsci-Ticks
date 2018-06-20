package uk.ac.cam.msr45.prejava.ex2;
import java.util.Scanner;

public class TinyLife {
	public static boolean getCell(long world, int col, int row){
		if(col>=0 && col<=7 && row>=0 && row<=7){ //check if col & row are in range
			int bitPosition = col + 8*row;
			return PackedLong.get(world,bitPosition);
		
		}
			else return false;

	}

	public static long setCell(long world, int col, int row, boolean value){
		if(col>=0 && col<=7 && row>=0 && row<=7){ //check if col & row are in range
			int bitPosition = col + 8*row;
			return PackedLong.set(world,bitPosition,value);	
		}
		else return world;
	}
	
	
	public static void print(long world) { 
   		System.out.println("-"); 
   		for (int row = 0; row < 8; row++) { 
      		for (int col = 0; col < 8; col++) {
         		System.out.print(getCell(world, col, row) ? "#" : "_"); 
      		}
      		System.out.println(); 
   		} 
	}
	public static int countNeighbours(long world, int col, int row){
		int count = 0;
		count+= getCell(world, col-1, row-1) ? 1 : 0;
		count+= getCell(world, col-1, row) ? 1 : 0;
		count+= getCell(world, col-1, row+1) ? 1 : 0;
		count+= getCell(world, col, row-1) ? 1 : 0;
		count+= getCell(world, col, row+1) ? 1 : 0;
		count+= getCell(world, col+1, row-1) ? 1 : 0;
		count+= getCell(world, col+1, row) ? 1 : 0;
		count+= getCell(world, col+1, row+1) ? 1 : 0;
	 	return count;
	 }
	public static boolean computeCell(long world,int col,int row) {

		// liveCell is true if the cell at position (col,row) in world is live
		boolean liveCell = getCell(world, col, row);
    
   		// neighbours is the number of live neighbours to cell (col,row)
   		int neighbours = countNeighbours(world, col, row);
   	
   		// we will return this value at the end of the method to indicate whether 
   		// cell (col,row) should be live in the next generation
   		boolean nextCell = false;
   		
   		//A live cell with less than two neighbours dies (underpopulation)
   		if (neighbours < 2) {
    		nextCell = false;
 	   }
    
    	//A live cell with two or three neighbours lives (a balanced population)
    	//TODO: write a if statement to check neighbours and update nextCell
    	if (liveCell && (neighbours == 2 || neighbours == 3)){
    		nextCell = true;
 	   }
    
    	//A live cell with with more than three neighbours dies (overcrowding)
    	//TODO: write a if statement to check neighbours and update nextCell
    	if (liveCell && (neighbours>3)){
    		nextCell = false;
 	   }
    	//A dead cell with exactly three live neighbours comes alive
    	//TODO: write a if statement to check neighbours and update nextCell
    	if (!(liveCell) && (neighbours==3)){
    		nextCell = true;
 	   }
 	   return nextCell;
    }

	public static long nextGeneration(long world){
		long newWorld= world;
		for (int row =0; row<8; row++){
			for (int col=0; col<8; col++){
				newWorld = setCell(newWorld,col,row,computeCell(world,col,row));	
			}
		}		   
	   return newWorld;
	}
	public static void play(long world) throws java.io.IOException {
		int userResponse = 0;
		while (userResponse != 'q') {
			print(world);
			userResponse = System.in.read();
			world = nextGeneration(world);
   		}
	}
	public static void main(String[] args) throws java.io.IOException {
		play(Long.decode(args[0]));
	}

}


