package com.sohuvideo.playerdemo.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Pgc implements Serializable {
    public static final String CATE_ID = "cate_id";
    public static final String CATE_NAME = "cate_name";
    public int cate_id;
    public String cate_name;
    private List<PgcChannel> mChannelList = new ArrayList<PgcChannel>();

    public List<PgcChannel> getmChannelList() {
        return mChannelList;
    }

    public void setmChannelList(List<PgcChannel> mChannelList) {
        this.mChannelList = mChannelList;
    }

}
