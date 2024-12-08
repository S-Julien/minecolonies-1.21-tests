package com.minecolonies.api.colony.buildings.workerbuildings.hospital.registry;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.colony.buildings.workerbuildings.hospital.modules.IPatientModule;
import com.minecolonies.api.util.constant.Constants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Registry implementation for hospital patient types.
 */
public class PatientTypeRegistries
{
    public static final ResourceLocation HURT_ID = new ResourceLocation(Constants.MOD_ID, "hurt");
    public static final ResourceLocation SICK_ID = new ResourceLocation(Constants.MOD_ID, "sick");

    public static RegistryObject<PatientTypeEntry> hurt;
    public static RegistryObject<PatientTypeEntry> sick;

    /**
     * Get the patient type registry.
     *
     * @return the patient type registry.
     */
    public static IForgeRegistry<PatientTypeEntry> getPatientTypeRegistries()
    {
        return IMinecoloniesAPI.getInstance().getPatientTypeRegistry();
    }

    @FunctionalInterface
    public interface PatientModuleProducer extends Function<CompoundTag, IPatientModule>
    {}

    /**
     * Entry for the hospital patient types.
     */
    public static class PatientTypeEntry implements Comparable<PatientTypeEntry>
    {
        private final ResourceLocation      registryName;
        private final PatientModuleProducer patientModuleProducer;

        /**
         * Default internal constructor.
         */
        public PatientTypeEntry(final ResourceLocation registryName, final PatientModuleProducer patientModuleProducer)
        {
            this.registryName = registryName;
            this.patientModuleProducer = patientModuleProducer;
        }

        /**
         * Produces a patient module based on their type.
         *
         * @param compound the extra compound data.
         * @return the patient module.
         */
        public IPatientModule producePatientModule(final CompoundTag compound)
        {
            return patientModuleProducer.apply(compound);
        }

        /**
         * Get the assigned registry name.
         *
         * @return the resource location.
         */
        public ResourceLocation getRegistryName()
        {
            return registryName;
        }

        @Override
        public int hashCode()
        {
            int result = registryName.hashCode();
            result = 31 * result + patientModuleProducer.hashCode();
            return result;
        }

        @Override
        public boolean equals(final Object o)
        {
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }

            final PatientTypeEntry that = (PatientTypeEntry) o;
            return registryName.equals(that.registryName) && patientModuleProducer.equals(that.patientModuleProducer);
        }

        @Override
        public int compareTo(@NotNull final PatientTypeRegistries.PatientTypeEntry o)
        {
            return this.registryName.compareTo(o.registryName);
        }
    }
}
