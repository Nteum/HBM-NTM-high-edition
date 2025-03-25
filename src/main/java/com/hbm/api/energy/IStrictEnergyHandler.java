package com.hbm.api.energy;

import com.hbm.api.Action;
import com.hbm.api.annotations.NothingNullByDefault;
import com.hbm.api.math.LongTransferUtils;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
//ref:mek
@NothingNullByDefault
@AutoRegisterCapability
public interface IStrictEnergyHandler {

    //这个handler管理的container总数量
    int getEnergyContainerCount();

    /**
     * Returns the energy stored in a given container.
     *
     * @param container Container to query.
     *
     * @return Energy in a given container. 0 if the container has no energy stored.
     */
    long getEnergy(int container);

    /**
     * Overrides the energy stored in the given container. This method may throw an error if it is called unexpectedly.
     *
     * @param container Container to modify
     * @param energy    Energy to set the container to (may be 0).
     *
     * @throws RuntimeException if the handler is called in a way that the handler was not expecting. Such as if it was not expecting this to be called at all.
     **/
    void setEnergy(int container, long energy);

    /**
     * Retrieves the maximum amount of energy that can be stored in a given container.
     *
     * <p>
     * <strong>IMPORTANT:</strong> This {@link long} <em>MUST NOT</em> be modified. This method is not for altering internal max energy. Any implementers who are
     * able to detect modification via this method should throw an exception. It is ENTIRELY reasonable and likely that the value returned here will be a copy.
     * </p>
     *
     * <p>
     * <strong><em>SERIOUSLY: DO NOT MODIFY THE RETURNED FLOATING LONG</em></strong>
     * </p>
     *
     * @param container Container to query.
     *
     * @return The maximum energy that can be stored in the container.
     */
    long getMaxEnergy(int container);

    /**
     * Retrieves the amount of energy that is needed to fill a given container.
     *
     * <p>
     * <strong>IMPORTANT:</strong> This {@link long} <em>MUST NOT</em> be modified. This method is not for altering remaining needed amount. Any implementers who
     * are able to detect modification via this method should throw an exception. It is ENTIRELY reasonable and likely that the value returned here will be a copy.
     * </p>
     *
     * <p>
     * <strong><em>SERIOUSLY: DO NOT MODIFY THE RETURNED FLOATING LONG</em></strong>
     * </p>
     *
     * @param container Container to query.
     *
     * @return The energy needed to fill the container.
     */
    long getNeededEnergy(int container);

    /**
     * <p>
     * Inserts energy into a given container and return the remainder. The {@link long} <em>should not</em> be modified in this function!
     * </p>
     *
     * @param container Container to insert to.
     * @param amount    Energy to insert. This must not be modified by the container.
     * @param action    The action to perform, either {@link Action#EXECUTE} or {@link Action#SIMULATE}
     *
     * @return The remaining energy that was not inserted (if the entire amount is accepted, then return {@link long#ZERO}). The returned {@link long} can
     * be safely modified afterwards.
     */
    long insertEnergy(int container, long amount, Action action);

    /**
     * Extracts energy from a specific container in this handler.
     * <p>
     * The returned value must be 0 if nothing is extracted, otherwise its must be less than or equal to {@code amount}.
     * </p>
     *
     * @param container Container to extract from.
     * @param amount    Amount of energy to extract (may be greater than the current stored amount or the container's capacity) This must not be modified by the handler.
     * @param action    The action to perform, either {@link Action#EXECUTE} or {@link Action#SIMULATE}
     *
     * @return Energy extracted from the container, must be 0 if no energy can be extracted. The returned {@link long} can be safely
     * modified after, so the container should return a new or copied {@link long}.
     */
    long extractEnergy(int container, long amount, Action action);

    //对所有container整体输入能量
    default long insertEnergy(long amount, Action action) {
        return LongTransferUtils.insert(amount, action, this::getEnergyContainerCount, this::getEnergy, this::insertEnergy);
    }

    //对所有container整体移除能量
    default long extractEnergy(long amount, Action action) {
        return LongTransferUtils.extract(amount, action, this::getEnergyContainerCount, this::extractEnergy);
    }
}