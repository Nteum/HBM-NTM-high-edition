package com.hbm.api.inventory;

public interface SlotAccCtl {
    int getSlot();
    default boolean allowIn(){return true;}
    default boolean allowOut(){return true;}
    default int in(){
        return allowIn() ? Integer.MAX_VALUE : 0;
    }

    default int out() {
        return allowOut() ? Integer.MAX_VALUE : 0;
    }
    default boolean isTrafficLimit(){return false;}
    static SlotAccCtl EMPTY = new SlotAccCtl() {
        @Override
        public int getSlot() {
            return -1;
        }
    };
    public static class SlotAccCtlBase implements SlotAccCtl{
        int slot;
        int in = Integer.MAX_VALUE;
        int out = Integer.MAX_VALUE;
        boolean trafficLimit = false;
        SlotAccCtlBase(int slot){
            this.slot = slot;
        }
        SlotAccCtlBase(int slot, boolean in, boolean out){
            this.slot = slot;
            if (!in) this.in = 0;
            if (!out) this.out = 0;
        }
        SlotAccCtlBase(int slot, int in, int out){
            this.slot = slot;
            this.in = in;
            this.out = out;
            trafficLimit = true;
        }
        @Override
        public int getSlot() {
            return slot;
        }

        @Override
        public boolean allowIn() {
            return in > 0;
        }

        @Override
        public boolean allowOut() {
            return out > 0;
        }

        @Override
        public int in() {
            return this.in;
        }

        @Override
        public int out() {
            return this.out;
        }

        @Override
        public boolean isTrafficLimit() {
            return trafficLimit;
        }
    }
}
