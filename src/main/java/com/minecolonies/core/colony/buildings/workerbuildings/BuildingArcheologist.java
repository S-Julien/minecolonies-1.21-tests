package com.minecolonies.core.colony.buildings.workerbuildings;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.jobs.registry.JobEntry;
import com.minecolonies.api.crafting.IRecipeStorage;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.equipment.ModEquipmentTypes;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.WorldUtil;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
import com.minecolonies.core.colony.buildings.modules.AbstractCraftingBuildingModule;
import com.minecolonies.core.colony.buildings.modules.MinimumStockModule;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.minecolonies.api.util.constant.BuildingConstants.CONST_DEFAULT_MAX_BUILDING_LEVEL;
import static com.minecolonies.api.util.constant.Constants.STACKSIZE;
import static com.minecolonies.api.util.constant.EquipmentLevelConstants.TOOL_LEVEL_WOOD_OR_GOLD;

public class BuildingArcheologist extends AbstractBuilding
{

    /**
     * Constant name for the archeologist building
     */
    private static final String ARCHEOLOGIST = "archeologist";

    /**
     * How many trips we've run this period
     */
    private static final String TAG_CURRENT_TRIPS = "current_trips";

    /**
     * Which day in the period is it?
     */
    private static final String TAG_CURRENT_DAY = "current_day";

    /**
     * How many trips we can make per period by default
     */
    private static final int MAX_PER_PERIOD = 1;

    /**
     * How many days are in a period by default
     */
    private static final int PERIOD_DAYS = 3;

    /**
     * Exclusion list id.
     */
    public static final String FOOD_EXCLUSION_LIST = "food";

    /**
     * Which day we're at in the current period
     */
    private int currentPeriodDay = 0;

    /**
     * How many trips we've done in the current period
     */
    private int currentTrips = 0;

    /**
     * ServerTime for the last 'day' snapshot, to track days when doDaylightCycle is not happening.
     */
    private long snapTime;

    public BuildingArcheologist(@NotNull IColony colony, BlockPos pos)
    {
        super(colony, pos);

        keepX.put(itemStack -> ItemStackUtils.hasEquipmentLevel(itemStack, ModEquipmentTypes.axe.get(), TOOL_LEVEL_WOOD_OR_GOLD, getMaxEquipmentLevel()), new Tuple<>(1, true));
        keepX.put(itemStack -> ItemStackUtils.hasEquipmentLevel(itemStack, ModEquipmentTypes.pickaxe.get(), TOOL_LEVEL_WOOD_OR_GOLD, getMaxEquipmentLevel()), new Tuple<>(1, true));
        keepX.put(itemStack -> ItemStackUtils.hasEquipmentLevel(itemStack, ModEquipmentTypes.shovel.get(), TOOL_LEVEL_WOOD_OR_GOLD, getMaxEquipmentLevel()), new Tuple<>(1, true));
        keepX.put(itemStack -> ItemStackUtils.hasEquipmentLevel(itemStack, ModEquipmentTypes.sword.get(), TOOL_LEVEL_WOOD_OR_GOLD, getMaxEquipmentLevel()), new Tuple<>(1, true));

        keepX.put(itemStack -> itemStack.getItem() instanceof BrushItem, new Tuple<>(1, true));

        keepX.put(itemStack -> !ItemStackUtils.isEmpty(itemStack)
            && itemStack.getItem() instanceof ArmorItem
            && ((ArmorItem) itemStack.getItem()).getEquipmentSlot() == EquipmentSlot.HEAD, new Tuple<>(1, true));
        keepX.put(itemStack -> !ItemStackUtils.isEmpty(itemStack)
            && itemStack.getItem() instanceof ArmorItem
            && ((ArmorItem) itemStack.getItem()).getEquipmentSlot() == EquipmentSlot.CHEST, new Tuple<>(1, true));
        keepX.put(itemStack -> !ItemStackUtils.isEmpty(itemStack)
            && itemStack.getItem() instanceof ArmorItem
            && ((ArmorItem) itemStack.getItem()).getEquipmentSlot() == EquipmentSlot.LEGS, new Tuple<>(1, true));
        keepX.put(itemStack -> !ItemStackUtils.isEmpty(itemStack)
            && itemStack.getItem() instanceof ArmorItem
            && ((ArmorItem) itemStack.getItem()).getEquipmentSlot() == EquipmentSlot.FEET, new Tuple<>(1, true));
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return ARCHEOLOGIST;
    }

