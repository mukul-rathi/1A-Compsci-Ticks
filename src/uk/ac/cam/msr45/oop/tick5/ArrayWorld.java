package uk.ac.cam.msr45.oop.tick5;

public class ArrayWorld extends World implements Cloneable{
	
	private boolean[][] mWorld;
	private int mWidth;
	private int mHeight;
	private boolean[] mDeadRow;

	public ArrayWorld(String format) throws PatternFormatException {
		//format is of form NAME:AUTHOR:WIDTH:HEIGHT:STARTUPPERCOL:STARTUPPERROW:CELLS
		super(format);
		mWidth = getPattern().getWidth();
		mHeight = getPattern().getHeight();
		mWorld = new boolean[mHeight][mWidth]; //By default all values in mWorld = false
		getPattern().initialise(this);
		mDeadRow = new boolean[mWidth];
		setDeadRows();


	}

	public ArrayWorld(Pattern p){
		super(p);
		mWidth = getPattern().getWidth();
		mHeight = getPattern().getHeight();
		mWorld = new boolean[mHeight][mWidth]; //By default all values in mWorld = false
		try {
			p.initialise(this);
		} catch (PatternFormatException e) {
			e.printStackTrace();
		}
		mDeadRow = new boolean[p.getWidth()];
		setDeadRows();
	}
	private void setDeadRows() {
		for (int i = 0; i < mHeight; i++) {
			boolean isDead = true;
			for (int j = 0; j < mWidth; ++j) {
				if (mWorld[i][j] == true) {
					isDead = false;
				}
			}
			if (isDead) {
				mWorld[i] = mDeadRow;
			}
		}
	}
	public ArrayWorld(ArrayWorld aw){
		super(aw);
		mWidth = aw.mWidth;
		mHeight = aw.mHeight;
		mDeadRow = aw.mDeadRow;
		mWorld = new boolean[mHeight][mWidth];
		for (int i = 0; i < aw.mHeight; i++) {
			if (aw.mWorld[i] == aw.mDeadRow) {
				mWorld[i] = mDeadRow;
			} else {
				mWorld[i] = aw.mWorld[i].clone();
			}
		}

	}

	public Object clone(){
		ArrayWorld clonedWorld =(ArrayWorld) super.clone();
		clonedWorld.mWorld = new boolean[mHeight][mWidth];
		for(int i=0;i<mHeight;i++){
			if(mWorld[i]== mDeadRow){
				clonedWorld.mWorld[i] = mDeadRow;
			}
			for(int j=0;j<mWidth;j++){
				clonedWorld.mWorld[i][j] = mWorld[i][j];
			}
		}
		return clonedWorld;

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


