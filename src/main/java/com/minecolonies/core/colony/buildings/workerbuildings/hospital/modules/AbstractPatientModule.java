package com.minecolonies.core.colony.buildings.workerbuildings.hospital.modules;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.workerbuildings.hospital.modules.IPatientModule;
import com.minecolonies.api.colony.buildings.workerbuildings.hospital.registry.PatientTypeRegistries.PatientTypeEntry;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingHospital;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public abstract class AbstractPatientModule implements IPatientModule
{
    /**
     * NBT tags.
     */
    private static final String TAG_CITIZEN_ID = "citizenId";

    private final PatientTypeEntry registryEntry;
    private final int              citizenId;

    protected AbstractPatientModule(final PatientTypeEntry registryEntry, final CompoundTag compound)
    {
        this(registryEntry, compound.getInt(TAG_CITIZEN_ID));
    }

    protected AbstractPatientModule(final PatientTypeEntry registryEntry, final int citizenId)
    {
        this.registryEntry = registryEntry;
        this.citizenId = citizenId;
    }

    @Override
    public PatientTypeEntry getRegistryEntry()
    {
        return registryEntry;
    }

    @Override
    public int getCitizenId()
    {
        return citizenId;
    }

    @Override
    public ICitizenData resolveCitizen(final IColony colony)
    {
        return colony.getCitizenManager().getCivilian(getCitizenId());
    }

    @Override
    public CompoundTag serializeNBT()
    {
        final CompoundTag compound = new CompoundTag();
        compound.putInt(TAG_CITIZEN_ID, citizenId);
        return compound;
    }

    @Override
    public void onEnterBed(final BuildingHospital hospital, final BlockPos bedPos)
    {
        resolveCitizen(hospital.getColony()).getEntity().ifPresent(entity -> entity.getCitizenSleepHandler().trySleep(bedPos));
    }

    @Override
    public void onFinish(final BuildingHospital hospital)
    {
        resolveCitizen(hospital.getColony()).getEntity().ifPresent(entity -> entity.getCitizenSleepHandler().onWakeUp());
    }

    @Override
    public int compareTo(@NotNull final IPatientModule o)
    {
        return Comparator.comparing(IPatientModule::getRegistryEntry)
                 .thenComparing(IPatientModule::getCitizenId)
                 .compare(this, o);
    }
}
