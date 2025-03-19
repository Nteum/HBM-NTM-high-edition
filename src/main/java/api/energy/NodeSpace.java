//package com.hbm.api.energy;
//
//import net.minecraft.core.BlockPos;
//
////存储电网的所有节点
//public class NodeSpace {
//    public static class PowerNode {
//
//        public BlockPos[] positions;
//        public DirPos[] connections;
//        public PowerNet net;
//        public boolean expired = false;
//        /**
//         * Okay so here's the deal: The code has shit idiot brain fungus. I don't know why. I re-tested every part involved several times.
//         * I don't know why. But for some reason, during neighbor checks, on certain arbitrary fucking places, the joining operation just fails.
//         * Disallowing nodes to create new networks fixed the problem completely, which is hardly surprising since they wouldn't be able to make
//         * a new net anyway and they will re-check neighbors until a net is found, so the solution is tautological in nature. So I tried limiting
//         * creation of new networks. Didn't work. So what's there left to do? Hand out a mark to any node that has changed networks, and let those
//         * recently modified nodes do another re-check. This creates a second layer of redundant operations, and in theory doubles (in practice,
//         * it might be an extra 20% due to break-off section sizes) the amount of CPU time needed for re-building the networks after joining or
//         * breaking, but it seems to allow those parts to connect back to their neighbor nets as they are supposed to. I am not proud of this solution,
//         * this issue shouldn't exist to begin with and I am going fucking insane but it is what it is.
//         */
//        public boolean recentlyChanged = true;
//
//        public PowerNode(BlockPos... positions) {
//            this.positions = positions;
//        }
//
//        public PowerNode setConnections(DirPos... connections) {
//            this.connections = connections;
//            return this;
//        }
//
//        public PowerNode addConnection(DirPos connection) {
//            DirPos[] newCons = new DirPos[this.connections.length + 1];
//            for(int i = 0; i < this.connections.length; i++) newCons[i] = this.connections[i];
//            newCons[newCons.length - 1] = connection;
//            this.connections = newCons;
//            return this;
//        }
//
//        public boolean hasValidNet() {
//            return this.net != null && this.net.isValid();
//        }
//
//        public void setNet(PowerNet net) {
//            this.net = net;
//            this.recentlyChanged = true;
//        }
//    }
//}
