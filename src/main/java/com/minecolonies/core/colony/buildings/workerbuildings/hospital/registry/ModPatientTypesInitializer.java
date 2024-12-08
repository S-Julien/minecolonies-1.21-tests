package com.minecolonies.core.colony.buildings.workerbuildings.hospital.registry;

import com.minecolonies.api.colony.buildings.workerbuildings.hospital.registry.PatientTypeRegistries;
import com.minecolonies.api.colony.buildings.workerbuildings.hospital.registry.PatientTypeRegistries.PatientModuleProducer;
import com.minecolonies.api.colony.buildings.workerbuildings.hospital.registry.PatientTypeRegistries.PatientTypeEntry;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.colony.buildings.workerbuildings.hospital.modules.HurtPatientModule;
import com.minecolonies.core.colony.buildings.workerbuildings.hospital.modules.SickPatientModule;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModPatientTypesInitializer
{
    public static final DeferredRegister<PatientTypeEntry> DEFERRED_REGISTER =
      DeferredRegister.create(new ResourceLocation(Constants.MOD_ID, "patient_types"), Constants.MOD_ID);
    static
    {
        PatientTypeRegistries.hurt = createEntry(PatientTypeRegistries.HURT_ID, HurtPatientModule::new);
        PatientTypeRegistries.sick = createEntry(PatientTypeRegistries.SICK_ID, SickPatientModule::new);
    }
    private ModPatientTypesInitializer()
    {
        throw new IllegalStateException("Tried to initialize: ModPatientTypesInitializer but this is a Utility class.");
    }

    private static RegistryObject<PatientTypeEntry> createEntry(final ResourceLocation registryName, final PatientModuleProducer producer)
    {
        final PatientTypeRegistries.PatientTypeEntry entry = new PatientTypeRegistries.PatientTypeEntry(registryName, producer);
        return DEFERRED_REGISTER.register(registryName.getPath(), () -> entry);
    }
}
