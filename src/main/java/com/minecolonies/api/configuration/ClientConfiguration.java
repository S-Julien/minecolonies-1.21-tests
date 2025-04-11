package com.minecolonies.api.configuration;

import com.minecolonies.api.configuration.builders.builder.ConfigBuilder;
import com.minecolonies.api.configuration.builders.builder.IConfigBuilder;
import com.minecolonies.api.configuration.builders.values.IConfigValue;

/**
 * Mod client configuration. Loaded clientside, not synced.
 */
public class ClientConfiguration
{
    public final IConfigValue<Boolean> citizenVoices;
    public final IConfigValue<Boolean> neighborbuildingrendering;
    public final IConfigValue<Integer> neighborbuildingrange;
    public final IConfigValue<Integer> buildgogglerange;
    public final IConfigValue<Boolean> colonyteamborders;
    public final IConfigValue<Boolean> holidayFeatures;

    /**
     * Builds client configuration.
     *
     * @param builder config builder
     */
    public ClientConfiguration(final IConfigBuilder builder)
    {
        final ConfigBuilder.ConfigCategoryBuilder gameplay = builder.createCategory("gameplay");
        citizenVoices = gameplay.defineBoolean("enablecitizenvoices", true);
        neighborbuildingrendering = gameplay.defineBoolean("neighborbuildingrendering", true);
        neighborbuildingrange = gameplay.defineInteger("neighborbuildingrange", 4, -2, 16);
        buildgogglerange = gameplay.defineInteger("buildgogglerange", 50, 1, 250);
        colonyteamborders = gameplay.defineBoolean("colonyteamborders", true);
        holidayFeatures = gameplay.defineBoolean("holidayfeatures", true);
    }
}
