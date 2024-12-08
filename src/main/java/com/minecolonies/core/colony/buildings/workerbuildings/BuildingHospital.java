package com.minecolonies.core.colony.buildings.workerbuildings;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.workerbuildings.hospital.PatientModuleDataManager;
import com.minecolonies.api.colony.buildings.workerbuildings.hospital.modules.IPatientModule;
import com.minecolonies.api.colony.buildings.workerbuildings.hospital.registry.PatientTypeRegistries;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.constant.NbtTagConstants;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
import com.minecolonies.core.datalistener.model.Disease;
import com.minecolonies.core.datalistener.DiseasesListener;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;
import static com.minecolonies.api.util.constant.Suppression.OVERRIDE_EQUALS;

/**
 * Class of the hospital building.
 */
@SuppressWarnings(OVERRIDE_EQUALS)
public class BuildingHospital extends AbstractBuilding
{
    /**
     * NBT tags.
     */
    private static final String TAG_PATIENT = "patient";

    /**
     * The hospital string.
     */
    private static final String HOSPITAL_DESC = "hospital";

    /**
     * Max building level of the hospital.
     */
    private static final int MAX_BUILDING_LEVEL = 5;

    /**
     * Map from beds to patients, 0 is empty.
     */
    @NotNull
    private final BiMap<BlockPos, Integer> bedMap = HashBiMap.create();

    /**
     * Map of patients of this hospital.
     */
    @NotNull
    private final Map<Integer, PatientModuleNode> patients = new TreeMap<>();

    /**
     * Instantiates a new hospital building.
     *
     * @param c the colony.
     * @param l the location
     */
    public BuildingHospital(final IColony c, final BlockPos l)
    {
        super(c, l);
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return HOSPITAL_DESC;
    }

    @Override
    public int getMaxBuildingLevel()
    {
        return MAX_BUILDING_LEVEL;
    }

    @Override
    public void deserializeNBT(final CompoundTag compound)
    {
        super.deserializeNBT(compound);
        final Map<Integer, PatientModuleNode> patientModules = new TreeMap<>();
        final ListTag patientTagList = compound.getList(TAG_PATIENTS, Tag.TAG_COMPOUND);
        for (int i = 0; i < patientTagList.size(); ++i)
        {
            final CompoundTag patientCompound = patientTagList.getCompound(i);
            if (compound.contains(TAG_PATIENTS))
            {
                if (patientCompound.contains(TAG_ID))
                {
                    // TODO: 1.22 Remove NBT migration
                    final int patientId = patientCompound.getInt(TAG_ID);
                    patientModules.put(patientId, new PatientModuleNode(PatientTypeRegistries.sick.get().producePatientModule(patientCompound)));
                }
                else
                {
                    final IPatientModule patientModule = PatientModuleDataManager.readPatientModule(patientCompound, TAG_PATIENT);
                    if (patientModule != null)
                    {
                        patientModules.put(patientModule.getCitizenId(), new PatientModuleNode(patientModule));
                    }
                }
            }
        }
        patients.clear();
        patients.putAll(patientModules);

        final Map<BlockPos, Integer> beds = new TreeMap<>();
        final ListTag bedTagList = compound.getList(TAG_BEDS, Tag.TAG_COMPOUND);
        for (int i = 0; i < bedTagList.size(); ++i)
        {
            final CompoundTag bedCompound = bedTagList.getCompound(i);
            final BlockPos bedPos = BlockPosUtil.read(bedCompound, TAG_POS);
            final int citizenId = bedCompound.getInt(TAG_ID);
            if (patientModules.containsKey(citizenId))
            {
                beds.put(bedPos, citizenId);
            }
        }
        bedMap.clear();
        bedMap.putAll(beds);
    }

    @Override
    public CompoundTag serializeNBT()
    {
        final CompoundTag compound = super.serializeNBT();
        @NotNull final ListTag bedTagList = new ListTag();
        for (@NotNull final Map.Entry<BlockPos, Integer> entry : bedMap.entrySet())
        {
            final CompoundTag bedCompound = new CompoundTag();
            BlockPosUtil.write(bedCompound, NbtTagConstants.TAG_POS, entry.getKey());
            bedCompound.putInt(TAG_ID, entry.getValue());
            bedTagList.add(bedCompound);
        }
        compound.put(TAG_BEDS, bedTagList);

        @NotNull final ListTag patientTagList = new ListTag();
        for (final PatientModuleNode patient : patients.values())
        {
            final CompoundTag patientCompound = new CompoundTag();
            PatientModuleDataManager.writePatientModule(patientCompound, TAG_PATIENT, patient.module);
            patientTagList.add(patientCompound);
        }
        compound.put(TAG_PATIENTS, patientTagList);
        return compound;
    }

    @Override
    public void registerBlockPosition(@NotNull final BlockState blockState, @NotNull final BlockPos pos, @NotNull final Level world)
    {
        super.registerBlockPosition(blockState, pos, world);

        if (blockState.getBlock() instanceof BedBlock)
        {
            if (blockState.getValue(BedBlock.PART) == BedPart.HEAD)
            {
                bedMap.put(pos, null);
            }
        }
    }

    /**
     * Get the list of beds.
     *
     * @return immutable copy
     */
    @NotNull
    public List<BlockPos> getBedList()
    {
        return ImmutableList.copyOf(bedMap.keySet());
    }

    /**
     * Get the list of patient files.
     *
     * @return immutable copy.
     */
    public List<IPatientModule> getPatients()
    {
        return ImmutableList.copyOf(patients.values().stream().map(m -> m.module).toList());
    }

