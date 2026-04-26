package com.hbm.utils.math;

public class BitUtil {
    /**
     * 获取指定范围位的取值
     * @param data 原始数据
     * @param offset 起始位（右起从0开始）
     * @param length 占用的位数
     * @return 对应的状态值
     */
    public static int get(int data, int offset, int length) {
        return (data >> offset) & ((1 << length) - 1);
    }

    /**
     * 设置指定范围位的值并返回新的 int
     * @param data 原始数据
     * @param offset 起始位
     * @param length 占用的位数
     * @param value 要设置的状态值
     * @return 修改后的完整数据
     */
    public static int set(int data, int offset, int length, int value) {
        int mask = (1 << length) - 1;
        // 1. 将目标区域清零 (AND 非掩码)
        data &= ~(mask << offset);
        // 2. 将值写入目标区域 (OR 移位后的值)
        // 确保 value 不会超过长度限制
        data |= (value & mask) << offset;
        return data;
    }

    // 快捷方法：获取/设置单轴位（1 bit）
    public static boolean getBool(int data, int offset) {
        return get(data, offset, 1) == 1;
    }

    public static int setBool(int data, int offset, boolean value) {
        return set(data, offset, 1, value ? 1 : 0);
    }
}
