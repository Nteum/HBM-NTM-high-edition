package com.hbm.render.util;

/**
 * Mixin-injected interface on {@link com.mojang.blaze3d.vertex.BufferBuilder}
 * that exposes the native address of the backing ByteBuffer and the write cursor,
 * allowing callers to write vertex data directly via Unsafe without a scratch copy.
 */
public interface DirectBufferAccess {

    /** Native address of the backing ByteBuffer's storage. Refreshed on buffer growth. */
    long hbm$bufferAddress();

    int hbm$nextElementByte();

    void hbm$setNextElementByte(int value);

    int hbm$vertices();

    void hbm$setVertices(int value);

    /** Ensure at least {@code bytes} additional bytes are available, growing if needed. */
    void hbm$ensureCapacity(int bytes);
}
