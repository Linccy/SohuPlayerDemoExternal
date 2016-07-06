package com.sohuvideo.playerdemo.net;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sohuvideo.playerdemo.entity.Pgc;
import com.sohuvideo.playerdemo.entity.PgcChannel;

public class PgcListProtocol extends BaseProtocol<Pgc> {
    //取对应一级分类下面的 2级分类
    private static final String URL = "http://open.mb.hd.sohu.com/v4/category/catecode/";
    private Pgc mPgc;
    private String apikey;

    public PgcListProtocol(Context context, Pgc mPgc, String apikey) {
        super(context);
        this.mPgc = mPgc;
        this.apikey = apikey;
    }

    @Override
    public String makeRequest() {
        return URL + mPgc.cate_id + ".json?api_key=" + apikey;
    }

    @Override
    public Pgc handleResponse(String response) {
        try {
            List<PgcChannel> mChannels = new ArrayList<PgcChannel>();
            JSONArray mArray = new JSONObject(response).getJSONArray("data");
            PgcChannel mChannel = null;
            for (int i = 0; i < mArray.length(); i++) {
                mChannel = new PgcChannel();
                JSONObject mObj = mArray.getJSONObject(i);
                mChannel.second_cate_code = mObj.optInt(PgcChannel.SECOND_CATE_CODE);
                mChannel.second_cate_name = mObj.optString(PgcChannel.SECOND_CATE_NAME);
                mChannel.first_cate_code = mObj.optInt(PgcChannel.FIRST_CATE_CODE);
                mChannels.add(mChannel);
            }
            mPgc.setmChannelList(mChannels);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mPgc;
    }

    @Override
    protected void handleError(int errorCode) {

    }

}
