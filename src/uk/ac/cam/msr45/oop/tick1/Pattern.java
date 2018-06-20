package uk.ac.cam.msr45.oop.tick1;

public class Pattern {

    private String mName;
    private String mAuthor;
    private int mWidth;
    private int mHeight;
    private int mStartCol;
    private int mStartRow;
    private String mCells;
    
    
    public String getName() {
       return mName;
    }
    public String getAuthor() {
       return mAuthor;
    }
    public int getWidth() {
       return mWidth;
    }
    public int getHeight() {
       return mHeight;
    }
    public int getStartCol() {
       return mStartCol;
    }
    public int getStartRow() {
       return mStartRow;
    }
	public String getCells() {
       return mCells;
    }
    public Pattern(String format) {
       //format is of form NAME:AUTHOR:WIDTH:HEIGHT:STARTUPPERCOL:STARTUPPERROW:CELLS
       String[] inputInfo = format.split(":");
       mName = inputInfo[0];
       mAuthor = inputInfo[1];
       mWidth = Integer.parseInt(inputInfo[2]);
       mHeight= Integer.parseInt(inputInfo[3]);
       mStartCol = Integer.parseInt(inputInfo[4]);
       mStartRow = Integer.parseInt(inputInfo[5]);
       mCells = inputInfo[6];
    }

    public void initialise(boolean[][] world) {
       //TODO: update the values in the 2D array representing the state of
       //      'world' as expressed by the contents of the field 'mCells'.
        String[] cellRowInfo = mCells.split(" ");
		char[][] cellInfo = new char[cellRowInfo.length][];
		for (int i = 0; i<cellRowInfo.length; i++){
			cellInfo[i] = cellRowInfo[i].toCharArray();
		}
		for(int row = 0; row < cellInfo.length; row++){
			for (int col=0; col < cellInfo[row].length; col++){
				if(cellInfo[row][col] == '1'){
					world[mStartRow+row][mStartCol + col]= true;
				}
			
			}		
		}
		
    }
    
    public void print() {
    	System.out.println("Name: " + mName);
    	System.out.println("Author: " + mAuthor);
    	System.out.println("Width: " + Integer.toString(mWidth));
		System.out.println("Height: " + Integer.toString(mHeight)); 
		System.out.println("StartCol: " + Integer.toString(mStartCol));
		System.out.println("StartRow " + Integer.toString(mStartRow));  
		System.out.println("Pattern: " + mCells);
 	
    }
    
    public static void main(String[] args) throws Exception {
  		Pattern p = new Pattern(args[0]);
  		p.print();
	}
}