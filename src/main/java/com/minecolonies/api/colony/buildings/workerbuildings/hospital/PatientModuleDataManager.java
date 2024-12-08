package com.minecolonies.api.colony.buildings.workerbuildings.hospital;

import com.minecolonies.api.colony.buildings.workerbuildings.hospital.modules.IPatientModule;
import com.minecolonies.api.colony.buildings.workerbuildings.hospital.registry.PatientTypeRegistries;
import com.minecolonies.api.colony.buildings.workerbuildings.hospital.registry.PatientTypeRegistries.PatientTypeEntry;
import com.minecolonies.api.util.Log;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class for reading and writing patient modules from NBT.
 */
public final class PatientModuleDataManager
{
    /**
     * NBT tags
     */
    private static final String TAG_MODULE_TYPE = "type";
    private static final String TAG_MODULE_DATA = "data";

    /**
     * Read a patient module from NBT.
     *
     * @param parent the parent NBT object.
     * @param key    the key to read from.
     * @return the output module, if able to be parsed.
     */
    public static @Nullable IPatientModule readPatientModule(final @NotNull CompoundTag parent, final @NotNull String key)
    {
        final CompoundTag data = parent.getCompound(key);
        final ResourceLocation type = new ResourceLocation(data.getString(TAG_MODULE_TYPE));
        final PatientTypeEntry patientTypeEntry = PatientTypeRegistries.getPatientTypeRegistries().getValue(type);
        if (patientTypeEntry == null)
        {
            return null;
        }

        try
        {
            return patientTypeEntry.producePatientModule(data.getCompound(TAG_MODULE_DATA));
        }
        catch (Exception ex)
        {
            Log.getLogger().warn("Failure parsing patient type entry: ", ex);
            return null;
        }
    }

    /**
     * Write a patient module to NBT.
     *
     * @param parent the parent NBT object.
     * @param key    the key to write to.
     * @param module the input module to write.
     */
    public static void writePatientModule(final @NotNull CompoundTag parent, final @NotNull String key, final @NotNull IPatientModule module)
    {
        final CompoundTag compound = new CompoundTag();
        compound.putString(TAG_MODULE_TYPE, module.getRegistryEntry().getRegistryName().toString());
        compound.put(TAG_MODULE_DATA, module.serializeNBT());
        parent.put(key, compound);
    }
}
