package com.hbm;

import com.hbm.api.text.ILangEntry;
import com.hbm.datagen.LanguageProvider;
import joptsimple.internal.Strings;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.inventory.FurnaceScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.SlabBlock;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public enum HBMLang implements ILangEntry {
    ITEMGROUP_ITEM("itemGroup","hbm_item","HBM 物品"),
    ITEMGROUP_BLOCK("itemGroup","hbm_block","HBM 方块"),
    ITEMGROUP_MACHINE("itemGroup","hbm_machine","HBM 机器"),
    ITEMGROUP_TOOL("itemGroup","hbm_tool","HBM 工具"),
    ITEMGROUP_WEAPON("itemGroup","hbm_weapon","HBM 武器"),
    // handoverTexts
    ENERGY("item","battery.tooltip","能量：%1$s"),
    FLUID_CAPACITY("item","fluid_capacity","容量：%s mB"),
    // blockentity name
    DIFURNACE("container","difurnace","高炉"),
    CRUCIBLE("container","crucible","坩埚"),
    ELECTRIC_FURNACE("container","electric_furnace","电炉"),
    BOILER("container","boiler","锅炉"),
    ELECTRIC_BOILER("container","electric_boiler","电锅炉"),
    NUCLEAR_BOILER("container","nuclear_boiler","核锅炉"),
    SHREDDER("container","shredder","粉碎机"),
    ASSEMBLER("container","assembler","装配机"),
    CHEMPLANT("container", "chemplant","化工厂"),
    BARREL("container", "barrel","桶"),
    BATTERY("container", "battery","电池"),
    TOKAMAK("container", "tokamak","托卡马克聚变堆"),
    RBMK("container", "rbmk","RBMK 反应堆"),
    CONTAINER_LAUNCHPAD("发射台"),
    // command
    COMMAND_DEBUG("command","debug","调试"),
    //Redstone Control
    REDSTONE_CONTROL_DISABLED("redstone_control", "disabled","禁用"),
    REDSTONE_CONTROL_HIGH("redstone_control", "high","高电平"),
    REDSTONE_CONTROL_LOW("redstone_control", "low","低电平"),
    REDSTONE_CONTROL_PULSE("redstone_control", "pulse","脉冲"),
    // upgrade
    UPGRADE_RADIUS("upgrade","radius.tooltip","力场范围升级\n半径 +16 / 消耗 +500\n可叠加至 16"),
    UPGRADE_HEALTH("upgrade","health.tooltip","力场耐久升级\n最大生命 +50 / 消耗 +250\n可叠加至 16"),
    UPGRADE_SMELTER("upgrade","smelter.tooltip","采矿激光升级\n熔炼方块，简单明了。"),
    UPGRADE_SHREDDER("upgrade","shredder.tooltip","采矿激光升级\n粉碎矿石"),
    UPGRADE_CENTRIFUGE("upgrade","centrifuge.tooltip","采矿激光升级\n不言自明"),
    UPGRADE_CRYSTALLIZER("upgrade","crystallizer.tooltip","采矿激光升级\n你的新好朋友"),
    UPGRADE_SCREAM("upgrade","scream.tooltip","采矿激光升级\n就像超级马里奥里所有方块其实都是奇诺比奥，\n不过这里是半条命的科学家，\n而且他们会叫。很大声。"),
    UPGRADE_NULLIFIER("upgrade","nullifier.tooltip","采矿激光升级\n50% 概率用 /dev/zero 覆写无价值物品\n50% 概率把无价值物品移至 /dev/null"),
    UPGRADE_GC_SPEED("upgrade","gc_speed.tooltip","气体离心机升级\n允许对 HEUF6 完全同位素分离，\n同时让你的离心机疯狂运转"),
    // fluid
    FT_GASEOUS("fluid", "gaseous.tooltip","[气体]"),
    FT_GASEOUS_ART("fluid", "gaseous_art.tooltip","[常温为气体]"),
    FT_LIQUID("fluid", "liquid.tooltip","[液体]"),
    FT_VISCOUS("fluid", "viscous.tooltip","[粘稠]"),
    FT_PLASMA("fluid", "plasma.tooltip","[等离子]"),
    FT_AMAT("fluid", "amat.tooltip","[反物质]"),
    FT_LEAD_CONTAINER("fluid", "lead_container.tooltip","[需危险品容器存放]"),
    FT_DELICIOUS("fluid", "delicious.tooltip","[美味]"),
    FT_UNSIPHONABLE("fluid", "unsiphonable.tooltip","[抽液器忽略]"),
    FT_FLAME("fluid", "flammable.tooltip","[易燃]"),
    FT_VENT_RADIATION("fluid", "vent_rad.tooltip","[通风释放辐射]"),
    FT_COMBUSTIBLE1("fluid", "combustible.tooltip1","[可燃]"),
    FT_COMBUSTIBLE2("fluid", "combustible.tooltip2","每桶提供 %s HE"),
    FT_COMBUSTIBLE3("fluid", "combustible.tooltip3","燃料等级：%s"),
    FT_THERMAL_CAPACITY("fluid","thermal_capacity.tooltip1","热容量：每 %2$s mB 提供 %1$s TU"),
    FT_EFFICIENCY("fluid","efficiency.tooltip2","[ %s ] 效率：%s %"),
    FT_CORROSIVE1("fluid","corrosive.tooltip1","[强腐蚀性]"),
    FT_CORROSIVE2("fluid","corrosive.tooltip2","[腐蚀性]"),
    FT_FLAMMABLE1("fluid","flammable.tooltip1","[易燃]"),
    FT_FLAMMABLE2("fluid","flammable.tooltip2","每桶提供 %s TU"),
    FT_HEATABLE1("fluid","heatable.tooltip1","[可加热]"),
    FT_PHEROMONE1("fluid","pheromone.tooltip1","[格利菲德信息素]"),
    FT_PHEROMONE2("fluid","pheromone.tooltip2","[改良信息素]"),
    FT_POISON("fluid","poison.tooltip","[有毒烟雾]"),
    FT_POLLUTION1("fluid","pollution.tooltip1","[污染]"),
    FT_POLLUTION2("fluid","pollution.tooltip2","泼洒时："),
    FT_POLLUTION3("fluid","pollution.tooltip3","燃烧时："),
    FT_PER_MB("fluid","per_mb.tooltip"," - 每 mB %s %s"),
    FT_PWRMODERATOR("fluid","pwr_moderator.tooltip","[压水堆通量倍增]"),
    FT_CORE_FLUX("fluid","core_flux.tooltip","反应堆通量 + %s %"),
    FT_RADIOACTIVE("fluid","radioactive.tooltip","[放射性]"),
    // GUI
    TOOLTIP_LEFT_TIME("gui","left_time.tooltip","请等待 %s s"),
    TOOLTIP_TANK_VOLUME("gui","volume.tooltip","%s : %s mB"),
    TOOLTIP_ENERGY("gui","stored_energy.tooltip","能量：%s HE"),
    // 大世界tooltip
    LOOKTOOLTIP_CHEMPLANT("block","chemplant.looktooltip","<- 储罐 %s"),
    TOOLTIP_GEIGER0("geiger","title", "盖格计数器"),
    TOOLTIP_GEIGER1("geiger","chunk_rad", "当前区块辐射："),
    TOOLTIP_GEIGER2("geiger","envrad", "环境总辐射："),
    TOOLTIP_GEIGER3("geiger","playerrad", "玩家污染："),
    TOOLTIP_GEIGER4("geiger","playerres", "玩家抗性："),
    ITEM_MISSILE_TIER("等级 %s"),
    ITEM_MISSILE_DESC_NOTLAUNCHABLE("无法发射！"),
    // debug
    CACHED_DATA("general","cached","缓存数据："),
    POS_DATA("general","data.pos","方块坐标 [%s]"),
    CHUNK_DATA("general", "data.chunk","区块坐标 %s"),
    BLOCK_STATE_LOSE("debug","debugwand.msg.block_lost","坐标 [%s] 未找到方块！"),
    BLOCK_STATE_INFO("debug","debugwand.msg.block_info","坐标 [%s] 的方块为 %s。"),
    // general （不用于特殊用途，仅仅作为文字）
    RECIPE("general","recipe","配方"),
    FUEL("燃料"),
    FUEL_CAPACITY("燃料容量：%s mB"),
    // effect
    EFFECT_RADIATION("effect","radiation","辐射"),
    // armor tooltip
    ARMOR_GEIGERSOUND("音频盖格计数器"),
    ARMOR_GEIGERHUD("内置盖格计数器HUD"),
    ARMOR_GLIDER("按潜行滑翔"),
    ARMOR_VATS("敌人HUD"),
    ARMOR_THERMAL("热成像瞄具"),
    ARMOR_HARDLANDING("重型着陆"),
    ARMOR_STEPSIZE("跨步高度：%s"),
    ARMOR_DASH("获得%s次冲刺"),
    ARMOR_FSB("套装加成："),
    TOOLTIP_CHARGERATE("充能：%s / %s"),
    ;

    private final String key;
    private String content = "";
    public boolean autoAdd = false;
    // 直接根据列表名称
    HBMLang(String content){
        this(content,true);
    }
    HBMLang(String content, boolean autoAdd){
        String[] split = this.name().toLowerCase().split("_");
        split[0] = split[0] + "." + HBM.MODID;
        this.key = Strings.join(split, ".");
        this.content = content;
        this.autoAdd = autoAdd;
    }
    HBMLang(String type, String path){
        this(type, path, "");
    }
    HBMLang(String type, String path, String content){
        this.key = Util.makeDescriptionId(type,HBM.rl(path));
        this.content = content;
    }
    public @NotNull String key(){
        return key;
    }
    public String content(){
        return content;
    }
}
