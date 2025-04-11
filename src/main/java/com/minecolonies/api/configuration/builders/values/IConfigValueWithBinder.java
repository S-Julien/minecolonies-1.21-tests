package com.minecolonies.api.configuration.builders.values;

import net.minecraftforge.common.ForgeConfigSpec;

public interface IConfigValueWithBinder<T> extends IConfigValue<T>
{
    /**
     * Set the Forge config spec binding on this config value, allowing the config value to be queried.
     *
     * @param builder the Forge config spec builder.
     */
    void setBinding(final ForgeConfigSpec.Builder builder);
}
