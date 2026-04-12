package com.hbm.Inventory.filter;

import com.hbm.HBMKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public interface HBMFilter extends Predicate<ItemStack>, INBTSerializable<CompoundTag> {
    static CompositeFilter create(){
        return new CompositeFilter();
    }

    static ItemFilter item(ItemStack itemStack, boolean isBlackList){
        return new ItemFilter(itemStack, isBlackList);
    }
    static ItemFilter item(ItemStack itemStack, boolean isBlackList, boolean isStrict){
        return new ItemFilter(itemStack, isBlackList, isStrict);
    }
    static CompositeFilter items(ItemStack ...stacks){
        CompositeFilter filter = new CompositeFilter();
        for (ItemStack stack : stacks) {
            filter.add(new ItemFilter(stack, true));
        }
        return filter;
    }
    static TagFilter tag(TagKey<Item> tag, boolean isBlackList){
        return new TagFilter(tag, isBlackList);
    }
    static NbtFilter nbt(String name, Tag tag, boolean isBlackList){
        return new NbtFilter(name, tag, isBlackList);
    }

    void setBlackList(boolean isBlackList);

    // 具体实现
    class CompositeFilter implements HBMFilter{
        List<HBMFilter> filters;
        boolean isOrLogic;
        boolean isBlackList = false;

        public CompositeFilter(){
            this(true);
        }
        public CompositeFilter(CompoundTag tag){
            this();
            deserializeNBT(tag);
        }
        public CompositeFilter(boolean isOrLogic){
            this.filters = new ArrayList<>();
            this.isOrLogic = isOrLogic;
        }
        @Override
        public boolean test(ItemStack stack) {
            boolean result = !isOrLogic;
            for (HBMFilter filter : filters) {
                boolean test = filter.test(stack);
                result = isOrLogic ? result | test : result & test;
                if (isOrLogic && result) return !isBlackList;
                if (!isOrLogic && !result) return isBlackList;
            }
            return result ^ isBlackList;
        }

        public CompositeFilter add(HBMFilter filter){
            this.filters.add(filter);
            return this;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("len", this.filters.size());
            tag.putBoolean("isOrLogic", isOrLogic);
            for (int i = 0; i < this.filters.size(); i++) {
                tag.put(i + "", filters.get(i).serializeNBT());
            }
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            int len = nbt.getInt("len");
            this.isOrLogic = nbt.getBoolean("isOrLogic");
            for (int i = 0; i < len; i++) {
                CompoundTag tag = nbt.getCompound(i + "");
                String type = tag.getString("type");
                switch (type) {
                    case "item" -> filters.add(new ItemFilter(tag));
                    case "tag" -> filters.add(new TagFilter(tag));
                    case "nbt" -> filters.add(new NbtFilter(tag));
                }
            }
        }

        @Override
        public void setBlackList(boolean isBlackList) {
            this.isBlackList = isBlackList;
            for (HBMFilter filter : this.filters) {
                filter.setBlackList(isBlackList);
            }
        }
    }
    class ItemFilter implements HBMFilter {
        private boolean isBlackList = false;
        private boolean isStrict = false;   //是否严格判断
        private ItemStack stack;
        public ItemFilter(CompoundTag tag){
            deserializeNBT(tag);
        }
        public ItemFilter(ItemStack stack, boolean isBlackList){
            this(stack, isBlackList, false);
        }
        public ItemFilter(ItemStack stack, boolean isBlackList, boolean isStrict){
            this.stack = stack;
            this.isBlackList = isBlackList;
            this.isStrict = isStrict;
        }
        @Override
        public boolean test(ItemStack stack) {
            return (isStrict ? ItemHandlerHelper.canItemStacksStack(this.stack, stack) : this.stack.is(stack.getItem())) ^ isBlackList;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putString("type", "item");
            tag.put(HBMKey.ITEM, stack.serializeNBT());
            tag.putBoolean("isBlackList", this.isBlackList);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.stack = ItemStack.of(nbt.getCompound(HBMKey.ITEM));
            this.isBlackList = nbt.getBoolean("isBlackList");
        }
        @Override
        public void setBlackList(boolean isBlackList) {
            this.isBlackList = isBlackList;
        }
    }

    class TagFilter implements HBMFilter {
        TagKey<Item> key;
        boolean isBlackList = false;
        public TagFilter(CompoundTag tag){
            deserializeNBT(tag);
        }
        public TagFilter(TagKey<Item> key, boolean isBlackList){
            this.key = key;
            this.isBlackList = isBlackList;
        }
        @Override
        public boolean test(ItemStack stack) {
            return stack.is(key) ^ isBlackList;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putString("type", "tag");
            tag.putString(HBMKey.TAG, this.key.location().toString());
            tag.putBoolean("isBlackList", this.isBlackList);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            ResourceLocation rl = ResourceLocation.tryParse(nbt.getString(HBMKey.TAG));
            this.key = TagKey.create(Registries.ITEM, rl);
            this.isBlackList = nbt.getBoolean("isBlackList");
        }
        @Override
        public void setBlackList(boolean isBlackList) {
            this.isBlackList = isBlackList;
        }
    }

    class NbtFilter implements HBMFilter {
        String name;
        Tag tag;
        boolean isBlackList = false;
        public NbtFilter(CompoundTag tag){
            deserializeNBT(tag);
        }
        public NbtFilter(String name, Tag tag, boolean isBlackList){
            this.name = name;
            this.tag = tag;
            this.isBlackList = isBlackList;
        }
        @Override
        public boolean test(ItemStack stack) {
            if (stack.hasTag()){
                Tag tag1 = stack.getTag().get(name);
                if (tag1 != null){
                    return tag1.equals(tag) ^ isBlackList;
                }
            }
            return isBlackList;
//            return stack.hasTag() && stack.getTag().get(name).equals(tag) ^ isBlackList;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putString("type", "tag");
            tag.putString("name", this.name);
            tag.put("nbt", this.tag);
            tag.putBoolean("isBlackList", this.isBlackList);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.name = nbt.getString("name");
            this.tag = nbt.get("nbt");
            this.isBlackList = nbt.getBoolean("isBlackList");
        }
        @Override
        public void setBlackList(boolean isBlackList) {
            this.isBlackList = isBlackList;
        }
    }
}
