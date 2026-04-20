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
    static RootFilter create(){
        return new RootFilter(0, 0);
    }
    static RootFilter create(int size){
        return new RootFilter(size, 0);
    }
    static ItemFilter create(ItemStack itemStack){
        return new ItemFilter(itemStack, false);
    }

    class RootFilter extends CompositeFilter{
        // 0 - OFF 禁止, 1 - WHITELIST, 2 - BLACKLIST, 3 - WILDCARD 默认出口
        int mode = 0;
        public RootFilter(int size, int mode){
            super(size);
            this.mode = mode;
        }

        @Override
        public boolean test(ItemStack stack) {
            return mode == 0 ? false : mode == 1 ? super.test(stack) : mode == 2 ? !super.test(stack) : true;
        }

        public void setBlackList(boolean blackList) {
            mode = blackList ? 2 : 1;
        }

        public int getMode(){
            return mode;
        }
        public void setMode(int mode){
            this.mode = mode;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = super.serializeNBT();
            tag.putInt(HBMKey.MODE, mode);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            super.deserializeNBT(nbt);
            this.mode = nbt.getInt(HBMKey.MODE);
        }
    }
    // 具体实现
    class CompositeFilter implements HBMFilter{
        List<HBMFilter> filters;
        boolean isOrLogic;
        public CompositeFilter(CompoundTag tag){
            this();
            deserializeNBT(tag);
        }
        public CompositeFilter(){
            this(0);
        }
        public CompositeFilter(int size){
            this(size, true);
        }
        public CompositeFilter(int size, boolean isOrLogic){
            this.filters = new ArrayList<>();
            this.isOrLogic = isOrLogic;
            for (int i = 0; i < size; i++) {
                filters.add(new BlankFilter());
            }
        }

        public List<HBMFilter> getFilters() {
            return filters;
        }

        @Override
        public boolean test(ItemStack stack) {
            boolean result = !isOrLogic;
            for (HBMFilter filter : filters) {
                boolean test = filter.test(stack);
                result = isOrLogic ? result | test : result & test;
                if (result == isOrLogic) return result;
            }
            return result;
        }

        public CompositeFilter add(HBMFilter filter){
            this.filters.add(filter);
            return this;
        }

        public CompositeFilter set(int i, HBMFilter filter){
            if (i >= 0 && i < filters.size()){
                this.filters.set(i, filter);
            }
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
            filters.clear();
            int len = nbt.getInt("len");
            this.isOrLogic = nbt.getBoolean("isOrLogic");
            for (int i = 0; i < len; i++) {
                CompoundTag tag = nbt.getCompound(i + "");
                String type = tag.getString("type");
                switch (type) {
                    case "blank" -> filters.add(new BlankFilter(tag));
                    case "item" -> filters.add(new ItemFilter(tag));
                    case "tag" -> filters.add(new TagFilter(tag));
                    case "nbt" -> filters.add(new NbtFilter(tag));
                }
            }
        }
    }
    // 空白过滤器，用于占位
    class BlankFilter implements HBMFilter{
        public BlankFilter(CompoundTag tag){
            deserializeNBT(tag);
        }
        public BlankFilter(){}
        //如果是黑名单，空过滤器输出true，白名单空过滤器输出false
        @Override
        public boolean test(ItemStack itemStack) {
            return false;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putString("type", "blank");
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
        }
    }
    class ItemFilter implements HBMFilter {
        private boolean isStrict = false;   //是否严格判断
        private ItemStack stack;
        public ItemFilter(CompoundTag tag){
            deserializeNBT(tag);
        }
        public ItemFilter(ItemStack stack, boolean isStrict){
            this.stack = stack;
            this.isStrict = isStrict;
        }
        @Override
        public boolean test(ItemStack stack) {
            return isStrict ? ItemHandlerHelper.canItemStacksStack(this.stack, stack) : this.stack.is(stack.getItem());
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putString("type", "item");
            tag.put(HBMKey.ITEM, stack.serializeNBT());
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.stack = ItemStack.of(nbt.getCompound(HBMKey.ITEM));
        }
    }

    class TagFilter implements HBMFilter {
        TagKey<Item> key;
        public TagFilter(CompoundTag tag){
            deserializeNBT(tag);
        }
        public TagFilter(TagKey<Item> key){
            this.key = key;
        }
        @Override
        public boolean test(ItemStack stack) {
            return stack.is(key);
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putString("type", "tag");
            tag.putString(HBMKey.TAG, this.key.location().toString());
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            ResourceLocation rl = ResourceLocation.tryParse(nbt.getString(HBMKey.TAG));
            this.key = TagKey.create(Registries.ITEM, rl);
        }
    }

    class NbtFilter implements HBMFilter {
        String name;
        Tag tag;
        public NbtFilter(CompoundTag tag){
            deserializeNBT(tag);
        }
        public NbtFilter(String name, Tag tag){
            this.name = name;
            this.tag = tag;
        }
        public NbtFilter(String name, Tag tag, boolean isBlackList){
            this.name = name;
            this.tag = tag;
        }
        @Override
        public boolean test(ItemStack stack) {
            if (stack.hasTag()){
                Tag tag1 = stack.getTag().get(name);
                if (tag1 != null){
                    return tag1.equals(tag);
                }
            }
            return false;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putString("type", "tag");
            tag.putString("name", this.name);
            tag.put("nbt", this.tag);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.name = nbt.getString("name");
            this.tag = nbt.get("nbt");
        }
    }
}
