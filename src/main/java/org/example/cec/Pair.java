package org.example.cec;

public class Pair {
    int pos;
    boolean work;
    Pair(int pos, boolean work){
        this.pos = pos;
        this.work = work;
    }
    Pair(int pos){this.pos = pos;}
    Pair(boolean work){this.work = work;}
    int getPos(){return pos;}
    boolean getWork(){return work;}

}