    @Override
    public int getMaxBuildingLevel()
    {
        return CONST_DEFAULT_MAX_BUILDING_LEVEL;
    }

    @Override
    public void onWakeUp()
    {
        super.onWakeUp();
        snapTime = colony.getWorld().getDayTime();
        if (this.currentPeriodDay < getPeriodDays())
        {
            this.currentPeriodDay++;
        }
        else
        {
            this.currentPeriodDay = 0;
            this.currentTrips = 0;
        }
    }

    @Override
    public void deserializeNBT(final CompoundTag compound)
    {
        super.deserializeNBT(compound);
        if (compound.contains(TAG_CURRENT_TRIPS))
        {
            this.currentTrips = compound.getInt(TAG_CURRENT_TRIPS);
        }

        if (compound.contains(TAG_CURRENT_DAY))
        {
            this.currentPeriodDay = compound.getInt(TAG_CURRENT_DAY);
        }
    }

    @Override
    public CompoundTag serializeNBT()
    {
        final CompoundTag compound = super.serializeNBT();

        compound.putInt(TAG_CURRENT_TRIPS, this.currentTrips);
        compound.putInt(TAG_CURRENT_DAY, this.currentPeriodDay);

        return compound;
    }

    @Override
    public int buildingRequiresCertainAmountOfItem(final ItemStack stack, final List<ItemStorage> localAlreadyKept, final boolean inventory, final JobEntry jobEntry)
    {
        if (stack.isEmpty())
        {
            return 0;
        }

        if (inventory && getFirstModuleOccurance(MinimumStockModule.class).isStocked(stack))
        {
            return stack.getCount();
        }

        // Check for materials needed to go on an expedition:
        IRecipeStorage rs = getFirstModuleOccurance(BuildingArcheologist.CraftingModule.class).getFirstRecipe(ItemStack::isEmpty);
        if (rs != null)
        {
            final ItemStorage kept = new ItemStorage(stack);
            boolean containsItem = rs.getInput().contains(kept);
            int keptCount = localAlreadyKept.stream().filter(storage -> storage.equals(kept)).mapToInt(ItemStorage::getAmount).sum();
            if (containsItem && (keptCount < STACKSIZE || !inventory))
            {
                if (localAlreadyKept.contains(kept))
                {
                    kept.setAmount(localAlreadyKept.remove(localAlreadyKept.indexOf(kept)).getAmount());
                }
                localAlreadyKept.add(kept);
                return 0;
            }
        }

        return super.buildingRequiresCertainAmountOfItem(stack, localAlreadyKept, inventory, jobEntry);
    }

    /**
     * Check to see if it's valid to do a trip by checking how many done in this current period
     *
     * @return true if the worker can go on an expedition
     */
    public boolean isReadyForTrip()
    {
        if (snapTime == 0)
        {
            snapTime = colony.getWorld().getDayTime();
        }
        if (Math.abs(colony.getWorld().getDayTime() - snapTime) >= 24000)
        {
            //Make sure we're incrementing if day/night cycle isn't running. 
            this.currentPeriodDay++;
        }
        return this.currentTrips < getMaxPerPeriod();
    }

    /**
     * Let the building know we're doing a trip
     */
    public void recordTrip()
    {
        this.currentTrips++;
    }

    /**
     * Get the max per period, potentially modified by research
     *
     * @return
     */
    public static int getMaxPerPeriod()
    {
        return MAX_PER_PERIOD;
    }

    /**
     * Get how many days are in a period, potentially modified by research.
     *
     * @return
     */
    public static int getPeriodDays()
    {
        return PERIOD_DAYS;
    }

    @Override
    public void onPlacement()
    {
        super.onPlacement();
        final Level world = colony.getWorld();
    }

    public static class CraftingModule extends AbstractCraftingBuildingModule.Custom
    {
        /**
         * Create a new module.
         *
         * @param jobEntry the entry of the job.
         */
        public CraftingModule(final JobEntry jobEntry)
        {
            super(jobEntry);
        }
    }
}
