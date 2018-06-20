package uk.ac.cam.msr45.oop.tick5;

import java.io.IOException;

public class PackedWorld extends World implements Cloneable{

    private long mWorld;

    public PackedWorld(String format) throws PatternFormatException,IOException{
        //format is of form NAME:AUTHOR:WIDTH:HEIGHT:STARTUPPERCOL:STARTUPPERROW:CELLS
        super(format);
        if (getPattern().getHeight()*getPattern().getWidth() >64) {
            throw new IOException("Error: World will not fit in long");
        }
        getPattern().initialise(this);


    }

    public PackedWorld(Pattern p){
        super(p);
        try {
            p.initialise(this);
        } catch (PatternFormatException e) {
            e.printStackTrace();
        }
    }

    public PackedWorld(PackedWorld p){
        super(p);
        mWorld = p.mWorld;

    }

    public Object clone(){
        PackedWorld clonedWorld = (PackedWorld) super.clone();
        return clonedWorld;
    }

    public int getBitPosition(int col, int row){
        return getPattern().getWidth()*row+col;
    }

    public boolean getCell( int col, int row) {
        if (row < 0 || row >= getPattern().getHeight()) return false;
        if (col < 0 || col >= getPattern().getWidth()) return false;
        // set "check" to equal 1 if the "position" bit in "packed" is set to 1
        // you should use bitwise operators (not % or similar)
        int position =getBitPosition(col, row);
        long check = (mWorld >> position)& 1;

        return (check == 1);
    }

    /*
     * Set the nth bit in the packed number to the value given
     * and return the new packed number
     */
    public void setCell(int col, int row, boolean value) {
        int position = getBitPosition(col, row);
        if (value) {
            mWorld|=(1L<<position);
            // update the value "packed" with the bit at "position" set to 1
        }
        else {
            mWorld&=~(1L<<position);

            // update the value "packed" with the bit a "position" set to 0
        }
    }
    public void nextGenerationImp1() {
        long newWorld =mWorld;
        for (int row = 0; row < getPattern().getHeight(); ++row) {
            for (int col = 0; col < getPattern().getWidth(); ++col) {
                boolean nextCell = computeCell(col, row);

                int position = getBitPosition(col, row);
                if (nextCell) {
                    newWorld|=(1L<<position);
                }
                else {
                    newWorld&=~(1L<<position);

                }

            }
        }

        mWorld = newWorld;
    }



}


