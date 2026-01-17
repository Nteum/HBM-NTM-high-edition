package com.hbm.api.block;

/**
 * Implemented by block entities that wish to participate in the
 * Chicago Pile style neutron simulation. Streams that intersect
 * a receiver will call {@link #receiveNeutrons(int)} with the
 * calculated flux for that tick.
 */
public interface IPileNeutronReceiver {

    /**
     * @param amount number of neutrons hitting this receiver this tick.
     */
    void receiveNeutrons(int amount);
}
