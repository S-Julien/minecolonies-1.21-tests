package com.minecolonies.core.blocks.huts;

import com.minecolonies.api.blocks.AbstractBlockHut;
import com.minecolonies.api.colony.buildings.ModBuildings;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;

public class BlockHutArcheologist extends AbstractBlockHut<BlockHutArcheologist>
{

    /**
     * Method to return the name of the block.
     *
     * @return Name of the block.
     */
    @Override
    public String getHutName()
    {
        return "blockhutarcheologist";
    }

    /**
     * Method to get the building registry entry.
     *
     * @return The building entry.
     */
    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.archeologist.get();
    }
}
