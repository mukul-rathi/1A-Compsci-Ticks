package uk.ac.cam.msr45.oop.tick3;

public class Pattern implements Comparable<Pattern> {

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
    public Pattern(String format) throws PatternFormatException {
       //format is of form NAME:AUTHOR:WIDTH:HEIGHT:STARTUPPERCOL:STARTUPPERROW:CELLS
       String[] inputInfo = format.split(":");
       if(inputInfo[0].equals("")){
           throw new PatternFormatException("Please specify a pattern.");
       }
       if(inputInfo.length != 7){
           throw new PatternFormatException("Invalid pattern format: Incorrect number " +
                   "of fields in pattern (found " + inputInfo.length + ").");
       }
       mName = inputInfo[0];
       mAuthor = inputInfo[1];
       try {
           mWidth = Integer.parseInt(inputInfo[2]);
       }
       catch (NumberFormatException e){
           throw new PatternFormatException("Invalid pattern format: Could not interpret " +
                   "the width field as a number ('"+ inputInfo[2]+"' given).)");
       }
        try {
            mHeight= Integer.parseInt(inputInfo[3]);
        }
        catch (NumberFormatException e){
            throw new PatternFormatException("Invalid pattern format: Could not interpret " +
                    "the height field as a number ('"+ inputInfo[3]+"' given).)");
        }
        try {
            mStartCol = Integer.parseInt(inputInfo[4]);
        }
        catch (NumberFormatException e){
            throw new PatternFormatException("Invalid pattern format: Could not interpret " +
                    "the startX field as a number ('"+ inputInfo[4]+"' given).)");
        }
        try {
            mStartRow = Integer.parseInt(inputInfo[5]);
        }
        catch (NumberFormatException e){
            throw new PatternFormatException("Invalid pattern format: Could not interpret " +
                    "the startY field as a number ('"+ inputInfo[5 ]+"' given.)");
        }
       mCells = inputInfo[6];
    }

    public void initialise(World world) throws PatternFormatException {
        String[] cellRowInfo = mCells.split(" ");
		char[][] cellInfo = new char[cellRowInfo.length][];
		for (int i = 0; i<cellRowInfo.length; i++){
			cellInfo[i] = cellRowInfo[i].toCharArray();
		}
		for(int row = 0; row < cellInfo.length; row++){
			for (int col=0; col < cellInfo[row].length; col++){
				if(cellInfo[row][col] == '1'){
					world.setCell(mStartCol+col,mStartRow + row, true);
				}
				else if(cellInfo[row][col]!= '0'){
				    throw new PatternFormatException("Invalid pattern format: " +
                            "Malformed pattern '" + mCells + "'.");
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

    @Override
    public int compareTo(Pattern o) { //sorts by Pattern Name
        return (this.mName.compareTo(o.getName()));
    }

}