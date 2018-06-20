package uk.ac.cam.msr45.oop.tick1;

public class ArrayLife {
	
	private boolean[][] mWorld;
	private int mWidth;
	private int mHeight;
	private Pattern mPattern;
	
	public ArrayLife(String format){
		//format is of form NAME:AUTHOR:WIDTH:HEIGHT:STARTUPPERCOL:STARTUPPERROW:CELLS
		mPattern = new Pattern(format);
		mWidth = mPattern.getWidth();
		mHeight = mPattern.getHeight();
		mWorld = new boolean[mHeight][mWidth]; //By default all values in mWorld = false
		mPattern.initialise(mWorld);
				
	
	}
	
	public boolean getCell(int col, int row){
		if (row < 0 || row >= mHeight) return false;
   		if (col < 0 || col >= mWidth) return false;

   		return mWorld[row][col];

	}

	public void setCell(int col, int row, boolean value){
		if( row>=0 && row< mHeight && col >=0 && col< mWidth){
			mWorld[row][col] = value;
		}
	}
	
	
	public void print() { 
   		for (int row = 0; row < mHeight; row++) { 
      		for (int col = 0; col < mWidth; col++) {
         		System.out.print(getCell(col, row) ? "#" : "_"); 
      		}
      		System.out.println(); 
   		} 
	}
	private int countNeighbours(int col, int row){
		int count = 0;
		count+= getCell(col-1, row-1) ? 1 : 0;
		count+= getCell(col-1, row) ? 1 : 0;
		count+= getCell(col-1, row+1) ? 1 : 0;
		count+= getCell(col, row-1) ? 1 : 0;
		count+= getCell(col, row+1) ? 1 : 0;
		count+= getCell(col+1, row-1) ? 1 : 0;
		count+= getCell(col+1, row) ? 1 : 0;
		count+= getCell(col+1, row+1) ? 1 : 0;
	 	return count;
	 }
	private boolean computeCell(int col, int row){

		// liveCell is true if the cell at position (col,row) in world is live
		boolean liveCell = getCell(col, row);
    
   		// neighbours is the number of live neighbours to cell (col,row)
   		int neighbours = countNeighbours(col, row);
   	
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

	public void nextGeneration() {
        boolean[][] nextGeneration = new boolean[mWorld.length][];
        for (int y = 0; y < mWorld.length; ++y) {
            nextGeneration[y] = new boolean[mWorld[y].length];
            for (int x = 0; x < mWorld[y].length; ++x) {
                boolean nextCell = computeCell(x, y);
                nextGeneration[y][x]=nextCell;
            }
        }
        mWorld = nextGeneration;
    }
    
	public void play() throws java.io.IOException {
		int userResponse = 0;
		while (userResponse != 'q') {
			print();
			userResponse = System.in.read();
			nextGeneration();
   		}
	}

	public static void main(String[] args) throws Exception {
  		ArrayLife al = new ArrayLife(args[0]);
  		al.play();
	}
}


