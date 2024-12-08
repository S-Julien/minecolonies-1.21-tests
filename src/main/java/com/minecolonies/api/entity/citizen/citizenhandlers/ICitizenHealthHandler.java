package com.minecolonies.api.entity.citizen.citizenhandlers;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

/**
 * Citizen health handler interface.
 */
public interface ICitizenHealthHandler
{
    /**
     * Check if the citizen is hurt and prefers to be healed.
     *
     * @return true if so.
     */
    boolean isHurt();

    /**
     * Check if the citizen is hurt and requires to be healed.
     *
     * @return true if so.
     */
    boolean canContinueToWork();

    /**
     * Check if the citizen is currently being healed at a hospital.
     *
     * @return the hospital building location.
     */
    BlockPos getActiveHospital();

    void setActiveHospital(final BlockPos hospitalPos);

    void autoHeal();

    void heal(float amount);

    void healFully();

    /**
     * Write the handler to NBT.
     *
     * @param compound the nbt to write it to.
     */
    void write(final CompoundTag compound);

    /**
     * Read the handler from NBT.
     *
     * @param compound the nbt to read it from.
     */
    void read(final CompoundTag compound);

    /**
     * Reset internal fields.
     */
    void reset();
}
