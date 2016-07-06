package com.sohuvideo.playerdemo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sohuvideo.playerdemo.entity.Pgc;
import com.sohuvideo.playerdemo.entity.PgcList;

/**
 * @author tinghaoma
 */
public class JsonUtils {
    /**
     * 读取assets下面的json配置文件
     *
     * @param context
     * @param fileName 文件名
     * @return
     */
    public static String getJson(Context context, String fileName) {
        if (context == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader inputStreamReader = null;
        try {
            AssetManager mAssertManager = context.getAssets();
            inputStreamReader = new InputStreamReader(mAssertManager.open(fileName));
            BufferedReader bf = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("SOHU","---------Please Check Assets pcg.json File Exist-----------");
            throw new RuntimeException("Please Check Assets pcg.json File Exist---------");
        } finally {
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                    inputStreamReader = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }

    public static PgcList getPgcList(String str) {
        PgcList pgcList = new PgcList();
        ArrayList<Pgc> mList = null;
        try {
            JSONObject mPgcObj = new JSONObject(str);
            JSONArray mPgcArray = mPgcObj.getJSONArray("data");
            int len = mPgcArray.length();
            pgcList.setCount(len);
            mList = new ArrayList<Pgc>();
            Pgc mPgc = null;
            for (int i = 0; i < len; i++) {
                mPgc = new Pgc();
                JSONObject obj = mPgcArray.getJSONObject(i);
                mPgc.cate_id = obj.optInt(Pgc.CATE_ID);
                mPgc.cate_name = obj.optString(Pgc.CATE_NAME);
                mList.add(mPgc);
            }
            pgcList.setPgcs(mList);
            return pgcList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
