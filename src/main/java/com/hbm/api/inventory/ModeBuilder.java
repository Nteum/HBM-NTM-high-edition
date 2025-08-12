package com.hbm.api.inventory;

import com.hbm.api.Mode;

import java.util.ArrayList;
import java.util.List;

public class ModeBuilder {
    List<Mode> modes;
    public ModeBuilder(){
        modes = new ArrayList<>();
    }
    public ModeBuilder addMode(Mode mode){
        modes.add(mode);
        return this;
    }
    public ModeBuilder addMode(int num, Mode mode){
        for (int i = 0; i < num; i++) {
            modes.add(mode);
        }
        return this;
    }
    public ModeBuilder addModes(Object ... objs){
        if (objs.length % 2 == 1 || objs.length == 0)return this;
        Object temp1;
        Object temp2;
        for (int i = 1; i < objs.length; i++) {
            temp2 = objs[i];temp1 = objs[i-1];
            if (temp1 instanceof Integer && temp2 instanceof Mode){
                addMode((Integer) temp1, (Mode) temp2);
            }
        }
        return this;
    }
    public List<Mode> get(){
        return modes;
    }
}
