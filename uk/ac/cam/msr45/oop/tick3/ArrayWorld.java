package uk.ac.cam.msr45.oop.tick3;

public class ArrayWorld extends World {
	
	private boolean[][] mWorld;
	private int mWidth;
	private int mHeight;

	public ArrayWorld(String format) throws PatternFormatException{
		//format is of form NAME:AUTHOR:WIDTH:HEIGHT:STARTUPPERCOL:STARTUPPERROW:CELLS
		super(format);
		mWidth = getPattern().getWidth();
		mHeight = getPattern().getHeight();
		mWorld = new boolean[mHeight][mWidth]; //By default all values in mWorld = false
		getPattern().initialise(this);


	}
	public ArrayWorld(Pattern p){
		super(p);
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





	public void nextGenerationImp1() {
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

}


