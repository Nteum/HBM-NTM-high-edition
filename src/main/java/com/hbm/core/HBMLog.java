package com.hbm.core;


import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

/**
 * Logger移出模组主类单独放一个类，这样迁移代码的时候一块迁移就行
 */
public class HBMLog {
    public static final Logger LOGGER = LogUtils.getLogger();
}
