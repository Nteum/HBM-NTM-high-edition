package com.hbm.utils;
/**
 * 数据类型的工具类，我快被这些东西搞疯了。
 * */
public class DataTypeHelper {
    public static int[] floatArr2IntArr(float[] floats){
        int[] result = new int[floats.length];
        for (int i = 0; i < floats.length; i++) {
            result[i] = Float.floatToIntBits(floats[i]);
        }
        return result;
    }
    public static float[] IntArr2FloatArr(int[] ints){
        float[] result = new float[ints.length];
        for (int i = 0; i < ints.length; i++) {
            result[i] = Float.intBitsToFloat(ints[i]);
        }
        return result;
    }
}
