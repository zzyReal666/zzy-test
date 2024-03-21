package com.zzy;

import cn.hutool.core.util.ArrayUtil;

/**
 * @author zzypersonally@gmail.com
 * @description
 * @since 2024/3/19 16:31
 */
public class ArrayTest {

   //合并数组
    public static String mergeArray(int[] arr1, int[] arr2) {
        int[] sum = ArrayUtil.addAll(arr1, arr2);
        return ArrayUtil.toString(sum);
    }
}
