package com.hbm.addational_data.entity.living;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContaminationEffectLists implements INBTSerializable<CompoundTag> {
    List<ContaminationEffect> effects;
    public ContaminationEffectLists(){
        this.effects = new ArrayList<>();
    }

    public List<ContaminationEffect> getEffects() {
        return effects;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        for (int i = 0; i < effects.size(); i++) {
            effects.get(i).save(tag, i);
        }
        tag.putInt("size", effects.size());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        int size = nbt.getInt("size");
        for (int i = 0; i < size; i++) {
            effects.add(ContaminationEffect.load(nbt, i));
        }
    }
    public static class ContaminationEffect {

        public float maxRad;
        public int maxTime;
        public int time;
        public boolean ignoreArmor;

        public ContaminationEffect(float rad, int time, boolean ignoreArmor) {
            this.maxRad = rad;
            this.maxTime = this.time = time;
            this.ignoreArmor = ignoreArmor;
        }

        public float getRad() {
            return maxRad * ((float)time / (float)maxTime);
        }

        public void serialize(ByteBuf buf) {
            buf.writeFloat(this.maxRad);
            buf.writeInt(this.maxTime);
            buf.writeInt(this.time);
            buf.writeBoolean(ignoreArmor);
        }

        public static ContaminationEffect deserialize(ByteBuf buf) {
            float maxRad = buf.readFloat();
            int maxTime = buf.readInt();
            int time = buf.readInt();
            boolean ignoreArmor = buf.readBoolean();
            ContaminationEffect effect = new ContaminationEffect(maxRad, maxTime, ignoreArmor);
            effect.time = time;
            return effect;
        }

        public void save(CompoundTag nbt, int index) {
            CompoundTag me = new CompoundTag();
            me.putFloat("maxRad", this.maxRad);
            me.putInt("maxTime", this.maxTime);
            me.putInt("time", this.time);
            me.putBoolean("ignoreArmor", ignoreArmor);
            nbt.put("cont_" + index, me);
        }

        public static ContaminationEffect load(CompoundTag nbt, int index) {
            CompoundTag me = (CompoundTag) nbt.get("cont_" + index);
            float maxRad = me.getFloat("maxRad");
            int maxTime = nbt.getInt("maxTime");
            int time = nbt.getInt("time");
            boolean ignoreArmor = nbt.getBoolean("ignoreArmor");

            ContaminationEffect effect = new ContaminationEffect(maxRad, maxTime, ignoreArmor);
            effect.time = time;
            return effect;
        }
    }
}
