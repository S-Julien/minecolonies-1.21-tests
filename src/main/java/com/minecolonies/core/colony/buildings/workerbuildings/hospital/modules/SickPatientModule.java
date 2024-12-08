package com.minecolonies.core.colony.buildings.workerbuildings.hospital.modules;

import com.minecolonies.api.colony.buildings.workerbuildings.hospital.registry.PatientTypeRegistries;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingHospital;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class SickPatientModule extends AbstractPatientModule
{
    public SickPatientModule(final int citizenId)
    {
        super(PatientTypeRegistries.sick.get(), citizenId);
    }

    public SickPatientModule(final CompoundTag compound)
    {
        super(PatientTypeRegistries.sick.get(), compound);
    }

    @Override
    public void onFinish(final BuildingHospital hospital)
    {
        resolveCitizen(hospital.getColony()).getEntity().ifPresent(entity -> {
            entity.releaseUsingItem();
            entity.stopUsingItem();
            entity.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        });
    }
}
