package uk.ac.cam.msr45.Algorithms.Tick1;

import uk.ac.cam.rkh23.Algorithms.Tick1.EmptyHeapException;
import uk.ac.cam.rkh23.Algorithms.Tick1.MaxCharHeapInterface;

public class MaxCharHeap implements MaxCharHeapInterface {
    private int mSize;
    private char[] mHeap;

    public MaxCharHeap(String s){
        s = s.toLowerCase();
        mHeap = s.toCharArray();
        mSize = mHeap.length;

        for(int i=mSize/2;i>=0;i--){
            maxHeapify(i);
        }

    }

    private void maxHeapify(int i) {
        int largest;
        if (leftChild(i)<= (mSize-1) && mHeap[leftChild(i)]>mHeap[i]){
            largest = leftChild(i);
        }
        else{
            largest=i;
        }
        if(rightChild(i) <= (mSize-1) && mHeap[rightChild(i)]>mHeap[largest]){
            largest = rightChild(i);
        }

        if(largest!=i){
            char temp = mHeap[i];
            mHeap[i] = mHeap[largest];
            mHeap[largest] = temp;
            maxHeapify(largest);
        }

    }

    private int rightChild(int i) {
        return 2*i+2;
    }

    private int leftChild(int i) {
        return 2*i+1;
    }

    private int parent(int i) {
        return (i-1)/2;
    }

    @Override
    public char getMax() throws EmptyHeapException {
        if (mSize==0) throw new EmptyHeapException();
        char max = mHeap[0];
        mHeap[0] = mHeap[mSize-- -1];
        maxHeapify(0);
        return max;

    }

    @Override
    public void insert(char ch) {

        if (mSize == mHeap.length){
            char[] newHeap = new char[2*mSize];
            for(int i=0; i<mSize;i++){
                newHeap[i] = mHeap[i];
            }
            mHeap = newHeap;
        }
        mHeap[mSize] = '\u0000'; //min value of a char
        increaseKey(mSize,Character.toLowerCase(ch));
        mSize++;


    }

    private void increaseKey(int i, char key) {
        mHeap[i] = key;
        while(i>0 && mHeap[parent(i)]<mHeap[i]){
            char temp = mHeap[i];
            mHeap[i] = mHeap[parent(i)];
            mHeap[parent(i)] = temp;
            i=parent(i);
        }

    }

    @Override
    public int getLength() {
        return mSize;
    }

}
