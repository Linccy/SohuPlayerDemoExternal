package com.sohuvideo.playerdemo.utils;

public class ArrayUtil {

    public final static int[] intValues(Integer[] params) {
        int len = params.length;
        int[] temp = new int[len];
        for (int i = 0; i < len; i++) {
            temp[i] = params[i];
        }
        return temp;
    }
}