    /**
     * Register a patient to the hospital.

     * @param module the patient module.
     */
    public void addPatient(final IPatientModule module)
    {
        patients.put(module.getCitizenId(), new PatientModuleNode(module));
        assignBed(module);
    }

    /**
     * Remove a patient from the list.
     *
     * @param patient the patient to remove.
     */
    public void finishPatient(final IPatientModule patient)
    {
        finishPatient(patient.getCitizenId());
    }

    /**
     * Remove a patient from the list.
     *
     * @param citizenId the citizen id.
     */
    public void finishPatient(final int citizenId)
    {
        final PatientModuleNode remove = patients.remove(citizenId);
        if (remove != null)
        {
            remove.module.onFinish(this);
        }
        final BlockPos bedPos = bedMap.inverse().get(citizenId);
        if (bedPos != null)
        {
            setBedOccupation(bedPos, false);
        }
    }

    @Override
    public Map<Predicate<ItemStack>, Tuple<Integer, Boolean>> getRequiredItemsAndAmount()
    {
        final Map<Predicate<ItemStack>, Tuple<Integer, Boolean>> map = super.getRequiredItemsAndAmount();
        map.put(BuildingHospital::isCureItem, new Tuple<>(10, false));
        return map;
    }

    /**
     * Check if the given itemstack is a cure item.
     *
     * @param stack the stack to test.
     * @return true if so.
     */
    private static boolean isCureItem(final ItemStack stack)
    {
        for (final Disease disease : DiseasesListener.getDiseases())
        {
            for (final ItemStorage cureItem : disease.cureItems())
            {
                return Disease.isCureItem(stack, cureItem);
            }
        }
        return false;
    }

    @Override
    public void onColonyTick(final IColony colony)
    {
        super.onColonyTick(colony);
        assignBeds();
    }

    /**
     * Attempt to assign a bed to a single patient.
     *
     * @param patient the patient.
     */
    private void assignBed(final IPatientModule patient)
    {
        BlockPos bedToOccupy = null;
        for (final Entry<BlockPos, Integer> bedEntry : bedMap.entrySet())
        {
            if (bedEntry.getValue() == null)
            {
                bedToOccupy = bedEntry.getKey();
                break;
            }
        }

        if (bedToOccupy != null)
        {
            bedMap.forcePut(bedToOccupy, patient.getCitizenId());
            setBedOccupation(bedToOccupy, false);
            patient.onEnterBed(this, bedToOccupy);
        }
    }

    private void assignBeds()
    {
        for (final PatientModuleNode patient : patients.values())
        {
            if (!bedMap.containsValue(patient.module.getCitizenId()))
            {
                assignBed(patient.module);
            }
        }
    }

    /**
     * Helper method to set bed occupation.
     *
     * @param bedPos   the position of the bed.
     * @param occupied if occupied.
     */
    private void setBedOccupation(final BlockPos bedPos, final boolean occupied)
    {
        final BlockState state = colony.getWorld().getBlockState(bedPos);
        if (state.is(BlockTags.BEDS))
        {
            colony.getWorld().setBlock(bedPos, state.setValue(BedBlock.OCCUPIED, occupied), 0x03);

            final BlockPos feetPos = bedPos.relative(state.getValue(BedBlock.FACING).getOpposite());
            final BlockState feetState = colony.getWorld().getBlockState(feetPos);

            if (feetState.is(BlockTags.BEDS))
            {
                colony.getWorld().setBlock(feetPos, feetState.setValue(BedBlock.OCCUPIED, occupied), 0x03);
            }
        }
    }

    @Override
    public void onWakeUp()
    {
        for (final Map.Entry<BlockPos, Integer> entry : new ArrayList<>(bedMap.entrySet()))
        {
            final BlockState state = colony.getWorld().getBlockState(entry.getKey());
            if (state.getBlock() instanceof BedBlock)
            {
                if (entry.getValue() == 0 && state.getValue(BedBlock.OCCUPIED))
                {
                    setBedOccupation(entry.getKey(), false);
                }
                else if (entry.getValue() != 0)
                {
                    final ICitizenData citizen = colony.getCitizenManager().getCivilian(entry.getValue());
                    if (citizen != null)
                    {
                        if (state.getValue(BedBlock.OCCUPIED))
                        {
                            if (!citizen.isAsleep() || citizen.getEntity().isEmpty() || citizen.getEntity().get().blockPosition().distSqr(entry.getKey()) > 2.0)
                            {
                                setBedOccupation(entry.getKey(), false);
                                bedMap.put(entry.getKey(), null);
                            }
                        }
                        else
                        {
                            if (citizen.isAsleep() && citizen.getEntity().isPresent() && citizen.getEntity().get().blockPosition().distSqr(entry.getKey()) < 2.0)
                            {
                                setBedOccupation(entry.getKey(), true);
                            }
                        }
                    }
                    else
                    {
                        bedMap.put(entry.getKey(), null);
                    }
                }
            }
            else
            {
                bedMap.remove(entry.getKey());
            }
        }
    }

    @Override
    public boolean canEat(final ItemStack stack)
    {
        if (isCureItem(stack))
        {
            return false;
        }

        return super.canEat(stack);
    }

    public boolean isPatientFinished(final int citizenId)
    {
        return !patients.containsKey(citizenId);
    }

    private record PatientModuleNode(IPatientModule module) implements Comparable<PatientModuleNode>
    {
        @Override
        public int compareTo(@NotNull final BuildingHospital.PatientModuleNode o)
        {
            if (module.getClass() != o.module.getClass())
            {
                return -1;
            }

            return module.compareTo(o.module);
        }
    }
}
