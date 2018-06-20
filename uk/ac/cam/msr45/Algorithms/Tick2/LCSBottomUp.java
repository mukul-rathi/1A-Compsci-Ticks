package uk.ac.cam.msr45.Algorithms.Tick2;

import uk.ac.cam.rkh23.Algorithms.Tick2.LCSFinder;

public class LCSBottomUp extends LCSFinder {
    public LCSBottomUp(String s1, String s2) {
        super(s1, s2);
        if(s1.length()>=1 && s2.length()>=1) {
            mTable = new int[s1.length()][s2.length()];
        }
    }

    @Override
    public int getLCSLength() {
        if(mTable==null) return 0;
        for(int j=0; j<mTable[0].length; j++){
            mTable[0][j] = (mString2.charAt(j)==mString1.charAt(0))? 1:0;
            if(j>=1 && mTable[0][j-1]==1){
                mTable[0][j] =1;
            }
        }
        for(int i=1; i<mTable.length; i++){
            mTable[i][0] = (mString2.charAt(0)==mString1.charAt(i))? 1:0;
            if(i>=1 && mTable[i-1][0]==1){
                mTable[i][0] =1;
            }
        }
        for(int i=1;i<mTable.length; i++) {
            for (int j = 1; j < mTable[0].length; j++) {
                if (mString1.charAt(i) == mString2.charAt(j)) {
                    mTable[i][j] = 1 + mTable[i-1][j-1];
                }
                else{
                    mTable[i][j] = Math.max(mTable[i-1][j], mTable[i][j-1]);
                }
            }
        }

        return mTable[mTable.length-1][mTable[0].length-1];
    }

    @Override
    public String getLCSString() {
        String ans = "";
        if(mTable==null) return ans;

        int i  = mTable.length-1;
        int j = mTable[0].length-1;
        while(mTable[i][j]>0 && i>0 && j>0) {
            if (mString1.charAt(i) == mString2.charAt(j)) {
                ans = mString1.charAt(i--) + ans;
                j--;
            }
            else {
              if(mTable[i][j]==mTable[i][j-1]){
                  j--;
              }
              else{
                  i--;
              }

            }
        }
        if(i==0 && mTable[i][j]==1){
            ans = mString1.charAt(i) + ans;
        }
        else if(j==0 && mTable[i][j]==1){
                ans = mString2.charAt(j) + ans;
        }



        return ans;
    }
}
