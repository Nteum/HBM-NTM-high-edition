package com.hbm.capabilities.resolver;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.List;

public class SidedInvWrapper extends SidedCapabilityWrapper<InvWrapper> {
    public SidedInvWrapper(InvWrapper content) {
        super(content);
    }
}
