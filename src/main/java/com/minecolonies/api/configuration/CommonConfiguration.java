package com.minecolonies.api.configuration;

import com.minecolonies.api.configuration.builders.builder.ConfigBuilder;
import com.minecolonies.api.configuration.builders.builder.IConfigBuilder;
import com.minecolonies.api.configuration.builders.values.IConfigValue;

public class CommonConfiguration
{
    public final IConfigValue<Boolean> generateSupplyLoot;
    public final IConfigValue<Boolean> rsEnableDebugLogging;

    /**
     * Builds client configuration.
     *
     * @param builder config builder
     */
    public CommonConfiguration(final IConfigBuilder builder)
    {
        final ConfigBuilder.ConfigCategoryBuilder gameplay = builder.createCategory("gameplay");
        generateSupplyLoot = gameplay.defineBoolean("generatesupplyloot", true);

        final ConfigBuilder.ConfigCategoryBuilder requestSystem = builder.createCategory("requestsystem");
        rsEnableDebugLogging = requestSystem.defineBoolean("enabledebuglogging", false);
    }
}
