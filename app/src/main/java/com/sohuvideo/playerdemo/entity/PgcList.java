package com.sohuvideo.playerdemo.entity;

import java.util.ArrayList;

public class PgcList {
    private int count;
    private ArrayList<Pgc> pgcs = new ArrayList<Pgc>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<Pgc> getPgcs() {
        return pgcs;
    }

    public void setPgcs(ArrayList<Pgc> pgcs) {
        this.pgcs = pgcs;
    }

}
