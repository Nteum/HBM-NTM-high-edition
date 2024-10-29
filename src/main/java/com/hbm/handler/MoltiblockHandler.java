//package com.hbm.handler;
//
//import com.hbm.block.machine.BaseMachineBlock;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.core.Vec3i;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.Block;
//
//public class MoltiblockHandler {
//    //判断方块占用的空间中是否有阻碍放置的方块。
//    public static boolean checkSpace(Level world, BlockPos pos, int[] dim, Vec3i offset, Direction dir) {
//
//        if(dim == null || dim.length != 6)
//            return false;
//
//        int count = 0, x = pos.getX(), y = pos.getY(), z = pos.getZ();
//
//        int[] rot = rotate(dim, dir);
//
//        for(int a = x - rot[4]; a <= x + rot[5]; a++) {
//            for(int b = y - rot[1]; b <= y + rot[0]; b++) {
//                for(int c = z - rot[2]; c <= z + rot[3]; c++) {
//                    if(!world.getBlockState(pos).canBeReplaced()) {
//                        return false;
//                    }
//
//                    count++;
//                    //方块上限是2000
//                    if(count > 2000) {
//                        System.out.println("checkspace: ded " + a + " " + b + " " + c + " " + x + " " + y + " " + z);
//                        return false;
//                    }
//                }
//            }
//        }
//        return true;
//    }
//    //填充方块对应的空间
//    public static void fillSpace(Level world, BlockPos pos, int[] dim, Block block, Direction dir) {
//        if(dim == null || dim.length != 6)
//            return;
//
//        int count = 0, x = pos.getX(), y = pos.getY(), z = pos.getZ();
//
//        int[] rot = rotate(dim, dir);
//
//        BaseMachineBlock.safeRem = true;
//
//        for(int a = x - rot[4]; a <= x + rot[5]; a++) {
//            for(int b = y - rot[1]; b <= y + rot[0]; b++) {
//                for(int c = z - rot[2]; c <= z + rot[3]; c++) {
//                    //放置方块
//                    world.setBlock(pos,block.defaultBlockState(),3);
//
//                    count++;
//                    if(count > 2000) {
//                        System.out.println("fillspace: ded " + a + " " + b + " " + c + " " + x + " " + y + " " + z);
//                        BaseMachineBlock.safeRem = false;
//                        return;
//                    }
//                }
//            }
//        }
//
//        BaseMachineBlock.safeRem = false;
//    }
//    //清空多方块的所有方块
//    @Deprecated
//    public static void emptySpace(Level world, BlockPos pos, int[] dim, Block block, Direction dir) {
//
//        if(dim == null || dim.length != 6)
//            return;
//
//        int count = 0;
//
//        System.out.println("emptyspace is deprecated and shouldn't even be executed");
//
//        int[] rot = rotate(dim, dir);
//
//        for(int a = x - rot[4]; a <= x + rot[5]; a++) {
//            for(int b = y - rot[1]; b <= y + rot[0]; b++) {
//                for(int c = z - rot[2]; c <= z + rot[3]; c++) {
//
//                    if(world.getBlock(a, b, c) == block)
//                        world.setBlockToAir(a, b, c);
//
//                    count++;
//
//                    if(count > 2000) {
//                        System.out.println("emptyspace: ded " + a + " " + b + " " + c);
//                        return;
//                    }
//                }
//            }
//        }
//    }
//    //根据方向替换各方向延伸的值
//    public static int[] rotate(int[] dim, Direction dir) {
//        if(dim == null)
//            return null;
//
//        if(dir == ForgeDirection.SOUTH)
//            return dim;
//
//        if(dir == ForgeDirection.NORTH) {
//            //                 U       D       N       S       W       E
//            return new int[] { dim[0], dim[1], dim[3], dim[2], dim[5], dim[4] };
//        }
//
//        if(dir == ForgeDirection.EAST) {
//            //                 U       D       N       S       W       E
//            return new int[] { dim[0], dim[1], dim[5], dim[4], dim[2], dim[3] };
//        }
//
//        if(dir == ForgeDirection.WEST) {
//            //                 U       D       N       S       W       E
//            return new int[] { dim[0], dim[1], dim[4], dim[5], dim[3], dim[2] };
//        }
//
//        return dim;
//    }
//}
