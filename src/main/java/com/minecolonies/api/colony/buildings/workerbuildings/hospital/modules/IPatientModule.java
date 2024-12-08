package com.minecolonies.api.colony.buildings.workerbuildings.hospital.modules;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.workerbuildings.hospital.registry.PatientTypeRegistries.PatientTypeEntry;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingHospital;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

public interface IPatientModule extends Comparable<IPatientModule>
{
    PatientTypeEntry getRegistryEntry();

    int getCitizenId();

    ICitizenData resolveCitizen(final IColony colony);

    CompoundTag serializeNBT();

    void onEnterBed(final BuildingHospital hospital, final BlockPos bedPos);

    void onFinish(final BuildingHospital hospital);
}
