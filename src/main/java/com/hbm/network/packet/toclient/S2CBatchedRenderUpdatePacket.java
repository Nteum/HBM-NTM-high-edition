package com.hbm.network.packet.toclient;

import com.hbm.network.IHBMMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class S2CBatchedRenderUpdatePacket implements IHBMMessage {

    private static final int INTS_PER_RANGE = 6;

    private final int[] packedRanges;

    public S2CBatchedRenderUpdatePacket(List<RenderRange> ranges) {
        this(flattenRanges(ranges));
    }

    private S2CBatchedRenderUpdatePacket(int[] packedRanges) {
        this.packedRanges = packedRanges;
    }

    public static S2CBatchedRenderUpdatePacket decode(FriendlyByteBuf buf) {
        return new S2CBatchedRenderUpdatePacket(buf.readVarIntArray());
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarIntArray(packedRanges);
    }

    @Override

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.level == null || minecraft.levelRenderer == null) {
                return;
            }

            for (int i = 0; i + (INTS_PER_RANGE - 1) < packedRanges.length; i += INTS_PER_RANGE) {
                markBlockRangeForRenderUpdate(
                        minecraft,
                        packedRanges[i],
                        packedRanges[i + 1],
                        packedRanges[i + 2],
                        packedRanges[i + 3],
                        packedRanges[i + 4],
                        packedRanges[i + 5]
                );
            }
        });
        context.setPacketHandled(true);
    }

    private static void markBlockRangeForRenderUpdate(Minecraft minecraft, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        minecraft.levelRenderer.setBlocksDirty(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private static int[] flattenRanges(List<RenderRange> ranges) {
        int[] packed = new int[ranges.size() * INTS_PER_RANGE];
        int index = 0;
        for (RenderRange range : ranges) {
            packed[index++] = range.minX();
            packed[index++] = range.minY();
            packed[index++] = range.minZ();
            packed[index++] = range.maxX();
            packed[index++] = range.maxY();
            packed[index++] = range.maxZ();
        }
        return packed;
    }

    public record RenderRange(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
    }
}
