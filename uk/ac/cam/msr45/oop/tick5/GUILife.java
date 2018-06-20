package uk.ac.cam.msr45.oop.tick5;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.Timer;

public class GUILife extends JFrame implements ListSelectionListener {
    private World mWorld;
    private PatternStore mStore;
    private ArrayList<World> mCachedWorlds = new ArrayList<World>();
    private GamePanel mGamePanel;
    private JButton mPlayButton;
    private Timer mTimer;
    private boolean mPlaying;

    public GUILife(PatternStore ps) {
        super("Game of Life");
        mStore=ps;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1024,768);

        add(createPatternsPanel(),BorderLayout.WEST);
        add(createControlPanel(),BorderLayout.SOUTH);
        add(createGamePanel(),BorderLayout.CENTER);



    }

    private void addBorder(JComponent component, String title) {
        Border etch = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        Border tb = BorderFactory.createTitledBorder(etch,title);
        component.setBorder(tb);
    }

    private JPanel createGamePanel() {
        mGamePanel = new GamePanel();
        addBorder(mGamePanel,"Game Panel");
        return mGamePanel;
    }

    private JPanel createPatternsPanel() {
        JPanel patt = new JPanel();
        addBorder(patt,"Patterns");
        patt.setLayout(new GridLayout(1,1,0,0));
        List<Pattern> sortedPatterns = mStore.getPatternsNameSorted();
        JList<Pattern> patternJList = new JList<Pattern>(
                sortedPatterns.toArray(new Pattern[sortedPatterns.size()]));
        patternJList.addListSelectionListener(this);
        patt.add(new JScrollPane(patternJList));
        return patt;
    }

    private JPanel createControlPanel() {
        JPanel ctrl =  new JPanel();
        addBorder(ctrl,"Controls");
        ctrl.setLayout(new GridLayout(1,3,0,0));
        JButton back =new JButton("< Back");
        //back.addActionListener(e->{moveBack();});
        back.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(mPlaying){
                    runOrPause();
                }
                moveBack();
            }
        });
        ctrl.add(back);
        mPlayButton = new JButton("Play");
        mPlayButton.addActionListener(e->runOrPause());
        ctrl.add(mPlayButton);
        JButton forward =new JButton("Forward >");
        forward.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(mPlaying){
                    runOrPause();
                }
                moveForward();
            }
        });
        //forward.addActionListener(e->moveForward());
        ctrl.add(forward);
        return ctrl;
    }

    private World copyWorld(boolean useCloning) {
        if(!useCloning) {
            if(mWorld instanceof ArrayWorld){
                ArrayWorld newWorld = new ArrayWorld((ArrayWorld)mWorld);
                return newWorld;
            }
            else {
                PackedWorld newWorld = new PackedWorld((PackedWorld)mWorld);
                return newWorld;
            }
        }
        else{
            return (World) mWorld.clone();
        }
    }
    private void moveBack(){
        if (mWorld.getGenerationCount()!=0) {
            mWorld = mCachedWorlds.get(mWorld.getGenerationCount()-1);
        }
        mGamePanel.display(mWorld);

    }
    private void moveForward(){
        if (mWorld==null) System.out.println("Please select a pattern to play (l to list):");
        else {
            if(mWorld.getGenerationCount()<(mCachedWorlds.size()-1)){
                mWorld = mCachedWorlds.get(mWorld.getGenerationCount()+1);
            }
            else {
                mWorld = copyWorld(true);
                mWorld.nextGeneration();
                mCachedWorlds.add(mWorld);
            }
            mGamePanel.display(mWorld);

        }
    }
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(mPlaying){
            runOrPause();
        }
        JList<Pattern> list = (JList<Pattern>) e.getSource();
        Pattern p = list.getSelectedValue();
        if(p.getWidth()*p.getHeight()>64){
            mWorld = new ArrayWorld(p);
        }
        else{
            mWorld = new PackedWorld(p);
        }
        mCachedWorlds = new ArrayList<World>();
        mCachedWorlds.add(mWorld);
        mGamePanel.display(mWorld);

    }
    private void runOrPause() {
        if (mPlaying) {
            mTimer.cancel();
            mPlaying=false;
            mPlayButton.setText("Play");
        }
        else {
            mPlaying=true;
            mPlayButton.setText("Stop");
            mTimer = new Timer(true);
            mTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    moveForward();
                }
            }, 0, 500);
        }
    }


    public static void main(String[] args) throws IOException{
        PatternStore ps = new PatternStore("http://www.cl.cam.ac.uk/teaching/1617/OOProg/ticks/life.txt");
        GUILife gui = new GUILife(ps);
        gui.setVisible(true);

    }

}