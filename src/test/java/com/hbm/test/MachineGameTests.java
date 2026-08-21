//package com.hbm.test;
//
//import com.hbm.blockentity.machine.ArcWelderEntity;
//import com.hbm.blockentity.machine.AshpitEntityBE;
//import com.hbm.blockentity.machine.BlastFurnaceEntity;
//import com.hbm.blockentity.machine.BreedingReactorEntity;
//import com.hbm.blockentity.machine.CatalyticCrackerEntity;
//import com.hbm.blockentity.machine.CatalyticReformerEntity;
//import com.hbm.blockentity.machine.CombustionEngineEntity;
//import com.hbm.blockentity.machine.CryoDistillEntity;
//import com.hbm.blockentity.machine.DeconEntity;
//import com.hbm.blockentity.machine.DeuteriumExtractorEntity;
//import com.hbm.blockentity.machine.EPressEntityBE;
//import com.hbm.blockentity.machine.FractionTowerEntity;
//import com.hbm.blockentity.machine.GasCentEntity;
//import com.hbm.blockentity.machine.HeatBoilerEntity;
//import com.hbm.blockentity.machine.LiquefactorEntity;
//import com.hbm.blockentity.machine.MilkReformerEntity;
//import com.hbm.blockentity.machine.MixerEntity;
//import com.hbm.blockentity.machine.RTGEntityBE;
//import com.hbm.blockentity.machine.RadiolysisEntity;
//import com.hbm.blockentity.machine.RefineryEntity;
//import com.hbm.blockentity.machine.SawmillEntity;
//import com.hbm.blockentity.machine.SirenEntity;
//import com.hbm.blockentity.machine.SolarBoilerEntity;
//import com.hbm.blockentity.machine.SteamEngineEntity;
//import com.hbm.blockentity.machine.StirlingEntity;
//import com.hbm.blockentity.machine.VacuumDistillEntity;
//import com.hbm.blockentity.machine.AlkylationEntity;
//import com.hbm.blockentity.machine.BigAssTankEntity;
//import com.hbm.core.contents.fluid.HBMFluids;
//import com.hbm.item.misc.ItemRTGPellet;
//import com.hbm.registries.ModBlocks;
//import com.hbm.registries.ModItems;
//import net.minecraft.core.BlockPos;
//import net.minecraft.gametest.framework.GameTest;
//import net.minecraft.gametest.framework.GameTestHelper;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.Items;
//import net.minecraft.world.level.block.Blocks;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraftforge.fluids.FluidStack;
//import net.minecraftforge.fluids.capability.IFluidHandler;
//import net.minecraftforge.fluids.capability.templates.FluidTank;
//
///**
// * 机器功能测试（GameTest 框架）。
// * 通过 runclient 的 /test 命令或 runGameTestServer 运行。
// * 测试：放置机器 → 验证 BE → 注入流体 → 放物品 → 查配方 → 销毁。
// */
//public class MachineGameTests {
//
//    @GameTest(template = "hbm:empty")
//    public static void placeLiquefactor(GameTestHelper helper){
//        BlockPos pos = new BlockPos(1, 2, 1);
//        helper.setBlock(pos, ModBlocks.MACHINE_LIQUEFACTOR.get().defaultBlockState());
//        helper.succeedWhen(() -> {
//            BlockEntity be = helper.getBlockEntity(pos);
//            if (be instanceof LiquefactorEntity liq){
//                // 注入煤油
//                int filled = liq.getTank().fill(new FluidStack(HBMFluids.COALOIL.source().get(), 100), IFluidHandler.FluidAction.EXECUTE);
//                if (filled != 100) helper.fail("注入煤油失败: " + filled);
//                // 放煤
//                liq.getItemHandler().setStackInSlot(0, new ItemStack(Items.COAL));
//                if (liq.getRecipes().isEmpty()) helper.fail("液化机无可用配方");
//            } else {
//                helper.fail("液化机 BE 创建失败: " + be);
//            }
//        });
//    }
//
//    @GameTest(template = "hbm:empty")
//    public static void placeRefinery(GameTestHelper helper){
//        BlockPos pos = new BlockPos(1, 2, 1);
//        helper.setBlock(pos, ModBlocks.MACHINE_REFINERY.get().defaultBlockState());
//        helper.succeedWhen(() -> {
//            BlockEntity be = helper.getBlockEntity(pos);
//            if (be instanceof RefineryEntity ref){
//                if (ref.getRecipes().isEmpty()) helper.fail("炼油厂无可用配方");
//            } else {
//                helper.fail("炼油厂 BE 创建失败: " + be);
//            }
//        });
//    }
//
//    @GameTest(template = "hbm:empty")
//    public static void destroyMachine(GameTestHelper helper){
//        BlockPos pos = new BlockPos(1, 2, 1);
//        helper.setBlock(pos, ModBlocks.MACHINE_LIQUEFACTOR.get().defaultBlockState());
//        helper.succeedWhen(() -> {
//            helper.setBlock(pos, Blocks.AIR.defaultBlockState());
//            if (helper.getBlockEntity(pos) != null) helper.fail("销毁后 BE 残留");
//        });
//    }
//
//    @GameTest(template = "hbm:empty")
//    public static void placeRTG(GameTestHelper helper){
//        BlockPos pos = new BlockPos(1, 2, 1);
//        helper.setBlock(pos, ModBlocks.MACHINE_RTG_GREY.get().defaultBlockState());
//        helper.succeedWhen(() -> {
//            BlockEntity be = helper.getBlockEntity(pos);
//            if (be instanceof RTGEntityBE rtg){
//                // 放一个 RTG 燃料棒
//                rtg.getItemHandler().setStackInSlot(0, new ItemStack(ModItems.PELLET_RTG.get()));
//                if (!(ModItems.PELLET_RTG.get() instanceof ItemRTGPellet)) helper.fail("pellet_rtg 不是 ItemRTGPellet");
//                // 等一 tick 后应有热量与能量
//                if (rtg.heat <= 0) helper.fail("RTG 无热量");
//                if (rtg.power <= 0) helper.fail("RTG 无能量");
//            } else {
//                helper.fail("RTG BE 创建失败: " + be);
//            }
//        });
//    }
//
//    @GameTest(template = "hbm:empty")
//    public static void placeEPress(GameTestHelper helper){
//        BlockPos pos = new BlockPos(1, 2, 1);
//        helper.setBlock(pos, ModBlocks.MACHINE_EPRESS.get().defaultBlockState());
//        helper.succeedWhen(() -> {
//            BlockEntity be = helper.getBlockEntity(pos);
//            if (be instanceof EPressEntityBE press){
//                if (press.getItemHandler().getSlots() != 5) helper.fail("E-Press 槽位数量错误");
//            } else {
//                helper.fail("E-Press BE 创建失败: " + be);
//            }
//        });
//    }
//
//    @GameTest(template = "hbm:empty")
//    public static void placeAshpit(GameTestHelper helper){
//        BlockPos pos = new BlockPos(1, 2, 1);
//        helper.setBlock(pos, ModBlocks.MACHINE_ASHPIT.get().defaultBlockState());
//        helper.succeedWhen(() -> {
//            BlockEntity be = helper.getBlockEntity(pos);
//            if (be instanceof AshpitEntityBE ashpit){
//                if (ashpit.getItemHandler().getSlots() != 5) helper.fail("灰烬收集器槽位数量错误");
//                // 投入灰烬并等待转换
//                ashpit.addAsh(com.hbm.item.ItemEnums.EnumAshType.WOOD, AshpitEntityBE.thresholdWood);
//            } else {
//                helper.fail("灰烬收集器 BE 创建失败: " + be);
//            }
//        });
//    }
//
//    @GameTest(template = "hbm:empty")
//    public static void placeRadiolysis(GameTestHelper helper){
//        BlockPos pos = new BlockPos(1, 2, 1);
//        helper.setBlock(pos, ModBlocks.MACHINE_RADIOLYSIS.get().defaultBlockState());
//        helper.succeedWhen(() -> {
//            BlockEntity be = helper.getBlockEntity(pos);
//            if (be instanceof RadiolysisEntity rad){
//                if (rad.getItemHandler().getSlots() != 15) helper.fail("辐射裂解装置槽位数量错误");
//                if (rad.getRecipes().isEmpty()) helper.fail("辐射裂解装置无可用配方");
//                // 放 RTG 燃料棒 → 应有热量与能量
//                rad.getItemHandler().setStackInSlot(0, new ItemStack(ModItems.PELLET_RTG.get()));
//                if (rad.heat <= 0) helper.fail("辐射裂解装置无热量");
//                if (rad.power <= 0) helper.fail("辐射裂解装置无能量");
//            } else {
//                helper.fail("辐射裂解装置 BE 创建失败: " + be);
//            }
//        });
//    }
//
//    @GameTest(template = "hbm:empty")
//    public static void placeArcWelder(GameTestHelper helper){
//        BlockPos pos = new BlockPos(1, 2, 1);
//        helper.setBlock(pos, ModBlocks.MACHINE_ARC_WELDER.get().defaultBlockState());
//        helper.succeedWhen(() -> {
//            BlockEntity be = helper.getBlockEntity(pos);
//            if (be instanceof ArcWelderEntity welder){
//                if (welder.getItemHandler().getSlots() != 8) helper.fail("电弧焊机槽位数量错误");
//                if (welder.getRecipes().isEmpty()) helper.fail("电弧焊机无可用配方");
//                // 放输入物品
//                welder.getItemHandler().setStackInSlot(0, new ItemStack(ModItems.PLATE_IRON.get(), 2));
//            } else {
//                helper.fail("电弧焊机 BE 创建失败: " + be);
//            }
//        });
//    }
//
//    @GameTest(template = "hbm:empty")
//    public static void placeMilkReformer(GameTestHelper helper){
//        BlockPos pos = new BlockPos(1, 2, 1);
//        helper.setBlock(pos, ModBlocks.MACHINE_MILK_REFORMER.get().defaultBlockState());
//        helper.succeedWhen(() -> {
//            BlockEntity be = helper.getBlockEntity(pos);
//            if (be instanceof MilkReformerEntity milk){
//                if (milk.getItemHandler().getSlots() != 11) helper.fail("牛奶改质器槽位数量错误");
//                // 注入牛奶
//                int filled = milk.getMilkTank().fill(new FluidStack(HBMFluids.MILK.source().get(), 1000), IFluidHandler.FluidAction.EXECUTE);
//                if (filled != 1000) helper.fail("牛奶注入失败: " + filled);
//            } else {
//                helper.fail("牛奶改质器 BE 创建失败: " + be);
//            }
//        });
//    }
//
//    @GameTest(template = "hbm:empty")
//    public static void placeBlastFurnace(GameTestHelper helper){
//        BlockPos pos = new BlockPos(1, 2, 1);
//        helper.setBlock(pos, ModBlocks.MACHINE_BLAST_FURNACE.get().defaultBlockState());
//        helper.succeedWhen(() -> {
//            BlockEntity be = helper.getBlockEntity(pos);
//            if (be instanceof BlastFurnaceEntity furnace){
//                if (furnace.getItemHandler().getSlots() != 5) helper.fail("高炉槽位数量错误");
//            } else {
//                helper.fail("高炉 BE 创建失败: " + be);
//            }
//        });
//    }
//
//    @GameTest(template = "hbm:empty")
//    public static void placeMixer(GameTestHelper helper){
//        BlockPos pos = new BlockPos(1, 2, 1);
//        helper.setBlock(pos, ModBlocks.MACHINE_MIXER.get().defaultBlockState());
//        helper.succeedWhen(() -> {
//            BlockEntity be = helper.getBlockEntity(pos);
//            if (be instanceof MixerEntity mixer){
//                if (mixer.getItemHandler().getSlots() != 5) helper.fail("混合机槽位数量错误");
//                if (mixer.getRecipes().isEmpty()) helper.fail("混合机无可用配方");
//                mixer.getInputTank1().fill(new FluidStack(HBMFluids.WATER.source().get(), 1000), IFluidHandler.FluidAction.EXECUTE);
//            } else {
//                helper.fail("混合机 BE 创建失败: " + be);
//            }
//        });
//    }
//
//    @GameTest(template = "hbm:empty")
//    public static void placeCatalyticReformer(GameTestHelper helper){
//        BlockPos pos = new BlockPos(1, 2, 1);
//        helper.setBlock(pos, ModBlocks.MACHINE_CATALYTIC_REFORMER.get().defaultBlockState());
//        helper.succeedWhen(() -> {
//            BlockEntity be = helper.getBlockEntity(pos);
//            if (be instanceof CatalyticReformerEntity ref){
//                if (ref.getItemHandler().getSlots() != 11) helper.fail("催化重整器槽位数量错误");
//                if (ref.getRecipes().isEmpty()) helper.fail("催化重整器无可用配方");
//            } else {
//                helper.fail("催化重整器 BE 创建失败: " + be);
//            }
//        });
//    }
//
//    @GameTest(template = "hbm:empty")
//    public static void placeVacuumDistill(GameTestHelper helper){
//        BlockPos pos = new BlockPos(1, 2, 1);
//        helper.setBlock(pos, ModBlocks.MACHINE_VACUUM_DISTILL.get().defaultBlockState());
//        helper.succeedWhen(() -> {
//            BlockEntity be = helper.getBlockEntity(pos);
//            if (be instanceof VacuumDistillEntity distill){
//                if (distill.getItemHandler().getSlots() != 12) helper.fail("真空蒸馏塔槽位数量错误");
//                if (distill.getRecipes().isEmpty()) helper.fail("真空蒸馏塔无可用配方");
//            } else {
//                helper.fail("真空蒸馏塔 BE 创建失败: " + be);
//            }
//        });
//    }
//
//    @GameTest(template = "hbm:empty")
//    public static void placeGasCent(GameTestHelper helper){
//        BlockPos pos = new BlockPos(1, 2, 1);
//        helper.setBlock(pos, ModBlocks.MACHINE_GASCENT.get().defaultBlockState());
//        helper.succeedWhen(() -> {
//            BlockEntity be = helper.getBlockEntity(pos);
//            if (be instanceof GasCentEntity cent){
//                if (cent.getItemHandler().getSlots() != 7) helper.fail("气体离心机槽位数量错误");
//                if (cent.getRecipes().isEmpty()) helper.fail("气体离心机无可用配方");
//            } else {
//                helper.fail("气体离心机 BE 创建失败: " + be);
//            }
//        });
//    }
//
//    @GameTest(template = "hbm:empty")
//    public static void placeCryoDistill(GameTestHelper helper){
//        BlockPos pos = new BlockPos(1, 2, 1);
//        helper.setBlock(pos, ModBlocks.MACHINE_CRYO_DISTILL.get().defaultBlockState());
//        helper.succeedWhen(() -> {
//            BlockEntity be = helper.getBlockEntity(pos);
//            if (be instanceof CryoDistillEntity cryo){
//                if (cryo.getItemHandler().getSlots() != 11) helper.fail("低温蒸馏器槽位数量错误");
//                if (cryo.getRecipes().isEmpty()) helper.fail("低温蒸馏器无可用配方");
//            } else {
//                helper.fail("低温蒸馏器 BE 创建失败: " + be);
//            }
//        });
//    }
//
//    @GameTest(template = "hbm:empty")
//    public static void placeCombustionEngine(GameTestHelper helper){
//        BlockPos pos = new BlockPos(1, 2, 1);
//        helper.setBlock(pos, ModBlocks.MACHINE_COMBUSTION_ENGINE.get().defaultBlockState());
//        helper.succeedWhen(() -> {
//            BlockEntity be = helper.getBlockEntity(pos);
//            if (be instanceof CombustionEngineEntity engine){
//                if (engine.getItemHandler().getSlots() != 5) helper.fail("内燃机槽位数量错误");
//                engine.getTank().fill(new FluidStack(HBMFluids.DIESEL.source().get(), 1000), IFluidHandler.FluidAction.EXECUTE);
//            } else {
//                helper.fail("内燃机 BE 创建失败: " + be);
//            }
//        });
//    }
//
//    @GameTest(template = "hbm:empty")
//    public static void placeStirling(GameTestHelper helper){
//        BlockPos pos = new BlockPos(1, 2, 1);
//        helper.setBlock(pos, ModBlocks.MACHINE_STIRLING.get().defaultBlockState());
//        helper.succeedWhen(() -> {
//            BlockEntity be = helper.getBlockEntity(pos);
//            if (be instanceof StirlingEntity stirling){
//                if (!stirling.hasCog) helper.fail("斯特林机缺少齿轮");
//            } else {
//                helper.fail("斯特林机 BE 创建失败: " + be);
//            }
//        });
//    }
//
//    @GameTest(template = "hbm:empty")
//    public static void placeSawmill(GameTestHelper helper){
//        BlockPos pos = new BlockPos(1, 2, 1);
//        helper.setBlock(pos, ModBlocks.MACHINE_SAWMILL.get().defaultBlockState());
//        helper.succeedWhen(() -> {
//            BlockEntity be = helper.getBlockEntity(pos);
//            if (be instanceof SawmillEntity sawmill){
//                if (sawmill.getItemHandler().getSlots() != 3) helper.fail("锯木机槽位数量错误");
//                sawmill.getItemHandler().setStackInSlot(0, new ItemStack(Blocks.OAK_LOG));
//                if (sawmill.getOutput(new ItemStack(Blocks.OAK_LOG)) == null) helper.fail("锯木机无原木配方");
//            } else {
//                helper.fail("锯木机 BE 创建失败: " + be);
//            }
//        });
//    }
//
//    @GameTest(template = "hbm:empty")
//    public static void placeBreedingReactor(GameTestHelper helper){
//        BlockPos pos = new BlockPos(1, 2, 1);
//        helper.setBlock(pos, ModBlocks.MACHINE_REACTOR_BREEDING.get().defaultBlockState());
//        helper.succeedWhen(() -> {
//            BlockEntity be = helper.getBlockEntity(pos);
//            if (be instanceof BreedingReactorEntity reactor){
//                if (reactor.getItemHandler().getSlots() != 2) helper.fail("增殖反应堆槽位数量错误");
//            } else {
//                helper.fail("增殖反应堆 BE 创建失败: " + be);
//            }
//        });
//    }
//
//    @GameTest(template = "hbm:empty")
//    public static void placeSteamEngine(GameTestHelper helper){
//        BlockPos pos = new BlockPos(1, 2, 1);
//        helper.setBlock(pos, ModBlocks.MACHINE_STEAM_ENGINE.get().defaultBlockState());
//        helper.succeedWhen(() -> {
//            BlockEntity be = helper.getBlockEntity(pos);
//            if (be instanceof SteamEngineEntity engine){
//                if (engine.getSteamTank() == null) helper.fail("蒸汽机罐体创建失败");
//            } else {
//                helper.fail("蒸汽机 BE 创建失败: " + be);
//            }
//        });
//    }
//
//    @GameTest(template = "hbm:empty")
//    public static void placeDeuteriumExtractor(GameTestHelper helper){
//        BlockPos pos = new BlockPos(1, 2, 1);
//        helper.setBlock(pos, ModBlocks.MACHINE_DEUTERIUM_EXTRACTOR.get().defaultBlockState());
//        helper.succeedWhen(() -> {
//            BlockEntity be = helper.getBlockEntity(pos);
//            if (be instanceof DeuteriumExtractorEntity ext){
//                ext.getWaterTank().fill(new FluidStack(HBMFluids.WATER.source().get(), 1000), IFluidHandler.FluidAction.EXECUTE);
//            } else {
//                helper.fail("氘提取器 BE 创建失败: " + be);
//            }
//        });
//    }
//
//    @GameTest(template = "hbm:empty")
//    public static void placeDecon(GameTestHelper helper){
//        BlockPos pos = new BlockPos(1, 2, 1);
//        helper.setBlock(pos, ModBlocks.MACHINE_DECON.get().defaultBlockState());
//        helper.succeedWhen(() -> {
//            BlockEntity be = helper.getBlockEntity(pos);
//            if (be instanceof DeconEntity) {
//                // 净化装置创建成功
//            } else {
//                helper.fail("净化装置 BE 创建失败: " + be);
//            }
//        });
//    }
//
//    @GameTest(template = "hbm:empty")
//    public static void placeCatalyticCracker(GameTestHelper helper){
//        BlockPos pos = new BlockPos(1, 2, 1);
//        helper.setBlock(pos, ModBlocks.MACHINE_CATALYTIC_CRACKER.get().defaultBlockState());
//        helper.succeedWhen(() -> {
//            BlockEntity be = helper.getBlockEntity(pos);
//            if (be instanceof CatalyticCrackerEntity cracker){
//                cracker.getInputTank().fill(new FluidStack(HBMFluids.OIL.source().get(), 1000), IFluidHandler.FluidAction.EXECUTE);
//                cracker.getSteamTank().fill(new FluidStack(HBMFluids.STEAM.source().get(), 1000), IFluidHandler.FluidAction.EXECUTE);
//            } else {
//                helper.fail("催化裂化塔 BE 创建失败: " + be);
//            }
//        });
//    }
//
//    @GameTest(template = "hbm:empty")
//    public static void placeHeatBoiler(GameTestHelper helper){
//        BlockPos pos = new BlockPos(1, 2, 1);
//        helper.setBlock(pos, ModBlocks.MACHINE_HEAT_BOILER.get().defaultBlockState());
//        helper.succeedWhen(() -> {
//            BlockEntity be = helper.getBlockEntity(pos);
//            if (be instanceof HeatBoilerEntity boiler){
//                boiler.getWaterTank().fill(new FluidStack(HBMFluids.WATER.source().get(), 1000), IFluidHandler.FluidAction.EXECUTE);
//            } else {
//                helper.fail("热锅炉 BE 创建失败: " + be);
//            }
//        });
//    }
//
//    @GameTest(template = "hbm:empty")
//    public static void placeFractionTower(GameTestHelper helper){
//        BlockPos pos = new BlockPos(1, 2, 1);
//        helper.setBlock(pos, ModBlocks.MACHINE_FRACTION_TOWER.get().defaultBlockState());
//        helper.succeedWhen(() -> {
//            BlockEntity be = helper.getBlockEntity(pos);
//            if (be instanceof FractionTowerEntity tower){
//                tower.getInputTank().fill(new FluidStack(HBMFluids.HEAVYOIL.source().get(), 1000), IFluidHandler.FluidAction.EXECUTE);
//            } else {
//                helper.fail("分馏塔 BE 创建失败: " + be);
//            }
//        });
//    }
//
//    @GameTest(template = "hbm:empty")
//    public static void placeAlkylation(GameTestHelper helper){
//        BlockPos pos = new BlockPos(1, 2, 1);
//        helper.setBlock(pos, ModBlocks.MACHINE_ALKYLATION.get().defaultBlockState());
//        helper.succeedWhen(() -> {
//            BlockEntity be = helper.getBlockEntity(pos);
//            if (be instanceof AlkylationEntity alkyl){
//                alkyl.getInputTank().fill(new FluidStack(HBMFluids.CHLOROMETHANE.source().get(), 1000), IFluidHandler.FluidAction.EXECUTE);
//            } else {
//                helper.fail("烷基化装置 BE 创建失败: " + be);
//            }
//        });
//    }
//
//    @GameTest(template = "hbm:empty")
//    public static void placeBigAssTank(GameTestHelper helper){
//        BlockPos pos = new BlockPos(1, 2, 1);
//        helper.setBlock(pos, ModBlocks.MACHINE_BIGASS_TANK.get().defaultBlockState());
//        helper.succeedWhen(() -> {
//            BlockEntity be = helper.getBlockEntity(pos);
//            if (be instanceof BigAssTankEntity tank){
//                tank.getTank().fill(new FluidStack(HBMFluids.WATER.source().get(), 10000), IFluidHandler.FluidAction.EXECUTE);
//            } else {
//                helper.fail("大型储罐 BE 创建失败: " + be);
//            }
//        });
//    }
//
//    @GameTest(template = "hbm:empty")
//    public static void placeSolarBoiler(GameTestHelper helper){
//        BlockPos pos = new BlockPos(1, 2, 1);
//        helper.setBlock(pos, ModBlocks.MACHINE_SOLAR_BOILER.get().defaultBlockState());
//        helper.succeedWhen(() -> {
//            BlockEntity be = helper.getBlockEntity(pos);
//            if (be instanceof SolarBoilerEntity boiler){
//                boiler.getWaterTank().fill(new FluidStack(HBMFluids.WATER.source().get(), 100), IFluidHandler.FluidAction.EXECUTE);
//            } else {
//                helper.fail("太阳能锅炉 BE 创建失败: " + be);
//            }
//        });
//    }
//
//    @GameTest(template = "hbm:empty")
//    public static void placeSiren(GameTestHelper helper){
//        BlockPos pos = new BlockPos(1, 2, 1);
//        helper.setBlock(pos, ModBlocks.MACHINE_SIREN.get().defaultBlockState());
//        helper.succeedWhen(() -> {
//            BlockEntity be = helper.getBlockEntity(pos);
//            if (be instanceof SirenEntity) {
//                // 警报器创建成功
//            } else {
//                helper.fail("警报器 BE 创建失败: " + be);
//            }
//        });
//    }
//}
