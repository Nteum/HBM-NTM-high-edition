package com.hbm.capabilities.network.validator;


import com.hbm.capabilities.network.network.DynamicNetwork;
import com.hbm.capabilities.network.transmitter.MechanicalPipe;
import com.hbm.capabilities.network.transmitter.Transmitter;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

//transmitter验证器，似乎只有流体才真的有用
public class CompatibleTransmitterValidator<ACCEPTOR, NETWORK extends DynamicNetwork<ACCEPTOR, NETWORK, TRANSMITTER>,
        TRANSMITTER extends Transmitter<ACCEPTOR, NETWORK, TRANSMITTER>> {
    public boolean isNetworkCompatible(NETWORK net) {
        return true;
    }

    /**
     * @param transmitter Orphan transmitter to check if it is valid against this validator.
     */
    public boolean isTransmitterCompatible(Transmitter<?, ?, ?> transmitter) {
        return true;
    }
//    public static class CompatibleFluidTransmitterValidator extends CompatibleTransmitterValidator<IFluidHandler, FluidNetwork, MechanicalPipe> {
//
//        private FluidStack buffer;
//
//        public CompatibleFluidTransmitterValidator(MechanicalPipe transmitter) {
//            buffer = transmitter.getBufferWithFallback();
//        }
//
//        private boolean compareBuffers(FluidStack otherBuffer) {
//            if (buffer.isEmpty()) {
//                buffer = otherBuffer;
//                return true;
//            }
//            return otherBuffer.isEmpty() || buffer.isFluidEqual(otherBuffer);
//        }
//
//        @Override
//        public boolean isNetworkCompatible(FluidNetwork network) {
//            if (super.isNetworkCompatible(network)) {
//                FluidStack otherBuffer;
//                if (network.getTransmitterValidator() instanceof CompatibleFluidTransmitterValidator validator) {
//                    //Null check it, but use instanceof to double-check it is actually the expected type
//                    otherBuffer = validator.buffer;
//                } else {
//                    otherBuffer = network.getBuffer();
//                    if (otherBuffer.isEmpty() && network.getPrevTransferAmount() > 0) {
//                        otherBuffer = network.lastFluid;
//                    }
//                }
//                return compareBuffers(otherBuffer);
//            }
//            return false;
//        }
//
//        @Override
//        public boolean isTransmitterCompatible(Transmitter<?, ?, ?> transmitter) {
//            return super.isTransmitterCompatible(transmitter) && transmitter instanceof MechanicalPipe pipe && compareBuffers(pipe.getBufferWithFallback());
//        }
//    }
}
