package uk.ac.cam.msr45.oop.tick2;

public abstract class World {
    private int mGeneration;
    private Pattern mPattern;

    public World(String pattern){
        mPattern = new Pattern(pattern);
    }
    public int getWidth(){
        return mPattern.getWidth();
    }
    public int getHeight(){
        return mPattern.getHeight();
    }
    public int getGenerationCount(){
        return mGeneration;
    }
    protected void incrementGenerationCount(){
        mGeneration++;
    }

    Pattern getPattern(){
       return mPattern;
    }
    public abstract boolean getCell(int col, int row);
    public abstract void setCell(int col, int row, boolean val);
    protected abstract void nextGenerationImp1();
    public void nextGeneration(){
        nextGenerationImp1();
        incrementGenerationCount();
    }


    protected int countNeighbours(int col, int row){
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
    protected boolean computeCell(int col, int row){

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
        if (liveCell && (neighbours == 2 || neighbours == 3)){
            nextCell = true;
        }

        //A live cell with with more than three neighbours dies (overcrowding)
        if (liveCell && (neighbours>3)){
            nextCell = false;
        }
        //A dead cell with exactly three live neighbours comes alive
        if (!(liveCell) && (neighbours==3)){
            nextCell = true;
        }
        return nextCell;
    }


}