package com.minecolonies.core.colony.buildings.workerbuildings.hospital.modules;

import com.minecolonies.api.colony.buildings.workerbuildings.hospital.registry.PatientTypeRegistries;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingHospital;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import static com.minecolonies.api.util.constant.StatisticsConstants.CITIZENS_HEALED;

public class HurtPatientModule extends AbstractPatientModule
{
    public HurtPatientModule(final int citizenId)
    {
        super(PatientTypeRegistries.hurt.get(), citizenId);
    }

    public HurtPatientModule(final CompoundTag compound)
    {
        super(PatientTypeRegistries.hurt.get(), compound);
    }

    @Override
    public void onEnterBed(final BuildingHospital hospital, final BlockPos bedPos)
    {
        super.onEnterBed(hospital, bedPos);
        resolveCitizen(hospital.getColony()).getEntity().ifPresent(entity -> {
            entity.getCitizenHealthHandler().setActiveHospital(hospital.getPosition());
        });
    }

    @Override
    public void onFinish(final BuildingHospital hospital)
    {
        super.onFinish(hospital);
        resolveCitizen(hospital.getColony()).getEntity().ifPresent(entity -> {
            entity.getCitizenHealthHandler().reset();
            entity.getCitizenHealthHandler().setActiveHospital(null);
        });
        hospital.getColony().getStatisticsManager().increment(CITIZENS_HEALED, hospital.getColony().getDay());
    }
}
