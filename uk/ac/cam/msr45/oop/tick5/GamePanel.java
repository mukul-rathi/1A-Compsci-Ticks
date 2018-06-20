package uk.ac.cam.msr45.oop.tick5;

import java.awt.Color;
import javax.swing.JPanel;

public class GamePanel extends JPanel {

    private World mWorld = null;

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        // Paint the background white
        g.setColor(java.awt.Color.WHITE);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        if (mWorld!=null) {
            float squareWidth = (float)this.getWidth()/(float)mWorld.getWidth();
            float squareHeight = (float)this.getHeight()/(float)mWorld.getHeight();
            float squareSize = (squareHeight<=squareWidth) ? squareHeight : squareWidth;
            for(int i =0; i<mWorld.getWidth(); i++){
                for (int j=0; j<mWorld.getHeight();j++){
                    int squareX = Math.round(i*squareSize);
                    int squareY = Math.round(j*squareSize);
                    int xLength = Math.round((i+1)*squareSize) - Math.round(i*squareSize);
                    int yLength = Math.round((j+1)*squareSize) - Math.round(j*squareSize);;
                    g.setColor(Color.LIGHT_GRAY);
                    g.drawRect(squareX, squareY, xLength, yLength);
                    if(mWorld.getCell(i,j)==true){
                        g.setColor(Color.BLACK);
                        g.fillRect(squareX, squareY, xLength, yLength);
                    }
                }
            }
            g.setColor(Color.BLACK);
            g.drawString("Generation: " + mWorld.getGenerationCount(), 6, this.getHeight()-10);
        }
    }

    public void display(World w) {
        mWorld = w;
        repaint();
    }
}