package com.hbm.addational_data;

import com.hbm.handler.pollution.PollutionType;

public class Pollution {
    float[] pollution = new float[Type.values().length];
    enum Type{
        SOOT, POISON, HEAVYMETAL, FALLOUT;
    }
}
